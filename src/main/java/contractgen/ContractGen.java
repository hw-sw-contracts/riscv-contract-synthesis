package contractgen;

import contractgen.generator.iverilog.ParallelIverilogGenerator;
import contractgen.riscv.cva6.CVA6;
import contractgen.riscv.ibex.IBEX;
import contractgen.riscv.isa.RISCVInstruction;
import contractgen.riscv.isa.contract.RISCVContract;
import contractgen.riscv.isa.contract.RISCV_OBSERVATION_TYPE;

import static contractgen.riscv.isa.contract.RISCV_OBSERVATION_TYPE.*;

import contractgen.riscv.isa.tests.RISCVIterativeTests;
import contractgen.riscv.isa.tests.RISCVSimpleTestCases;
import contractgen.updater.ILPUpdater;
import contractgen.util.FileUtils;
import contractgen.util.StringUtils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Entrypoint for contract candidate generation
 */
public class ContractGen {

    /**
     * Main method.
     *
     * @param args Command line arguments.
     * @throws IOException On filesystem errors.
     */
    public static void main(String[] args) throws IOException {
        full_eval_cfg(CONFIG.ibex_small());
        full_eval_cfg(CONFIG.ibex_large());
        full_eval_cfg(CONFIG.cva6_small());
        full_eval_cfg(CONFIG.cva6_large());
    }

    private static void full_eval_cfg(CONFIG cfg) throws IOException {
        run(cfg);
        genStats(cfg, TEMPLATE.BASE);
        genStats(cfg, TEMPLATE.ALIGN);
        genStats(cfg, TEMPLATE.BRANCH);
        genStats(cfg, TEMPLATE.DEPENDENCIES);
    }

    public enum TEMPLATE {BASE, ALIGN, BRANCH, DEPENDENCIES}

    public static void genStats(CONFIG cfg, TEMPLATE template) throws IOException {
        Set<RISCV_OBSERVATION_TYPE> contract_template = switch (template) {
            case BASE -> getBase();
            case ALIGN -> Stream.concat(getBase().stream(), getAligned().stream()).collect(Collectors.toSet());
            case BRANCH ->
                    Stream.concat(Stream.concat(getBase().stream(), getAligned().stream()), getBranch().stream()).collect(Collectors.toSet());
            case DEPENDENCIES ->
                    Stream.concat(Stream.concat(Stream.concat(getBase().stream(), getAligned().stream()), getBranch().stream()), getDependencies().stream()).collect(Collectors.toSet());
        };
        String name = switch (template) {
            case BASE -> "statistics-base.csv";
            case ALIGN -> "statistics-align.csv";
            case BRANCH -> "statistics-branch.csv";
            case DEPENDENCIES -> "statistics-deps.csv";
        };
        Statistics.genStatsParallel(
                cfg.getPATH() + cfg.NAME + "/training-testcases.json",
                cfg.getPATH() + cfg.NAME + "/eval-testcases.json",
                cfg.getPATH() + cfg.NAME + "/" + name,
                cfg.THREADS,
                0,
                contract_template
        );

    }

    public static void run(CONFIG cfg) throws IOException {
        String path = cfg.getPATH() + cfg.NAME + "/";
        Files.createDirectories(Paths.get(path));
        cfg.toJSON(new FileWriter(Path.of(path + "config.json").toFile()));

        // TRAINING
        Contract training_contract = switch (cfg.TRAINING_SOURCE) {
            case NEW -> {
                TestCases training_tc = new RISCVIterativeTests(cfg.subsets, cfg.allowed_observations, cfg.TRAINING_NEW_SEED, cfg.THREADS, cfg.TRAINING_NEW_COUNT);
                Generator training_generator = new ParallelIverilogGenerator(cfg.CORE == CONFIG.PROCESSOR.IBEX ? new IBEX(new ILPUpdater(), training_tc) : new CVA6(new ILPUpdater(), training_tc), cfg.THREADS, cfg.DEBUG, cfg);
                generate(training_generator, path + "training");
                yield training_generator.MARCH.getISA().getContract();
            }
            case EXISTING -> {
                FileReader reader = new FileReader(cfg.getPATH() + cfg.TRAINING_EXISTING_NAME + "/" + (cfg.TRAINING_EXISTING_FLIP_T_E ? "eval" : "training") + "-testcases.json");
                Contract contract = RISCVContract.fromJSON(reader);
                contract.update(true);
                yield contract;
            }
            case PREDEFINED -> cfg.TRAINING_PREDEFINED;
        };

        // EVAL
        List<TestResult> eval_results = switch (cfg.EVAL_SOURCE) {
            case NEW -> {
                TestCases eval_tc = new RISCVIterativeTests(cfg.subsets, cfg.allowed_observations, cfg.EVAL_NEW_SEED, cfg.THREADS, cfg.EVAL_NEW_COUNT);
                Generator eval_generator = new ParallelIverilogGenerator(cfg.CORE == CONFIG.PROCESSOR.IBEX ? new IBEX(new ILPUpdater(), eval_tc) : new CVA6(new ILPUpdater(), eval_tc), cfg.THREADS, cfg.DEBUG, cfg);
                generate(eval_generator, path + "eval");
                yield eval_generator.MARCH.getISA().getContract().getTestResults();
            }
            case EXISTING -> {
                FileReader reader = new FileReader(cfg.getPATH() + cfg.EVAL_EXISTING_NAME + "/" + (cfg.EVAL_EXISTING_FLIP_T_E ? "training" : "eval") + "-testcases.json");
                yield RISCVContract.fromJSON(reader).getTestResults();
            }
            case PREDEFINED -> throw new IllegalArgumentException("Cannot use a PREDEFINED contract for evaluation.");
        };

        // STATISTICS

        basicStats(training_contract, eval_results, cfg.NAME, path + "stats", cfg.getPATH());

    }

    /**
     * @param contract    The contract to check against.
     * @param results     The test results that are checked.
     * @param name        The name of the run.
     * @param path        The path to write stats.
     * @param global_path The path to write to global table.
     * @throws IOException
     */
    private static void basicStats(Contract contract, List<TestResult> results, String name, String path, String global_path) throws IOException {
        int true_positive = 0;
        int true_negative = 0;
        int false_positive = 0;
        int false_negative = 0;
        Map<Observation, Integer> fp_counter = new HashMap<>();
        Set<TestResult> fn_set = new HashSet<>();
        for (TestResult res : results) {
            boolean covered = contract.covers(res);
            if (res.isAdversaryDistinguishable()) {
                if (covered)
                    true_positive++;
                else {
                    false_negative++;
                    fn_set.add(res);
                }
            } else {
                if (covered) {
                    Contract.whyCovers(contract.getCurrentContract(), res).forEach(obs -> {
                        if (fp_counter.containsKey(obs)) {
                            fp_counter.put(obs, fp_counter.get(obs) + 1);
                        } else {
                            fp_counter.put(obs, 1);
                        }
                    });
                    false_positive++;
                } else
                    true_negative++;
            }
        }
        System.out.println("Results: true_negative: " + true_negative + ", false_negative: " + false_negative + ", true_positive: " + true_positive + ", false_positive: " + false_positive);
        StringBuilder fp_str = new StringBuilder();
        fp_counter.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).forEach(e -> fp_str.append(e.getValue()).append("\t").append(e.getKey()).append("\n"));
        Files.write(Path.of(path + "-false-positives.txt"), fp_str.toString().getBytes());
        StringBuilder fn_str = new StringBuilder();
        fn_set.forEach(res -> fn_str.append(res.getIndex() / 2).append(":\t").append(res.getPossibleObservations()).append("\n"));
        Files.write(Path.of(path + "-false-negatives.txt"), fn_str.toString().getBytes());
        double precision = ((double) true_positive) / ((double) true_positive + false_positive);
        double sensitivity = ((double) true_positive) / ((double) true_positive + false_negative);
        double accuracy = ((double) true_positive + true_negative) / ((double) true_negative + true_positive + false_negative + false_positive);
        StringBuilder stats_str = new StringBuilder();
        stats_str.append("name,traningTotal,evalTotal,trueNegative,falseNegative,truePositive,falsePositive,precision,sensitivity,accuracy\n");
        StringBuilder raw_stats = new StringBuilder();
        raw_stats.append(name).append(",").append(contract.getTotal() / 2).append(",").append(results.size() / 2).append(",");
        raw_stats.append(true_negative / 2).append(",").append(false_negative / 2).append(",").append(true_positive / 2).append(",").append(false_positive / 2).append(",");
        raw_stats.append(precision).append(",").append(sensitivity).append(",").append(accuracy).append("\n");
        stats_str.append(raw_stats);
        Files.write(Path.of(path + ".csv"), stats_str.toString().getBytes());
        if (!Files.exists(Path.of(global_path + "stats.csv"))) {
            Files.write(Path.of(global_path + "stats.csv"), stats_str.toString().getBytes());
        } else {
            Files.write(Path.of(global_path + "stats.csv"), raw_stats.toString().getBytes(), StandardOpenOption.APPEND);
        }
    }

    /**
     * Starts contract candidate generation and stores the results in a file.
     *
     * @param generator The generator to be used.
     * @param path      The path to write the serialized contract to.
     * @throws IOException on Filesystem errors.
     */
    public static void generate(Generator generator, String path) throws IOException {
        long start = System.currentTimeMillis();
        Contract contract = generator.generate();
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("Generation time: " + timeElapsed);
        System.out.println(contract);
        Files.write(Path.of(path + "-contract.txt"), contract.toString().getBytes());
        contract.toJSON(new FileWriter(Path.of(path + "-testcases.json").toFile()));
    }

    /**
     * @param evaluator The evaluator to be used.
     * @param path      The path of the file containing the serialized contract candidate
     */
    public static void evaluate(Evaluator evaluator, String path) {
        evaluator.evaluate(path);
    }

}

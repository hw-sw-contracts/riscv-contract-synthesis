package contractgen;

import contractgen.generator.iverilog.ParallelIverilogGenerator;
import contractgen.riscv.cva6.CVA6;
import contractgen.riscv.ibex.IBEX;
import contractgen.riscv.isa.RISCV_SUBSET;
import contractgen.riscv.isa.tests.RISCVRandomTests;
import contractgen.updater.ILPUpdater;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * Entrypoint for contract candidate generation
 */
public class ContractGen {

    /**
     * Main method.
     *
     * @param args          Command line arguments.
     * @throws IOException  On filesystem errors.
     */
    public static void main(String[] args) throws IOException {
        TestCases training =  new RISCVRandomTests(Set.of(RISCV_SUBSET.BASE, RISCV_SUBSET.M), 2032023, 3 * 20);
        TestCases eval =  new RISCVRandomTests(Set.of(RISCV_SUBSET.BASE, RISCV_SUBSET.M), 20230302, 3 * 100);

        // Run Contract Candidate Generation on Ibex
        Generator ibex_generator = new ParallelIverilogGenerator(new IBEX(new ILPUpdater(), training), 12, false);
        String ibex_path_training = "results/ibex/ibex-training-20000.json";
        generate(ibex_generator, ibex_path_training);
        ibex_generator = new ParallelIverilogGenerator(new IBEX(new ILPUpdater(), eval), 12, false);
        String ibex_path_eval = "results/ibex/ibex-eval-100000.json";
        generate(ibex_generator, ibex_path_eval);
        String ibex_path_stats = "results/ibex/ibex-stats.csv";
        Statistics.genStatsParallel(ibex_path_training, ibex_path_eval, ibex_path_stats , 12, 0);
        Evaluator ibex_eval = new Evaluator(new IBEX(new ILPUpdater(), eval), 12);
        evaluate(ibex_eval, ibex_path_training);

        // Run Contract Candidate Generation on CVA6
        Generator cva6_generator = new ParallelIverilogGenerator(new CVA6(new ILPUpdater(), training), 12, false);
        String cva6_path_training = "results/cva6/cva6-training-20000.json";
        generate(cva6_generator, cva6_path_training);
        cva6_generator = new ParallelIverilogGenerator(new CVA6(new ILPUpdater(), eval), 12, false);
        String cva6_path_eval = "results/cva6/cva6-eval-100000.json";
        generate(cva6_generator, cva6_path_eval);
        String cva6_path_stats = "results/cva6/cva6-stats.csv";
        Statistics.genStatsParallel(cva6_path_training, cva6_path_eval, cva6_path_stats , 12, 0);
        Evaluator cva6_eval = new Evaluator(new CVA6(new ILPUpdater(), eval), 12);
        evaluate(cva6_eval, cva6_path_training);
    }

    /**
     * Starts contract candidate generation and stores the results in a file.
     *
     * @param generator The generator to be used.
     * @param path      The path to write the serialized contract to.
     */
    public static void generate(Generator generator, String path) {
        Contract contract = generator.generate();
        System.out.println(contract);
        try {
            Files.write(Path.of(path), contract.toJSON().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param evaluator The evaluator to be used.
     * @param path      The path of the file containing the serialized contract candidate
     */
    public static void evaluate(Evaluator evaluator, String path) {
        evaluator.evaluate(path);
    }

}

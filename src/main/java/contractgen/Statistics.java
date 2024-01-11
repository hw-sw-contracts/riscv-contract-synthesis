package contractgen;

import contractgen.riscv.isa.contract.RISCVContract;
import contractgen.riscv.isa.contract.RISCV_OBSERVATION_TYPE;
import contractgen.updater.ILPUpdater;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Generates statistics from two sets of evaluated test cases.
 */
public class Statistics {

    /**
     * @param training             The path containing the deserialized contract generated from the training set.
     * @param eval                 The path containing the deserialized contract generated from the evaluation set.
     * @param results              The path to store the results in.
     * @param COUNT                The number of threads to use.
     * @param startingAt           The index at which the statistics generation should resume in case it has been interrupted.
     * @param allowed_observations The set of allowed observations.
     * @throws IOException On filesystem errors.
     */
    public static void genStatsParallel(String training, String eval, String results, int COUNT, int startingAt, Set<RISCV_OBSERVATION_TYPE> allowed_observations) throws IOException {

        FileWriter fstream = new FileWriter(results, startingAt > 0); //true tells to append data.
        BufferedWriter out = new BufferedWriter(fstream);
        if (startingAt <= 0) {
            out.write("index;needs_update;updated;size;true_positive_self;true_negative_self;false_positive_self;false_negative_self;true_positive_eval;true_negative_eval;false_positive_eval;false_negative_eval;\n");
        }
        RISCVContract evalset = RISCVContract.fromJSON(new FileReader(eval));
        evalset.restrictObservations(allowed_observations);
        List<Thread> runners = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            runners.add(new Thread(new StatisticsRunner(i, COUNT, training, evalset, out, startingAt, allowed_observations), "Runner_" + (i + 1)));
        }
        runners.forEach(Thread::start);
        runners.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        out.close();
    }

    /**
     * @param id                   The id of the runner.
     * @param COUNT                The total number of runners.
     * @param training             The training set.
     * @param evalset              The eval set.
     * @param out                  The file to write the output to.
     * @param startingAt           Where to start at in case of an interruption.
     * @param allowed_observations The set of allowed observations.
     */
    private record StatisticsRunner(int id, int COUNT, String training, RISCVContract evalset, BufferedWriter out,
                                    int startingAt,
                                    Set<RISCV_OBSERVATION_TYPE> allowed_observations) implements Runnable {
        @Override
        public void run() {
            int STEP_SIZE = 2;
            Integer end = 2000;
            RISCVContract reference;
            try {
                reference = RISCVContract.fromJSON(new FileReader(training));
                reference.sort();
                reference.restrictObservations(allowed_observations);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            RISCVContract contract = new RISCVContract(new ILPUpdater());
            for (int i = 0; i < startingAt; i++) {
                contract.add(reference.getTestResults().get(i));
            }
            end = end == null ? reference.getTestResults().size() : end;

            for (int i = startingAt; i < end; i++) {
                if (i % STEP_SIZE != 0 || (i / STEP_SIZE) % COUNT != id) {
                    contract.add(reference.getTestResults().get(i));
                    continue;
                }
                contract.update(true);
                TestResult addition = reference.getTestResults().get(i);
                boolean needsUpdate = addition.isAdversaryDistinguishable() && !contract.covers(addition);
                Set<Observation> old = contract.getCurrentContract();
                contract.add(reference.getTestResults().get(i));
                contract.update(true);
                boolean updated = !Objects.equals(old, contract.getCurrentContract());
                int size = contract.getCurrentContract().size();
                int true_positive_self = 0;
                int true_negative_self = 0;
                int false_positive_self = 0;
                int false_negative_self = 0;
                for (TestResult item : contract.getTestResults()) {
                    boolean covered = contract.covers(item);
                    if (item.isAdversaryDistinguishable()) {
                        if (covered)
                            true_positive_self++;
                        else
                            false_negative_self++;
                    } else {
                        if (covered)
                            false_positive_self++;
                        else
                            true_negative_self++;
                    }
                }

                int true_positive_eval = 0;
                int true_negative_eval = 0;
                int false_positive_eval = 0;
                int false_negative_eval = 0;
                Map<Observation, Integer> counter = new HashMap<>();
                for (TestResult item : evalset.getTestResults()) {
                    boolean covered = contract.covers(item);
                    if (item.isAdversaryDistinguishable()) {
                        if (covered)
                            true_positive_eval++;
                        else
                            false_negative_eval++;
                    } else {
                        if (covered) {
                            Contract.whyCovers(contract.getCurrentContract(), item).forEach(obs -> {
                                if (counter.containsKey(obs)) {
                                    counter.put(obs, counter.get(obs) + 1);
                                } else {
                                    counter.put(obs, 1);
                                }
                            });
                            false_positive_eval++;
                        } else
                            true_negative_eval++;
                    }
                }
                System.out.print("Progress: " + i + " of " + reference.getTestResults().size() + "\r");
                StringBuilder result = new StringBuilder();
                result.append(i).append(";");
                result.append(needsUpdate).append(";");
                result.append(updated).append(";");
                result.append(size).append(";");
                result.append(true_positive_self).append(";");
                result.append(true_negative_self).append(";");
                result.append(false_positive_self).append(";");
                result.append(false_negative_self).append(";");
                result.append(true_positive_eval).append(";");
                result.append(true_negative_eval).append(";");
                result.append(false_positive_eval).append(";");
                result.append(false_negative_eval).append(";");
                result.append("\n");
                try {
                    synchronized (out) {
                        out.write(result.toString());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

package contractgen;

import contractgen.riscv.isa.contract.RISCVContract;
import contractgen.updater.ILPUpdater;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Generates statistics from two sets of evaluated test cases.
 */
public class Statistics {

    /**
     * @param training      The path containing the deserialized contract generated from the training set.
     * @param eval          The path containing the deserialized contract generated from the evaluation set.
     * @param results       The path to store the results in.
     * @param COUNT         The number of threads to use.
     * @param startingAt    The index at which the statistics generation should resume in case it has been interrupted.
     * @throws IOException  On filesystem errors.
     */
    public static void genStatsParallel(String training, String eval, String results, int COUNT, int startingAt) throws IOException {

        FileWriter fstream = new FileWriter(results, startingAt > 0); //true tells to append data.
        BufferedWriter out = new BufferedWriter(fstream);
        if (startingAt <= 0) {
            out.write("index;needs_update;updated;size;true_positive_self;true_negative_self;false_positive_self;false_negative_self;true_positive_eval;true_negative_eval;false_positive_eval;false_negative_eval;\n");
        }
        List<Thread> runners = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            runners.add(new Thread(new StatisticsRunner(i, COUNT, training, eval, out, startingAt), "Runner_" + (i + 1)));
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

    private record StatisticsRunner(int id, int COUNT, String training, String eval, BufferedWriter out,
                                    int startingAt) implements Runnable {
        @Override
            public void run() {
                RISCVContract reference;
                RISCVContract evalset;
                try {
                    reference = RISCVContract.fromJSON(Files.readString(Path.of(training)));
                    reference.sort();
                    evalset = RISCVContract.fromJSON(Files.readString(Path.of(eval)));
                    evalset.sort();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                RISCVContract contract = new RISCVContract(new ILPUpdater());
                for (int i = 0; i < startingAt; i++) {
                    contract.add(reference.getTestResults().get(i));
                }
                for (int i = startingAt; i < reference.getTestResults().size(); i++) {
                    if (i % COUNT != id) {
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
                    for (TestResult item : evalset.getTestResults()) {
                        boolean covered = contract.covers(item);
                        if (item.isAdversaryDistinguishable()) {
                            if (covered)
                                true_positive_eval++;
                            else
                                false_negative_eval++;
                        } else {
                            if (covered)
                                false_positive_eval++;
                            else
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

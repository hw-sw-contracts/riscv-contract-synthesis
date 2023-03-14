package contractgen.generator.iverilog;

import contractgen.*;
import contractgen.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generate a contract using Icarus Verilog using multiple threads.
 */
public class ParallelIverilogGenerator extends Generator {

    private final int COUNT;
    private final boolean DEBUG;

    /**
     * @param MARCH The microarchitecture to be used.
     * @param COUNT The number of threads to be used.
     * @param DEBUG Whether statistical values should be collected.
     */
    public ParallelIverilogGenerator(contractgen.MARCH MARCH, final int COUNT, final boolean DEBUG) {
        super(MARCH);
        this.COUNT = COUNT;
        this.DEBUG = DEBUG;
    }

    @Override
    public Contract generate() {
            System.out.println("Evaluating " + MARCH.getISA().getTestCases().getTestCaseList().size() + " test cases.");
            AtomicInteger atomic_i = new AtomicInteger(0);
            Map<Integer, Boolean> changes = new HashMap<>();
            Map<Integer, Integer> sizes = new HashMap<>();
            MARCH.compile();
            List<Thread> runners = new ArrayList<>();
            for (int i = 0; i < COUNT; i++) {
                runners.add(new Thread(new Runner(MARCH, i + 1, COUNT, atomic_i, changes, sizes, DEBUG), "Runner_" + (i + 1)));
            }
            runners.forEach(Thread::start);
            runners.forEach(t -> {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            MARCH.getISA().getContract().update(true);
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                changes.forEach((k, e) -> sb.append(k).append(";").append(e).append(";\n"));
                try {
                    Files.write(Path.of("changes-" + MARCH.getName() + "-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".csv"), sb.toString().getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                StringBuilder sb_sizes = new StringBuilder();
                sizes.forEach((k, e) -> sb_sizes.append(k).append(";").append(e).append(";\n"));
                try {
                    Files.write(Path.of("sizes-" + MARCH.getName() + "-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".csv"), sb_sizes.toString().getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return MARCH.getISA().getContract();
    }

    public static class Runner implements Runnable {

        private final MARCH MARCH;
        private final int id;
        private final int COUNT;
        private final AtomicInteger atomic_i;
        private final Map<Integer, Boolean> changes;
        private final Map<Integer, Integer> sizes;
        private final boolean DEBUG;

        Runner(MARCH MARCH, int id, int COUNT, AtomicInteger atomic_i, Map<Integer, Boolean> changes, Map<Integer, Integer> sizes, final boolean DEBUG) {
            this.MARCH = MARCH;
            this.id = id;
            this.COUNT = COUNT;
            this.atomic_i = atomic_i;
            this.changes = changes;
            this.sizes = sizes;
            this.DEBUG = DEBUG;
        }

        @Override
        public void run() {
            for (TestCase testCase: MARCH.getISA().getTestCases().getChunk(id, COUNT)) {
                MARCH.writeTestCase(id, testCase);
                SIMULATION_RESULT pass = MARCH.simulate(id);
                int i = atomic_i.incrementAndGet();
                System.out.printf("Current progress: %d of %d.\r", i, MARCH.getISA().getTestCases().getTestCaseList().size());
                boolean change = false;
                switch (pass) {
                    case SUCCESS, FALSE_POSITIVE -> {
                        Pair<TestResult, TestResult> positive_ctx = MARCH.extractDifferences(id, testCase.getIndex());
                        synchronized (MARCH.getISA().getContract()) {
                            MARCH.getISA().getContract().add(positive_ctx.left());
                            MARCH.getISA().getContract().add(positive_ctx.right());
                            if (DEBUG) {
                                change = MARCH.getISA().getContract().update(true);
                            }

                        }
                    }
                    case FAIL -> {
                        Pair<TestResult, TestResult> ctx = MARCH.extractCTX(id, testCase);
                        synchronized (MARCH.getISA().getContract()) {
                            MARCH.getISA().getContract().add(ctx.left());
                            MARCH.getISA().getContract().add(ctx.right());
                            if (DEBUG) {
                                change = MARCH.getISA().getContract().update(true);
                            }

                        }
                    }
                    case TIMEOUT, UNKNOWN -> {
                        if (DEBUG) {
                            System.out.println("Problem with test case:" + pass);
                            System.out.println(testCase);
                        }
                    }
                }
                if (DEBUG) {
                    synchronized (changes) {
                        changes.put(i, change);
                    }
                    synchronized (sizes) {
                        sizes.put(i, MARCH.getISA().getContract().getSize());
                    }
                }
            }
        }
    }
}

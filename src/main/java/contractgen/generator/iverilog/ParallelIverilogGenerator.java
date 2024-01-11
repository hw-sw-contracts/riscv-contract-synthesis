package contractgen.generator.iverilog;

import contractgen.*;
import contractgen.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Generate a contract using Icarus Verilog using multiple threads.
 */
public class ParallelIverilogGenerator extends Generator {

    /**
     * The number of threads to be used.
     */
    private final int COUNT;
    /**
     * Whether the execution time should be debugged.
     */
    private final boolean DEBUG;

    /**
     * The evaluated configuration.
     */
    private final CONFIG CFG;

    /**
     * @param MARCH The microarchitecture to be used.
     * @param COUNT The number of threads to be used.
     * @param DEBUG Whether statistical values should be collected.
     * @param CFG   The evaluated configuration
     */
    public ParallelIverilogGenerator(contractgen.MARCH MARCH, final int COUNT, final boolean DEBUG, final CONFIG CFG) {
        super(MARCH);
        this.COUNT = COUNT;
        this.DEBUG = DEBUG;
        this.CFG = CFG;
    }

    @Override
    public Contract generate() throws IOException {
        System.out.println("Evaluating " + MARCH.getISA().getTestCases().getTotalNumber() + " test cases.");
        AtomicInteger atomic_i = new AtomicInteger(0);
        FileWriter stats = null;
        long start = 0;
        long finish = 0;
        long timeElapsed = 0;
        if (DEBUG) {
            stats = new FileWriter(CFG.getPATH() + CFG.NAME + "/" + "debug.txt");
            start = System.currentTimeMillis();
        }
        MARCH.compile();
        if (DEBUG) {
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;
            stats.write("Compilation time: " + timeElapsed + "\n");
        }
        List<Thread> runners = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            runners.add(new Thread(new Runner(MARCH, i + 1, atomic_i, DEBUG, stats), "Runner_" + (i + 1)));
        }
        runners.forEach(Thread::start);
        runners.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        if (DEBUG) {
            start = System.currentTimeMillis();
        }
        MARCH.getISA().getContract().update(true);
        if (DEBUG) {
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;
            stats.write("Update time: " + timeElapsed + "\n");
            stats.flush();
            stats.close();
        }
        return MARCH.getISA().getContract();
    }

    /**
     * A contractgen runner.
     */
    public static class Runner implements Runnable {

        /**
         * The microarchitecture.
         */
        private final MARCH MARCH;
        /**
         * The id of the runner.
         */
        private final int id;
        /**
         * A synchronized counter to show a progress.
         */
        private final AtomicInteger atomic_i;
        /**
         * Whether the execution time should be debugged.
         */
        private final boolean DEBUG;

        /**
         * A file to write statistics to
         */
        private final FileWriter stats;

        /**
         * @param MARCH    The microarchitecture.
         * @param id       The id of the runner.
         * @param atomic_i A synchronized counter to show a progress.
         * @param DEBUG    Whether the execution time should be debugged.
         * @param stats    A file to write statistics to
         */
        Runner(MARCH MARCH, int id, AtomicInteger atomic_i, final boolean DEBUG, FileWriter stats) {
            this.MARCH = MARCH;
            this.id = id;
            this.atomic_i = atomic_i;
            this.DEBUG = DEBUG;
            this.stats = stats;
        }

        @Override
        public void run() {
            AtomicInteger x = new AtomicInteger();
            AtomicLong sum_sim = new AtomicLong();
            AtomicLong sum_ana = new AtomicLong();
            MARCH.getISA().getTestCases().getIterator(id - 1).forEachRemaining(testCase -> {
                x.getAndIncrement();
                long start = 0;
                long finish = 0;
                long timeElapsed = 0;
                if (DEBUG) {
                    start = System.currentTimeMillis();
                }
                MARCH.writeTestCase(id, testCase);
                SIMULATION_RESULT pass = MARCH.simulate(id);
                if (DEBUG) {
                    finish = System.currentTimeMillis();
                    timeElapsed = finish - start;
                    sum_sim.addAndGet(timeElapsed);
                }
                int i = atomic_i.incrementAndGet();
                System.out.printf("Current progress: %d of %d.\r", i, MARCH.getISA().getTestCases().getTotalNumber());
                boolean change = false;
                switch (pass) {
                    case SUCCESS, FALSE_POSITIVE -> {
                        if (DEBUG) {
                            start = System.currentTimeMillis();
                        }
                        Pair<TestResult, TestResult> positive_ctx = MARCH.extractDifferences(id, testCase.getIndex());
                        if (DEBUG) {
                            finish = System.currentTimeMillis();
                            timeElapsed = finish - start;
                            sum_ana.addAndGet(timeElapsed);
                        }
                        synchronized (MARCH.getISA().getContract()) {
                            MARCH.getISA().getContract().add(positive_ctx.left());
                            MARCH.getISA().getContract().add(positive_ctx.right());
                        }
                    }
                    case FAIL -> {

                        if (DEBUG) {
                            start = System.currentTimeMillis();
                        }
                        Pair<TestResult, TestResult> ctx = MARCH.extractCTX(id, testCase);
                        if (DEBUG) {
                            finish = System.currentTimeMillis();
                            timeElapsed = finish - start;
                            sum_ana.addAndGet(timeElapsed);
                        }
                        synchronized (MARCH.getISA().getContract()) {
                            MARCH.getISA().getContract().add(ctx.left());
                            MARCH.getISA().getContract().add(ctx.right());
                        }
                    }
                    case TIMEOUT, UNKNOWN -> {
                        System.out.println("Problem with test case:" + pass);
                        System.out.println(testCase);
                    }
                }
            });
            if (DEBUG) {
                try {
                    stats.write("[" + id + "]\tSimulation:\t" + (sum_sim.get() / x.get()) + "ms\t\t Analysis:\t" + (sum_ana.get() / x.get()) + "ms\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

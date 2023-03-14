package contractgen;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Evaluates a generated contract by checking whether the contract correctly classifies the given set of test cases.
 */
public class Evaluator {

    private final MARCH MARCH;
    private final int COUNT;

    /**
     * @param MARCH The microarchitecture to be used in evaluation
     * @param COUNT The number of threads to be used in evaluation
     */
    public Evaluator(MARCH MARCH, int COUNT) {
        this.MARCH = MARCH;
        this.COUNT = COUNT;
    }

    /**
     * Starts the evaluation using the contract candidate provided as parameter.
     *
     * @param path The path of the file containing the serialized contract candidate.
     */
    public void evaluate(String path) {
        try {
            MARCH.getISA().loadContract(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(MARCH.getISA().getContract());
        MARCH.compile();

        AtomicInteger atomic_i = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        AtomicInteger false_positive = new AtomicInteger(0);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger timeout = new AtomicInteger(0);
        List<Thread> runners = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            runners.add(new Thread(new Evaluator.Runner(MARCH, i + 1, COUNT,atomic_i, fail, false_positive, success, timeout), "Runner_" + (i + 1)));
        }
        long start = System.currentTimeMillis();
        runners.forEach(Thread::start);
        runners.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("Out of " + MARCH.getISA().getTestCases().getTestCaseList().size() + " test cases, " + fail.get() + " failed, " + false_positive.get() + " were falsely classified, " + success.get() + " succeeded and " + timeout.get() + "timed out.");
        Duration duration = Duration.ofMillis(end - start);
        long HH = duration.toHours();
        long MM = duration.toMinutesPart();
        long SS = duration.toSecondsPart();
        System.out.println("Total duration:" + String.format("%02d:%02d:%02d", HH, MM, SS));
    }

    /**
     * Wrapper for the evaluation to allow multiple threads.
     */
    public static class Runner implements Runnable {

        private final MARCH MARCH;
        private final int id;
        private final int COUNT;
        private final AtomicInteger atomic_i;
        private final AtomicInteger fail;
        private final AtomicInteger false_positive;
        private final AtomicInteger success;
        private final AtomicInteger timeout;


        Runner(MARCH MARCH, int id, int COUNT, AtomicInteger atomic_i, AtomicInteger fail, AtomicInteger false_positive, AtomicInteger success, AtomicInteger timeout) {
            this.MARCH = MARCH;
            this.id = id;
            this.COUNT = COUNT;
            this.atomic_i = atomic_i;
            this.fail = fail;
            this.false_positive = false_positive;
            this.success = success;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            for (TestCase testCase: MARCH.getISA().getTestCases().getChunk(id, COUNT)) {
                MARCH.writeTestCase(id, testCase);
                SIMULATION_RESULT pass = MARCH.simulate(id);
                int i = atomic_i.incrementAndGet();
                System.out.printf("Current progress: %d of %d.\r", i, MARCH.getISA().getTestCases().getTestCaseList().size());
                switch (pass) {
                    case SUCCESS -> success.incrementAndGet();
                    case FAIL -> fail.incrementAndGet();
                    case FALSE_POSITIVE -> false_positive.incrementAndGet();
                    case TIMEOUT -> timeout.incrementAndGet();
                    case UNKNOWN -> System.out.println("Unknown error.");
                }
            }
        }
    }
}

package contractgen;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Evaluator {

    private final MARCH MARCH;
    private final int COUNT;

    public Evaluator(MARCH MARCH, int COUNT) {
        this.MARCH = MARCH;
        this.COUNT = COUNT;
    }

    public void evaluate(String path) {
        try {
            MARCH.getISA().loadContract(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(MARCH.getISA().getContract());
        MARCH.compile();

        AtomicInteger atomic_i = new AtomicInteger(0);
        AtomicInteger count = new AtomicInteger(0);
        List<Thread> runners = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            runners.add(new Thread(new Evaluator.Runner(MARCH, i + 1, COUNT,atomic_i, count), "Runner_" + (i + 1)));
        }
        runners.forEach(Thread::start);
        runners.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("Out of " + MARCH.getISA().getTestCases().getTestCaseList().size() + " test cases, " + count.get() + " failed.");
    }

    public static class Runner implements Runnable {

        private final MARCH MARCH;
        private final int id;
        private final int COUNT;
        private final AtomicInteger atomic_i;
        private final AtomicInteger counter;


        Runner(MARCH MARCH, int id, int COUNT, AtomicInteger atomic_i, AtomicInteger counter) {
            this.MARCH = MARCH;
            this.id = id;
            this.COUNT = COUNT;
            this.atomic_i = atomic_i;
            this.counter = counter;
        }

        @Override
        public void run() {
            for (TestCase testCase: MARCH.getISA().getTestCases().getChunk(id, COUNT)) {
                MARCH.writeTestCase(id, testCase);
                //System.out.println(testCase);
                //milis = System.currentTimeMillis();
                boolean pass = MARCH.simulate(id);
                //long time = System.currentTimeMillis() - milis;
                //avg = avg + time;
                int i = atomic_i.incrementAndGet();
                System.out.printf("Current progress: %d of %d.\r", i, MARCH.getISA().getTestCases().getTestCaseList().size());
                if (!pass) {
                    counter.incrementAndGet();
                    //Pair<TestResult, TestResult> positive_ctx = MARCH.extractDifferences(1);
                    //System.out.println(positive_ctx.getLeft());
                    //System.out.println("FAIL");
                    //System.out.println(testCase);
                } else {
                    //System.out.println("PASS");
                }
            }
        }
    }
}

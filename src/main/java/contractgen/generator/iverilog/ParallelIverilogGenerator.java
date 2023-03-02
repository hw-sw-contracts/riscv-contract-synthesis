package contractgen.generator.iverilog;

import contractgen.*;
import contractgen.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelIverilogGenerator extends Generator {

    private final int COUNT;
    private final boolean DEBUG;

    public ParallelIverilogGenerator(contractgen.MARCH march, final int COUNT, final boolean DEBUG) {
        super(march);
        this.COUNT = COUNT;
        this.DEBUG = DEBUG;
    }

    @Override
    public Contract generate() {
            System.out.println("Evaluating " + MARCH.getISA().getTestCases().getTestCaseList().size() + " test cases.");
            AtomicInteger atomic_i = new AtomicInteger(0);
            Map<Integer, Boolean> changes = new HashMap<>();
            //for (RISCV_TYPE type: RISCV_TYPE.values()) {
            //    MARCH.getISA().getContract().add(new RISCVCounterexample(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.OPCODE))));
            //}
        /*
        try {
            MARCH.getISA().loadContract(Path.of("contract.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        */
            MARCH.compile();
            List<Thread> runners = new ArrayList<>();
            for (int i = 0; i < COUNT; i++) {
                runners.add(new Thread(new Runner(MARCH, i + 1, COUNT, atomic_i, changes, DEBUG), "Runner_" + (i + 1)));
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
                    Files.write(Path.of("changes.csv"), sb.toString().getBytes());
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
        private final boolean DEBUG;

        Runner(MARCH MARCH, int id, int COUNT, AtomicInteger atomic_i, Map<Integer, Boolean> changes, final boolean DEBUG) {
            this.MARCH = MARCH;
            this.id = id;
            this.COUNT = COUNT;
            this.atomic_i = atomic_i;
            this.changes = changes;
            this.DEBUG = DEBUG;
        }

        @Override
        public void run() {
            // compile
            //long milis = System.currentTimeMillis();
            //MARCH.compile();
            //System.out.println(System.currentTimeMillis() - milis);
            // copy binary and write test case
            //int i = 0;
            //long avg = 0;
            for (TestCase testCase: MARCH.getISA().getTestCases().getChunk(id, COUNT)) {
                MARCH.writeTestCase(id, testCase);
                //System.out.println(testCase);
                //milis = System.currentTimeMillis();
                boolean pass = MARCH.simulate(id);
                //long time = System.currentTimeMillis() - milis;
                //avg = avg + time;
                int i = atomic_i.incrementAndGet();
                System.out.printf("Current progress: %d of %d.\r", i, MARCH.getISA().getTestCases().getTestCaseList().size());
                boolean change = false;
                if (!pass) {
                    //System.out.println(testCase);
                    Pair<TestResult, TestResult> ctx = MARCH.extractCTX(id, testCase);
                    synchronized (MARCH.getISA().getContract()) {
                        MARCH.getISA().getContract().add(ctx.left());
                        MARCH.getISA().getContract().add(ctx.right());
                        if (DEBUG) {
                            change = MARCH.getISA().getContract().update(true);
                        }
                        //MARCH.getISA().getContract().updateContractWithPositiveExamples();
                        //MARCH.compile();
                        //System.out.println("New Contract: \n" + MARCH.getISA().getContract().toString());
                    }
                } else {
                    //System.out.println(testCase);
                    Pair<TestResult, TestResult> positive_ctx = MARCH.extractDifferences(id);
                    synchronized (MARCH.getISA().getContract()) {
                        MARCH.getISA().getContract().add(positive_ctx.left());
                        MARCH.getISA().getContract().add(positive_ctx.right());
                        if (DEBUG) {
                            change = MARCH.getISA().getContract().update(true);
                        }
                        //MARCH.getISA().getContract().updateContract();
                        //MARCH.compile();
                        //System.out.println("New Contract: \n" + MARCH.getISA().getContract().toString());
                    }
                }
                if (DEBUG) {
                    synchronized (changes) {
                        changes.put(i, change);
                    }
                }
            }
        }
    }
}

package contractgen;

import contractgen.riscv.ibex.IBEX;
import contractgen.riscv.isa.RISCV_TYPE;
import contractgen.riscv.isa.contract.RISCVCounterexample;
import contractgen.riscv.isa.contract.RISCVObservation;
import contractgen.riscv.isa.contract.RISCV_OBSERVATION_TYPE;
import contractgen.riscv.isa.tests.RISCVRandomTests;
import contractgen.riscv.isa.tests.RISCVSimpleTestCases;
import contractgen.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ContractGen {

    private final MARCH MARCH;
    private static final String BINARY_DIR = "/home/yosys/bachelor/ibex_compiled/ibex";

    public ContractGen(contractgen.MARCH march) {
        MARCH = march;
    }

    public void generateWithSMT() {

        for (TestCase testCase: MARCH.getISA().getTestCases().getTestCaseList()) {
            MARCH.generateSources(testCase, null);
            String coverTrace = MARCH.runCover(150);
            int steps = MARCH.extractSteps(coverTrace);
            boolean pass = MARCH.run(steps);
            if (!pass) {
                Pair<Counterexample, Counterexample> ctx = MARCH.extractCTX(testCase);
                MARCH.getISA().getContract().add(ctx.getLeft());
                MARCH.getISA().getContract().add(ctx.getRight());
                MARCH.getISA().getContract().updateContract();
            }
        }
        System.out.println(MARCH.getISA().getContract());

    }

    public void generateWithIVerilog() {
        // compile
        long milis = System.currentTimeMillis();
        MARCH.compile();
        System.out.println(System.currentTimeMillis() - milis);
        // copy binary and write test case
        int i = 0;
        long avg = 0;
        for (TestCase testCase: MARCH.getISA().getTestCases().getTestCaseList()) {
            MARCH.writeTestCase(testCase);
            milis = System.currentTimeMillis();
            boolean pass = MARCH.simulate();
            i++;
            long time = System.currentTimeMillis() - milis;
            avg = avg + time;
            System.out.printf("Current progress: %d of %d. Stats: last %d ms, avg %d ms\r", i, MARCH.getISA().getTestCases().getTestCaseList().size(), time, avg / i);
            if (!pass) {
                System.out.println(testCase);
                Pair<Counterexample, Counterexample> ctx = MARCH.extractCTX(testCase);
                MARCH.getISA().getContract().add(ctx.getLeft());
                MARCH.getISA().getContract().add(ctx.getRight());
                MARCH.getISA().getContract().updateContract();
                MARCH.compile();
                System.out.println("New Contract: \n" + MARCH.getISA().getContract().toString());
            }
        }
        System.out.println(MARCH.getISA().getContract());
    }

    public void generateInParallel(final int COUNT) {
        AtomicInteger atomic_i = new AtomicInteger(0);
        for (RISCV_TYPE type: RISCV_TYPE.values()) {
            MARCH.getISA().getContract().add(new RISCVCounterexample(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.OPCODE))));
        }
        MARCH.compile();
        List<Thread> runners = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            runners.add(new Thread(new Runner(MARCH, i + 1, COUNT, atomic_i), "Runner_" + (i + 1)));
        }
        runners.forEach(Thread::start);
        runners.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void main(String[] args) {
        //ContractGen contractGen = new ContractGen(new SimplePipelineMARCH(new PipelineTests()));
        //contractGen.generateWithSMT();
        ContractGen contractGen = new ContractGen((new IBEX(new RISCVRandomTests(12345678))));
        System.out.println("Evaluating " + contractGen.MARCH.getISA().getTestCases().getTestCaseList().size() + " test cases.");
        //contractGen.generateWithIVerilog();
        contractGen.generateInParallel(12);
    }

    public static class Runner implements Runnable {

        private final MARCH MARCH;
        private final int id;
        private final int COUNT;
        private final AtomicInteger atomic_i;

        Runner(MARCH MARCH, int id, int COUNT, AtomicInteger atomic_i) {
            this.MARCH = MARCH;
            this.id = id;
            this.COUNT = COUNT;
            this.atomic_i = atomic_i;
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
                //milis = System.currentTimeMillis();
                boolean pass = MARCH.simulate(id);
                //long time = System.currentTimeMillis() - milis;
                //avg = avg + time;
                System.out.printf("Current progress: %d of %d.\r", atomic_i.incrementAndGet(), MARCH.getISA().getTestCases().getTestCaseList().size());
                if (!pass) {
                    //System.out.println(testCase);
                    Pair<Counterexample, Counterexample> ctx = MARCH.extractCTX(id, testCase);
                    synchronized (MARCH.getISA().getContract()) {
                        MARCH.getISA().getContract().add(ctx.getLeft());
                        MARCH.getISA().getContract().add(ctx.getRight());
                        MARCH.getISA().getContract().updateContract();
                        //MARCH.compile();
                        //System.out.println("New Contract: \n" + MARCH.getISA().getContract().toString());
                    }
                }
            }
            System.out.println(MARCH.getISA().getContract());
        }
    }
}

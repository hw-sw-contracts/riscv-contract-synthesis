package contractgen.generator.iverilog;

import contractgen.*;
import contractgen.util.Pair;

/**
 * A Generator that uses Icarus verilog to simulate test cases.
 */
public class SimpleIVerilogGenerator extends Generator {

    /**
     * @param MARCH The microarchitecture to be used.
     */
    public SimpleIVerilogGenerator(contractgen.MARCH MARCH) {
        super(MARCH);
    }

    @Override
    public Contract generate() {
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
            SIMULATION_RESULT pass = MARCH.simulate();
            i++;
            long time = System.currentTimeMillis() - milis;
            avg = avg + time;
            System.out.printf("Current progress: %d of %d. Stats: last %d ms, avg %d ms\r", i, MARCH.getISA().getTestCases().getTestCaseList().size(), time, avg / i);
            if (pass == SIMULATION_RESULT.FAIL) {
                System.out.println(testCase);
                Pair<TestResult, TestResult> ctx = MARCH.extractCTX(testCase);
                MARCH.getISA().getContract().add(ctx.left());
                MARCH.getISA().getContract().add(ctx.right());
                MARCH.getISA().getContract().update(true);
                MARCH.compile();
                System.out.println("New Contract: \n" + MARCH.getISA().getContract().toString());
            }
        }
        System.out.println(MARCH.getISA().getContract());
        return MARCH.getISA().getContract();
    }
}

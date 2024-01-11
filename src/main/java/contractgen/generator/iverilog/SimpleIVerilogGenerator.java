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
        final long[] milis = {System.currentTimeMillis()};
        MARCH.compile();
        System.out.println(System.currentTimeMillis() - milis[0]);
        // copy binary and write test case
        final int[] i = {0};
        final long[] avg = {0};
        MARCH.getISA().getTestCases().getIterator(0).forEachRemaining(testCase ->  {
            MARCH.writeTestCase(testCase);
            milis[0] = System.currentTimeMillis();
            SIMULATION_RESULT pass = MARCH.simulate();
            i[0]++;
            long time = System.currentTimeMillis() - milis[0];
            avg[0] = avg[0] + time;
            System.out.printf("Current progress: %d of %d. Stats: last %d ms, avg %d ms\r", i[0], MARCH.getISA().getTestCases().getTotalNumber(), time, avg[0] / i[0]);
            if (pass == SIMULATION_RESULT.FAIL) {
                System.out.println(testCase);
                Pair<TestResult, TestResult> ctx = MARCH.extractCTX(testCase);
                MARCH.getISA().getContract().add(ctx.left());
                MARCH.getISA().getContract().add(ctx.right());
                MARCH.getISA().getContract().update(true);
                MARCH.compile();
                System.out.println("New Contract: \n" + MARCH.getISA().getContract().toString());
            }
        });
        System.out.println(MARCH.getISA().getContract());
        return MARCH.getISA().getContract();
    }
}

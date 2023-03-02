package contractgen.riscv.isa.tests;

import contractgen.TestCases;

public class RISCVRandomTests extends TestCases {

    public RISCVRandomTests(long seed, int repetitions) {
        super(new RISCVTestGenerator(seed, repetitions).generate());
    }
}

package contractgen.riscv.isa.tests;

import contractgen.TestCases;

public class RISCVRandomTests extends TestCases {

    public RISCVRandomTests(long seed) {
        super(new RISCVTestGenerator(seed, 100).generate());
    }
}

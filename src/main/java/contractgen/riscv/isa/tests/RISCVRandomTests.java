package contractgen.riscv.isa.tests;

import contractgen.TestCases;
import contractgen.riscv.isa.RISCV_SUBSET;

import java.util.Set;

public class RISCVRandomTests extends TestCases {

    public RISCVRandomTests(Set<RISCV_SUBSET> subsets, long seed, int repetitions) {
        super(new RISCVTestGenerator(subsets, seed, repetitions).generate());
    }
}

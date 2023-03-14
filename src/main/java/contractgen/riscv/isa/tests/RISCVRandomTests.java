package contractgen.riscv.isa.tests;

import contractgen.TestCases;
import contractgen.riscv.isa.RISCV_SUBSET;

import java.util.Set;

/**
 * Random set of test cases for RISC-V
 */
public class RISCVRandomTests extends TestCases {

    /**
     * @param subsets       The subsets of the RISC-V ISA that should be considered.
     * @param seed          A seed for the pseudo-random generation.
     * @param repetitions   The number of test cases per possible observation that should be generated.
     */
    public RISCVRandomTests(Set<RISCV_SUBSET> subsets, long seed, int repetitions) {
        super(new RISCVTestGenerator(subsets, seed, repetitions).generate());
    }
}

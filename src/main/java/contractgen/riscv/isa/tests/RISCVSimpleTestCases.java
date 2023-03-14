package contractgen.riscv.isa.tests;

import contractgen.TestCase;
import contractgen.TestCases;
import contractgen.riscv.isa.RISCVInstruction;
import contractgen.riscv.isa.RISCVProgram;
import contractgen.riscv.isa.RISCVTestCase;

import java.util.HashMap;
import java.util.List;

/**
 * A set of one test case that can be used for debugging purposes.
 */
public class RISCVSimpleTestCases extends TestCases {

    /**
     * Simple test cases for RISC-V.
     */
    public RISCVSimpleTestCases() {
        super(getSimpleTests());
    }

    private static List<TestCase> getSimpleTests() {
        RISCVProgram p1 = new RISCVProgram(new HashMap<>(), List.of(RISCVInstruction.ADDI(1, 0, 10), RISCVInstruction.ADD(2, 1, 1), RISCVInstruction.ADDI(3, 2, 10)));
        RISCVProgram p2 = new RISCVProgram(new HashMap<>(), List.of(RISCVInstruction.ADDI(1, 0, 10), RISCVInstruction.ADD(2, 1, 1), RISCVInstruction.ADDI(1, 1, 4)));
        RISCVTestCase tc1 = new RISCVTestCase(p1, p2, 3, 0);
        return List.of(tc1);
    }
}

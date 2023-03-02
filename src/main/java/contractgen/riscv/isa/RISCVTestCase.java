package contractgen.riscv.isa;

import contractgen.TestResult;
import contractgen.Program;
import contractgen.TestCase;

public class RISCVTestCase extends TestCase {

    public RISCVTestCase(Program program1, Program program2, int maxInstructionCount) {
        super(program1, program2, maxInstructionCount);
    }

    public RISCVTestCase(Program program1, Program program2, int maxInstructionCount, TestResult ctx) {
        super(program1, program2, maxInstructionCount, ctx);
    }
}

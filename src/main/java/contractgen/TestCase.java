package contractgen;

public abstract class TestCase {

    private final Program program1;
    private final Program program2;
    private final int maxInstructionCount;
    private final TestResult ctx;

    public TestCase(Program program1, Program program2, int maxInstructionCount) {
        this.program1 = program1;
        this.program2 = program2;
        this.maxInstructionCount = maxInstructionCount;
        this.ctx = null;
    }

    public TestCase(Program program1, Program program2, int maxInstructionCount, TestResult ctx) {
        this.program1 = program1;
        this.program2 = program2;
        this.maxInstructionCount = maxInstructionCount;
        this.ctx = ctx;
    }

    public Program getProgram1() {
        return program1;
    }

    public Program getProgram2() {
        return program2;
    }

    public int getMaxInstructionCount() {
        return maxInstructionCount;
    }

    @Override
    public String toString() {
        return "TestCase{" +
                "program1=" + program1 +
                ", program2=" + program2 +
                ", maxInstructionCount=" + maxInstructionCount +
                ", ctx=" + ctx +
                '}';
    }

    public TestResult getLikelyCTX() {
        return this.ctx;
    }
}

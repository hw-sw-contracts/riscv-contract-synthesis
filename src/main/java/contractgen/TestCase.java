package contractgen;

/**
 * An abstract test case.
 */
public abstract class TestCase {
    /**
     * The program to be evaluated on core 1.
     */
    private final Program program1;

    /**
     * The program to be evaluated on core 2.
     */
    private final Program program2;
    private final int maxInstructionCount;

    /**
     * A test result that would differentiate the two programs.
     */
    private final TestResult res;
    private final int index;

    /**
     * @param program1              The program to be evaluated on core 1.
     * @param program2              The program to be evaluated on core 2.
     * @param maxInstructionCount   The number of instructions included in the programs.
     * @param index                 The index used to identify the test case.
     */
    public TestCase(Program program1, Program program2, int maxInstructionCount, int index) {
        this.program1 = program1;
        this.program2 = program2;
        this.maxInstructionCount = maxInstructionCount;
        this.res = null;
        this.index = index;
    }

    /**
     * @param program1              The program to be evaluated on core 1.
     * @param program2              The program to be evaluated on core 2.
     * @param maxInstructionCount   The number of instructions included in the programs.
     * @param res                   A test result that would differentiate the two programs.
     * @param index                 The index used to identify the test case.
     */
    public TestCase(Program program1, Program program2, int maxInstructionCount, TestResult res, int index) {
        this.program1 = program1;
        this.program2 = program2;
        this.maxInstructionCount = maxInstructionCount;
        this.res = res;
        this.index = index;
    }

    /**
     * @return Program 1.
     */
    public Program getProgram1() {
        return program1;
    }

    /**
     * @return Program 2.
     */
    public Program getProgram2() {
        return program2;
    }

    /**
     * @return The maximal number of instructions in the programs.
     */
    public int getMaxInstructionCount() {
        return maxInstructionCount;
    }

    @Override
    public String toString() {
        return "TestCase{" +
                "program1=" + program1 +
                ", program2=" + program2 +
                ", maxInstructionCount=" + maxInstructionCount +
                ", ctx=" + res +
                '}';
    }

    /**
     * @return A possible test result.
     */
    public TestResult getLikelyCTX() {
        return this.res;
    }

    /**
     * @return The index of this test case.
     */
    public int getIndex() {
        return index;
    }
}

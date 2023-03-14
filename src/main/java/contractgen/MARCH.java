package contractgen;

import contractgen.util.Pair;

/**
 * Abstract microarchitecture.
 */
public abstract class MARCH {

    /**
     * The corresponding instruction set architecture.
     */
    private final ISA ISA;

    protected MARCH(ISA isa) {
        ISA = isa;
    }

    /**
     * @return The instruction set architecture.
     */
    public ISA getISA() {
        return ISA;
    }


    /**
     * Prepares the sources for the bounded model check.
     *
     * @param testCase  The test case to be included in the sources.
     * @param max_count Maximal number of instructions to be evaluated.
     */
    public abstract void generateSources(TestCase testCase, Integer max_count);

    /**
     * Used in preparation for the bounded model check to check how many steps will be required.
     *
     * @param steps Maximal number of steps in the bounded model check.
     * @return      The output of the bounded model check.
     */
    public abstract String runCover(int steps);

    /**
     * @param coverTrace The output from the bounded model check.
     * @return           The minimal number of steps required to complete the program.
     */
    public abstract int extractSteps(String coverTrace);

    /**
     * Runs the bounded model check.
     *
     * @param steps Number of steps to be checked.
     * @return      Whether the formal properties held during execution.
     */
    public abstract boolean run(int steps);

    /**
     * Extracts the results from a failing test case.
     *
     * @param testCase The test case that failed for further reference.
     * @return         A pair of test results, one for each core, including observations that would allow to distinguish the executions.
     */
    public abstract Pair<TestResult, TestResult> extractCTX(TestCase testCase);

    /**
     * Extracts the results from a failing test case.
     *
     * @param id        The id of the current thread to avoid filesystem conflicts.
     * @param testCase The test case that failed for further reference.
     * @return         A pair of test results, one for each core, including observations that would allow to distinguish the executions.
     */
    public abstract Pair<TestResult, TestResult> extractCTX(int id, TestCase testCase);

    /**
     * Extracts the differences from the VCD file on disk.
     *
     * @param index    Index of the test case to allow to associate a test case with this result.
     * @return         A pair of test results, one for each core, including observations that would allow to distinguish the executions.
     */
    public abstract Pair<TestResult, TestResult> extractDifferences(int index);

    /**
     * Extracts the differences from the VCD file on disk.
     *
     * @param id        The id of the current thread to avoid filesystem conflicts.
     * @param index    Index of the test case to allow to associate a test case with this result.
     * @return         A pair of test results, one for each core, including observations that would allow to distinguish the executions.
     */
    public abstract Pair<TestResult, TestResult> extractDifferences(int id, int index);

    /**
     * Compiles the microarchitecture for simulation.
     */
    public abstract void compile();

    /**
     * Writes a given test case on disk and prepares its simulation.
     *
     * @param testCase  The test case to be written on disk.
     */
    public abstract void writeTestCase(TestCase testCase);

    /**
     * Writes a given test case on disk and prepares its simulation.
     *
     * @param id        The id of the current thread to avoid filesystem conflicts.
     * @param testCase  The test case to be written on disk.
     */
    public abstract void writeTestCase(int id, TestCase testCase);

    /**
     * Starts the simulation of the previously written test case.
     *
     * @return The result of the simulation.
     */
    public abstract SIMULATION_RESULT simulate();

    /**
     * Starts the simulation of the previously written test case.
     *
     * @param id        The id of the current thread to avoid filesystem conflicts.
     * @return          The result of the simulation.
     */
    public abstract SIMULATION_RESULT simulate(int id);

    /**
     * @return The name of the microarchitecture.
     */
    public abstract String getName();
}

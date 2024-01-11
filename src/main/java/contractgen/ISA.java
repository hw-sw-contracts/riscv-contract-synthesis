package contractgen;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Abstract instruction set architecture.
 */
public abstract class ISA {
    /**
     * The set of test cases to be used for generation or evaluation.
     */
    private final TestCases testCases;

    /**
     * @param testCases The set of testcases.
     */
    protected ISA(TestCases testCases) {
        this.testCases = testCases;
    }

    /**
     * @return The set of test cases to be used for generation or evaluation.
     */
    public TestCases getTestCases() {
        return testCases;
    }

    /**
     * @return The current contract.
     */
    public abstract Contract getContract();

    /**
     * Loads a deserialized contract from a file.
     *
     * @param path The path of the file containing the serialized contract candidate.
     * @throws IOException On filesystem errors.
     */
    public abstract void loadContract(Path path) throws IOException;
}

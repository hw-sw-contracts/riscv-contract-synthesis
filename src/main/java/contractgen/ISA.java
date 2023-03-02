package contractgen;

import java.io.IOException;
import java.nio.file.Path;

public abstract class ISA {

    private final TestCases testCases;

    protected ISA(TestCases testCases) {
        this.testCases = testCases;
    }

    public TestCases getTestCases() {
        return testCases;
    }

    public abstract Contract getContract();

    public abstract void loadContract(Path path) throws IOException;
}

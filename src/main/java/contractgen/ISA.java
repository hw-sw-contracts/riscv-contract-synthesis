package contractgen;

import java.util.stream.IntStream;

public abstract class ISA {

    private final TestCases testCases;

    protected ISA(TestCases testCases) {
        this.testCases = testCases;
    }

    public TestCases getTestCases() {
        return testCases;
    }

    public abstract Contract getContract();
}

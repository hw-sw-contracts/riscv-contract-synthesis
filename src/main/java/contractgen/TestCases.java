package contractgen;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class TestCases {

    private final List<TestCase> testCaseList;

    protected TestCases(List<TestCase> testCaseList) {
        this.testCaseList = testCaseList;
    }

    public List<TestCase> getTestCaseList() {
        return testCaseList;
    }

    public List<TestCase> getChunk(int id, int COUNT) {
        return IntStream.range(0, testCaseList.size()).filter(n -> n % COUNT == (id - 1)).mapToObj(testCaseList::get).collect(Collectors.toList());
    }
}

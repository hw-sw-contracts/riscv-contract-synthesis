package contractgen;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A set of test cases.
 */
public abstract class TestCases {

    private final List<TestCase> testCaseList;

    protected TestCases(List<TestCase> testCaseList) {
        this.testCaseList = testCaseList;
    }

    /**
     * @return A list containing all test cases.
     */
    public List<TestCase> getTestCaseList() {
        return testCaseList;
    }

    /**
     * Used to distribute test cases between threads.
     *
     * @param id    The id of the thread.
     * @param COUNT The total number of threads.
     * @return      The chunk for the given thread.
     */
    public List<TestCase> getChunk(int id, int COUNT) {
        return IntStream.range(0, testCaseList.size()).filter(n -> n % COUNT == (id - 1)).mapToObj(testCaseList::get).collect(Collectors.toList());
    }
}

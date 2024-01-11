package contractgen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A set of test cases.
 */
public abstract class TestCases {

    /**
     * A list of iterators generating new testcases.
     */
    private final List<Iterator<TestCase>> iterators;
    /**
     * The total count of testcases.
     */
    private final int count;
    /**
     * A list of all test cases, if available.
     */
    private final List<TestCase> testCaseList;

    /**
     * Constructs a set of test cases based on a list. It creates one iterator.
     *
     * @param testCaseList The list containing the test cases.
     */
    protected TestCases(List<TestCase> testCaseList) {
        this.iterators = List.of(testCaseList.listIterator());
        this.count = testCaseList.size();
        this.testCaseList = testCaseList;
    }


    /**
     * Constructs a set of test cases based on interators.
     *
     * @param iterators A list of several iterators.
     * @param count     The total number of test cases.
     */
    protected TestCases(List<Iterator<TestCase>> iterators, int count) {
        this.count = count;
        this.iterators = iterators;
        this.testCaseList = null;
    }

    /**
     * @param id The id of the iterator to get.
     * @return The selected iterator.
     */
    public Iterator<TestCase> getIterator(int id) {
        return iterators.get(id);
    }

    /**
     * @return The total number of test cases
     */
    public int getTotalNumber() {
        return count;
    }

    /**
     * Obtain a´test case from its id. This only works if the test cases were provided as list and not as iterators.
     *
     * @param idx The index.
     * @return The test case for the specified index or null if there are only iterators available.
     */
    public TestCase getIndex(int idx) {
        if (testCaseList == null) {
            return null;
        } else {
            return testCaseList.get(idx);
        }
    }
}

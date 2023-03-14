package contractgen;

import java.util.List;
import java.util.Set;

/**
 * Interface of an updater for the contract.
 */
public interface Updater {

    /**
     * @param testResults The test results to be considered in the computation.
     * @return            The computed contract.
     */
    Set<Observation> update(List<TestResult> testResults);
}

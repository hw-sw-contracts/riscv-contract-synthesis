package contractgen;

import java.util.List;
import java.util.Set;

/**
 * Interface of an updater for the contract.
 */
public interface Updater {

    /**
     * @param testResults The test results to be considered in the computation.
     * @param oldContract The old contract to be used as hint.
     * @return The computed contract.
     */
    Set<Observation> update(List<TestResult> testResults, Set<Observation> oldContract);
}

package contractgen;

import contractgen.util.Pair;

/**
 * Extracts test results from a simulation.
 */
public interface Extractor {

    /**
     * @param PATH                     The simulation path.
     * @param adversaryDistinguishable Whether the adversary was able to distinguish the executions.
     * @param index                    The index of the current testcase
     * @return The extracted test results.
     */
    Pair<TestResult, TestResult> extractResults(String PATH, boolean adversaryDistinguishable, int index);
}

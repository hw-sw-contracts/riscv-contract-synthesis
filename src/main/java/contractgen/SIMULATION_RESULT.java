package contractgen;

/**
 * The result of a simulation.
 */
public enum SIMULATION_RESULT {
    /**
     * The included contract covered any leakage in this simulation.
     */
    SUCCESS,
    /**
     * There was leakage that could not be explained by the included contract.
     */
    FAIL,
    /**
     * The contract reported the executions as distinguishable, however, the adversary was not able to observe any difference.
     */
    FALSE_POSITIVE,
    /**
     * The simulation did not complete in time.
     */
    TIMEOUT,
    /**
     * An unknown error occurred.
     */
    UNKNOWN
}

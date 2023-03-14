package contractgen;

import java.util.Collection;
import java.util.Set;

/**
 * The result of the evaluation of a test case.
 */
public abstract class TestResult {

    protected final Set<Observation> observations;
    private final boolean adversaryDistinguishable;
    private final int index;

    /**
     * @param observations              The observations that would make the executions distinguishable.
     * @param adversaryDistinguishable  Whether the adversary was able to distinguish the executions.
     * @param index                     The index of the relevant test case for further reference.
     */
    public TestResult(Set<Observation> observations, boolean adversaryDistinguishable, int index) {
        this.observations = observations;
        this.adversaryDistinguishable = adversaryDistinguishable;
        this.index = index;
    }

    /**
     * @return The observations that would make the executions distinguishable.
     */
    public abstract Collection<Observation> getPossibleObservations();

    /**
     * @return Whether the adversary was able to distinguish the executions.
     */
    public boolean isAdversaryDistinguishable() {
        return adversaryDistinguishable;
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    /**
     * @return The observation with the lowest value that would allow to distinguish the two executions.
     */
    public abstract Observation getBestObservation();

    /**
     * @return The index of the relevant test case.
     */
    public int getIndex() {
        return index;
    }
}

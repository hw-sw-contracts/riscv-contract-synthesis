package contractgen;

import java.util.Collection;
import java.util.Set;

public abstract class TestResult {

    protected final Set<Observation> observations;
    private final boolean adversaryDistinguishable;

    public TestResult(Set<Observation> observations, boolean adversaryDistinguishable) {
        this.observations = observations;
        this.adversaryDistinguishable = adversaryDistinguishable;
    }

    public abstract Collection<Observation> getPossibleObservations();

    public boolean isAdversaryDistinguishable() {
        return adversaryDistinguishable;
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    public abstract Observation getBestObservation();
}

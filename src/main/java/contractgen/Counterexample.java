package contractgen;

import java.util.Collection;
import java.util.Set;

public abstract class Counterexample {
    protected final Set<Observation> possibilities;

    public Counterexample(Set<Observation> possibilities) {
        this.possibilities = possibilities;
    }

    public abstract Collection<Observation> getPossibleObservations();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    public abstract Observation getBestObservation();
}

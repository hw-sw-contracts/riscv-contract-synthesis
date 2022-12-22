package contractgen.simple.isa.contract;

import contractgen.Counterexample;
import contractgen.Observation;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleCounterexample extends Counterexample {

    public SimpleCounterexample(Set<SimpleObservation> possibilities) {
        super(possibilities.stream().map(o -> (Observation) o).collect(Collectors.toSet()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleCounterexample that = (SimpleCounterexample) o;
        return Objects.equals(possibilities, that.possibilities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(possibilities);
    }

    @Override
    public Observation getBestObservation() {
        return possibilities.stream().min(Comparator.comparingInt(Observation::getValue)).orElseThrow();
    }

    @Override
    public Set<Observation> getPossibleObservations() {
        return possibilities;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\t Counterexample: \n");
        possibilities.forEach(obs -> sb.append(obs.toString()).append("\n"));
        return sb.toString();
    }
}

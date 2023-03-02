package contractgen.simple.isa.contract;

import contractgen.TestResult;
import contractgen.Observation;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleTestResult extends TestResult {

    public SimpleTestResult(Set<SimpleObservation> possibilities, boolean adversaryDistinguishable) {
        super(possibilities.stream().map(o -> (Observation) o).collect(Collectors.toSet()), adversaryDistinguishable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleTestResult that = (SimpleTestResult) o;
        return Objects.equals(observations, that.observations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(observations);
    }

    @Override
    public Observation getBestObservation() {
        return observations.stream().min(Comparator.comparingInt(Observation::getValue)).orElseThrow();
    }

    @Override
    public Set<Observation> getPossibleObservations() {
        return observations;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\t Counterexample: \n");
        observations.forEach(obs -> sb.append(obs.toString()).append("\n"));
        return sb.toString();
    }
}

package contractgen.riscv.isa.contract;

import contractgen.TestResult;
import contractgen.Observation;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RISCVTestResult extends TestResult {

    public RISCVTestResult(Set<RISCVObservation> possibilities, boolean adversaryDistinguishable) {
        super(possibilities.stream().map(o -> (Observation) o).collect(Collectors.toSet()), adversaryDistinguishable);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RISCVTestResult that = (RISCVTestResult) o;
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
        StringBuilder sb = new StringBuilder("\t Observations: \n");
        observations.forEach(obs -> sb.append(obs.toString()).append("\n"));
        sb.append("\t\t").append(isAdversaryDistinguishable());
        return sb.toString();
    }
}

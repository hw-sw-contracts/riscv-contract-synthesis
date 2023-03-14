package contractgen.simple.isa.contract;

import contractgen.TestResult;
import contractgen.Observation;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A test result from evaluating a test case in the simple ISA.
 */
public class SimpleTestResult extends TestResult {

    /**
     * @param possibilities             The observations that would distinguish the executions in this test case.
     * @param adversaryDistinguishable  Whether the adversary was able to distinguish the two executions.
     * @param index                     The index of the test case for further reference.
     */
    public SimpleTestResult(Set<SimpleObservation> possibilities, boolean adversaryDistinguishable, int index) {
        super(possibilities.stream().map(o -> (Observation) o).collect(Collectors.toSet()), adversaryDistinguishable, index);
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

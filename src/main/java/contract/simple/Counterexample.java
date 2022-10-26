package contract.simple;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

public class Counterexample {

    final private Set<Observation> possibilities;

    public Counterexample(Set<Observation> possibilities) {
        this.possibilities = possibilities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Counterexample that = (Counterexample) o;
        return Objects.equals(possibilities, that.possibilities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(possibilities);
    }

    public Observation getBest() {
        return possibilities.stream().min(Comparator.comparingInt(Observation::getValue)).orElseThrow();
    }

    public Set<Observation> getPossibilities() {
        return possibilities;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\t Counterexample: \n");
        possibilities.forEach(obs -> sb.append(obs.toString()).append("\n"));
        return sb.toString();
    }
}

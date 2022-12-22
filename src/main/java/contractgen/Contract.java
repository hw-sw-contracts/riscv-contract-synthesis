package contractgen;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Contract {

    private Set<Counterexample> counterexamples = new HashSet<>();
    private Set<Observation> current_contract = new HashSet<>();

    public void updateContract() {
            if (coversAll())
                return;
            current_contract = new HashSet<>();
            Set<Counterexample> remaining = counterexamples.stream().filter(ctx -> !covers(ctx)).collect(Collectors.toSet());
            while (!remaining.isEmpty()) {
                current_contract.add(remaining.stream().findFirst().get().getBestObservation());
                remaining = remaining.stream().filter(ctx -> !covers(ctx)).collect(Collectors.toSet());
            }
    }

    public void updateContractPremium() {
        Set<Observation> all = new HashSet<>();
        counterexamples.forEach(ctx -> all.addAll(ctx.getPossibleObservations()));
        current_contract = setcover(this::coversAll, Comparator.comparingInt(a -> a.stream().mapToInt(Observation::getValue).sum()), all);
    }

    public boolean coversAll() {
        return coversAll(current_contract);
    }

    public boolean coversAll(Set<Observation> set) {
        return counterexamples.stream().filter(ctx -> covers(set, ctx)).collect(Collectors.toSet()).containsAll(counterexamples);
    }

    public boolean covers(Counterexample ctx) {
        return covers(current_contract, ctx);
    }

    public boolean covers(Set<Observation> set, Counterexample ctx) {
        return set.stream().anyMatch(obs -> ctx.getPossibleObservations().contains(obs));
    }

    public void add(Counterexample ctx) {
        counterexamples.add(ctx);
    }

    public abstract String printContract();

    public Set<Counterexample> getCounterexamples() {
        return counterexamples;
    }

    public Set<Observation> getCurrentContract() {
        return current_contract;
    }

    private static <T> Set<T> setcover(Predicate<Set<T> > predicate, Comparator<Set<T>> comparator, Set<T> set)
    {
        Set<Set<T>> possibilities = new HashSet<>();
        for (T t: set) {
            Set<Set<T>> new_possibilities = possibilities.stream().map(HashSet::new).collect(Collectors.toSet());
            new_possibilities.forEach(s -> s.add(t));
            new_possibilities.add(Set.of(t));
            possibilities.addAll(new_possibilities);
        }
        return possibilities.stream().sorted(comparator).filter(predicate).findFirst().orElse(null);
    }

    @Override
    public abstract String toString();

}

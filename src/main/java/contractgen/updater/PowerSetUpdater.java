package contractgen.updater;

import contractgen.Contract;
import contractgen.Observation;
import contractgen.TestResult;
import contractgen.Updater;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Computes a contract building the powerset of all possible contracts and afterwards selecting the cheapest one.
 */
public class PowerSetUpdater implements Updater {

    @Override
    public Set<Observation> update(List<TestResult> testResults, Set<Observation> ignored) {
        Set<Observation> all = new HashSet<>();
        testResults.forEach(ctx -> all.addAll(ctx.getPossibleObservations()));
        return setCover(set -> Contract.coversAll(testResults, set), Comparator.comparingInt(a -> a.stream().mapToInt(Observation::getValue).sum()), all);
    }

    /**
     * @param set A set.
     * @param <T> The type of each element.
     * @return The powerset of the given set.
     */
    private static <T> Set<Set<T>> enumeratePossibilities(Set<T> set) {
        Set<Set<T>> possibilities = new HashSet<>();
        for (T t : set) {
            Set<Set<T>> new_possibilities = possibilities.stream().map(HashSet::new).collect(Collectors.toSet());
            new_possibilities.forEach(s -> s.add(t));
            new_possibilities.add(Set.of(t));
            possibilities.addAll(new_possibilities);
        }
        return possibilities;
    }

    /**
     * @param predicate  A predicate the set needs to satisfy.
     * @param comparator A comparator to compare multiple sets.
     * @param set        The set of possibilities.
     * @param <T>        The type of each element.
     * @return The cheapest subset that satiisfies the predicate.
     */
    private static <T> Set<T> setCover(Predicate<Set<T>> predicate, Comparator<Set<T>> comparator, Set<T> set) {
        Set<Set<T>> possibilities = enumeratePossibilities(set);
        return possibilities.stream().sorted(comparator).filter(predicate).findFirst().orElse(null);
    }
}

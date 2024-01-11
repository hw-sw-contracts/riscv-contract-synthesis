package contractgen.updater;

import contractgen.Contract;
import contractgen.Observation;
import contractgen.TestResult;
import contractgen.Updater;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Iteratively computes the contract and afterwards removing observations leading to many false positives
 * if the contract is still valid.
 */
public class TwoWayUpdater implements Updater {


    @Override
    public Set<Observation> update(List<TestResult> testResults, Set<Observation> ignored) {
        Set<Observation> new_contract;
        Set<Observation> negative = new HashSet<>();
        testResults.stream().filter(TestResult::isAdversaryDistinguishable).forEach(ctx -> negative.addAll(ctx.getPossibleObservations()));
        List<Observation> positive = new ArrayList<>();
        testResults.stream().filter(TestResult::isAdversaryDistinguishable).forEach(pe -> positive.addAll(pe.getPossibleObservations()));
        Map<Observation, Long> values = toMap(positive);
        List<Observation> sorted_obs = negative.stream().sorted(Comparator.comparingInt(a -> Math.toIntExact(values.getOrDefault(a, 0L)))).toList();
        new_contract = new HashSet<>();
        int i = 0;
        while (!Contract.coversAll(testResults, new_contract)) {
            new_contract.add(sorted_obs.get(i));
            i++;
        }
        List<Observation> ctr = new_contract.stream().sorted(Comparator.comparingInt(Observation::getValue)).toList();
        for (int j = ctr.size() - 1;  j >= 0; j--) {
            new_contract.remove(ctr.get(j));
            if (!Contract.coversAll(testResults, new_contract)) {
                new_contract.add(ctr.get(j));
            }
        }
        return new_contract;
    }

    /**
     * Converts a list to a map counting the occurrences of each element.
     *
     * @param lst The list to be converted.
     * @param <T> The type of the elements.
     * @return    The map.
     */
    public static <T> Map<T, Long> toMap(List<T> lst) {
        return lst.stream().collect(Collectors.groupingBy(s -> s,
                Collectors.counting()));
    }
}

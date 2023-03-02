package contractgen.updater;

import contractgen.Contract;
import contractgen.Observation;
import contractgen.TestResult;
import contractgen.Updater;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IterativeUpdater implements Updater {
    @Override
    public Set<Observation> update(List<TestResult> testResults) {
        Set<Observation> new_contract = new HashSet<>();
        Set<TestResult> remaining = testResults.stream().filter(ctx -> !Contract.covers(new_contract, ctx)).collect(Collectors.toSet());
        while (!remaining.isEmpty()) {
            new_contract.add(remaining.stream().findFirst().get().getBestObservation());
            remaining = remaining.stream().filter(ctx -> !Contract.covers(new_contract, ctx)).collect(Collectors.toSet());
        }
        return new_contract;
    }
}

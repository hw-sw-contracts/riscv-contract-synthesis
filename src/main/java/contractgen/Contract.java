package contractgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Contract {

    private final List<TestResult> testResults = new ArrayList<>();
    private Set<Observation> current_contract = new HashSet<>();

    private final Updater updater;

    protected Contract(Updater updater) {
        this.updater = updater;
    }

    public boolean update(boolean force) {
        if (!force && coversAll()) return false;
        Set<Observation> old = current_contract;
        current_contract = updater.update(testResults);
        return !Objects.equals(old, current_contract);
    }

    public boolean coversAll() {
        return coversAll(testResults, current_contract);
    }

    public boolean coversAll(Set<Observation> set) {
        return coversAll(testResults, set);
    }

    public static boolean coversAll(List<TestResult> testResults, Set<Observation> set) {
        return testResults.stream().filter(TestResult::isAdversaryDistinguishable).filter(ctx -> covers(set, ctx)).collect(Collectors.toSet()).containsAll(testResults);
    }

    public boolean covers(TestResult ctx) {
        return covers(current_contract, ctx);
    }

    public static boolean covers(Set<Observation> set, TestResult ctx) {
        return set.stream().anyMatch(obs -> ctx.getPossibleObservations().contains(obs));
    }

    public void add(TestResult ctx) {
        testResults.add(ctx);
    }

    public abstract String printContract();

    public List<TestResult> getTestResults() {
        return testResults;
    }

    public Set<Observation> getCurrentContract() {
        return current_contract;
    }

    @Override
    public abstract String toString();

    public String toJSON() {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return Objects.equals(testResults, contract.testResults) && Objects.equals(updater, contract.updater);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testResults, updater);
    }
}

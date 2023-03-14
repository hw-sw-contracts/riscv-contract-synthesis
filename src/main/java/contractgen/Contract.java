package contractgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract contract.
 */
public abstract class Contract {

    private final List<TestResult> testResults = new ArrayList<>();
    private Set<Observation> current_contract = new HashSet<>();


    /**
     * The updater that is used to compute the contract from the collected test results.
     */
    private final Updater updater;

    protected Contract(Updater updater) {
        this.updater = updater;
    }

    /**
     * Computes a contract candidate that covers all collected test results.
     *
     * @param force Whether the contract should be updated even if the current contract covers all test results.
     * @return      Whether the contract has changed.
     */
    public boolean update(boolean force) {
        if (!force && coversAll()) return false;
        Set<Observation> old = current_contract;
        current_contract = updater.update(testResults);
        return !Objects.equals(old, current_contract);
    }

    /**
     * @return Whether the current computed contract covers all collected test results.
     */
    public boolean coversAll() {
        return coversAll(testResults, current_contract);
    }

    /**
     * @param contract The contract to be used.
     * @return         Whether the given contract covers all collected test results.
     */
    public boolean coversAll(Set<Observation> contract) {
        return coversAll(testResults, contract);
    }

    /**
     * @param testResults The test results to be used.
     * @param contract    The contract to be used.
     * @return            Whether the given contract covers the given test results.
     */
    public static boolean coversAll(List<TestResult> testResults, Set<Observation> contract) {
        return testResults.stream().filter(TestResult::isAdversaryDistinguishable).filter(ctx -> covers(contract, ctx)).collect(Collectors.toSet()).containsAll(testResults);
    }

    /**
     * @param res The test result to be checked.
     * @return    Whether the current computed contract covers the given result.
     */
    public boolean covers(TestResult res) {
        return covers(current_contract, res);
    }

    /**
     * @param contract The contract to be used.
     * @param res      The test result to be checked.
     * @return         Whether the given contract covers the given result.
     */
    public static boolean covers(Set<Observation> contract, TestResult res) {
        return contract.stream().anyMatch(obs -> res.getPossibleObservations().contains(obs));
    }

    /**
     * Adds a new test result to the contract.
     *
     * @param res The test result to be added.
     */
    public void add(TestResult res) {
        testResults.add(res);
    }

    /**
     * Prints the contract in a format to be used in Verilog modules.
     *
     * @return The current contract.
     */
    public abstract String printContract();

    /**
     * @return The number of elements in the current computed contract.
     */
    public int getSize() {
        return current_contract.size();
    }

    /**
     * Sorts the test results by their respective index.
     * Can be used to obtain reproducible results after generating the contract with multiple threads.
     */
    public void sort() {
        this.testResults.sort(Comparator.comparingInt(TestResult::getIndex));
    }

    /**
     * @return Currently collected test results.
     */
    public List<TestResult> getTestResults() {
        return testResults;
    }

    /**
     * @return The current computed contract.
     */
    public Set<Observation> getCurrentContract() {
        return current_contract;
    }

    @Override
    public abstract String toString();

    /**
     * @return A string with the json representation of the instance.
     */
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

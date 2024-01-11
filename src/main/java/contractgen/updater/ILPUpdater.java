package contractgen.updater;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import contractgen.Observation;
import contractgen.TestResult;
import contractgen.Updater;
import contractgen.util.Pair;

import java.util.*;

/**
 * Updates the contract using integer linear programming.
 */
public class ILPUpdater implements Updater {
    @Override
    public Set<Observation> update(List<TestResult> testResults, Set<Observation> oldContract) {

        Map<MPVariable, Double> hint = new HashMap<>();
        boolean oldMethod = true;
        // Select between one variable per indistinguishable test case (oldMethod) and one variable per observation (that leads to n false positives) to optimize memory usage
        List<TestResult> positive =
                testResults.stream().filter(res -> !res.getPossibleObservations().isEmpty()).filter(TestResult::isAdversaryIndistinguishable).toList();
        List<TestResult> negative =
                testResults.stream().filter(res -> !res.getPossibleObservations().isEmpty()).filter(TestResult::isAdversaryDistinguishable).toList();

        Loader.loadNativeLibraries();
        MPSolver solver = MPSolver.createSolver("CP_SAT");

        Set<Observation> allObservations = new HashSet<>();
        testResults.forEach(res -> allObservations.addAll(res.getPossibleObservations()));

        // variables representing whether obs is chosen or not
        HashMap<Object, Pair<MPVariable, Integer>> positive_covered;
        HashMap<Observation, MPVariable> selected_observations = new HashMap<>(allObservations.size());
        for (Observation obs : allObservations) {
            MPVariable var = solver.makeIntVar(0, 1, obs.toString());
            selected_observations.put(obs, var);
            hint.put(var, oldContract.contains(obs) ? 1.0 : 0.0);
        }
        if (oldMethod) {
            positive_covered = new HashMap<>(positive.size());
            for (TestResult pe : positive) {
                Pair<MPVariable, Integer> var = positive_covered.getOrDefault(pe, new Pair<>(solver.makeIntVar(0, 1, pe.toString()), 0));
                positive_covered.put(pe, new Pair<>(var.left(), var.right() + 1));
                hint.put(var.left(), oldContract.stream().anyMatch(o -> pe.getPossibleObservations().contains(o)) ? 1.0 : 0.0);
            }
        } else {
            // count the number of times each observation occurs in the positive test cases
            HashMap<Observation, Integer> occurrences = new HashMap<>(allObservations.size());
            for (Observation obs : allObservations) {
                occurrences.put(obs, 0);
            }
            for (TestResult pe : positive) {
                for (Observation obs : pe.getPossibleObservations()) {
                    occurrences.put(obs, occurrences.get(obs) + 1);
                }
            }
            positive_covered = new HashMap<>(positive.size());
            for (Observation obs : allObservations) {
                positive_covered.put(obs, new Pair<>(solver.makeIntVar(0, 1, obs.toString()), occurrences.get(obs)));
            }
        }

        // Minimize number of tests without violation covered
        MPObjective objective = solver.objective();
        for (Pair<MPVariable, Integer> var : positive_covered.values()) {
            objective.setCoefficient(var.left(), var.right());
        }
        objective.setMinimization();

        if (oldMethod) {
            for (TestResult pe : positive) {
                for (Observation obs : pe.getPossibleObservations()) {
                    MPConstraint constraint = solver.makeConstraint(0.0, MPSolver.infinity());
                    constraint.setCoefficient(positive_covered.get(pe).left(), 1);
                    constraint.setCoefficient(selected_observations.get(obs), -1);
                }
            }
        } else {
            // if the observation is selected -> do = 1, then ca = 1
            for (Observation obs : allObservations) {
                MPConstraint constraint = solver.makeConstraint(0.0, MPSolver.infinity());
                constraint.setCoefficient(positive_covered.get(obs).left(), 1);
                constraint.setCoefficient(selected_observations.get(obs), -1);
            }
        }

        // for every negative test case, at least one observation must be chosen
        for (TestResult ctx : negative) {
            MPConstraint constraint = solver.makeConstraint(1.0, MPSolver.infinity());
            for (Observation obs : ctx.getPossibleObservations()) {
                constraint.setCoefficient(selected_observations.get(obs), 1);
            }
        }

        List<Map.Entry<MPVariable, Double>> entries = hint.entrySet().stream().toList();

        MPVariable[] hint_var = new MPVariable[hint.size()];
        double[] hint_val;
        entries.stream().map(Map.Entry::getKey).toList().toArray(hint_var);
        hint_val = entries.stream().map(Map.Entry::getValue).mapToDouble(Double::doubleValue).toArray();
        solver.setHint(hint_var, hint_val);
        solver.solve();

        hint.clear();
        for (MPVariable var : hint_var) {
            hint.put(var, var.solutionValue());
        }

        double goal = objective.value();
        objective.clear();

        MPConstraint primaryGoal = solver.makeConstraint(goal, goal);
        for (Pair<MPVariable, Integer> var : positive_covered.values()) {
            primaryGoal.setCoefficient(var.left(), var.right());
        }

        // Minimize overall size of the contract
        for (MPVariable var : selected_observations.values()) {
            objective.setCoefficient(var, 1);
        }
        objective.setMinimization();

        entries = hint.entrySet().stream().toList();
        entries.stream().map(Map.Entry::getKey).toList().toArray(hint_var);
        hint_val = entries.stream().map(Map.Entry::getValue).mapToDouble(Double::doubleValue).toArray();
        solver.setHint(hint_var, hint_val);
        solver.solve();

        Set<Observation> new_contract = new HashSet<>();
        for (Map.Entry<Observation, MPVariable> entry : selected_observations.entrySet()) {
            if (entry.getValue().solutionValue() > 0.0) {
                new_contract.add(entry.getKey());
            }
        }
        return new_contract;
    }
}

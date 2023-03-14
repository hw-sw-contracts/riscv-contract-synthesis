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
    public Set<Observation> update(List<TestResult> testResults) {
        List<TestResult> positive = testResults.stream().filter(res -> !res.getPossibleObservations().isEmpty()).filter(res -> !res.isAdversaryDistinguishable()).toList();
        @SuppressWarnings("Convert2MethodRef")
        List<TestResult> negative = testResults.stream().filter(res -> !res.getPossibleObservations().isEmpty()).filter(res -> res.isAdversaryDistinguishable()).toList();

        Loader.loadNativeLibraries();
        MPSolver solver = MPSolver.createSolver("CP_SAT");

        Set<Observation> allObservations = new HashSet<>();
        testResults.forEach(ctx -> allObservations.addAll(ctx.getPossibleObservations()));

        HashMap<Observation, MPVariable> do_mapping = new HashMap<>(allObservations.size());
        for (Observation obs: allObservations) {
            do_mapping.put(obs, solver.makeIntVar(0, 1, obs.toString()));
        }

        HashMap<TestResult, Pair<MPVariable, Integer>> ca_mapping = new HashMap<>(positive.size());
        for (TestResult pe: positive) {
            ca_mapping.put(pe, new Pair<>(solver.makeIntVar(0, 1, pe.toString()), ca_mapping.getOrDefault(pe, new Pair<>(null, 0)).right() + 1));
        }

        // Minimize number of tests without violation covered
        MPObjective objective = solver.objective();
        for (Pair<MPVariable, Integer> var: ca_mapping.values()) {
            objective.setCoefficient(var.left(), var.right());
        }
        objective.setMinimization();

        for (TestResult pe: positive) {
            for (Observation obs: pe.getPossibleObservations()) {
                MPConstraint constraint = solver.makeConstraint(0.0, MPSolver.infinity());
                constraint.setCoefficient(ca_mapping.get(pe).left(), 1);
                constraint.setCoefficient(do_mapping.get(obs), -1);
            }
        }

        for (TestResult ctx: negative) {
            MPConstraint constraint = solver.makeConstraint(1.0, MPSolver.infinity());
            for (Observation obs: ctx.getPossibleObservations()) {
                constraint.setCoefficient(do_mapping.get(obs), 1);
            }
        }

        solver.solve();

        double goal = objective.value();
        objective.clear();

        MPConstraint primaryGoal = solver.makeConstraint(goal, goal);
        for (Pair<MPVariable, Integer> var: ca_mapping.values()) {
            primaryGoal.setCoefficient(var.left(), var.right());
        }

        // Minimize overall size of the contract
        for (MPVariable var: do_mapping.values()) {
            objective.setCoefficient(var, 1);
        }
        objective.setMinimization();

        solver.solve();

        Set<Observation> new_contract = new HashSet<>();
        for (Map.Entry<Observation, MPVariable> entry: do_mapping.entrySet()) {
            if (entry.getValue().solutionValue() > 0.0) {
                new_contract.add(entry.getKey());
            }
        }
        return new_contract;
    }
}

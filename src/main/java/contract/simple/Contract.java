package contract.simple;

import isa.simple.TYPE;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Contract {

    private Set<Counterexample> counterexamples = new HashSet<>();

    private Set<Observation> current_contract = new HashSet<>();

    public void updateContract() {
        if (coversAll())
            return;
        current_contract = new HashSet<>();
        Set<Counterexample> remaining = counterexamples.stream().filter(ctx -> !covers(ctx)).collect(Collectors.toSet());
        while (!remaining.isEmpty()) {
            current_contract.add(remaining.stream().findFirst().get().getBest());
            remaining = remaining.stream().filter(ctx -> !covers(ctx)).collect(Collectors.toSet());
        }
    }

    public void updateContractPremium() {;
        Set<Observation> all = new HashSet<>();
        counterexamples.forEach(ctx -> all.addAll(ctx.getPossibilities()));
        current_contract = setcover(this::coversAll, Comparator.comparingInt(a -> a.stream().mapToInt(Observation::getValue).sum()), all);
    }

    boolean coversAll() {
        return coversAll(current_contract);
    }

    boolean coversAll(Set<Observation> set) {
        return counterexamples.stream().filter(ctx -> covers(set, ctx)).collect(Collectors.toSet()).containsAll(counterexamples);
    }

    boolean covers(Counterexample ctx) {
        return covers(current_contract, ctx);
    }

    boolean covers(Set<Observation> set, Counterexample ctx) {
        return set.stream().anyMatch(obs -> ctx.getPossibilities().contains(obs));
    }

    public void add(Counterexample ctx) {
        counterexamples.add(ctx);
    }

    public String printContract() {

        Map<TYPE, List<Observation>> observations_per_type =
                current_contract.stream().collect(Collectors.groupingBy(Observation::getType));

        StringBuilder sb = new StringBuilder();

        observations_per_type.forEach((type, observations) -> {
            sb.append(type.generateContract("1"));
            for(OBSERVATION_TYPE observation_type: OBSERVATION_TYPE.values()) {
                sb.append(observation_type.generateObservation("1", observations.stream().anyMatch(o -> o.getObservation() == observation_type)));
            }
            sb.append("end\n");
        });

        observations_per_type.forEach((type, observations) -> {
            sb.append(type.generateContract("2"));
            for(OBSERVATION_TYPE observation_type: OBSERVATION_TYPE.values()) {
                sb.append(observation_type.generateObservation("2", observations.stream().anyMatch(o -> o.getObservation() == observation_type)));
            }
            sb.append("end\n");
        });

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Current counterexamples: \n");
        counterexamples.forEach(ctx ->sb.append(ctx.toString()).append("\n"));
        sb.append("Inferred contract: \n");
        current_contract.forEach(obs -> sb.append(obs.toString()).append("\n"));
        return sb.toString();
    }

    // Interface
    // Declaring the interface thereby taking
    // abstract methods of the interface
    interface Filter<T> {

        boolean matches(T t);
    }

    private static <T> Set<T>
    setcover(Predicate<Set<T> > predicate, Comparator<Set<T>> comparator, Set<T> set)
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

    public static void main(String[] args) {
        Contract c = new Contract();
        Set<Observation> obs = new HashSet<>();
        obs.add(new Observation(TYPE.MUL, OBSERVATION_TYPE.OPCODE));
        obs.add(new Observation(TYPE.MUL, OBSERVATION_TYPE.RS1));
        c.add(new Counterexample(obs));
        Set<Observation> obs2 = new HashSet<>();
        obs2.add(new Observation(TYPE.MUL, OBSERVATION_TYPE.RS1));
        c.add(new Counterexample(obs2));
        c.updateContractPremium();
        System.out.println(c);
    }

    /*
    if (op_1 == `ALU_MUL_IMM) begin
                ctr_observation_1.op = 0;
                ctr_observation_1.op1 = 0;
                //ctr_observation_1.op2 = op2_1;
                ctr_observation_1.res = 0;
            end
            else if (op_1 == `ALU_MUL_REG) begin
                ctr_observation_1.op = 0;
                ctr_observation_1.op1 = 0;
                //ctr_observation_1.op2 = regfile_1_i[op2_1];
                ctr_observation_1.res = 0;
            end

            if (op_2 == `ALU_MUL_IMM) begin
                ctr_observation_2.op = 0;
                ctr_observation_2.op1 = 0;
                //ctr_observation_2.op2 = op2_2;
                ctr_observation_2.res = 0;
            end
            else if (op_2 == `ALU_MUL_REG) begin
                ctr_observation_2.op = 0;
                ctr_observation_2.op1 = 0;
                //ctr_observation_2.op2 = regfile_2_i[op2_2];
                ctr_observation_2.res = 0;
            end
     */
}

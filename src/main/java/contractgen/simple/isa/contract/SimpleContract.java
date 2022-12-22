package contractgen.simple.isa.contract;

import contractgen.Contract;
import contractgen.simple.isa.SIMPLE_TYPE;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SimpleContract extends Contract {

    public String printContract() {

        Map<SIMPLE_TYPE, List<SimpleObservation>> observations_per_type =
                getCurrentContract().stream().map(obs -> (SimpleObservation) obs).collect(Collectors.groupingBy(SimpleObservation::getType)); // TODO

        StringBuilder sb = new StringBuilder();

        observations_per_type.forEach((type, observations) -> {
            sb.append(type.generateContract("1"));
            for (SIMPLE_OBSERVATION_TYPE observation_type : SIMPLE_OBSERVATION_TYPE.values()) {
                sb.append(observation_type.generateObservation("1", observations.stream().anyMatch(o -> o.getObservation() == observation_type)));
            }
            sb.append("end\n");
        });

        observations_per_type.forEach((type, observations) -> {
            sb.append(type.generateContract("2"));
            for (SIMPLE_OBSERVATION_TYPE observation_type : SIMPLE_OBSERVATION_TYPE.values()) {
                sb.append(observation_type.generateObservation("2", observations.stream().anyMatch(o -> o.getObservation() == observation_type)));
            }
            sb.append("end\n");
        });

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Current counterexamples: \n");
        getCounterexamples().forEach(ctx ->sb.append(ctx.toString()).append("\n"));
        sb.append("Inferred contract: \n");
        getCurrentContract().forEach(obs -> sb.append(obs.toString()).append("\n"));
        return sb.toString();
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
        SimpleContract c = new SimpleContract();
        Set<SimpleObservation> obs = new HashSet<>();
        obs.add(new SimpleObservation(SIMPLE_TYPE.MUL, SIMPLE_OBSERVATION_TYPE.OPCODE));
        obs.add(new SimpleObservation(SIMPLE_TYPE.MUL, SIMPLE_OBSERVATION_TYPE.RS1));
        c.add(new SimpleCounterexample(obs));
        Set<SimpleObservation> obs2 = new HashSet<>();
        obs2.add(new SimpleObservation(SIMPLE_TYPE.MUL, SIMPLE_OBSERVATION_TYPE.RS1));
        c.add(new SimpleCounterexample(obs2));
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

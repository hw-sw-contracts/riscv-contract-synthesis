package contractgen.riscv.isa.contract;

import contractgen.Contract;
import contractgen.riscv.isa.RISCV_TYPE;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RISCVContract extends Contract {

    @Override
    public String printContract() {
        Map<RISCV_TYPE, List<RISCVObservation>> observations_per_type =
                getCurrentContract().stream().map(obs -> (RISCVObservation) obs).collect(Collectors.groupingBy(RISCVObservation::getType)); // TODO

        StringBuilder sb = new StringBuilder();

        observations_per_type.forEach((type, observations) -> {
            sb.append(type.generateContract("1"));
            for (RISCV_OBSERVATION_TYPE observation_type : RISCV_OBSERVATION_TYPE.values()) {
                sb.append(observation_type.generateObservation("1", observations.stream().anyMatch(o -> o.getObservation() == observation_type)));
            }
            sb.append("end\n");
        });

        observations_per_type.forEach((type, observations) -> {
            sb.append(type.generateContract("2"));
            for (RISCV_OBSERVATION_TYPE observation_type : RISCV_OBSERVATION_TYPE.values()) {
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
}

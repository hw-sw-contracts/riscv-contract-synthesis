package contractgen.riscv.isa.contract;

import contractgen.Observation;
import contractgen.riscv.isa.RISCV_TYPE;

import java.util.Objects;

public class RISCVObservation implements Observation {

    final private RISCV_TYPE type;
    final private RISCV_OBSERVATION_TYPE observation;

    public RISCVObservation(RISCV_TYPE type, RISCV_OBSERVATION_TYPE observation) {
        this.type = type;
        this.observation = observation;
    }

    public RISCV_TYPE getType() {
        return type;
    }

    public RISCV_OBSERVATION_TYPE getObservation() {
        return observation;
    }

    @Override
    public int getValue() {
        return observation.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RISCVObservation that = (RISCVObservation) o;
        return type == that.type && observation == that.observation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, observation);
    }

    @Override
    public String toString() {
        return "\t\t" + type.toString() + ": " + observation.toString();
    }


}

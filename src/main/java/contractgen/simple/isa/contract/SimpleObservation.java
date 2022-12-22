package contractgen.simple.isa.contract;

import contractgen.Observation;
import contractgen.simple.isa.SIMPLE_TYPE;

import java.util.Objects;

public class SimpleObservation implements Observation {

    final private SIMPLE_TYPE type;
    final private SIMPLE_OBSERVATION_TYPE observation;

    public SimpleObservation(SIMPLE_TYPE type, SIMPLE_OBSERVATION_TYPE observation) {
        this.type = type;
        this.observation = observation;
    }

    public SIMPLE_TYPE getType() {
        return type;
    }

    public SIMPLE_OBSERVATION_TYPE getObservation() {
        return observation;
    }

    public int getValue() {
        return observation.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleObservation that = (SimpleObservation) o;
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

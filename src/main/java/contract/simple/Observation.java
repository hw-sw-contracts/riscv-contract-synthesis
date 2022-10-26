package contract.simple;

import isa.simple.TYPE;

import java.util.Objects;

public class Observation {

    final private TYPE type;
    final private OBSERVATION_TYPE observation;

    public Observation(TYPE type, OBSERVATION_TYPE observation) {
        this.type = type;
        this.observation = observation;
    }

    public TYPE getType() {
        return type;
    }

    public OBSERVATION_TYPE getObservation() {
        return observation;
    }

    public int getValue() {
        return observation.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Observation that = (Observation) o;
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

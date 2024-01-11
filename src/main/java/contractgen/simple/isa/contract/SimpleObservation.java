package contractgen.simple.isa.contract;

import contractgen.Observation;
import contractgen.simple.isa.SIMPLE_TYPE;

/**
 * An oservation for the simple ISA.
 *
 * @param type          The type of the instruction this observation is associated to.
 * @param observation   The kind of observation.
 */
public record SimpleObservation(SIMPLE_TYPE type, SIMPLE_OBSERVATION_TYPE observation) implements Observation {

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
    public String toString() {
        return "\t\t" + type.toString() + ": " + observation.toString();
    }

    @Override
    public boolean isApplicable() {
        return true;
    }
}

package contractgen;

/**
 * The observation interface.
 */
public interface Observation {

    /**
     * @return the value of the observation corresponding to its severity.
     */
    int getValue();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();
}

package contractgen;

public interface Observation {
    int getValue();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();
}

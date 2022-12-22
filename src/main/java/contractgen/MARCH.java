package contractgen;

import contractgen.util.Pair;

public abstract class MARCH {

    private final ISA ISA;


    protected MARCH(ISA isa) {
        ISA = isa;
    }

    public ISA getISA() {
        return ISA;
    }

    public abstract void generateSources(TestCase testCase, Integer max_count);

    public abstract String runCover(int steps);


    public abstract int extractSteps(String coverTrace);

    public abstract boolean run(int steps);

    public abstract Pair<Counterexample, Counterexample> extractCTX(TestCase testCase);

    public abstract Pair<Counterexample, Counterexample> extractCTX(int id, TestCase testCase);

    public abstract void compile();

    public abstract void writeTestCase(TestCase testCase);

    public abstract void writeTestCase(int id, TestCase testCase);

    public abstract boolean simulate();

    public abstract boolean simulate(int id);
}

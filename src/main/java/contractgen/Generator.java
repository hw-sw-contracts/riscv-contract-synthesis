package contractgen;

public abstract class Generator {

    public final MARCH MARCH;

    public Generator(contractgen.MARCH march) {
        MARCH = march;
    }

    public abstract Contract generate();
}

package contractgen;

import java.io.IOException;

/**
 * Wrapper to start contract generation using different backends.
 */
public abstract class Generator {

    /**
     * The microarchitecture to be used.
     */
    public final MARCH MARCH;

    /**
     * @param march The microarchitecture to be used.
     */
    public Generator(contractgen.MARCH march) {
        MARCH = march;
    }

    /**
     * Starts contract generation.
     *
     * @return The generated contract.
     */
    public abstract Contract generate() throws IOException;
}

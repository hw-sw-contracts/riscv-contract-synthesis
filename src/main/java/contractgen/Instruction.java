package contractgen;

/**
 * The instruction interface.
 */
public interface Instruction {

    /**
     * @return The instruction encoded in binary format.
     */
    String toBinaryEncoding();

    /**
     * @return The instruction encoded in hexadecimal format.
     */
    String toHexEncoding();
}

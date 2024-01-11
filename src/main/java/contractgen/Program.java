package contractgen;

/**
 * The program interface.
 */
public interface Program {

    /**
     * Prints symbolic variables for the bounded model check in Verilog syntax.
     *
     * @return All symbolic variables in Verilog syntax.
     */
    String printSymbolic();

    /**
     * Prints the program in Verilog syntax.
     *
     * @param address The offset at which the first instruction should be located in memory.
     * @return        The program in Verilog syntax.
     */
    String printProgram(Integer address);

    /**
     * Computes the highest address of the included instructions.
     *
     * @param base The offset at which the first instruction should be located in memory.
     * @return     The highest address.
     */
    int maxAddress(int base);

    /**
     * Writes the instructions to instantiate the initial architectural state in a file.
     *
     * @param path The path to be used to store the file.
     */
    void printInit(String path);

    /**
     * Writes the instructions of the program in a file.
     *
     * @param path The path to be used to store the file.
     */
    void printInstr(String path);
}

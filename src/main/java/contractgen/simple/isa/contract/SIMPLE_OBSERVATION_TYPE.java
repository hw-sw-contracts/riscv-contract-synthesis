package contractgen.simple.isa.contract;

/**
 * The possible observations in the simple ISA.
 */
public enum SIMPLE_OBSERVATION_TYPE {
    /**
     * The opcode.
     */
    OPCODE(1, "op"),
    /**
     * The destination register.
     */
    RD(2, "rd"),
    /**
     * The first operand's register number.
     */
    RS1(3, "rs1"),
    /**
     * The second operand's register number.
     */
    RS2(4, "rs2"),
    /**
     * The first operand.
     */
    REG_RS1(5, "reg_rs1"),
    /**
     * The second operand.
     */
    REG_RS2(6, "reg_rs2");

    /**
     * The severity of the observation
     */
    final int value;
    /**
     * The verilog encoding of the observation
     */
    private final String encoding;

    /**
     * @param value    The severity of the observation
     * @param encoding The verilog encoding of the observation
     */
    SIMPLE_OBSERVATION_TYPE(int value, String encoding) {
        this.value = value;
        this.encoding = encoding;
    }

    /**
     * Generates a Verilog encoding of the observation.
     *
     * @param suffix         The index of the core to select the accurate value.
     * @param hasObservation Whether any observation should be produced.
     * @return The respective observation in Verilog.
     */
    public String generateObservation(String suffix, boolean hasObservation) {
        return "ctr_observation_" + suffix + "." + this.encoding + " = " + (hasObservation ? encoding + "_" + suffix : "0") + ";\n";
    }
}

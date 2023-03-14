package contractgen.riscv.isa.contract;

/**
 * Possible observation types according to the contract template.
 */
public enum RISCV_OBSERVATION_TYPE {

    /**
     * The type of the instruction.
     */
    TYPE(1, "format"),
    /**
     * The opcode of the instruction.
     */
    OPCODE(2, "op"),
    /**
     * The funct3 of the instruction.
     */
    FUNCT3(3, "funct_3"),
    /**
     * The funct7 of the instruction
     */
    FUNCT5(4, "funct_7"),
    /**
     * The destination register.
     */
    RD(5, "rd"),
    /**
     * The first operand's register number.
     */
    RS1(6, "rs1"),
    /**
     * The second operand's register number.
     */
    RS2(7, "rs2"),
    /**
     * The immediate value.
     */
    IMM(8, "imm"),
    /**
     * The first operand.
     */
    REG_RS1(9, "reg_rs1"),
    /**
     * The second operand.
     */
    REG_RS2(10, "reg_rs2"),
    /**
     * The result value.
     */
    REG_RD(11, "reg_rd"),
    /**
     * The accessed memory address.
     */
    MEM_ADDR(12, "mem_addr"),
    /**
     * The data read from memory.
     */
    MEM_R_DATA(13, "mem_r_data"),
    /**
     * The data written to memory.
     */
    MEM_W_DATA(14, "mem_w_data");

    /**
     * The severity of an observation type.
     */
    public final int value;
    private final String encoding;
    RISCV_OBSERVATION_TYPE(int value, String encoding) {
        this.value = value;
        this.encoding = encoding;
    }

    /**
     * Generates a Verilog encoding of the observation.
     *
     * @param suffix           The index of the core to select the accurate value.
     * @param hasObservation   Whether any observation should be produced.
     * @return                 The respective observation in Verilog.
     */
    public String generateObservation(String suffix, boolean hasObservation) {
        return "ctr_observation_" + suffix + "." + this.encoding + " = " + (hasObservation ? encoding + "_" + suffix : "0") + ";\n";
    }
}

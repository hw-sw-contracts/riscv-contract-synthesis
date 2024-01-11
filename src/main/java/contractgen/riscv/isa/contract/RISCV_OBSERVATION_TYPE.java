package contractgen.riscv.isa.contract;

import java.util.Set;

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
    MEM_W_DATA(14, "mem_w_data"),
    /**
     * The data written to memory.
     */
    IS_BRANCH(15, "is_branch"),
    /**
     * The data written to memory.
     */
    BRANCH_TAKEN(16, "branch_taken"),
    /**
     * Whether the memory address is word aligned.
     */
    IS_ALIGNED(17, "is_aligned"),
    /**
     * Whether the memory address is halfword aligned.
     */
    IS_HALF_ALIGNED(18, "is_half_aligned"),
    /**
     * The new PC.
     */
    NEW_PC(19, "new_pc"),

    /**
     * Read-after-Write dependency with instruction -1.
     */
    RAW_RS1_1(20, "raw_rs1_1"),
    /**
     * Read-after-Write dependency with instruction -2.
     */
    RAW_RS1_2(23, "raw_rs1_2"),
    /**
     * Read-after-Write dependency with instruction -3.
     */
    RAW_RS1_3(26, "raw_rs1_3"),
    /**
     * Read-after-Write dependency with instruction -4.
     */
    RAW_RS1_4(29, "raw_rs1_4"),
    /**
     * Read-after-Write dependency with instruction -1.
     */
    RAW_RS2_1(21, "raw_rs2_1"),
    /**
     * Read-after-Write dependency with instruction -2.
     */
    RAW_RS2_2(24, "raw_rs2_2"),
    /**
     * Read-after-Write dependency with instruction -3.
     */
    RAW_RS2_3(27, "raw_rs2_3"),
    /**
     * Read-after-Write dependency with instruction -4.
     */
    RAW_RS2_4(30, "raw_rs2_4"),
    /**
     * Write-after-Write dependency with instruction -1.
     */
    WAW_1(22, "waw_1"),
    /**
     * Write-after-Write dependency with instruction -2.
     */
    WAW_2(25, "waw_2"),
    /**
     * Write-after-Write dependency with instruction -3.
     */
    WAW_3(28, "waw_3"),
    /**
     * Write-after-Write dependency with instruction -4.
     */
    WAW_4(31, "waw_4");

    /**
     * The severity of an observation type.
     */
    public final int value;
    /**
     * The verilog encoding of an observation.
     */
    private final String encoding;

    /**
     * @param value    The severity of the observation.
     * @param encoding The verilog encoding.
     */
    RISCV_OBSERVATION_TYPE(int value, String encoding) {
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
        return "ctr_observation_" + suffix + "." + this.encoding + " = " + (hasObservation ? "{1'b1, " + this.encoding + "_" + suffix + "}" : "0") + ";\n";
    }

    /**
     * @return the base contract template
     */
    public static Set<RISCV_OBSERVATION_TYPE> getBase() {
        return Set.of(
                TYPE,
                OPCODE,
                FUNCT3,
                FUNCT5,
                RD,
                RS1,
                RS2,
                IMM,
                REG_RS1,
                REG_RS2,
                REG_RD,
                MEM_ADDR,
                MEM_R_DATA,
                MEM_W_DATA
        );
    }

    /**
     * @return the alignedness contract template
     */
    public static Set<RISCV_OBSERVATION_TYPE> getAligned() {
        return Set.of(
                IS_ALIGNED,
                IS_HALF_ALIGNED
        );
    }

    /**
     * @return the branch contract template
     */
    public static Set<RISCV_OBSERVATION_TYPE> getBranch() {
        return Set.of(
                IS_BRANCH,
                BRANCH_TAKEN,
                NEW_PC
        );
    }

    /**
     * @return the dependencies contract template
     */
    public static Set<RISCV_OBSERVATION_TYPE> getDependencies() {
        return Set.of(
                RAW_RS1_1,
                RAW_RS1_2,
                RAW_RS1_3,
                RAW_RS1_4,
                RAW_RS2_1,
                RAW_RS2_2,
                RAW_RS2_3,
                RAW_RS2_4,
                WAW_1,
                WAW_2,
                WAW_3,
                WAW_4
        );
    }
}

package contractgen.riscv.isa.contract;

public enum RISCV_OBSERVATION_TYPE {

    TYPE(1, "format"),
    OPCODE(2, "op"),
    FUNCT3(3, "funct_3"),
    FUNCT5(4, "funct_7"),
    RD(5, "rd"),
    RS1(6, "rs1"),
    RS2(7, "rs2"),
    IMM(8, "imm"),
    REG_RS1(9, "reg_rs1"),
    REG_RS2(10, "reg_rs2"),
    MEM_RS1(11, "mem_rs1"),
    MEM_RS2(12, "mem_rs2"),

    //RFVI
    REG_RD(13, "reg_rd"),
    MEM_ADDR(14, "mem_addr"),
    MEM_R_DATA(15, "mem_r_data"),
    MEM_W_DATA(16, "mem_w_data");

    public final int value;
    private final String encoding;
    RISCV_OBSERVATION_TYPE(int value, String encoding) {
        this.value = value;
        this.encoding = encoding;
    }

    public String generateObservation(String suffix, boolean hasObservation) {
        return "ctr_observation_" + suffix + "." + this.encoding + " = " + (hasObservation ? encoding + "_" + suffix : "0") + ";\n";
    }
}

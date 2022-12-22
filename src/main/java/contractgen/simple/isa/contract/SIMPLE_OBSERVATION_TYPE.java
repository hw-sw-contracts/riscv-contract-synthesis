package contractgen.simple.isa.contract;

public enum SIMPLE_OBSERVATION_TYPE {
    OPCODE(1, "op"), RD(2, "rd"), RS1(3, "rs1"), RS2(4, "rs2"), REG_RS1(5, "reg_rs1"), REG_RS2(6, "reg_rs2");

    public final int value;
    private final String encoding;
    SIMPLE_OBSERVATION_TYPE(int value, String encoding) {
        this.value = value;
        this.encoding = encoding;
    }

    public String generateObservation(String suffix, boolean hasObservation) {
        return "ctr_observation_" + suffix + "." + this.encoding + " = " + (hasObservation ? encoding + "_" + suffix : "0") + ";\n";
    }
}

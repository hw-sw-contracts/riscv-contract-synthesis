package contractgen.riscv.isa;

public enum RISCV_TYPE {
    LUI("lui", RISCV_FORMAT.UTYPE, "0110111"),
    AUIPC("aupc", RISCV_FORMAT.UTYPE, "0010111"),
    JAL("jal", RISCV_FORMAT.JTYPE, "1101111"),
    JALR("jalr", RISCV_FORMAT.ITYPE, "1100111", "000"),
    BEQ("beq", RISCV_FORMAT.BTYPE, "1100011", "000"),
    BNE("bne", RISCV_FORMAT.BTYPE, "1100011", "001"),
    BLT("blt", RISCV_FORMAT.BTYPE, "1100011", "100"),
    BGE("bge", RISCV_FORMAT.BTYPE, "1100011", "101"),
    BLTU("bltu", RISCV_FORMAT.BTYPE, "1100011", "110"),
    BGEU("bgeu", RISCV_FORMAT.BTYPE, "1100011", "111"),
    LB("lb", RISCV_FORMAT.ITYPE, "0000011", "000"),
    LH("lh", RISCV_FORMAT.ITYPE, "0000011", "001"),
    LW("lw", RISCV_FORMAT.ITYPE, "0000011", "010"),
    LBU("lbu", RISCV_FORMAT.ITYPE, "0000011", "100"),
    LHU("lhu", RISCV_FORMAT.ITYPE, "0000011", "101"),
    SB("sb", RISCV_FORMAT.STYPE, "0100011", "000"),
    SH("sh", RISCV_FORMAT.STYPE, "0100011", "001"),
    SW("sw", RISCV_FORMAT.STYPE, "0100011", "010"),
    ADDI("addi", RISCV_FORMAT.ITYPE, "0010011", "000"),
    SLTI("slti", RISCV_FORMAT.ITYPE, "0010011", "010"),
    SLTIU("sltui", RISCV_FORMAT.ITYPE, "0010011", "011"),
    XORI("xorii", RISCV_FORMAT.ITYPE, "0010011", "100"),
    ORI("ori", RISCV_FORMAT.ITYPE, "0010011", "110"),
    ANDI("andi", RISCV_FORMAT.ITYPE, "0010011", "111"),
    SLLI("slli", RISCV_FORMAT.RTYPE, "0010011", "001", "0000000"),
    SRLI("srli", RISCV_FORMAT.RTYPE, "0010011", "101", "0000000"),
    SRAI("srai", RISCV_FORMAT.RTYPE, "0010011", "101", "0100000"),
    ADD("add", RISCV_FORMAT.RTYPE, "0110011", "000", "0000000"),
    SUB("sub", RISCV_FORMAT.RTYPE, "0110011", "000", "0100000"),
    SLL("sll", RISCV_FORMAT.RTYPE, "0110011", "001", "0000000"),
    SLT("slt", RISCV_FORMAT.RTYPE, "0110011", "010", "0000000"),
    SLTU("sltu", RISCV_FORMAT.RTYPE, "0110011", "011", "0000000"),
    XOR("xor", RISCV_FORMAT.RTYPE, "0110011", "100", "0000000"),
    SRL("srl", RISCV_FORMAT.RTYPE, "0110011", "101", "0000000"),
    SRA("sra", RISCV_FORMAT.RTYPE, "0110011", "101", "0100000"),
    OR("or", RISCV_FORMAT.RTYPE, "0110011", "110", "0000000"),
    AND("and", RISCV_FORMAT.RTYPE, "0110011", "111", "0000000");

    private final String name;

    private final RISCV_FORMAT format;
    private final String opcode;

    private final String funct3;
    private final String funct7;

    RISCV_TYPE(String name, RISCV_FORMAT format, String opcode) {
        this.name = name;
        this.format = format;
        this.opcode = opcode;
        this.funct3 = null;
        this.funct7 = null;
    }

    RISCV_TYPE(String name, RISCV_FORMAT format, String opcode, String funct3) {
        this.name = name;
        this.format = format;
        this.opcode = opcode;
        this.funct3 = funct3;
        this.funct7 = null;
    }

    RISCV_TYPE(String name, RISCV_FORMAT format, String opcode, String funct3, String funct7) {
        this.name = name;
        this.format = format;
        this.opcode = opcode;
        this.funct3 = funct3;
        this.funct7 = funct7;
    }

    public String getOpcode() {
        return opcode;
    }

    public String getFunct3() {
        return funct3;
    }

    public String getFunct7() {
        return funct7;
    }

    public String getName() {
        return name;
    }

    public RISCV_FORMAT getFormat() {
        return format;
    }

    public String generateContract(String suffix) {
        if (funct3 == null)
            return "if (op_" + suffix + " == 'b" + opcode + ") begin\n";
        if (funct7 == null)
            return "if (op_" + suffix + " == 'b" + opcode + " && funct_3_" + suffix + " == 'b" + funct3 + ") begin\n";
        return "if (op_" + suffix + " == 'b" + opcode + " && funct_3_" + suffix + " == 'b" + funct3 +  " && funct_7_" + suffix + " == 'b" + funct7 + ") begin\n";
    }
}

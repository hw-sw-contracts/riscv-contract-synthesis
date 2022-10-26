package isa.riscv;

public enum TYPE {
    LUI("lui", FORMAT.UTYPE, "0110111"),
    AUIPC("aupc", FORMAT.UTYPE, "0010111"),
    JAL("jal", FORMAT.JTYPE, "1101111"),
    JALR("jalr", FORMAT.ITYPE, "1100111", "000"),
    BEQ("beq", FORMAT.BTYPE, "1100011", "000"),
    BNE("bne", FORMAT.BTYPE, "1100011", "001"),
    BLT("blt", FORMAT.BTYPE, "1100011", "100"),
    BGE("bge", FORMAT.BTYPE, "1100011", "101"),
    BLTU("bltu", FORMAT.BTYPE, "1100011", "110"),
    BGEU("bgeu", FORMAT.BTYPE, "1100011", "111"),
    LB("lb", FORMAT.ITYPE, "0000011", "000"),
    LH("lh", FORMAT.ITYPE, "0000011", "001"),
    LW("lw", FORMAT.ITYPE, "0000011", "010"),
    LBU("lbu", FORMAT.ITYPE, "0000011", "100"),
    LHU("lhu", FORMAT.ITYPE, "0000011", "101"),
    SB("sb", FORMAT.STYPE, "0100011", "000"),
    SH("sh", FORMAT.STYPE, "0100011", "001"),
    SW("sw", FORMAT.STYPE, "0100011", "010"),
    ADDI("addi", FORMAT.ITYPE, "0010011", "000"),
    SLTI("slti", FORMAT.ITYPE, "0010011", "010"),
    SLTIU("sltui", FORMAT.ITYPE, "0010011", "011"),
    XORI("xorii", FORMAT.ITYPE, "0010011", "100"),
    ORI("ori", FORMAT.ITYPE, "0010011", "110"),
    ANDI("andi", FORMAT.ITYPE, "0010011", "111"),
    SLLI("slli", FORMAT.RTYPE, "0010011", "001", "0000000"),
    SRLI("srli", FORMAT.RTYPE, "0010011", "101", "0000000"),
    SRAI("srai", FORMAT.RTYPE, "0010011", "101", "0100000"),
    ADD("add", FORMAT.RTYPE, "0110011", "000", "0000000"),
    SUB("sub", FORMAT.RTYPE, "0110011", "000", "0100000"),
    SLL("sll", FORMAT.RTYPE, "0110011", "001", "0000000"),
    SLT("slt", FORMAT.RTYPE, "0110011", "010", "0000000"),
    SLTU("sltu", FORMAT.RTYPE, "0110011", "011", "0000000"),
    XOR("xor", FORMAT.RTYPE, "0110011", "100", "0000000"),
    SRL("srl", FORMAT.RTYPE, "0110011", "101", "0000000"),
    SRA("sra", FORMAT.RTYPE, "0110011", "101", "0100000"),
    OR("or", FORMAT.RTYPE, "0110011", "110", "0000000"),
    AND("and", FORMAT.RTYPE, "0110011", "111", "0000000");

    private final String name;

    private final FORMAT format;
    private final String opcode;

    private final String funct3;
    private final String funct7;

    TYPE(String name, FORMAT format, String opcode) {
        this.name = name;
        this.format = format;
        this.opcode = opcode;
        this.funct3 = null;
        this.funct7 = null;
    }

    TYPE(String name, FORMAT format, String opcode, String funct3) {
        this.name = name;
        this.format = format;
        this.opcode = opcode;
        this.funct3 = funct3;
        this.funct7 = null;
    }

    TYPE(String name, FORMAT format, String opcode, String funct3, String funct7) {
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

    public FORMAT getFormat() {
        return format;
    }
}

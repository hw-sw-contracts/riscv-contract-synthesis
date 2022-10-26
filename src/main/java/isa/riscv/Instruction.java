package isa.riscv;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Instruction {

    private final TYPE type;
    private final Integer rd;
    private final Integer rs1;
    private final Integer rs2;
    private final Long imm;

    public Instruction(TYPE type, Integer rd, Integer rs1, Integer rs2, Long imm) {
        this.type = type;
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.imm = imm;
    }

    public String toBinaryEncoding() {
        return switch (type.getFormat()) {
            case RTYPE -> type.getFunct7() + encodeRegister(rs2) + encodeRegister(rs1) + type.getFunct3() + encodeRegister(rd) + type.getOpcode();
            case ITYPE -> encodeImmediate(imm, type.getFormat()) + encodeRegister(rs1) + type.getFunct3() + encodeRegister(rd) + type.getOpcode();
            case STYPE -> encodeImmediate(imm, type.getFormat()).substring(0, 7) + encodeRegister(rs2) + encodeRegister(rs1) + type.getFunct3() + encodeImmediate(imm, type.getFormat()).substring(7, 12);
            case BTYPE -> encodeImmediate(imm, type.getFormat()).substring(0, 7) + encodeRegister(rs2) + encodeRegister(rs1) + type.getFunct3() + encodeImmediate(imm, type.getFormat()).substring(7, 12);
            case UTYPE -> encodeImmediate(imm, type.getFormat()) + encodeRegister(rd) + type.getOpcode();
            case JTYPE -> encodeImmediate(imm, type.getFormat()) + encodeRegister(rd) + type.getOpcode();
        };
    }

    public String toHexEncoding() {
        return Instruction.toHexEncoding(this.toBinaryEncoding());
    }

    public static Instruction parseBinaryString(String instruction) {
        if (instruction.length() != 32) return null;
        System.out.println("Looking for " + instruction.substring(25));
        Set<TYPE> candidates = Set.of(TYPE.values());
        candidates = candidates.stream().filter(t -> t.getOpcode().equals(instruction.substring(25))).collect(Collectors.toUnmodifiableSet());
        if (candidates.size() > 1)
            candidates = candidates.stream().filter(t -> t.getFunct3().equals(instruction.substring(17, 20))).collect(Collectors.toUnmodifiableSet());
        if (candidates.size() > 1)
            candidates = candidates.stream().filter(t -> t.getFunct7().equals(instruction.substring(0, 7))).collect(Collectors.toUnmodifiableSet());
        if (candidates.size() != 1)
            throw new IllegalArgumentException("Unknown instruction");
        TYPE t = candidates.stream().findFirst().orElseThrow();

        int rd = Integer.parseUnsignedInt(instruction.substring(20, 25),2);
        int rs1 = Integer.parseUnsignedInt(instruction.substring(12, 17),2);
        int rs2 = Integer.parseUnsignedInt(instruction.substring(7, 12),2);
        long imm_i = Long.parseLong(instruction.substring(0, 12),2);
        long imm_s = Long.parseLong(instruction.substring(0, 7) + instruction.substring(20, 25),2);
        long imm_b = Long.parseLong(instruction.substring(0, 1) + instruction.substring(24, 25) + instruction.substring(1, 7) + instruction.substring(20, 24) + "0",2);
        long imm_u = Long.parseLong(instruction.substring(0, 20) + String.format("%012d", 0),2);
        long imm_j = Long.parseLong(instruction.substring(0, 1) + instruction.substring(12, 20) + instruction.substring(11, 12) + instruction.substring(1, 11) + "0", 2);

        return switch (t.getFormat()) {
            case RTYPE -> Instruction.RTYPE(t, rd, rs1, rs2);
            case ITYPE -> Instruction.ITYPE(t, rd, rs1, imm_i);
            case STYPE -> Instruction.STYPE(t, rs1, rs2, imm_s);
            case BTYPE -> Instruction.BTYPE(t, rs1, rs2, imm_b);
            case UTYPE -> Instruction.UTYPE(t, rd, imm_u);
            case JTYPE -> Instruction.JTYPE(t, rd, imm_j);
        };
    }

    public static Instruction parseHexString(String instruction) {
        if (instruction.length() == 10 && instruction.substring(0, 2).equals("0x"))
            instruction = instruction.substring(2);
        if (instruction.length() != 8) return null;
        return Instruction.parseBinaryString(Instruction.toBinaryEncoding(instruction));
    }



    public static String toBinaryEncoding(String hexStr) {
        long decimal = Long.parseUnsignedLong(hexStr,16);
        return String.format("%32s", Long.toBinaryString(decimal)).replace(' ', '0');
    }

    public static String toHexEncoding(String binaryStr) {
        long decimal = Long.parseUnsignedLong(binaryStr,2);
        return String.format("%8s", Long.toHexString(decimal)).replace(' ', '0');
    }

    private String encodeRegister(Integer register) {
        return String.format("%5s", Integer.toBinaryString(register)).replace(' ', '0');
    }

    private String encodeImmediate(Long immediate, FORMAT format) {
        return switch (format) {

            case RTYPE -> {throw new IllegalArgumentException("RTYPE instructions have no immediate.");}
            case ITYPE, STYPE -> String.format("%12s", Long.toBinaryString(immediate)).replace(' ', '0');
            case BTYPE -> {
                String s = String.format("%13s", Long.toBinaryString(immediate)).replace(' ', '0');
                yield s.substring(0, 1) + s.substring(2, 8) + s.substring(8, 12) + s.substring(1, 2);
            }
            case UTYPE -> String.format("%32s", Long.toBinaryString(immediate)).replace(' ', '0').substring(0, 20);
            case JTYPE -> {
                String s = String.format("%21s", Long.toBinaryString(immediate)).replace(' ', '0');
                yield s.substring(0, 1) + s.substring(10, 20) + s.substring(9, 10) + s.substring(1, 9);
            }
        };
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "type=" + type +
                ", rd=" + rd +
                ", rs1=" + rs1 +
                ", rs2=" + rs2 +
                ", imm=" + imm +
                '}';
    }

    public static void main(String[] args) {
        Instruction add = Instruction.ADD(10, 11, 12);
        System.out.println(add.toBinaryEncoding());
        System.out.println(add.toHexEncoding());
        Instruction i = Instruction.parseBinaryString(add.toBinaryEncoding());
        System.out.println(i);
        System.out.println(Instruction.parseHexString("0x3e800093"));
        System.out.println(Instruction.parseHexString("0x7d008113"));
        System.out.println(Instruction.parseHexString("0xc1810193"));
        System.out.println(Instruction.parseHexString("0x83018213"));
        System.out.println(Instruction.parseHexString("0x3e820293"));
        Instruction addi = Instruction.ADDI(1, 0, 1001);
        System.out.println(addi.toHexEncoding());
    }


    public static Instruction RTYPE(TYPE type, Integer rd, Integer rs1, Integer rs2) {
        return new Instruction(type, rd, rs1, rs2, null);
    }

    public static Instruction ITYPE(TYPE type, Integer rd, Integer rs1, Long imm) {
        return new Instruction(type, rd, rs1, null, imm);
    }

    public static Instruction STYPE(TYPE type, Integer rs1, Integer rs2, Long imm) {
        return new Instruction(type, null, rs1, rs2, imm);
    }

    public static Instruction BTYPE(TYPE type, Integer rs1, Integer rs2, Long imm) {
        return new Instruction(type, null, rs1, rs2, imm);
    }

    public static Instruction UTYPE(TYPE type, Integer rd, Long imm) {
        return new Instruction(type, rd, null, null, imm);
    }

    public static Instruction JTYPE(TYPE type, Integer rd, Long imm) {
        return new Instruction(type, rd, null, null, imm);
    }

    public static Instruction LUI(int rd, long imm) {
        return Instruction.UTYPE(TYPE.LUI, rd, imm);
    }

    public static Instruction AUIPC(int rd, long imm) {
        return Instruction.UTYPE(TYPE.AUIPC, rd, imm);
    }

    public static Instruction JAL(int rd, long imm) {
        return Instruction.JTYPE(TYPE.JAL, rd, imm);
    }

    public static Instruction JALR(int rd, int rs1, long imm) {
        return Instruction.ITYPE(TYPE.JALR, rd, rs1, imm);
    }

    public static Instruction BEQ(int rs1, int rs2, long imm) {
        return Instruction.BTYPE(TYPE.BEQ, rs1, rs2, imm);
    }

    public static Instruction BNE(int rs1, int rs2, long imm) {
        return Instruction.BTYPE(TYPE.BNE, rs1, rs2, imm);
    }

    public static Instruction BLT(int rs1, int rs2, long imm) {
        return Instruction.BTYPE(TYPE.BLT, rs1, rs2, imm);
    }

    public static Instruction BGE(int rs1, int rs2, long imm) {
        return Instruction.BTYPE(TYPE.BGE, rs1, rs2, imm);
    }

    public static Instruction BLTU(int rs1, int rs2, long imm) {
        return Instruction.BTYPE(TYPE.BLTU, rs1, rs2, imm);
    }

    public static Instruction BGEU(int rs1, int rs2, long imm) {
        return Instruction.BTYPE(TYPE.BGEU, rs1, rs2, imm);
    }

    public static Instruction LB(int rd, int rs1, long imm) {
        return Instruction.ITYPE(TYPE.LB, rd, rs1, imm);
    }

    public static Instruction LH(int rd, int rs1, long imm) {
        return Instruction.ITYPE(TYPE.LH, rd, rs1, imm);
    }

    public static Instruction LW(int rd, int rs1, long imm) {
        return Instruction.ITYPE(TYPE.LW, rd, rs1, imm);
    }

    public static Instruction LBU(int rd, int rs1, long imm) {
        return Instruction.ITYPE(TYPE.LBU, rd, rs1, imm);
    }

    public static Instruction LHU(int rd, int rs1, long imm) {
        return Instruction.ITYPE(TYPE.LHU, rd, rs1, imm);
    }

    public static Instruction SB(int rs1, int rs2, long imm) {
        return Instruction.STYPE(TYPE.SB, rs1, rs2, imm);
    }

    public static Instruction SH(int rs1, int rs2, long imm) {
        return Instruction.STYPE(TYPE.SH, rs1, rs2, imm);
    }

    public static Instruction SW(int rs1, int rs2, long imm) {
        return Instruction.STYPE(TYPE.SW, rs1, rs2, imm);
    }

    public static Instruction ADDI(int rd, int rs1, long imm) {
        return Instruction.ITYPE(TYPE.ADDI, rd, rs1, imm);
    }

    public static Instruction SLTI(int rd, int rs1, long imm) {
        return Instruction.ITYPE(TYPE.SLTI, rd, rs1, imm);
    }

    public static Instruction SLTIU(int rd, int rs1, long imm) {
        return Instruction.ITYPE(TYPE.SLTIU, rd, rs1, imm);
    }

    public static Instruction XORI(int rd, int rs1, long imm) {
        return Instruction.ITYPE(TYPE.XORI, rd, rs1, imm);
    }

    public static Instruction ORI(int rd, int rs1, long imm) {
        return Instruction.ITYPE(TYPE.ORI, rd, rs1, imm);
    }

    public static Instruction ANDI(int rd, int rs1, long imm) {
        return Instruction.ITYPE(TYPE.ANDI, rd, rs1, imm);
    }

    public static Instruction SLLI(int rd, int rs1, int shamt) {
        return Instruction.RTYPE(TYPE.SLLI, rd, rs1, shamt);
    }

    public static Instruction SRLI(int rd, int rs1, int shamt) {
        return Instruction.RTYPE(TYPE.SRLI, rd, rs1, shamt);
    }

    public static Instruction SRAI(int rd, int rs1, int shamt) {
        return Instruction.RTYPE(TYPE.SRAI, rd, rs1, shamt);
    }

    public static Instruction ADD(int rd, int rs1, int rs2) {
        return Instruction.RTYPE(TYPE.ADD, rd, rs1, rs2);
    }

    public static Instruction SUB(int rd, int rs1, int rs2) {
        return Instruction.RTYPE(TYPE.SUB, rd, rs1, rs2);
    }

    public static Instruction SLL(int rd, int rs1, int rs2) {
        return Instruction.RTYPE(TYPE.SLL, rd, rs1, rs2);
    }

    public static Instruction SLT(int rd, int rs1, int rs2) {
        return Instruction.RTYPE(TYPE.SLT, rd, rs1, rs2);
    }

    public static Instruction SLTU(int rd, int rs1, int rs2) {
        return Instruction.RTYPE(TYPE.SLTU, rd, rs1, rs2);
    }

    public static Instruction XOR(int rd, int rs1, int rs2) {
        return Instruction.RTYPE(TYPE.XOR, rd, rs1, rs2);
    }

    public static Instruction SRL(int rd, int rs1, int rs2) {
        return Instruction.RTYPE(TYPE.SRL, rd, rs1, rs2);
    }

    public static Instruction SRA(int rd, int rs1, int rs2) {
        return Instruction.RTYPE(TYPE.SRA, rd, rs1, rs2);
    }

    public static Instruction OR(int rd, int rs1, int rs2) {
        return Instruction.RTYPE(TYPE.OR, rd, rs1, rs2);
    }

    public static Instruction AND(int rd, int rs1, int rs2) {
        return Instruction.RTYPE(TYPE.AND, rd, rs1, rs2);
    }

    public static final Set<TYPE> RTYPE_INSTRUCTIONS = Stream.of(TYPE.SLLI, TYPE.SRLI, TYPE.SRAI, TYPE.ADD, TYPE.SUB, TYPE.SLL, TYPE.SLT, TYPE.SLTU, TYPE.XOR, TYPE.SRL, TYPE.SRA, TYPE.OR, TYPE.AND).collect(Collectors.toUnmodifiableSet());
    public static final Set<TYPE> ITYPE_INSTRUCTIONS = Stream.of(TYPE.JALR, TYPE.LB, TYPE.LH, TYPE.LW, TYPE.LBU, TYPE.LHU, TYPE.ADDI, TYPE.SLTI, TYPE.SLTIU, TYPE.XORI, TYPE.ORI, TYPE.ANDI).collect(Collectors.toUnmodifiableSet());
    public static final Set<TYPE> STYPE_INSTRUCTIONS = Stream.of(TYPE.SB, TYPE.SH, TYPE.SW).collect(Collectors.toUnmodifiableSet());
    public static final Set<TYPE> BTYPE_INSTRUCTIONS = Stream.of(TYPE.BEQ, TYPE.BNE, TYPE.BLT, TYPE.BGE, TYPE.BLTU, TYPE.BGEU).collect(Collectors.toUnmodifiableSet());
    public static final Set<TYPE> UTYPE_INSTRUCTIONS = Stream.of(TYPE.LUI, TYPE.AUIPC).collect(Collectors.toUnmodifiableSet());
    public static final Set<TYPE> JTYPE_INSTRUCTIONS = Stream.of(TYPE.JAL).collect(Collectors.toUnmodifiableSet());
}

package isa.simple;

import java.util.Set;
import java.util.stream.Collectors;

import static isa.simple.TYPE.*;

public class Instruction {
    private final TYPE type;
    private final Integer rd;
    private final Integer rs1;
    private final Integer rs2;

    public Instruction(TYPE type, Integer rd, Integer rs1, Integer rs2) {
        this.type = type;
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
    }

    public TYPE getType() {
        return type;
    }

    public Integer getRd() {
        return rd;
    }

    public Integer getRs1() {
        return rs1;
    }

    public Integer getRs2() {
        return rs2;
    }

    public String toBinaryEncoding() {
        return type.getOpcode() + encodeRegister(rd) + encodeRegister(rs1) + encodeRegister(rs2);
    }

    public String toHexEncoding() {
        return Instruction.toHexEncoding(this.toBinaryEncoding());
    }

    public static Instruction parseBinaryString(String instruction) {
        if (instruction.length() != 32) return null;
        System.out.println("Looking for " + instruction.substring(0,8));
        Set<TYPE> candidates = Set.of(TYPE.values());
        candidates = candidates.stream().filter(t -> t.getOpcode().equals(instruction.substring(0, 8))).collect(Collectors.toUnmodifiableSet());
        if (candidates.size() != 1)
            throw new IllegalArgumentException("Unknown instruction");
        TYPE t = candidates.stream().findFirst().orElseThrow();

        int rd = Integer.parseUnsignedInt(instruction.substring(8, 16),2);
        int rs1 = Integer.parseUnsignedInt(instruction.substring(16, 24),2);
        int rs2 = Integer.parseUnsignedInt(instruction.substring(24, 32),2);

        return new Instruction(t, rd, rs1, rs2);
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
        return String.format("%8s", Integer.toBinaryString(register)).replace(' ', '0');
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "type=" + type +
                ", rd=" + rd +
                ", rs1=" + rs1 +
                ", rs2=" + rs2 +
                '}';
    }

    public static Instruction ADD(int rd, int rs1, int rs2) {
        return new Instruction(ADD, rd, rs1, rs2);
    }

    public static Instruction ADDI(int rd, int rs1, int imm) {
        return new Instruction(ADDI, rd, rs1, imm);
    }

    public static Instruction MUL(int rd, int rs1, int rs2) {
        return new Instruction(MUL, rd, rs1, rs2);
    }

    public static Instruction MULI(int rd, int rs1, int imm) {
        return new Instruction(MULI, rd, rs1, imm);
    }

    public static Instruction NO_OP() {
        return new Instruction(NO_OP, 0, 0,0);
    }
}

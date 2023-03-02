package contractgen.simple.isa;

import contractgen.Instruction;
import contractgen.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class SimpleInstruction implements Instruction {

    private final SIMPLE_TYPE type;
    private final Integer rd;
    private final Integer rs1;
    private final Integer rs2;

    public SimpleInstruction(SIMPLE_TYPE type, Integer rd, Integer rs1, Integer rs2) {
        this.type = type;
        this.rd = rd;
        this.rs1 = rs1;
        this.rs2 = rs2;
    }

    private SimpleInstruction(SimpleInstruction simpleInstruction) {
        if (simpleInstruction == null) throw new IllegalArgumentException("No instruction provided!");
        this.type = getType();
        this.rd = getRd();
        this.rs1 = getRs1();
        this.rs2 = getRs2();
    }

    public SimpleInstruction(String encoded, int base) {
        this(base == 2 ? parseBinaryString(encoded) : base == 16 ? parseHexString(encoded) : null);
    }

    public SIMPLE_TYPE getType() {
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

    @Override
    public String toBinaryEncoding() {
        return type.getOpcode() + encodeRegister(rd) + encodeRegister(rs1) + encodeRegister(rs2);
    }

    @Override
    public String toHexEncoding() {
        return StringUtils.toHexEncoding(this.toBinaryEncoding());
    }

    public static SimpleInstruction parseBinaryString(String instruction) {
        if (instruction.length() != 32) return null;
        System.out.println("Looking for " + instruction.substring(0,8));
        Set<SIMPLE_TYPE> candidates = Set.of(SIMPLE_TYPE.values());
        candidates = candidates.stream().filter(t -> t.getOpcode().equals(instruction.substring(0, 8))).collect(Collectors.toUnmodifiableSet());
        if (candidates.size() != 1)
            throw new IllegalArgumentException("Unknown instruction");
        SIMPLE_TYPE t = candidates.stream().findFirst().orElseThrow();

        int rd = Integer.parseUnsignedInt(instruction.substring(8, 16),2);
        int rs1 = Integer.parseUnsignedInt(instruction.substring(16, 24),2);
        int rs2 = Integer.parseUnsignedInt(instruction.substring(24, 32),2);

        return new SimpleInstruction(t, rd, rs1, rs2);
    }

    public static SimpleInstruction parseHexString(String instruction) {
        if (instruction.length() == 10 && instruction.startsWith("0x"))
            instruction = instruction.substring(2);
        if (instruction.length() != 8) return null;
        return SimpleInstruction.parseBinaryString(StringUtils.toBinaryEncoding(instruction));
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

    public static SimpleInstruction ADD(int rd, int rs1, int rs2) {
        return new SimpleInstruction(SIMPLE_TYPE.ADD, rd, rs1, rs2);
    }

    public static SimpleInstruction ADDI(int rd, int rs1, int imm) {
        return new SimpleInstruction(SIMPLE_TYPE.ADDI, rd, rs1, imm);
    }

    public static SimpleInstruction MUL(int rd, int rs1, int rs2) {
        return new SimpleInstruction(SIMPLE_TYPE.MUL, rd, rs1, rs2);
    }

    public static SimpleInstruction MULI(int rd, int rs1, int imm) {
        return new SimpleInstruction(SIMPLE_TYPE.MULI, rd, rs1, imm);
    }

    public static SimpleInstruction NO_OP() {
        return new SimpleInstruction(SIMPLE_TYPE.NO_OP, 0, 0,0);
    }
}

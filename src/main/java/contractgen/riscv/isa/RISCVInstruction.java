package contractgen.riscv.isa;

import contractgen.Instruction;
import contractgen.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A RISC-V instruction.
 *
 * @param type  The type of the instruction.
 * @param rd    The destination register.
 * @param rs1   The first operand.
 * @param rs2   The second operand.
 * @param imm   The immediate value.
 */
@SuppressWarnings("MissingJavadoc")
public record RISCVInstruction(RISCV_TYPE type, Integer rd, Integer rs1, Integer rs2, Long imm) implements Instruction {

    /**
     * @param rd The new destination register.
     * @return   A clone of the instruction with the given destination register.
     */
    public RISCVInstruction cloneAlteringRD(Integer rd) {
        return new RISCVInstruction(this.type, rd, this.rs1, this.rs2, this.imm);
    }

    /**
     * @param rs1 The new first operand.
     * @return    A clone of the instruction with the given first operand.
     */
    public RISCVInstruction cloneAlteringRS1(Integer rs1) {
        return new RISCVInstruction(this.type, this.rd, rs1, this.rs2, this.imm);
    }

    /**
     * @param rs2 The new second operand.
     * @return    A clone of the instruction with the given second opeand.
     */
    public RISCVInstruction cloneAlteringRS2(Integer rs2) {
        return new RISCVInstruction(this.type, this.rd, this.rs1, rs2, this.imm);
    }

    /**
     * @param imm The new immediate value.
     * @return    A clone of the instruction with the given immediate value.
     */
    public RISCVInstruction cloneAlteringIMM(Long imm) {
        return new RISCVInstruction(this.type, this.rd, this.rs1, this.rs2, imm);
    }

    @Override
    public String toBinaryEncoding() {
        return switch (type.getFormat()) {
            case RTYPE -> type.getFunct7() + encodeRegister(rs2) + encodeRegister(rs1) + type.getFunct3() + encodeRegister(rd) + type.getOpcode();
            case ITYPE -> encodeImmediate(imm, type.getFormat()) + encodeRegister(rs1) + type.getFunct3() + encodeRegister(rd) + type.getOpcode();
            case STYPE, BTYPE -> encodeImmediate(imm, type.getFormat()).substring(0, 7) + encodeRegister(rs2) + encodeRegister(rs1) + type.getFunct3() + encodeImmediate(imm, type.getFormat()).substring(7, 12) + type.getOpcode();
            case UTYPE, JTYPE -> encodeImmediate(imm, type.getFormat()) + encodeRegister(rd) + type.getOpcode();
        };
    }

    @Override
    public String toHexEncoding() {
        return StringUtils.toHexEncoding(this.toBinaryEncoding());
    }

    /**
     * @param instr The instruction encoded in binary
     * @return      The parsed instruction.
     */
    public static RISCVInstruction parseBinaryString(String instr) {
        if (instr.length() != 32) instr = StringUtils.expandToLength(instr, 32, '0');
        final String instruction = instr;
        Set<RISCV_TYPE> candidates = Set.of(RISCV_TYPE.values());
        candidates = candidates.stream().filter(t -> t.getOpcode().equals(instruction.substring(25))).collect(Collectors.toUnmodifiableSet());
        if (candidates.size() > 1)
            candidates = candidates.stream().filter(t -> t.getFunct3().equals(instruction.substring(17, 20))).collect(Collectors.toUnmodifiableSet());
        if (candidates.size() > 1)
            candidates = candidates.stream().filter(t -> t.getFunct7().equals(instruction.substring(0, 7))).collect(Collectors.toUnmodifiableSet());
        if (candidates.size() != 1)
            throw new IllegalArgumentException("Unknown instruction with op = " + instruction.substring(25) + "funct3 = " + instruction.substring(17, 20) + "funct7 = " + instruction.substring(0, 7));
        RISCV_TYPE t = candidates.stream().findFirst().orElseThrow();

        int rd = Integer.parseUnsignedInt(instruction.substring(20, 25), 2);
        int rs1 = Integer.parseUnsignedInt(instruction.substring(12, 17), 2);
        int rs2 = Integer.parseUnsignedInt(instruction.substring(7, 12), 2);
        long imm_i = Long.parseLong(instruction.substring(0, 12), 2);
        long imm_s = Long.parseLong(instruction.substring(0, 7) + instruction.substring(20, 25), 2);
        long imm_b = Long.parseLong(instruction.charAt(0) + instruction.substring(24, 25) + instruction.substring(1, 7) + instruction.substring(20, 24) + "0", 2);
        long imm_u = Long.parseLong(instruction.substring(0, 20) + String.format("%012d", 0), 2);
        long imm_j = Long.parseLong(instruction.charAt(0) + instruction.substring(12, 20) + instruction.charAt(11) + instruction.substring(1, 11) + "0", 2);

        return switch (t.getFormat()) {
            case RTYPE -> RISCVInstruction.RTYPE(t, rd, rs1, rs2);
            case ITYPE -> RISCVInstruction.ITYPE(t, rd, rs1, imm_i);
            case STYPE -> RISCVInstruction.STYPE(t, rs1, rs2, imm_s);
            case BTYPE -> RISCVInstruction.BTYPE(t, rs1, rs2, imm_b);
            case UTYPE -> RISCVInstruction.UTYPE(t, rd, imm_u);
            case JTYPE -> RISCVInstruction.JTYPE(t, rd, imm_j);
        };
    }

    /**
     * @param instruction The instruction encoded in hexadecimal.
     * @return            The parsed instruction.
     */
    public static RISCVInstruction parseHexString(String instruction) {
        if (instruction.length() == 10 && instruction.startsWith("0x"))
            instruction = instruction.substring(2);
        if (instruction.length() != 8) return null;
        return RISCVInstruction.parseBinaryString(StringUtils.toBinaryEncoding(instruction));
    }

    private String encodeRegister(Integer register) {
        return String.format("%5s", Integer.toBinaryString(register)).replace(' ', '0');
    }

    private String encodeImmediate(Long immediate, RISCV_FORMAT format) {
        return switch (format) {
            case RTYPE -> throw new IllegalArgumentException("RTYPE instructions have no immediate.");
            case ITYPE, STYPE -> String.format("%12s", Long.toBinaryString(immediate)).replace(' ', '0');
            case BTYPE -> {
                String s = String.format("%13s", Long.toBinaryString(immediate)).replace(' ', '0');
                yield s.charAt(0) + s.substring(2, 8) + s.substring(8, 12) + s.charAt(1);
            }
            case UTYPE -> String.format("%32s", Long.toBinaryString(immediate)).replace(' ', '0').substring(0, 20);
            case JTYPE -> {
                String s = String.format("%21s", Long.toBinaryString(immediate)).replace(' ', '0');
                yield s.charAt(0) + s.substring(10, 20) + s.charAt(9) + s.substring(1, 9);
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


    public static RISCVInstruction RTYPE(RISCV_TYPE type, Integer rd, Integer rs1, Integer rs2) {
        return new RISCVInstruction(type, rd, rs1, rs2, null);
    }

    public static RISCVInstruction ITYPE(RISCV_TYPE type, Integer rd, Integer rs1, Long imm) {
        return new RISCVInstruction(type, rd, rs1, null, imm);
    }

    public static RISCVInstruction STYPE(RISCV_TYPE type, Integer rs1, Integer rs2, Long imm) {
        return new RISCVInstruction(type, null, rs1, rs2, imm);
    }

    public static RISCVInstruction BTYPE(RISCV_TYPE type, Integer rs1, Integer rs2, Long imm) {
        return new RISCVInstruction(type, null, rs1, rs2, imm);
    }

    public static RISCVInstruction UTYPE(RISCV_TYPE type, Integer rd, Long imm) {
        return new RISCVInstruction(type, rd, null, null, imm);
    }

    public static RISCVInstruction JTYPE(RISCV_TYPE type, Integer rd, Long imm) {
        return new RISCVInstruction(type, rd, null, null, imm);
    }

    public static RISCVInstruction LUI(int rd, long imm) {
        return RISCVInstruction.UTYPE(RISCV_TYPE.LUI, rd, imm);
    }

    public static RISCVInstruction AUIPC(int rd, long imm) {
        return RISCVInstruction.UTYPE(RISCV_TYPE.AUIPC, rd, imm);
    }

    public static RISCVInstruction JAL(int rd, long imm) {
        return RISCVInstruction.JTYPE(RISCV_TYPE.JAL, rd, imm);
    }

    public static RISCVInstruction JALR(int rd, int rs1, long imm) {
        return RISCVInstruction.ITYPE(RISCV_TYPE.JALR, rd, rs1, imm);
    }

    public static RISCVInstruction BEQ(int rs1, int rs2, long imm) {
        return RISCVInstruction.BTYPE(RISCV_TYPE.BEQ, rs1, rs2, imm);
    }

    public static RISCVInstruction BNE(int rs1, int rs2, long imm) {
        return RISCVInstruction.BTYPE(RISCV_TYPE.BNE, rs1, rs2, imm);
    }

    public static RISCVInstruction BLT(int rs1, int rs2, long imm) {
        return RISCVInstruction.BTYPE(RISCV_TYPE.BLT, rs1, rs2, imm);
    }

    public static RISCVInstruction BGE(int rs1, int rs2, long imm) {
        return RISCVInstruction.BTYPE(RISCV_TYPE.BGE, rs1, rs2, imm);
    }

    public static RISCVInstruction BLTU(int rs1, int rs2, long imm) {
        return RISCVInstruction.BTYPE(RISCV_TYPE.BLTU, rs1, rs2, imm);
    }

    public static RISCVInstruction BGEU(int rs1, int rs2, long imm) {
        return RISCVInstruction.BTYPE(RISCV_TYPE.BGEU, rs1, rs2, imm);
    }

    public static RISCVInstruction LB(int rd, int rs1, long imm) {
        return RISCVInstruction.ITYPE(RISCV_TYPE.LB, rd, rs1, imm);
    }

    public static RISCVInstruction LH(int rd, int rs1, long imm) {
        return RISCVInstruction.ITYPE(RISCV_TYPE.LH, rd, rs1, imm);
    }

    public static RISCVInstruction LW(int rd, int rs1, long imm) {
        return RISCVInstruction.ITYPE(RISCV_TYPE.LW, rd, rs1, imm);
    }

    public static RISCVInstruction LBU(int rd, int rs1, long imm) {
        return RISCVInstruction.ITYPE(RISCV_TYPE.LBU, rd, rs1, imm);
    }

    public static RISCVInstruction LHU(int rd, int rs1, long imm) {
        return RISCVInstruction.ITYPE(RISCV_TYPE.LHU, rd, rs1, imm);
    }

    public static RISCVInstruction SB(int rs1, int rs2, long imm) {
        return RISCVInstruction.STYPE(RISCV_TYPE.SB, rs1, rs2, imm);
    }

    public static RISCVInstruction SH(int rs1, int rs2, long imm) {
        return RISCVInstruction.STYPE(RISCV_TYPE.SH, rs1, rs2, imm);
    }

    public static RISCVInstruction SW(int rs1, int rs2, long imm) {
        return RISCVInstruction.STYPE(RISCV_TYPE.SW, rs1, rs2, imm);
    }

    public static RISCVInstruction ADDI(int rd, int rs1, long imm) {
        return RISCVInstruction.ITYPE(RISCV_TYPE.ADDI, rd, rs1, imm);
    }

    public static RISCVInstruction SLTI(int rd, int rs1, long imm) {
        return RISCVInstruction.ITYPE(RISCV_TYPE.SLTI, rd, rs1, imm);
    }

    public static RISCVInstruction SLTIU(int rd, int rs1, long imm) {
        return RISCVInstruction.ITYPE(RISCV_TYPE.SLTIU, rd, rs1, imm);
    }

    public static RISCVInstruction XORI(int rd, int rs1, long imm) {
        return RISCVInstruction.ITYPE(RISCV_TYPE.XORI, rd, rs1, imm);
    }

    public static RISCVInstruction ORI(int rd, int rs1, long imm) {
        return RISCVInstruction.ITYPE(RISCV_TYPE.ORI, rd, rs1, imm);
    }

    public static RISCVInstruction ANDI(int rd, int rs1, long imm) {
        return RISCVInstruction.ITYPE(RISCV_TYPE.ANDI, rd, rs1, imm);
    }

    public static RISCVInstruction SLLI(int rd, int rs1, int shamt) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.SLLI, rd, rs1, shamt);
    }

    public static RISCVInstruction SRLI(int rd, int rs1, int shamt) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.SRLI, rd, rs1, shamt);
    }

    public static RISCVInstruction SRAI(int rd, int rs1, int shamt) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.SRAI, rd, rs1, shamt);
    }

    public static RISCVInstruction ADD(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.ADD, rd, rs1, rs2);
    }

    public static RISCVInstruction SUB(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.SUB, rd, rs1, rs2);
    }

    public static RISCVInstruction SLL(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.SLL, rd, rs1, rs2);
    }

    public static RISCVInstruction SLT(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.SLT, rd, rs1, rs2);
    }

    public static RISCVInstruction SLTU(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.SLTU, rd, rs1, rs2);
    }

    public static RISCVInstruction XOR(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.XOR, rd, rs1, rs2);
    }

    public static RISCVInstruction SRL(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.SRL, rd, rs1, rs2);
    }

    public static RISCVInstruction SRA(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.SRA, rd, rs1, rs2);
    }

    public static RISCVInstruction OR(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.OR, rd, rs1, rs2);
    }

    public static RISCVInstruction AND(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.AND, rd, rs1, rs2);
    }

    public static RISCVInstruction MUL(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.MUL, rd, rs1, rs2);
    }

    public static RISCVInstruction MULH(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.MULH, rd, rs1, rs2);
    }

    public static RISCVInstruction MULHSU(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.MULHSU, rd, rs1, rs2);
    }

    public static RISCVInstruction MULHU(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.MULHU, rd, rs1, rs2);
    }

    public static RISCVInstruction DIV(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.DIV, rd, rs1, rs2);
    }

    public static RISCVInstruction DIVU(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.DIVU, rd, rs1, rs2);
    }

    public static RISCVInstruction REM(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.REM, rd, rs1, rs2);
    }

    public static RISCVInstruction REMU(int rd, int rs1, int rs2) {
        return RISCVInstruction.RTYPE(RISCV_TYPE.REMU, rd, rs1, rs2);
    }

    public static RISCVInstruction NOP() {
        return RISCVInstruction.ITYPE(RISCV_TYPE.ADDI, 0, 0, 0L);
    }
}

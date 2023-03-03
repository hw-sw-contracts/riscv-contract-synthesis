package contractgen.riscv.isa.tests;

import contractgen.TestCase;
import contractgen.riscv.isa.*;
import contractgen.riscv.isa.contract.RISCVTestResult;
import contractgen.riscv.isa.contract.RISCVObservation;
import contractgen.riscv.isa.contract.RISCV_OBSERVATION_TYPE;
import contractgen.util.Pair;
import contractgen.util.StringUtils;

import java.util.*;
import java.util.stream.Stream;

public class RISCVTestGenerator {
    private static final int NUMBER_REGISTERS = 32;
    private static final long MAX_REG = 4294967296L;
    private static final int MAX_IMM_I = 4096; // 2^12
    private static final long MAX_IMM_B = 8192; // 2^13
    private static final long MAX_IMM_U = 4294967296L; // 2^32
    private static final long MAX_IMM_J = 2097152; // 2^21

    private final Random r;
    private final int repetitions;
    private final List<RISCV_TYPE> types;

    RISCVTestGenerator(Set<RISCV_SUBSET> subsets, long seed, int repetitions) {
        r = new Random(seed);
        this.repetitions = repetitions;
        this.types = Arrays.stream(RISCV_TYPE.values()).filter(t -> subsets.contains(t.getSubset())).toList();
    }

    public List<TestCase> generate() {
        List<TestCase> cases = new ArrayList<>();
        for (int i = 0; i < repetitions; i++) {
            for (RISCV_TYPE type: types) {
                Map<Integer, Integer> registers = randomRegisters();
                List<RISCVInstruction> suffix = randomSequence(r.nextInt(5, 25));
                RISCVInstruction instruction = randomInstructionFromType(type);
                for (RISCV_OBSERVATION_TYPE observation: RISCV_OBSERVATION_TYPE.values()) {
                    Pair<List<RISCVInstruction>, List<RISCVInstruction>> prefix = alterObservation(observation, instruction);
                    if (prefix != null) {
                        cases.add(new RISCVTestCase(
                                new RISCVProgram(registers, Stream.concat(prefix.left().stream(), suffix.stream()).toList()),
                                new RISCVProgram(registers, Stream.concat(prefix.right().stream(), suffix.stream()).toList()),
                                Integer.max(prefix.left().size() + suffix.size(), prefix.right().size() + suffix.size()), new RISCVTestResult(Set.of(new RISCVObservation(type, observation)), true)));
                    }
                }
            }
            if (i % 3 == 0)
                cases.add(new RISCVTestCase(new RISCVProgram(randomRegisters(), randomSequence(r.nextInt(5, 50))), new RISCVProgram(randomRegisters(), randomSequence(r.nextInt(5, 50))), 50));
        }
        return cases;

    }

    private Map<Integer, Integer> randomRegisters() {
        Map<Integer, Integer> result = new HashMap<>(NUMBER_REGISTERS - 1);
        for (int i = 1; i < NUMBER_REGISTERS; i++) {
            if (r.nextBoolean()) {
                result.put(i, r.nextInt(MAX_IMM_I));
            } else {
                if (r.nextBoolean()) {
                    result.put(i, null);
                }
            }
        }
        return result;
    }

    private Pair<List<RISCVInstruction>, List<RISCVInstruction>> alterObservation(RISCV_OBSERVATION_TYPE type, RISCVInstruction instruction) {
        return switch (type) {
            case TYPE, OPCODE, FUNCT5, FUNCT3, MEM_ADDR, MEM_W_DATA -> null;
            case RD -> {
                if (instruction.rd() == null) yield null;
                RISCVInstruction ins1 = instruction.cloneAlteringRD(r.nextInt(1, NUMBER_REGISTERS));
                RISCVInstruction ins2 = instruction.cloneAlteringRD(r.nextInt(1, NUMBER_REGISTERS));
                yield new Pair<>(List.of(ins1), List.of(ins2));
            }
            case RS1 -> {
                if (instruction.rs1() == null) yield null;
                RISCVInstruction ins1 = instruction.cloneAlteringRS1(r.nextInt(NUMBER_REGISTERS));
                RISCVInstruction ins2 = instruction.cloneAlteringRS1(r.nextInt(NUMBER_REGISTERS));
                yield new Pair<>(List.of(ins1), List.of(ins2));
            }
            case RS2 -> {
                if (instruction.rs2() == null) yield null;
                RISCVInstruction ins1 = instruction.cloneAlteringRS2(r.nextInt(NUMBER_REGISTERS));
                RISCVInstruction ins2 = instruction.cloneAlteringRS2(r.nextInt(NUMBER_REGISTERS));
                yield new Pair<>(List.of(ins1), List.of(ins2));
            }
            case IMM -> {
                if (instruction.imm() == null) yield null;
                RISCVInstruction ins1 = instruction.cloneAlteringIMM(r.nextLong(getBound(instruction)));
                RISCVInstruction ins2 = instruction.cloneAlteringIMM(r.nextLong(getBound(instruction)));
                yield new Pair<>(List.of(ins1), List.of(ins2));
            }
            case REG_RS1 -> {
                if (instruction.rs1() == null) yield null;
                RISCVInstruction ins1 = RISCVInstruction.ADDI(instruction.rs1(), 0, r.nextLong(MAX_IMM_I));
                RISCVInstruction ins2 = RISCVInstruction.ADDI(instruction.rs1(), 0, r.nextLong(MAX_IMM_I));
                yield new Pair<>(List.of(ins1, instruction), List.of(ins2, instruction));
            }
            case REG_RS2 -> {
                if (instruction.rs2() == null) yield null;
                RISCVInstruction ins1 = RISCVInstruction.ADDI(instruction.rs2(), 0, r.nextLong(MAX_IMM_I));
                RISCVInstruction ins2 = RISCVInstruction.ADDI(instruction.rs2(), 0, r.nextLong(MAX_IMM_I));
                yield new Pair<>(List.of(ins1, instruction), List.of(ins2, instruction));
            }
            case MEM_RS1 -> {
                if (instruction.rs1() == null) yield null;
                long address = r.nextLong(MAX_IMM_I);
                RISCVInstruction val1 = RISCVInstruction.ADDI(31, 0, r.nextLong(MAX_IMM_I));
                RISCVInstruction val2 = RISCVInstruction.ADDI(30, 0, r.nextLong(MAX_IMM_I));
                RISCVInstruction instr_addr = RISCVInstruction.ADDI(instruction.rs1(), 0, address);
                RISCVInstruction ins1 = RISCVInstruction.SW(instruction.rs1(), 31, 0);
                RISCVInstruction ins2 = RISCVInstruction.SW(instruction.rs1(), 30, 0);
                yield new Pair<>(List.of(val1, val2, instr_addr, ins1, instruction), List.of(val1, val2, instr_addr, ins2, instruction));
            }
            case MEM_RS2 -> {
                if (instruction.rs2() == null) yield null;
                long address = r.nextLong(MAX_IMM_I);
                RISCVInstruction val1 = RISCVInstruction.ADDI(31, 0, r.nextLong(MAX_IMM_I));
                RISCVInstruction val2 = RISCVInstruction.ADDI(30, 0, r.nextLong(MAX_IMM_I));
                RISCVInstruction instr_addr = RISCVInstruction.ADDI(instruction.rs2(), 0, address);
                RISCVInstruction ins1 = RISCVInstruction.SW(instruction.rs2(), 31, 0);
                RISCVInstruction ins2 = RISCVInstruction.SW(instruction.rs2(), 30, 0);
                yield new Pair<>(List.of(val1, val2, instr_addr, ins1, instruction), List.of(val1, val2, instr_addr, ins2, instruction));
            }
            case REG_RD -> {
                if (instruction.rd() == null) yield null;
                RISCVInstruction ins1 = instruction.cloneAlteringRD(r.nextInt(NUMBER_REGISTERS));
                RISCVInstruction ins2 = instruction.cloneAlteringRD(r.nextInt(NUMBER_REGISTERS));
                yield new Pair<>(List.of(ins1), List.of(ins2));
            }
            case MEM_R_DATA -> {
                if (instruction.imm() == null || instruction.rs1() == null) yield null;
                long address = r.nextLong(MAX_IMM_I);
                RISCVInstruction val1 = RISCVInstruction.ADDI(31, 0, r.nextLong(MAX_IMM_I));
                RISCVInstruction val2 = RISCVInstruction.ADDI(30, 0, r.nextLong(MAX_IMM_I));
                RISCVInstruction instr_addr_1 = RISCVInstruction.ADDI(instruction.rs1(), 0, address);
                RISCVInstruction ins1 = RISCVInstruction.SW(instruction.rs1(), 31, 0);
                RISCVInstruction ins2 = RISCVInstruction.SW(instruction.rs1(), 30, 0);
                RISCVInstruction instr_addr_2 = RISCVInstruction.ADDI(instruction.rs1(), instruction.rs1(), instruction.imm());
                yield new Pair<>(List.of(val1, val2, instr_addr_1, ins1, instr_addr_2, instruction), List.of(val1, val2, instr_addr_1, ins2, instr_addr_2, instruction));
            }
        };
    }

    private static long getBound(RISCVInstruction instruction) {
        return switch (instruction.type().getFormat()) {
            case RTYPE -> 0L;
            case ITYPE, STYPE -> MAX_IMM_I;
            case BTYPE -> MAX_IMM_B;
            case UTYPE -> MAX_IMM_U;
            case JTYPE -> MAX_IMM_J;
        };
    }

    private List<RISCVInstruction> randomSequence(int size) {
        List<RISCVInstruction> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            RISCV_TYPE type = types.get(r.nextInt(types.size() - 1));
            result.add(randomInstructionFromType(type));
        }
        return result;
    }

    private RISCVInstruction randomInstructionFromType(RISCV_TYPE type) {
        return  switch (type.getFormat()) {
            case RTYPE -> RISCVInstruction.RTYPE(type, r.nextInt(1, NUMBER_REGISTERS), r.nextInt(NUMBER_REGISTERS), r.nextInt(NUMBER_REGISTERS));
            case ITYPE -> RISCVInstruction.ITYPE(type, r.nextInt(1, NUMBER_REGISTERS), r.nextInt(NUMBER_REGISTERS), r.nextLong(MAX_IMM_I));
            case STYPE -> RISCVInstruction.STYPE(type, r.nextInt(NUMBER_REGISTERS), r.nextInt(NUMBER_REGISTERS), r.nextLong(MAX_IMM_I));
            case BTYPE -> RISCVInstruction.BTYPE(type, r.nextInt(NUMBER_REGISTERS), r.nextInt(NUMBER_REGISTERS), r.nextLong(MAX_IMM_B));
            case UTYPE -> RISCVInstruction.UTYPE(type, r.nextInt(1, NUMBER_REGISTERS), r.nextLong(MAX_IMM_I - 1, MAX_IMM_U));
            case JTYPE -> RISCVInstruction.JTYPE(type, r.nextInt(1, NUMBER_REGISTERS), r.nextLong(MAX_IMM_J));
        };
    }
}

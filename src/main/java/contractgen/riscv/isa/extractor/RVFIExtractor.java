package contractgen.riscv.isa.extractor;

import contractgen.Extractor;
import contractgen.TestResult;
import contractgen.riscv.isa.RISCVInstruction;
import contractgen.riscv.isa.contract.RISCVObservation;
import contractgen.riscv.isa.contract.RISCVTestResult;
import contractgen.riscv.isa.contract.RISCV_OBSERVATION_TYPE;
import contractgen.util.Pair;
import contractgen.util.StringUtils;
import contractgen.util.vcd.Module;
import contractgen.util.vcd.VcdFile;
import contractgen.util.vcd.Wire;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Extracts possible contract observations from the RVFI intere.
 */
public class RVFIExtractor implements Extractor {
    @Override
    public Pair<TestResult, TestResult> extractResults(String PATH, boolean adversaryDistinguishable, int index) {
        VcdFile vcd;
        try {
            vcd = new VcdFile(Files.readString(Path.of(PATH + "sim.vcd")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Set<RISCVObservation> obs1 = new HashSet<>();
        Set<RISCVObservation> obs2 = new HashSet<>();
        Wire retire_count = vcd.getTop().getChild("control").getWire("retire_count");
        int currentCount = Integer.parseInt(retire_count.getValueAt(retire_count.getLastChangeTime()), 2);
        while (currentCount > 0) {
            Integer retire_time = retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentCount));

            if (!compareInstructions(vcd, retire_time, obs1, obs2)) {
                // invalid instruction
                currentCount--;
                continue;
            }
            RISCVInstruction instr_1 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("ctr").getWire("instr_1_i").getValueAt(retire_time));
            RISCVInstruction instr_2 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("ctr").getWire("instr_2_i").getValueAt(retire_time));

            compareRegisters(vcd, retire_time, instr_1, instr_2, obs1, obs2);
            compareMemory(vcd, retire_time, instr_1, instr_2, obs1, obs2);
            compareBranch(vcd, retire_time, instr_1, instr_2, obs1, obs2);

            for (int distance = 1; distance <= 4; distance++) {
                compareDependencies(vcd, retire_count, currentCount, distance, instr_1, instr_2, obs1, obs2);
            }
            currentCount--;
        }
        return new Pair<>(new RISCVTestResult(obs1, adversaryDistinguishable, index * 2), new RISCVTestResult(obs2, adversaryDistinguishable, (index * 2) + 1));
    }

    /**
     * @param vcd          The VCD file.
     * @param retire_count the wire counting retirements.
     * @param currentCount the instruction currently being analyzed.
     * @param distance     the distance currently under inspection.
     * @param instr_1      the first instruction.
     * @param instr_2      the second instruction.
     * @param obs1         the current set of observations for execution one.
     * @param obs2         the current set of observations for execution two.
     */
    private void compareDependencies(VcdFile vcd, Wire retire_count, Integer currentCount, int distance, RISCVInstruction instr_1, RISCVInstruction instr_2, Set<RISCVObservation> obs1, Set<RISCVObservation> obs2) {
        try {
            // TODO when applicable
            Integer previous_retire_time = retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentCount - distance));
            RISCVInstruction previous_instr_1 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("ctr").getWire("instr_1_i").getValueAt(previous_retire_time));
            RISCVInstruction previous_instr_2 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("ctr").getWire("instr_2_i").getValueAt(previous_retire_time));

            if ((instr_1.hasRS1() && instr_2.hasRS1()) && (previous_instr_1.hasRD() && previous_instr_2.hasRD()) && Objects.equals(instr_1.rs1(), previous_instr_1.rd()) && !Objects.equals(instr_2.rs1(), previous_instr_2.rd())) {
                obs1.add(new RISCVObservation(previous_instr_1.type(), getDependencyObservationType(DEPENDENCY.RAW_RS1, distance)));
                obs2.add(new RISCVObservation(previous_instr_1.type(), getDependencyObservationType(DEPENDENCY.RAW_RS1, distance)));
            }
            if ((instr_1.hasRS1() && instr_2.hasRS1()) && (previous_instr_1.hasRD() && previous_instr_2.hasRD()) && !Objects.equals(instr_1.rs1(), previous_instr_1.rd()) && Objects.equals(instr_2.rs1(), previous_instr_2.rd())) {
                obs1.add(new RISCVObservation(previous_instr_2.type(), getDependencyObservationType(DEPENDENCY.RAW_RS1, distance)));
                obs2.add(new RISCVObservation(previous_instr_2.type(), getDependencyObservationType(DEPENDENCY.RAW_RS1, distance)));
            }
            if ((instr_1.hasRS2() && instr_2.hasRS2()) && (previous_instr_1.hasRD() && previous_instr_2.hasRD()) && Objects.equals(instr_1.rs2(), previous_instr_1.rd()) && !Objects.equals(instr_2.rs2(), previous_instr_2.rd())) {
                obs1.add(new RISCVObservation(previous_instr_1.type(), getDependencyObservationType(DEPENDENCY.RAW_RS2, distance)));
                obs2.add(new RISCVObservation(previous_instr_1.type(), getDependencyObservationType(DEPENDENCY.RAW_RS2, distance)));
            }
            if ((instr_1.hasRS2() && instr_2.hasRS2()) && (previous_instr_1.hasRD() && previous_instr_2.hasRD()) && !Objects.equals(instr_1.rs2(), previous_instr_1.rd()) && Objects.equals(instr_2.rs2(), previous_instr_2.rd())) {
                obs1.add(new RISCVObservation(previous_instr_2.type(), getDependencyObservationType(DEPENDENCY.RAW_RS2, distance)));
                obs2.add(new RISCVObservation(previous_instr_2.type(), getDependencyObservationType(DEPENDENCY.RAW_RS2, distance)));
            }
            if ((instr_1.hasRD() && instr_2.hasRD()) && (previous_instr_1.hasRD() && previous_instr_2.hasRD()) && Objects.equals(instr_1.rd(), previous_instr_1.rd()) && !Objects.equals(instr_2.rd(), previous_instr_2.rd())) {
                obs1.add(new RISCVObservation(previous_instr_1.type(), getDependencyObservationType(DEPENDENCY.WAW, distance)));
                obs2.add(new RISCVObservation(previous_instr_1.type(), getDependencyObservationType(DEPENDENCY.WAW, distance)));
            }
            if ((instr_1.hasRD() && instr_2.hasRD()) && (previous_instr_1.hasRD() && previous_instr_2.hasRD()) && !Objects.equals(instr_1.rd(), previous_instr_1.rd()) && Objects.equals(instr_2.rd(), previous_instr_2.rd())) {
                obs1.add(new RISCVObservation(previous_instr_2.type(), getDependencyObservationType(DEPENDENCY.WAW, distance)));
                obs2.add(new RISCVObservation(previous_instr_2.type(), getDependencyObservationType(DEPENDENCY.WAW, distance)));
            }
        } catch (Exception ignored) {

        }
    }

    /**
     * @param vcd         the VCD file.
     * @param retire_time the retire time of the given instructions.
     * @param instr_1     the first instruction.
     * @param instr_2     the second instruction.
     * @param obs1        the current set of observations for execution one.
     * @param obs2        the current set of observations for execution two.
     */
    private void compareBranch(VcdFile vcd, Integer retire_time, RISCVInstruction instr_1, RISCVInstruction instr_2, Set<RISCVObservation> obs1, Set<RISCVObservation> obs2) {
        Module ctr = vcd.getTop().getChild("ctr");
        String is_branch_1 = ctr.getWire("is_branch_1").getValueAt(retire_time);
        String is_branch_2 = ctr.getWire("is_branch_2").getValueAt(retire_time);
        String branch_taken_1 = ctr.getWire("branch_taken_1").getValueAt(retire_time);
        String branch_taken_2 = ctr.getWire("branch_taken_2").getValueAt(retire_time);
        String new_pc_1 = ctr.getWire("new_pc_1").getValueAt(retire_time);
        String new_pc_2 = ctr.getWire("new_pc_2").getValueAt(retire_time);

        if ((instr_1.isCONTROL() && instr_2.isCONTROL()) && !Objects.equals(is_branch_1, is_branch_2)) {
            obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.IS_BRANCH));
            obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.IS_BRANCH));
        }
        if ((instr_1.isCONTROL() && instr_2.isCONTROL()) && !Objects.equals(branch_taken_1, branch_taken_2)) {
            obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.BRANCH_TAKEN));
            obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.BRANCH_TAKEN));
        }
        if ((instr_1.isCONTROL() && instr_2.isCONTROL()) && !Objects.equals(new_pc_1, new_pc_2)) {
            obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.NEW_PC));
            obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.NEW_PC));
        }
    }

    /**
     * @param vcd         the VCD file.
     * @param retire_time the retire time of the given instructions.
     * @param instr_1     the first instruction.
     * @param instr_2     the second instruction.
     * @param obs1        the current set of observations for execution one.
     * @param obs2        the current set of observations for execution two.
     */
    private void compareMemory(VcdFile vcd, Integer retire_time, RISCVInstruction instr_1, RISCVInstruction instr_2, Set<RISCVObservation> obs1, Set<RISCVObservation> obs2) {
        Module ctr = vcd.getTop().getChild("ctr");
        String mem_addr_1 = ctr.getWire("mem_addr_1").getValueAt(retire_time);
        String mem_addr_2 = ctr.getWire("mem_addr_2").getValueAt(retire_time);
        String mem_r_data_1 = ctr.getWire("mem_r_data_1").getValueAt(retire_time);
        String mem_r_data_2 = ctr.getWire("mem_r_data_2").getValueAt(retire_time);
        String mem_w_data_1 = ctr.getWire("mem_w_data_1").getValueAt(retire_time);
        String mem_w_data_2 = ctr.getWire("mem_w_data_2").getValueAt(retire_time);
        String is_aligned_1 = ctr.getWire("is_aligned_1").getValueAt(retire_time);
        String is_aligned_2 = ctr.getWire("is_aligned_2").getValueAt(retire_time);
        String is_half_aligned_1 = ctr.getWire("is_half_aligned_1").getValueAt(retire_time);
        String is_half_aligned_2 = ctr.getWire("is_half_aligned_2").getValueAt(retire_time);

        if ((instr_1.isMEM() && instr_2.isMEM()) && !Objects.equals(mem_addr_1, mem_addr_2)) {
            obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.MEM_ADDR));
            obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.MEM_ADDR));
        }
        if ((instr_1.isLOAD() && instr_2.isLOAD()) && !Objects.equals(mem_r_data_1, mem_r_data_2)) {
            obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.MEM_R_DATA));
            obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.MEM_R_DATA));
        }
        if ((instr_1.isSTORE() && instr_2.isSTORE()) && !Objects.equals(mem_w_data_1, mem_w_data_2)) {
            obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.MEM_W_DATA));
            obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.MEM_W_DATA));
        }
        if ((instr_1.isMEM() && instr_2.isMEM()) && !Objects.equals(is_aligned_1, is_aligned_2)) {
            obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.IS_ALIGNED));
            obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.IS_ALIGNED));
        }
        if ((instr_1.isMEM() && instr_2.isMEM()) && !Objects.equals(is_half_aligned_1, is_half_aligned_2)) {
            obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.IS_HALF_ALIGNED));
            obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.IS_HALF_ALIGNED));
        }
    }

    /**
     * @param vcd         the VCD file.
     * @param retire_time the retire time of the given instructions.
     * @param instr_1     the first instruction.
     * @param instr_2     the second instruction.
     * @param obs1        the current set of observations for execution one.
     * @param obs2        the current set of observations for execution two.
     */
    private void compareRegisters(VcdFile vcd, Integer retire_time, RISCVInstruction instr_1, RISCVInstruction instr_2, Set<RISCVObservation> obs1, Set<RISCVObservation> obs2) {
        Module ctr = vcd.getTop().getChild("ctr");
        String reg_rs1_1 = ctr.getWire("reg_rs1_1").getValueAt(retire_time);
        String reg_rs1_2 = ctr.getWire("reg_rs1_2").getValueAt(retire_time);
        String reg_rs2_1 = ctr.getWire("reg_rs2_1").getValueAt(retire_time);
        String reg_rs2_2 = ctr.getWire("reg_rs2_2").getValueAt(retire_time);
        String reg_rd_1 = ctr.getWire("reg_rd_1").getValueAt(retire_time);
        String reg_rd_2 = ctr.getWire("reg_rd_2").getValueAt(retire_time);

        if ((instr_1.hasRS1() && instr_2.hasRS1()) && !Objects.equals(reg_rs1_1, reg_rs1_2)) {
            obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.REG_RS1));
            obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.REG_RS1));
        }
        if ((instr_1.hasRS2() && instr_2.hasRS2()) && !Objects.equals(reg_rs2_1, reg_rs2_2)) {
            obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.REG_RS2));
            obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.REG_RS2));
        }
        if ((instr_1.hasRD() && instr_2.hasRD()) && !Objects.equals(reg_rd_1, reg_rd_2)) {
            obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.REG_RD));
            obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.REG_RD));
        }
    }

    /**
     * @param vcd         the VCD file.
     * @param retire_time the retire time of the given instructions.
     * @param obs1        the current set of observations for execution one.
     * @param obs2        the current set of observations for execution two.
     * @return whether any error occurred.
     */
    private boolean compareInstructions(VcdFile vcd, Integer retire_time, Set<RISCVObservation> obs1, Set<RISCVObservation> obs2) {
        RISCVInstruction instr_1;
        RISCVInstruction instr_2;
        try {
            instr_1 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("ctr").getWire("instr_1_i").getValueAt(retire_time));
            instr_2 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("ctr").getWire("instr_2_i").getValueAt(retire_time));

            if (!Objects.equals(instr_1.type(), instr_2.type())) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.OPCODE));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.OPCODE));
            }

            if ((instr_1.hasRD() && instr_2.hasRD()) && !Objects.equals(instr_1.rd(), instr_2.rd())) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.RD));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.RD));
            }
            if ((instr_1.hasRS1() && instr_2.hasRS1()) && !Objects.equals(instr_1.rs1(), instr_2.rs1())) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.RS1));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.RS1));
            }
            if ((instr_1.hasRS2() && instr_2.hasRS2()) && !Objects.equals(instr_1.rs2(), instr_2.rs2())) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.RS2));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.RS2));
            }
            if ((instr_1.hasIMM() && instr_2.hasIMM()) && !Objects.equals(instr_1.imm(), instr_2.imm())) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.IMM));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.IMM));
            }
            return true;
        } catch (Exception e) {
            // invalid instruction
            return false;
        }
    }

    /**
     * The considered types of dependencies.
     */
    private enum DEPENDENCY {
        /**
         * Read-After-Write on RS1.
         */
        RAW_RS1,
        /**
         * Read-After-Write on RS2.
         */
        RAW_RS2,
        /**
         * Write-After-Write
         */
        WAW
    }

    /**
     * @param dep      the dependency
     * @param distance the distance
     * @return the respective observation.
     */
    private RISCV_OBSERVATION_TYPE getDependencyObservationType(DEPENDENCY dep, int distance) {
        switch (dep) {
            case RAW_RS1 -> {
                if (distance == 1) return RISCV_OBSERVATION_TYPE.RAW_RS1_1;
                if (distance == 2) return RISCV_OBSERVATION_TYPE.RAW_RS1_2;
                if (distance == 3) return RISCV_OBSERVATION_TYPE.RAW_RS1_3;
                if (distance == 4) return RISCV_OBSERVATION_TYPE.RAW_RS1_4;
            }
            case RAW_RS2 -> {
                if (distance == 1) return RISCV_OBSERVATION_TYPE.RAW_RS2_1;
                if (distance == 2) return RISCV_OBSERVATION_TYPE.RAW_RS2_2;
                if (distance == 3) return RISCV_OBSERVATION_TYPE.RAW_RS2_3;
                if (distance == 4) return RISCV_OBSERVATION_TYPE.RAW_RS2_4;
            }
            case WAW -> {
                if (distance == 1) return RISCV_OBSERVATION_TYPE.WAW_1;
                if (distance == 2) return RISCV_OBSERVATION_TYPE.WAW_2;
                if (distance == 3) return RISCV_OBSERVATION_TYPE.WAW_3;
                if (distance == 4) return RISCV_OBSERVATION_TYPE.WAW_4;
            }
        }
        return null;
    }
}

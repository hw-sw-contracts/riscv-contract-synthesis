package contractgen.riscv.ibex;

import contractgen.*;
import contractgen.riscv.isa.RISCV;
import contractgen.riscv.isa.RISCVInstruction;
import contractgen.riscv.isa.RISCV_TYPE;
import contractgen.riscv.isa.contract.RISCVTestResult;
import contractgen.riscv.isa.contract.RISCVObservation;
import contractgen.riscv.isa.contract.RISCV_OBSERVATION_TYPE;
import contractgen.util.Pair;
import contractgen.util.StringUtils;
import contractgen.util.vcd.Module;
import contractgen.util.vcd.VcdFile;
import contractgen.util.vcd.Wire;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static contractgen.util.FileUtils.copyFileOrFolder;
import static contractgen.util.FileUtils.replaceString;
import static contractgen.util.ScriptUtils.runScript;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * The Ibex microarchitecture.
 */
public class
IBEX extends MARCH {

    private static final String TEMPLATE_PATH = "/home/yosys/resources/ibex/";
    protected String BASE_PATH = "/home/yosys/output/ibex/generated/";
    protected String COMPILATION_PATH = "/home/yosys/output/ibex/compiled/";
    protected String SIMULATION_PATH = "/home/yosys/output/ibex/simulation/";

    /**
     * @param updater   The updater to be used to update the contract.
     * @param testCases The test cases to be used for generation or evaluation.
     */
    public IBEX(Updater updater, TestCases testCases) {
        super(new RISCV(updater, testCases));
    }

    @Override
    public void generateSources(TestCase testCase, Integer max_count) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String runCover(int steps) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int extractSteps(String coverTrace) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean run(int steps) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Pair<TestResult, TestResult> extractCTX(TestCase testCase) {
        return extractCTX(SIMULATION_PATH, testCase);
    }

    @Override
    public Pair<TestResult, TestResult> extractCTX(int id, TestCase testCase) {
        return extractCTX(SIMULATION_PATH + id + "/", testCase);
    }

    private Pair<TestResult, TestResult> extractCTX(String PATH, TestCase testCase) {
        VcdFile vcd;
        try {
            vcd = new VcdFile(Files.readString(Path.of(PATH + "sim.vcd")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int failTime = vcd.getTop().getChild("atk").getWire("atk_equiv_o").getLastChangeTime();
        int fetch_1 = Integer.parseInt(vcd.getTop().getChild("control").getWire("fetch_1_count").getValueAt(failTime), 2);
        int fetch_2 = Integer.parseInt(vcd.getTop().getChild("control").getWire("fetch_2_count").getValueAt(failTime), 2);
        int retire = Integer.parseInt(vcd.getTop().getChild("control").getWire("retire_count").getValueAt(failTime), 2);
        int currentGuess = Integer.max(fetch_1, fetch_2);
        while (currentGuess >= retire && simulateSteps(PATH, currentGuess) == SIMULATION_RESULT.FAIL) {
            currentGuess--;
        }
        simulateSteps(PATH, currentGuess + 1);
        return extractDifferences(PATH, true, testCase.getIndex());
    }

    @Override
    public Pair<TestResult, TestResult> extractDifferences(int index) {
        return extractDifferences(SIMULATION_PATH, false, index);
    }

    @Override
    public Pair<TestResult, TestResult> extractDifferences(int id, int index) {
        return extractDifferences(SIMULATION_PATH + id + "/", false, index);
    }

    private Pair<TestResult, TestResult> extractDifferences(String PATH, boolean adversaryDistinguishable, int index) {
        VcdFile vcd;
        try {
            vcd = new VcdFile(Files.readString(Path.of(PATH + "sim.vcd")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Set<RISCVObservation> obs1 = new HashSet<>();
        Set<RISCVObservation> obs2 = new HashSet<>();
        Wire retire_count = vcd.getTop().getChild("control").getWire("retire_count");
        Wire fetch_1_count = vcd.getTop().getChild("control").getWire("fetch_1_count");
        Wire fetch_2_count = vcd.getTop().getChild("control").getWire("fetch_2_count");
        assert fetch_1_count != null;
        assert fetch_2_count != null;
        int currentGuess = Integer.parseInt(retire_count.getValueAt(retire_count.getLastChangeTime()), 2);
        while (currentGuess > 0) {
            Integer retire_time = retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess));
            RISCVInstruction instr_1;
            RISCVInstruction instr_2;
            try {
                instr_1 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("ctr").getWire("instr_1_i").getValueAt(retire_time));
                instr_2 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("ctr").getWire("instr_2_i").getValueAt(retire_time));
            } catch (Exception e) {
                // Might be an unaligned jump -> invalid instruction
                currentGuess--;
                continue;
            }

            Module ctr = vcd.getTop().getChild("ctr");
            String reg_rs1_1 = ctr.getWire("reg_rs1_1").getValueAt(retire_time);
            String reg_rs1_2 = ctr.getWire("reg_rs1_2").getValueAt(retire_time);
            String reg_rs2_1 = ctr.getWire("reg_rs2_1").getValueAt(retire_time);
            String reg_rs2_2 = ctr.getWire("reg_rs2_2").getValueAt(retire_time);
            String reg_rd_1 = ctr.getWire("reg_rd_1").getValueAt(retire_time);
            String reg_rd_2 = ctr.getWire("reg_rd_2").getValueAt(retire_time);

            String mem_addr_1 = ctr.getWire("mem_addr_1").getValueAt(retire_time);
            String mem_addr_2 = ctr.getWire("mem_addr_2").getValueAt(retire_time);
            String mem_r_data_1 = ctr.getWire("mem_r_data_1").getValueAt(retire_time);
            String mem_r_data_2 = ctr.getWire("mem_r_data_2").getValueAt(retire_time);
            String mem_w_data_1 = ctr.getWire("mem_w_data_1").getValueAt(retire_time);
            String mem_w_data_2 = ctr.getWire("mem_w_data_2").getValueAt(retire_time);


            if (!Objects.equals(instr_1.type(), instr_2.type())) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.OPCODE));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.OPCODE));
            }

            if (!Objects.equals(instr_1.rd(), instr_2.rd())) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.RD));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.RD));
            }
            if (!Objects.equals(instr_1.rs1(), instr_2.rs1())) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.RS1));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.RS1));
            }
            if (!Objects.equals(instr_1.rs2(), instr_2.rs2())) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.RS2));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.RS2));
            }
            if (!Objects.equals(instr_1.imm(), instr_2.imm())) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.IMM));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.IMM));
            }
            if (!Objects.equals(reg_rs1_1, reg_rs1_2)) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.REG_RS1));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.REG_RS1));
            }
            if (!Objects.equals(reg_rs2_1, reg_rs2_2)) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.REG_RS2));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.REG_RS2));
            }
            if (!Objects.equals(reg_rd_1, reg_rd_2)) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.REG_RD));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.REG_RD));
            }

            if (!Objects.equals(mem_addr_1, mem_addr_2)) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.MEM_ADDR));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.MEM_ADDR));
            }
            if (!Objects.equals(mem_r_data_1, mem_r_data_2)) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.MEM_R_DATA));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.MEM_R_DATA));
            }
            if (!Objects.equals(mem_w_data_1, mem_w_data_2)) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.MEM_W_DATA));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.MEM_W_DATA));
            }

            currentGuess--;
        }

        return new Pair<>(new RISCVTestResult(obs1, adversaryDistinguishable, index * 2), new RISCVTestResult(obs2, adversaryDistinguishable, (index * 2) + 1));
    }

    @Override
    public void compile() {
        try {
            copyFileOrFolder(Path.of(TEMPLATE_PATH).toFile(), Path.of(BASE_PATH).toFile(), REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // contract
        synchronized (getISA().getContract()) {
            replaceString(BASE_PATH + "verif/ctr.sv", "/* CONTRACT */", getISA().getContract().printContract());
        }
        runScript("/bin/bash " + BASE_PATH + "compile.sh " + BASE_PATH + " " + COMPILATION_PATH, false, 240);
        System.out.println("Compilation finished.");
    }

    @Override
    public void writeTestCase(TestCase testCase) {
        writeTestCase(SIMULATION_PATH, testCase);
    }

    @Override
    public void writeTestCase(int id, TestCase testCase) {
        writeTestCase(SIMULATION_PATH + id + "/", testCase);
    }

    private void writeTestCase(String PATH, TestCase testCase) {
        try {
            copyFileOrFolder(Path.of(COMPILATION_PATH + "ibex").toFile(), Path.of(PATH + "ibex").toFile(), REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        testCase.getProgram1().printInit(PATH + "init_1.dat");
        testCase.getProgram1().printInstr(PATH + "memory_1.dat");
        testCase.getProgram2().printInit(PATH + "init_2.dat");
        testCase.getProgram2().printInstr(PATH + "memory_2.dat");
        try {
            Files.write( Paths.get(PATH + "count.dat"), StringUtils.toHexEncoding((long) (testCase.getMaxInstructionCount() + 31)).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SIMULATION_RESULT simulate() {
        return simulate(SIMULATION_PATH);
    }

    @Override
    public SIMULATION_RESULT simulate(int id) {
        return simulate(SIMULATION_PATH + id + "/");
    }

    @Override
    public String getName() {
        return "ibex";
    }

    private SIMULATION_RESULT simulate(String PATH) {
        String output = runScript(PATH + "ibex", true, 30);
        assert output != null;
        if (output.contains("FAIL"))
            return SIMULATION_RESULT.FAIL;
        if (output.contains("FALSE_POSITIVE"))
            return SIMULATION_RESULT.FALSE_POSITIVE;
        if (output.contains("SUCCESS"))
            return SIMULATION_RESULT.SUCCESS;
        if (output.contains("TIMEOUT"))
            return SIMULATION_RESULT.TIMEOUT;
        return SIMULATION_RESULT.UNKNOWN;
    }

    private SIMULATION_RESULT simulateSteps(String PATH, int steps) {
        try {
            Files.write(Paths.get(PATH + "count.dat"), StringUtils.toHexEncoding((long) steps).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return simulate(PATH);
    }
}

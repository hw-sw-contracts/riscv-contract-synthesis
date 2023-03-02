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
import contractgen.util.vcd.VcdFile;
import contractgen.util.vcd.Wire;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static contractgen.util.FileUtils.copyFileOrFolder;
import static contractgen.util.FileUtils.replaceString;
import static contractgen.util.ScriptUtils.runScript;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class IBEX extends MARCH {

    private static final String TEMPLATE_PATH = "/home/yosys/resources/ibex/";
    protected String BASE_PATH = "/home/yosys/output/ibex/generated/";
    protected String COMPILATION_PATH = "/home/yosys/output/ibex/compiled/";
    protected String SIMULATION_PATH = "/home/yosys/output/ibex/simulation/";

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


    public Pair<TestResult, TestResult> extractCTX(String PATH, TestCase testCase) {
        // TODO
        //if (testCase.getLikelyCTX() != null) {
        //    return new Pair<>(testCase.getLikelyCTX(), testCase.getLikelyCTX());
        //}
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
        while (currentGuess >= retire && !simulateSteps(PATH, currentGuess)) {
            currentGuess--;
        }
        simulateSteps(PATH, currentGuess + 1);
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
        currentGuess = Integer.parseInt(fetch_1_count.getValueAt(fetch_1_count.getLastChangeTime()), 2);
        while (currentGuess > 0) { // && (obs1.isEmpty() || obs2.isEmpty())) {
            //System.out.println("Looking at time " + fetch_1_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)));
            //RISCVInstruction instr_1 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("ctr").getWire("instr_1_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess))));
            //RISCVInstruction instr_2 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("ctr").getWire("instr_2_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess))));
            RISCVInstruction instr_1 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("instr_mem_1").getWire("instr_o").getValueAt(fetch_1_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess))));
            RISCVInstruction instr_2 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("instr_mem_2").getWire("instr_o").getValueAt(fetch_2_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess))));
            assert instr_1 != null;
            //System.out.println(instr_1);
            assert instr_2 != null;
            //System.out.println(instr_2);
            String regfile_1 = vcd.getTop().getChild("ctr").getWire("regfile_1_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)) - 20);
            String regfile_2 = vcd.getTop().getChild("ctr").getWire("regfile_2_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)) - 20);
            assert regfile_1 != null;
            assert regfile_2 != null;
            String mem_addr_1 = vcd.getTop().getChild("ctr").getWire("mem_addr_1_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)) - 20);
            String mem_addr_2 = vcd.getTop().getChild("ctr").getWire("mem_addr_2_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)) - 20);
            assert mem_addr_1 != null;
            assert mem_addr_2 != null;
            String mem_data_1 = vcd.getTop().getChild("ctr").getWire("mem_data_1_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)) - 20);
            String mem_data_2 = vcd.getTop().getChild("ctr").getWire("mem_data_2_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)) - 20);
            assert mem_data_1 != null;
            assert mem_data_2 != null;

            if (instr_1.type() == RISCV_TYPE.JALR && instr_1.imm() == 3946 && instr_1.rd() == 12) {
                System.out.println("Strange case here:");
                System.out.println(getRegisterValue(regfile_1, instr_1.rs1()));
                System.out.println(getRegisterValue(regfile_2, instr_2.rs1()));
                System.out.println("Value -20 was " + (retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)) - 20));
                System.out.println(regfile_1);
                System.out.println(regfile_2);
            }


            if (!Objects.equals(instr_1.type(), instr_2.type())) {
                //throw new IllegalStateException("Why?" + instr_1 + instr_2);
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
            if (!Objects.equals(getRegisterValue(regfile_1, instr_1.rs1()), getRegisterValue(regfile_2, instr_2.rs1()))) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.REG_RS1));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.REG_RS1));
            }
            if (!Objects.equals(getRegisterValue(regfile_1, instr_1.rs2()), getRegisterValue(regfile_2, instr_2.rs2()))) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.REG_RS2));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.REG_RS2));
            }
            if (!Objects.equals(getMemoryValue(mem_addr_1, mem_data_1, getRegisterValue(regfile_1, instr_1.rs1())), getMemoryValue(mem_addr_2, mem_data_2, getRegisterValue(regfile_2, instr_2.rs1())))) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.MEM_RS1));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.MEM_RS1));
            }
            if (!Objects.equals(getMemoryValue(mem_addr_1, mem_data_1, getRegisterValue(regfile_1, instr_1.rs2())), getMemoryValue(mem_addr_2, mem_data_2, getRegisterValue(regfile_2, instr_2.rs2())))) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.MEM_RS2));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.MEM_RS2));
            }

            currentGuess--;
        }

        if (obs1.isEmpty() && obs2.isEmpty()) {
            throw new IllegalStateException("Something went wrong here...");
        }

        if (obs1.size() == 1 && obs1.contains(new RISCVObservation(RISCV_TYPE.ADDI, RISCV_OBSERVATION_TYPE.IMM))) {
            System.out.println(testCase);
            System.out.println(PATH);
            throw new IllegalStateException("What is going on?!");
        }

        return new Pair<>(new RISCVTestResult(obs1, true), new RISCVTestResult(obs2, true));
    }

    @Override
    public Pair<TestResult, TestResult> extractDifferences() {
        return extractDifferences(SIMULATION_PATH);
    }

    @Override
    public Pair<TestResult, TestResult> extractDifferences(int id) {
        return extractDifferences(SIMULATION_PATH + id + "/");
    }

    public Pair<TestResult, TestResult> extractDifferences(String PATH) {
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
            //System.out.println("Looking at time " + fetch_1_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)));
            //RISCVInstruction instr_1 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("ctr").getWire("instr_1_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess))));
            //RISCVInstruction instr_2 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("ctr").getWire("instr_2_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess))));
            RISCVInstruction instr_1 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("instr_mem_1").getWire("instr_o").getValueAt(fetch_1_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess))));
            RISCVInstruction instr_2 = RISCVInstruction.parseBinaryString(vcd.getTop().getChild("instr_mem_2").getWire("instr_o").getValueAt(fetch_2_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess))));
            assert instr_1 != null;
            //System.out.println(instr_1);
            assert instr_2 != null;
            //System.out.println(instr_2);
            String regfile_1 = vcd.getTop().getChild("ctr").getWire("regfile_1_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)) - 20);
            String regfile_2 = vcd.getTop().getChild("ctr").getWire("regfile_2_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)) - 20);
            assert regfile_1 != null;
            assert regfile_2 != null;
            String mem_addr_1 = vcd.getTop().getChild("ctr").getWire("mem_addr_1_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)) - 20);
            String mem_addr_2 = vcd.getTop().getChild("ctr").getWire("mem_addr_2_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)) - 20);
            assert mem_addr_1 != null;
            assert mem_addr_2 != null;
            String mem_data_1 = vcd.getTop().getChild("ctr").getWire("mem_data_1_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)) - 20);
            String mem_data_2 = vcd.getTop().getChild("ctr").getWire("mem_data_2_i").getValueAt(retire_count.getFirstTimeValue(StringUtils.toBinaryEncoding((long) currentGuess)) - 20);
            assert mem_data_1 != null;
            assert mem_data_2 != null;


            if (!Objects.equals(instr_1.type(), instr_2.type())) {
                //throw new IllegalStateException("Why?" + instr_1 + instr_2);
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
            if (!Objects.equals(getRegisterValue(regfile_1, instr_1.rs1()), getRegisterValue(regfile_2, instr_2.rs1()))) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.REG_RS1));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.REG_RS1));
            }
            if (!Objects.equals(getRegisterValue(regfile_1, instr_1.rs2()), getRegisterValue(regfile_2, instr_2.rs2()))) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.REG_RS2));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.REG_RS2));
            }
            if (!Objects.equals(getMemoryValue(mem_addr_1, mem_data_1, getRegisterValue(regfile_1, instr_1.rs1())), getMemoryValue(mem_addr_2, mem_data_2, getRegisterValue(regfile_2, instr_2.rs1())))) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.MEM_RS1));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.MEM_RS1));
            }
            if (!Objects.equals(getMemoryValue(mem_addr_1, mem_data_1, getRegisterValue(regfile_1, instr_1.rs2())), getMemoryValue(mem_addr_2, mem_data_2, getRegisterValue(regfile_2, instr_2.rs2())))) {
                obs1.add(new RISCVObservation(instr_1.type(), RISCV_OBSERVATION_TYPE.MEM_RS2));
                obs2.add(new RISCVObservation(instr_2.type(), RISCV_OBSERVATION_TYPE.MEM_RS2));
            }

            currentGuess--;
        }

        //if (obs1.isEmpty() && obs2.isEmpty()) {
        //    throw new IllegalStateException("Something went wrong here...");
        //}

        return new Pair<>(new RISCVTestResult(obs1, false), new RISCVTestResult(obs2, false));
    }

    private Long getRegisterValue(String regfile, Integer number) {
        if (number == null || number >= 32) return null;
        regfile = StringUtils.expandToLength(regfile, 1024, '0');
        // {R31, R30, ... R2, R1, R0}
        return Long.parseLong(regfile.substring((31 - number) * 32, (31 - number + 1) * 32), 2);
    }

    private Long getMemoryValue(String memory_addresses, String memory_data, Long address) {
        if (address == null || address >= Long.parseLong("FFFFFFFF", 16)) return null;
        memory_addresses = StringUtils.expandToLength(memory_addresses, 32 * 32, '0');
        memory_data = StringUtils.expandToLength(memory_data, 8 * 32, '0');
        long result = address;
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 4; j++) {
                if (memory_addresses.substring(i * 32, (i + 1) * 32).equals(Long.toBinaryString(address + j))) {
                    result = Long.parseLong(Long.toBinaryString(result).substring(0, 8 * j) + memory_data.substring(i * 8, (i+1) * 8) + Long.toBinaryString(result).substring(8 * (j+1), 32), 2);
                }
            }
        }
        return result;
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
        runScript("/bin/bash " + BASE_PATH + "compile.sh " + BASE_PATH + " " + COMPILATION_PATH, false);
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

    public void writeTestCase(String PATH, TestCase testCase) {
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
    public boolean simulate() {
        return simulate(SIMULATION_PATH);
    }

    @Override
    public boolean simulate(int id) {
        return simulate(SIMULATION_PATH + id + "/");
    }

    @Override
    public String getName() {
        return "ibex";
    }

    public boolean simulate(String PATH) {
        String output = runScript(PATH + "ibex", true);
        assert output != null;
        return !output.contains("FAIL");
    }

    public boolean simulateSteps(String PATH, int steps) {
        try {
            Files.write(Paths.get(PATH + "count.dat"), StringUtils.toHexEncoding((long) steps).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return simulate(PATH);
    }
}

package contractgen.simple.march.singleycycle;

import contractgen.*;
import contractgen.simple.isa.SimpleISA;
import contractgen.simple.isa.SimpleInstruction;
import contractgen.simple.isa.contract.SIMPLE_OBSERVATION_TYPE;
import contractgen.simple.isa.contract.SimpleTestResult;
import contractgen.simple.isa.contract.SimpleObservation;
import contractgen.util.Pair;
import contractgen.util.vcd.VcdFile;
import contractgen.util.vcd.Wire;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static contractgen.util.FileUtils.copyFileOrFolder;
import static contractgen.util.FileUtils.replaceString;
import static contractgen.util.ScriptUtils.runScript;

/**
 * A simple toy processor
 */
public class SimpleMARCH extends MARCH {

    private static final String TEMPLATE_PATH = "/home/yosys/resources/simple/";
    protected String BASE_PATH = "/home/yosys/output/simple/generated/";
    protected String ADDITIONAL_DEFINITIONS = "";

    /**
     * @param updater   The updater to be used to update the contract.
     * @param testCases The test cases to be used for generation or evaluation.
     */
    public SimpleMARCH(Updater updater, TestCases testCases) {
        super(new SimpleISA(updater, testCases));
    }

    @Override
    public void generateSources(TestCase testCase, Integer max_count) {
        //copy source to target using Files Class
        try {
            copyFileOrFolder(Path.of(TEMPLATE_PATH).toFile(), Path.of(BASE_PATH).toFile(), REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // contract
        synchronized (getISA().getContract()) {
            replaceString(BASE_PATH + "rtl/ctr.sv", "/* CONTRACT */", getISA().getContract().printContract());
        }

        // instruction memory
        replaceString(BASE_PATH + "rtl/instr_mem_1.sv", "/* Instruction Memory 1 - Variables */", testCase.getProgram1().printSymbolic());
        replaceString(BASE_PATH + "rtl/instr_mem_1.sv", "/* Instruction Memory 1 - Instructions */", testCase.getProgram1().printProgram(Integer.parseInt("10", 16)));
        replaceString(BASE_PATH + "rtl/instr_mem_2.sv", "/* Instruction Memory 2 - Variables */", testCase.getProgram2().printSymbolic());
        replaceString(BASE_PATH + "rtl/instr_mem_2.sv", "/* Instruction Memory 2 - Instructions */", testCase.getProgram2().printProgram(Integer.parseInt("10", 16)));

        //control module
        replaceString(BASE_PATH + "rtl/control.sv", "/* Max Instruction Count */", String.valueOf(max_count != null ? max_count : testCase.getMaxInstructionCount()));
        replaceString(BASE_PATH + "rtl/control.sv", "/* Max Instruction Address 1 */", "16'h" + Integer.toHexString(testCase.getProgram1().maxAddress(Integer.parseInt("10", 16))));
        replaceString(BASE_PATH + "rtl/control.sv", "/* Max Instruction Address 2 */", "16'h" + Integer.toHexString(testCase.getProgram2().maxAddress(Integer.parseInt("10", 16))));

        // formal
        replaceString(BASE_PATH + "syn/formal.sv", "/* Max Instruction Address 1 */", "16'h" + Integer.toHexString(testCase.getProgram1().maxAddress(Integer.parseInt("10", 16))));
        replaceString(BASE_PATH + "syn/formal.sv", "/* Max Instruction Address 2 */", "16'h" + Integer.toHexString(testCase.getProgram2().maxAddress(Integer.parseInt("10", 16))));
    }

    @Override
    public String runCover(int steps) {
        generateCoverSBY(steps, BASE_PATH + "syn/cover/");
        return runScript(BASE_PATH + "syn/verif.sh", false, 3600);
    }

    private void generateCoverSBY(int steps, String path) {
        try {
            copyFileOrFolder(Path.of(TEMPLATE_PATH + "syn/verif.sh").toFile(), Path.of(BASE_PATH + "syn/verif.sh").toFile(), REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        replaceString(BASE_PATH + "syn/verif_cover.sby", "/* Number Steps */", String.valueOf(steps));
        replaceString(BASE_PATH + "syn/verif.sh", "/* PATH */", BASE_PATH + "syn");
        replaceString(BASE_PATH + "syn/verif.sh", "/* Output Directory */", path);
        replaceString(BASE_PATH + "syn/verif.sh", "/* SBY File */", "verif_cover.sby");
        replaceString(BASE_PATH + "syn/verif.sh", "/* Additional Definitions */", ADDITIONAL_DEFINITIONS);
    }

    @Override
    public int extractSteps(String coverTrace) {
        String regex = ".*Checking cover reachability in step (\\d*)..";
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = p.matcher(coverTrace);
        if (!m.find()) {
            System.out.println("Something went wrong, cannot find step count.");
            throw new IllegalArgumentException("Could not extract step count.");
        }
        int steps = Integer.parseInt(m.group(1)) + 1;
        System.out.println("Step count should be " + steps);
        return steps;
    }

    @Override
    public boolean run(int steps) {
        generateSBY(steps, BASE_PATH + "syn/run/");
        runScript(BASE_PATH + "syn/verif.sh", false, 3600);
        if (!Files.exists(Path.of(BASE_PATH + "syn/run/verif/FAIL"))) {
            System.out.println("No violation.");
            return true;
        }
        return false;
    }

    protected void generateSBY(int steps, String path) {
        try {
            copyFileOrFolder(Path.of(TEMPLATE_PATH + "syn/verif.sh").toFile(), Path.of(BASE_PATH + "syn/verif.sh").toFile(), REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        replaceString(BASE_PATH + "syn/verif.sby", "/* Number Steps */", String.valueOf(steps));
        replaceString(BASE_PATH + "syn/verif.sh", "/* PATH */", BASE_PATH + "syn");
        replaceString(BASE_PATH + "syn/verif.sh", "/* Output Directory */", path);
        replaceString(BASE_PATH + "syn/verif.sh", "/* SBY File */", "verif.sby");
        replaceString(BASE_PATH + "syn/verif.sh", "/* Additional Definitions */", ADDITIONAL_DEFINITIONS);
    }

    @Override
    public Pair<TestResult, TestResult> extractCTX(TestCase testCase) {
        VcdFile ctx;
        try {
            ctx = new VcdFile(Files.readString(Path.of(BASE_PATH + "/syn/run/verif/engine_0/trace.vcd")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Pair<Pair<Instruction, Integer>, Pair<Instruction, Integer>> c = findInstructionsPipeline(ctx, contract, test);
        Pair<Pair<Instruction, Integer>, Pair<Instruction, Integer>> c = findInstructionsSequential(ctx);
        SimpleInstruction instr_1 = (SimpleInstruction) c.left().left();
        Integer fetch_1 = c.left().right();
        SimpleInstruction instr_2 = (SimpleInstruction) c.right().left();
        Integer fetch_2 = c.right().right();


        // find possible contract templates
        Set<SimpleObservation> differences_1 = new HashSet<>();
        Set<SimpleObservation> differences_2 = new HashSet<>();
        assert instr_1 != null;
        assert instr_2 != null;
        if (instr_1.getType() != instr_2.getType()) {
            differences_1.add(new SimpleObservation(instr_1.getType(), SIMPLE_OBSERVATION_TYPE.OPCODE));
            differences_2.add(new SimpleObservation(instr_2.getType(), SIMPLE_OBSERVATION_TYPE.OPCODE));
        }
        if (!Objects.equals(instr_1.getRd(), instr_2.getRd())) {
            differences_1.add(new SimpleObservation(instr_1.getType(), SIMPLE_OBSERVATION_TYPE.RD));
            differences_2.add(new SimpleObservation(instr_2.getType(), SIMPLE_OBSERVATION_TYPE.RD));
        }
        if (!Objects.equals(instr_1.getRs1(), instr_2.getRs1())) {
            differences_1.add(new SimpleObservation(instr_1.getType(), SIMPLE_OBSERVATION_TYPE.RS1));
            differences_2.add(new SimpleObservation(instr_2.getType(), SIMPLE_OBSERVATION_TYPE.RS1));
        }
        if (!Objects.equals(instr_1.getRs2(), instr_2.getRs2())) {
            differences_1.add(new SimpleObservation(instr_1.getType(), SIMPLE_OBSERVATION_TYPE.RS2));
            differences_2.add(new SimpleObservation(instr_2.getType(), SIMPLE_OBSERVATION_TYPE.RS2));
        }
        if (instr_1.getRs1() < 8 && instr_2.getRs1() < 8 && !Objects.equals(getRegisterValue(ctx, 1, instr_1.getRs1(), fetch_1), getRegisterValue(ctx, 2, instr_2.getRs1(), fetch_2))) {
            differences_1.add(new SimpleObservation(instr_1.getType(), SIMPLE_OBSERVATION_TYPE.REG_RS1));
            differences_2.add(new SimpleObservation(instr_2.getType(), SIMPLE_OBSERVATION_TYPE.REG_RS1));
        }
        if (instr_1.getRs2() < 8 && instr_2.getRs2() < 8 && !Objects.equals(getRegisterValue(ctx, 1, instr_1.getRs2(), fetch_1), getRegisterValue(ctx, 2, instr_2.getRs2(), fetch_2))) {
            differences_1.add(new SimpleObservation(instr_1.getType(), SIMPLE_OBSERVATION_TYPE.REG_RS2));
            differences_2.add(new SimpleObservation(instr_2.getType(), SIMPLE_OBSERVATION_TYPE.REG_RS2));
        }
        return new Pair<>(new SimpleTestResult(differences_1, true, 0), new SimpleTestResult(differences_2, true, 0));
    }

    @Override
    public Pair<TestResult, TestResult> extractCTX(int id, TestCase testCase) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Pair<TestResult, TestResult> extractDifferences(int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Pair<TestResult, TestResult> extractDifferences(int id, int index) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void compile() {
        throw new UnsupportedOperationException("Not implemented");
    }

    private Pair<Pair<Instruction, Integer>, Pair<Instruction, Integer>> findInstructionsSequential(VcdFile ctx) {
        Integer violation = ctx.getTop().getWire("atk_equiv").getLastChangeTime();
        System.out.println("Violation at " + violation);
        Integer fetch_1 = ctx.getTop().getChild("control").getWire("fetch_1_count").getLastChangeBeforeTime(violation);
        Integer fetch_2 = ctx.getTop().getChild("control").getWire("fetch_2_count").getLastChangeBeforeTime(violation);
        System.out.println("Fetch of instructions at " + fetch_1 +" and " + fetch_2);
        String instr_1_b = ctx.getTop().getWire("instr_1").getValueAt(fetch_1);
        System.out.println("Instr 1: " + instr_1_b);
        Instruction instr_1 = SimpleInstruction.parseBinaryString(instr_1_b);
        System.out.println(instr_1);
        String instr_2_b = ctx.getTop().getWire("instr_2").getValueAt(fetch_2);
        System.out.println("Instr 2: " + instr_2_b);
        Instruction instr_2 = SimpleInstruction.parseBinaryString(instr_2_b);
        System.out.println(instr_2);
        return new Pair<>(new Pair<>(instr_1, fetch_1), new Pair<>(instr_2, fetch_2));
    }

    protected String getRegisterValue(VcdFile ctx, int i, Integer register, Integer time) {
        if (i != 1 && i != 2)
            throw new IllegalArgumentException();
        Wire regfile = i == 1 ? ctx.getTop().getWire("regfile_1") : ctx.getTop().getWire("regfile_2");
        String value = regfile.getValueAt(time);
        // regfile is {r[7], r[6], r[5], r[4], r[3], r[2], r[1], r[0]}
        return value.substring((7 - register) * 32, (7 - register + 1) * 32);
    }

    @Override
    public void writeTestCase(TestCase testCase) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void writeTestCase(int id, TestCase testCase) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public SIMULATION_RESULT simulate() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public SIMULATION_RESULT simulate(int id) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getName() {
        return "simple";
    }
}

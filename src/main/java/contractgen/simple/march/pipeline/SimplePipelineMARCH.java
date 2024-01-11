package contractgen.simple.march.pipeline;

import contractgen.*;
import contractgen.simple.isa.SimpleInstruction;
import contractgen.simple.isa.contract.SIMPLE_OBSERVATION_TYPE;
import contractgen.simple.isa.contract.SimpleTestResult;
import contractgen.simple.isa.contract.SimpleObservation;
import contractgen.simple.march.singleycycle.SimpleMARCH;
import contractgen.util.Pair;
import contractgen.util.vcd.VcdFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static contractgen.util.ScriptUtils.runScript;

/**
 * The pipelined version of the simple toy processor.
 */
public class SimplePipelineMARCH extends SimpleMARCH {

    /**
     * @param updater   The updater to be used to update the contract.
     * @param testCases The test cases to be used for generation or evaluation.
     */
    public SimplePipelineMARCH(Updater updater, TestCases testCases) {
        super(updater, testCases);
        ADDITIONAL_DEFINITIONS = "--define=PIPELINE";
    }

    @Override
    public Pair<TestResult, TestResult> extractCTX(TestCase testCase) {
        VcdFile ctx;
        try {
            ctx = new VcdFile(Files.readString(Path.of(BASE_PATH + "/syn/run/verif/engine_0/trace.vcd")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Pair<Pair<Instruction, Integer>, Pair<Instruction, Integer>> c = findInstructionsPipeline(ctx, testCase);
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
        return new Pair<>(new SimpleTestResult(differences_1, true, testCase.getIndex()), new SimpleTestResult(differences_2, true, testCase.getIndex()));
    }

    /**
     * @param ctx      The trace
     * @param testCase The test case
     * @return The two instructions evaluated while the attacker was able to distinguish the executions.
     */
    private Pair<Pair<Instruction, Integer>, Pair<Instruction, Integer>> findInstructionsPipeline(VcdFile ctx, TestCase testCase) {
        Integer violation = ctx.getTop().getWire("atk_equiv").getLastChangeTime();
        System.out.println("Violation at " + violation);
        int fetch_1_count = Integer.parseInt(ctx.getTop().getChild("control").getWire("fetch_1_count").getValueAt(violation), 2);
        int fetch_2_count = Integer.parseInt(ctx.getTop().getChild("control").getWire("fetch_2_count").getValueAt(violation), 2);
        int retire_count = Integer.parseInt(ctx.getTop().getChild("control").getWire("retire_count").getValueAt(violation), 2);
        int fetch = Integer.max(fetch_1_count, fetch_2_count);
        int responsible = -1;
        for (int i = retire_count + 1; i <= fetch; i++) {
            generateSources(testCase, i);
            String path = "verif_out/count_" + i;
            generateSBY(50, path);
            runScript(BASE_PATH + "/syn/verif.sh", true, 3600);
            if (Files.exists(Path.of(BASE_PATH + "/syn/" + path + "/verif/FAIL"))) {
                System.out.println("First violation with " + i + " instructions.");
                responsible = i;
                break;
            }
        }
        assert responsible != -1;
        Integer fetch_1 = ctx.getTop().getChild("control").getWire("fetch_1_count").getFirstTimeValue(Integer.toBinaryString(responsible));
        Integer fetch_2 = ctx.getTop().getChild("control").getWire("fetch_2_count").getFirstTimeValue(Integer.toBinaryString(responsible));
        System.out.println("Fetch of instructions at " + fetch_1 + " and " + fetch_2);
        String instr_1_b = ctx.getTop().getChild("core_1").getWire("instr_ex").getValueAt(fetch_1);
        System.out.println("Instr 1: " + instr_1_b);
        Instruction instr_1 = SimpleInstruction.parseBinaryString(instr_1_b);
        System.out.println(instr_1);
        String instr_2_b = ctx.getTop().getChild("core_2").getWire("instr_ex").getValueAt(fetch_2);
        System.out.println("Instr 2: " + instr_2_b);
        Instruction instr_2 = SimpleInstruction.parseBinaryString(instr_2_b);
        System.out.println(instr_2);
        return new Pair<>(new Pair<>(instr_1, fetch_1), new Pair<>(instr_2, fetch_2));
    }

    @Override
    public String getName() {
        return "simple-pipeline";
    }
}

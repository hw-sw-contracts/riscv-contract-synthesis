package contractgen.simple.isa.testsuites;

import contractgen.TestCase;
import contractgen.TestCases;
import contractgen.simple.isa.SimpleInstruction;
import contractgen.simple.isa.SimpleProgram;
import contractgen.simple.isa.SimpleTestCase;

import java.util.HashMap;
import java.util.List;

/**
 * Tests for the pipelined microarchitecture.
 */
public class PipelineTests extends TestCases {

    /**
     * Tests for the pipelined microarchitecture.
     */
    public PipelineTests() {
        super(generatePipelineTestCases());
    }

    /**
     * @return Tests for the pipelined microarchitecture.
     */
    public static List<TestCase> generatePipelineTestCases() {
        List<SimpleInstruction> p1 = List.of(
                SimpleInstruction.ADDI(1, 0, 8),
                SimpleInstruction.ADDI(2, 0, 2),
                SimpleInstruction.NO_OP(),
                SimpleInstruction.NO_OP(),
                SimpleInstruction.MUL(3, 1, 2),
                SimpleInstruction.ADDI(4, 0, 4),
                SimpleInstruction.ADDI(5, 0, 5),
                SimpleInstruction.ADDI(6, 0, 6));
        List<SimpleInstruction> p2 = List.of(
                SimpleInstruction.ADDI(1, 0, 8),
                SimpleInstruction.ADDI(2, 0, 2),
                SimpleInstruction.NO_OP(),
                SimpleInstruction.NO_OP(),
                SimpleInstruction.MUL(3, 2, 1),
                SimpleInstruction.ADDI(4, 0, 4),
                SimpleInstruction.ADDI(5, 0, 5),
                SimpleInstruction.ADDI(6, 0, 6));

        TestCase standard = new SimpleTestCase(new SimpleProgram(new HashMap<>(), p1), new SimpleProgram(new HashMap<>(), p2), 15, 0);


        List<SimpleInstruction> p3 = List.of(
                SimpleInstruction.ADDI(1, 0, 2),
                SimpleInstruction.ADDI(2, 0, 8),
                SimpleInstruction.NO_OP(),
                SimpleInstruction.NO_OP(),
                SimpleInstruction.MUL(3, 1, 2),
                SimpleInstruction.ADDI(4, 0, 4),
                SimpleInstruction.ADDI(5, 0, 5),
                SimpleInstruction.ADDI(6, 0, 6));
        List<SimpleInstruction> p4 = List.of(
                SimpleInstruction.ADDI(1, 0, 8),
                SimpleInstruction.ADDI(2, 0, 2),
                SimpleInstruction.NO_OP(),
                SimpleInstruction.NO_OP(),
                SimpleInstruction.MUL(3, 1, 2),
                SimpleInstruction.ADDI(4, 0, 4),
                SimpleInstruction.ADDI(5, 0, 5),
                SimpleInstruction.ADDI(6, 0, 6));
        TestCase content = new SimpleTestCase(new SimpleProgram(new HashMap<>(), p3), new SimpleProgram(new HashMap<>(), p4), 15, 1);

        List<SimpleInstruction> p5 = List.of(
                SimpleInstruction.ADDI(1, 0, 2),
                SimpleInstruction.ADDI(2, 0, 8),
                SimpleInstruction.NO_OP(),
                SimpleInstruction.NO_OP(),
                SimpleInstruction.MUL(3, 1, 2),
                SimpleInstruction.ADDI(4, 0, 4),
                SimpleInstruction.ADDI(5, 0, 5),
                SimpleInstruction.ADDI(6, 0, 6));
        List<SimpleInstruction> p6 = List.of(
                SimpleInstruction.ADDI(1, 0, 2),
                SimpleInstruction.ADDI(2, 0, 2),
                SimpleInstruction.NO_OP(),
                SimpleInstruction.NO_OP(),
                SimpleInstruction.MUL(3, 1, 2),
                SimpleInstruction.ADDI(4, 0, 4),
                SimpleInstruction.ADDI(5, 0, 5),
                SimpleInstruction.ADDI(6, 0, 6));
        TestCase content_2 = new SimpleTestCase(new SimpleProgram(new HashMap<>(), p5), new SimpleProgram(new HashMap<>(), p6), 15, 2);

        return List.of(standard, content, content_2);
    }
}

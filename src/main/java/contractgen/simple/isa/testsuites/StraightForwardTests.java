package contractgen.simple.isa.testsuites;

import contractgen.TestCase;
import contractgen.TestCases;
import contractgen.simple.isa.SimpleProgram;
import contractgen.simple.isa.SimpleTestCase;
import contractgen.simple.isa.SimpleInstruction;

import java.util.HashMap;
import java.util.List;

/**
 * Tests for the simple microarchitecture.
 */
public class StraightForwardTests extends TestCases {

    /**
     * Tests for the simple microarchitecture.
     */
    public StraightForwardTests() {
        super(generateSimpleTestCases());
    }


    /**
     * @return Tests for the simple microarchitecture.
     */
    private static List<TestCase> generateSimpleTestCases() {
        List<SimpleInstruction> p1 = List.of(
                SimpleInstruction.ADDI(1, 0, 8),
                SimpleInstruction.ADDI(2, 0, 2),
                SimpleInstruction.MUL(3, 1, 2));
        List<SimpleInstruction> p2 = List.of(
                SimpleInstruction.ADDI(1, 0, 8),
                SimpleInstruction.ADDI(2, 0, 2),
                SimpleInstruction.MUL(3, 2, 1));

        SimpleTestCase standard = new SimpleTestCase(new SimpleProgram(new HashMap<>(), p1), new SimpleProgram(new HashMap<>(), p2), 4, 0);


        List<SimpleInstruction> p3 = List.of(
                SimpleInstruction.ADDI(1, 0, 2),
                SimpleInstruction.ADDI(2, 0, 8),
                SimpleInstruction.MUL(3, 1, 2));
        List<SimpleInstruction> p4 = List.of(
                SimpleInstruction.ADDI(1, 0, 8),
                SimpleInstruction.ADDI(2, 0, 2),
                SimpleInstruction.MUL(3, 1, 2));
        SimpleTestCase content = new SimpleTestCase(new SimpleProgram(new HashMap<>(), p3), new SimpleProgram(new HashMap<>(), p4), 4, 1);

        List<SimpleInstruction> p5 = List.of(
                SimpleInstruction.ADDI(1, 0, 2),
                SimpleInstruction.ADDI(2, 0, 8),
                SimpleInstruction.MUL(3, 1, 2));
        List<SimpleInstruction> p6 = List.of(
                SimpleInstruction.ADDI(1, 0, 2),
                SimpleInstruction.ADDI(2, 0, 2),
                SimpleInstruction.MUL(3, 1, 2));
        SimpleTestCase content_2 = new SimpleTestCase(new SimpleProgram(new HashMap<>(), p5), new SimpleProgram(new HashMap<>(), p6), 4, 2);
        return List.of(standard, content, content_2);
    }


}

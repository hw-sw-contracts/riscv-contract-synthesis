package contractgen.simple.isa;

import contractgen.TestCase;

/**
 * A test case for the simple ISA.
 */
public class SimpleTestCase extends TestCase {

    /**
     * @param program1              The program to be executed on core 1.
     * @param program2              The program to be executed on core 2.
     * @param maxInstructionCount   The maximal number of instructions in the programs.
     * @param index                 The index of this test case.
     */
    public SimpleTestCase(SimpleProgram program1, SimpleProgram program2, int maxInstructionCount, int index) {
        super(program1, program2, maxInstructionCount, index);
    }
}

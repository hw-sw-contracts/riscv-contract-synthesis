package contractgen.simple.isa;

import contractgen.Program;

import java.util.List;
import java.util.Map;

/**
 * A program for the toy example.
 */
public class SimpleProgram implements Program {

    /**
     * The number of registers in the ISA
     */
    private static final int NUMBER_REGISTERS = 8;
    /**
     * A mapping for each register to a specific value
     */
    private final Map<Integer, Integer> registers;

    /**
     * A sequence of instructions to be evaluated
     */
    private final List<SimpleInstruction> program;

    /**
     * @param registers The initial values of the registers.
     * @param program   The sequence of instructions.
     */
    public SimpleProgram(Map<Integer, Integer> registers, List<SimpleInstruction> program) {
        this.registers = registers;
        this.program = program;
    }

    @Override
    public String printSymbolic() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < NUMBER_REGISTERS; i++) {
            if (registers.containsKey(i) && registers.get(i) != 0) {
                if (registers.get(i) == null) {
                    sb.append("* anyconst *) reg [31:0] symbolic_");
                    sb.append(i);
                    sb.append(";\n");
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String printProgram(Integer address) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < NUMBER_REGISTERS; i++) {
            if (registers.containsKey(i) && registers.get(i) != 0) {
                if (registers.get(i) == null) {
                    sb.append("mem[(16'h");
                    sb.append(Integer.toHexString(address));
                    sb.append(" >> 2)] <= symbolic_");
                    sb.append(i);
                    sb.append(";\n");
                    address += 4;
                } else {
                    sb.append("mem[(16'h");
                    sb.append(Integer.toHexString(address));
                    sb.append(" >> 2)] <= 32'h");
                    sb.append(SimpleInstruction.ADDI(i, 0, registers.get(i)).toHexEncoding());
                    sb.append(";\n");
                    address += 4;
                }
            }
        }

        for (SimpleInstruction simpleInstruction : program) {
            sb.append("mem[(16'h");
            sb.append(Integer.toHexString(address));
            sb.append(" >> 2)] <= 32'h");
            sb.append(simpleInstruction.toHexEncoding());
            sb.append(";\n");
            address += 4;
        }
        return sb.toString();
    }


    @Override
    public int maxAddress(int base) {
        for (int i = 1; i < NUMBER_REGISTERS; i++) {
            if (registers.containsKey(i) && registers.get(i) != 0) {
                base += 4;
            }
        }
        base += program.size() * 4;
        return base;
    }

    @Override
    public void printInit(String path) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void printInstr(String path) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String toString() {
        return "Program{" +
                "registers=" + registers +
                ", program=" + program +
                '}';
    }
}

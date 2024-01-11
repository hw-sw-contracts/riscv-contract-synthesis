package contractgen.riscv.isa;

import contractgen.Program;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * A RISC-V program
 */
public class RISCVProgram implements Program {

    /**
     * The number of architectural registers.
     */
    private static final int NUMBER_REGISTERS = 32;
    /**
     * The initial value of each register.
     */
    private final Map<Integer, Integer> registers;

    /**
     * The sequence of instructions
     */
    private final List<RISCVInstruction> program;

    /**
     * @param registers The initial register values.
     * @param program   The sequence of instructions.
     */
    public RISCVProgram(Map<Integer, Integer> registers, List<RISCVInstruction> program) {
        this.registers = registers;
        this.program = program;
    }

    @Override
    public String printSymbolic() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String printProgram(Integer address) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int maxAddress(int base) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void printInit(String path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < NUMBER_REGISTERS; i++) {
            if (registers.containsKey(i) && registers.get(i) != null) {
                sb.append(RISCVInstruction.ADDI(i, 0, registers.get(i)).toHexEncoding());
            } else {
                sb.append(RISCVInstruction.NOP().toHexEncoding());
            }
            sb.append("\n");
        }
        try {
            Files.write(Paths.get(path), sb.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void printInstr(String path) {
        StringBuilder sb = new StringBuilder();
        for (RISCVInstruction instruction : program) {
            sb.append(instruction.toHexEncoding());
            sb.append("\n");
        }
        try {
            Files.write(Paths.get(path), sb.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "RISCVProgram{" +
                "registers=" + registers +
                ", program=" + program +
                '}';
    }
}

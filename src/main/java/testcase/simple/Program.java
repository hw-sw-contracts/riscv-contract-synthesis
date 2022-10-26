package testcase.simple;

import isa.simple.Instruction;

import java.util.List;
import java.util.Map;

public class Program {

    private static final int NUMBER_REGISTERS = 8;
    private final Map<Integer, Integer> registers;

    private final List<Instruction> program;

    public Program(Map<Integer, Integer> registers, List<Instruction> program) {
        this.registers = registers;
        this.program = program;
    }


    public String printSymbolic () {
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

    public String printProgram (Integer address) {
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
                    sb.append(Instruction.ADDI(i, 0, registers.get(i)).toHexEncoding());
                    sb.append(";\n");
                    address += 4;
                }
            }
        }

        for (Instruction instruction: program) {
            sb.append("mem[(16'h");
            sb.append(Integer.toHexString(address));
            sb.append(" >> 2)] <= 32'h");
            sb.append(instruction.toHexEncoding());
            sb.append(";\n");
            address += 4;
        }
        return sb.toString();
    }


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
    public String toString() {
        return "Program{" +
                "registers=" + registers +
                ", program=" + program +
                '}';
    }
}

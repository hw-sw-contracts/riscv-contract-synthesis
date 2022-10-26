package testcase.simple;

import isa.simple.Instruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Testcase {

    private final Program program1;
    private final Program program2;
    private final int maxInstructionCount;

    public Testcase(Program program1, Program program2, int maxInstructionCount) {
        this.program1 = program1;
        this.program2 = program2;
        this.maxInstructionCount = maxInstructionCount;
    }

    public Program getProgram1() {
        return program1;
    }

    public Program getProgram2() {
        return program2;
    }

    public int getMaxInstructionCount() {
        return maxInstructionCount;
    }

    public static List<Testcase> getTestcases() {
        List<Instruction> p1 = List.of(
                Instruction.ADDI(1, 0, 8),
                Instruction.ADDI(2, 0, 2),
                Instruction.MUL(3, 1, 2));
        List<Instruction> p2 = List.of(
                Instruction.ADDI(1, 0, 8),
                Instruction.ADDI(2, 0, 2),
                Instruction.MUL(3, 2, 1));

        Testcase standard = new Testcase(new Program(new HashMap<>(), p1), new Program(new HashMap<>(), p2), 4);


        List<Instruction> p3 = List.of(
                Instruction.ADDI(1, 0, 2),
                Instruction.ADDI(2, 0, 8),
                Instruction.MUL(3, 1, 2));
        List<Instruction> p4 = List.of(
                Instruction.ADDI(1, 0, 8),
                Instruction.ADDI(2, 0, 2),
                Instruction.MUL(3, 1, 2));
        Testcase content = new Testcase(new Program(new HashMap<>(), p3), new Program(new HashMap<>(), p4), 4);

        List<Instruction> p5 = List.of(
                Instruction.ADDI(1, 0, 2),
                Instruction.ADDI(2, 0, 8),
                Instruction.MUL(3, 1, 2));
        List<Instruction> p6 = List.of(
                Instruction.ADDI(1, 0, 2),
                Instruction.ADDI(2, 0, 2),
                Instruction.MUL(3, 1, 2));
        Testcase content_2 = new Testcase(new Program(new HashMap<>(), p5), new Program(new HashMap<>(), p6), 4);

        return List.of(standard, content, content_2);
    }

    public static List<Testcase> getMultiCoreTestcases() {
        List<Instruction> p1 = List.of(
                Instruction.ADDI(1, 0, 8),
                Instruction.ADDI(2, 0, 2),
                Instruction.NO_OP(),
                Instruction.NO_OP(),
                Instruction.MUL(3, 1, 2),
                Instruction.ADDI(4, 0, 4),
                Instruction.ADDI(5, 0, 5),
                Instruction.ADDI(6, 0, 6));
        List<Instruction> p2 = List.of(
                Instruction.ADDI(1, 0, 8),
                Instruction.ADDI(2, 0, 2),
                Instruction.NO_OP(),
                Instruction.NO_OP(),
                Instruction.MUL(3, 2, 1),
                Instruction.ADDI(4, 0, 4),
                Instruction.ADDI(5, 0, 5),
                Instruction.ADDI(6, 0, 6));

        Testcase standard = new Testcase(new Program(new HashMap<>(), p1), new Program(new HashMap<>(), p2), 15);


        List<Instruction> p3 = List.of(
                Instruction.ADDI(1, 0, 2),
                Instruction.ADDI(2, 0, 8),
                Instruction.NO_OP(),
                Instruction.NO_OP(),
                Instruction.MUL(3, 1, 2),
                Instruction.ADDI(4, 0, 4),
                Instruction.ADDI(5, 0, 5),
                Instruction.ADDI(6, 0, 6));
        List<Instruction> p4 = List.of(
                Instruction.ADDI(1, 0, 8),
                Instruction.ADDI(2, 0, 2),
                Instruction.NO_OP(),
                Instruction.NO_OP(),
                Instruction.MUL(3, 1, 2),
                Instruction.ADDI(4, 0, 4),
                Instruction.ADDI(5, 0, 5),
                Instruction.ADDI(6, 0, 6));
        Testcase content = new Testcase(new Program(new HashMap<>(), p3), new Program(new HashMap<>(), p4), 15);

        List<Instruction> p5 = List.of(
                Instruction.ADDI(1, 0, 2),
                Instruction.ADDI(2, 0, 8),
                Instruction.NO_OP(),
                Instruction.NO_OP(),
                Instruction.MUL(3, 1, 2),
                Instruction.ADDI(4, 0, 4),
                Instruction.ADDI(5, 0, 5),
                Instruction.ADDI(6, 0, 6));
        List<Instruction> p6 = List.of(
                Instruction.ADDI(1, 0, 2),
                Instruction.ADDI(2, 0, 2),
                Instruction.NO_OP(),
                Instruction.NO_OP(),
                Instruction.MUL(3, 1, 2),
                Instruction.ADDI(4, 0, 4),
                Instruction.ADDI(5, 0, 5),
                Instruction.ADDI(6, 0, 6));
        Testcase content_2 = new Testcase(new Program(new HashMap<>(), p5), new Program(new HashMap<>(), p6), 15);

        return List.of(standard, content, content_2);
    }

    public static List<Testcase> getRandomTestCases(int count, long seed) {
        Random r = new Random(seed);
        List<Testcase> testcases = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int instrcount = r.nextInt(3, 7);
            List<Instruction> p1 = new ArrayList<>();
            List<Instruction> p2 = new ArrayList<>();
            for (int j = 0; j < instrcount; j++) {
                switch (r.nextInt(5)) {

                    case 0 ->  {
                        p1.add(Instruction.NO_OP());
                        p2.add(Instruction.NO_OP());
                    }
                    case 1 -> {
                        p1.add(Instruction.ADD(r.nextInt(8), r.nextInt(8), r.nextInt(8)));
                        p2.add(Instruction.ADD(r.nextInt(8), r.nextInt(8), r.nextInt(8)));
                    }
                    case 2 -> {
                        p1.add(Instruction.ADDI(r.nextInt(8), r.nextInt(8), r.nextInt(256)));
                        p2.add(Instruction.ADDI(r.nextInt(8), r.nextInt(8), r.nextInt(256)));
                    }
                    case 3 -> {
                        p1.add(Instruction.MUL(r.nextInt(8), r.nextInt(8), r.nextInt(8)));
                        p2.add(Instruction.MUL(r.nextInt(8), r.nextInt(8), r.nextInt(8)));
                    }
                    case 4 -> {
                        p1.add(Instruction.MULI(r.nextInt(8), r.nextInt(8), r.nextInt(256)));
                        p2.add(Instruction.MULI(r.nextInt(8), r.nextInt(8), r.nextInt(256)));
                    }
                }
            }
            HashMap<Integer, Integer> r1 = new HashMap<>();
            HashMap<Integer, Integer> r2 = new HashMap<>();
            for (int j = 0; j < 8; j++) {
                r1.put(j, r.nextInt(256));
                r2.put(j, r.nextInt(256));
            }
            testcases.add(new Testcase(new Program(r1, p1), new Program(r2, p2), instrcount + 8));
        }
        return testcases;
    }

    @Override
    public String toString() {
        return "Testcase{" +
                "program1=" + program1 +
                ", program2=" + program2 +
                ", maxInstructionCount=" + maxInstructionCount +
                '}';
    }
}

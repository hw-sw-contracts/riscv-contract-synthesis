package contractgen.simple.isa.testsuites;

import contractgen.TestCase;
import contractgen.TestCases;
import contractgen.simple.isa.SimpleInstruction;
import contractgen.simple.isa.SimpleProgram;
import contractgen.simple.isa.SimpleTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RandomTests extends TestCases {

    protected RandomTests(int count, long seed) {
        super(generateRandomTestCases(count, seed));
    }

    private static List<TestCase> generateRandomTestCases(int count, long seed) {
        Random r = new Random(seed);
        List<TestCase> testcases = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int instrcount = r.nextInt(3, 7);
            List<SimpleInstruction> p1 = new ArrayList<>();
            List<SimpleInstruction> p2 = new ArrayList<>();
            for (int j = 0; j < instrcount; j++) {
                switch (r.nextInt(5)) {

                    case 0 ->  {
                        p1.add(SimpleInstruction.NO_OP());
                        p2.add(SimpleInstruction.NO_OP());
                    }
                    case 1 -> {
                        p1.add(SimpleInstruction.ADD(r.nextInt(8), r.nextInt(8), r.nextInt(8)));
                        p2.add(SimpleInstruction.ADD(r.nextInt(8), r.nextInt(8), r.nextInt(8)));
                    }
                    case 2 -> {
                        p1.add(SimpleInstruction.ADDI(r.nextInt(8), r.nextInt(8), r.nextInt(256)));
                        p2.add(SimpleInstruction.ADDI(r.nextInt(8), r.nextInt(8), r.nextInt(256)));
                    }
                    case 3 -> {
                        p1.add(SimpleInstruction.MUL(r.nextInt(8), r.nextInt(8), r.nextInt(8)));
                        p2.add(SimpleInstruction.MUL(r.nextInt(8), r.nextInt(8), r.nextInt(8)));
                    }
                    case 4 -> {
                        p1.add(SimpleInstruction.MULI(r.nextInt(8), r.nextInt(8), r.nextInt(256)));
                        p2.add(SimpleInstruction.MULI(r.nextInt(8), r.nextInt(8), r.nextInt(256)));
                    }
                }
            }
            HashMap<Integer, Integer> r1 = new HashMap<>();
            HashMap<Integer, Integer> r2 = new HashMap<>();
            for (int j = 0; j < 8; j++) {
                r1.put(j, r.nextInt(256));
                r2.put(j, r.nextInt(256));
            }
            testcases.add(new SimpleTestCase(new SimpleProgram(r1, p1), new SimpleProgram(r2, p2), instrcount + 8));
        }
        return testcases;
    }
}

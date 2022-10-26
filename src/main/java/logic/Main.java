package logic;

import contract.simple.Contract;
import contract.simple.Counterexample;
import contract.simple.OBSERVATION_TYPE;
import contract.simple.Observation;
import isa.simple.Instruction;
import testcase.simple.Testcase;
import util.Pair;
import vcd.VcdFile;
import vcd.Wire;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Main {

    private static final String TEMPLATE_PATH = "/home/yosys/bachelor/simple_proc_template/";
    private static final String BASE_PATH = "/home/yosys/bachelor/simple_proc_generated/";

    public static void main(String[] args) throws IOException {
        // inst. empty contract
        Contract contract = new Contract();
        // get all testcases
        //List<Testcase> testcases = Testcase.getMultiCoreTestcases();
        List<Testcase> testcases = Testcase.getTestcases();
        //List<Testcase> testcases = Testcase.getRandomTestCases(20, 123456);

        // test each case
        int chunkSize = 3;
        for (int k = 0; k < testcases.size(); k += chunkSize) {
            int end = Math.min(testcases.size(), k + chunkSize);
            List<Testcase> chunk = testcases.subList(k, end);
            List<Thread> threads = new ArrayList<>();
            for (int i = 0; i < chunk.size(); i++) {
                Thread t = new Thread(new Runner(contract, chunk.get(i), i));
                threads.add(t);
                t.start();
                Object o = new Object();
                synchronized (o) {
                    try {
                        o.wait(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        /*
        for (Testcase testcase: testcases) {
            run(contract, testcase);
        }
         */
    }
}

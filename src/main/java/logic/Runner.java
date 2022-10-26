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
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Runner implements Runnable {

    private final Contract contract;
    private final Testcase testcase;
    private final String BASE_PATH;
    private final int id;
    private static final String TEMPLATE_PATH = "/home/yosys/bachelor/simple_proc_template/";

    public Runner(Contract contract, Testcase testcase, int id) {
        this.contract = contract;
        this.testcase = testcase;
        this.id = id;
        BASE_PATH = "/home/yosys/bachelor/simple_proc_generated_" + id + "/";
    }

    @Override
    public void run() {
        generateSources(Optional.empty());
        // test number of steps
        int max_steps = 150;
        String path_c = "verif_out/try_steps";
        generateCoverSBY(max_steps, path_c);
        String result = runScript(BASE_PATH + "syn/verif.sh", false);

        // Checking cover reachability in step 23

        String regex = ".*Checking cover reachability in step (\\d*)..";
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = p.matcher(result);
        if (!m.find()) {
            System.out.println("Something went wrong, cannot find step count.");
            return;
        }
        int steps = Integer.parseInt(m.group(1)) + 1;
        System.out.println("Step count should be " + steps);



        String path = "verif_out/steps_" + steps;
        generateSBY(steps, path);
        runScript(BASE_PATH + "/syn/verif.sh", false);


        if (!Files.exists(Path.of(BASE_PATH + "/syn/" + path + "/verif/FAIL"))) {
            System.out.println("No violation.");
            System.out.println("Testcase: \n" + testcase);
            return;
        }

        // extract counterexample
        VcdFile ctx;
        try {
            ctx = new VcdFile(Files.readString(Path.of(BASE_PATH + "/syn/" + path + "/verif/engine_0/trace.vcd")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Pair<Pair<Instruction, Integer>, Pair<Instruction, Integer>> c = findInstructionsPipeline(ctx, contract, test);
        Pair<Pair<Instruction, Integer>, Pair<Instruction, Integer>> c = findInstructionsSequential(ctx);
        Instruction instr_1 = c.getLeft().getLeft();
        Integer fetch_1 = c.getLeft().getRight();
        Instruction instr_2 = c.getRight().getLeft();
        Integer fetch_2 = c.getRight().getRight();


        // find possible contract templates
        Set<Observation> differences_1 = new HashSet<>();
        Set<Observation> differences_2 = new HashSet<>();
        assert instr_1 != null;
        assert instr_2 != null;
        if (instr_1.getType() != instr_2.getType()) {
            differences_1.add(new Observation(instr_1.getType(), OBSERVATION_TYPE.OPCODE));
            differences_2.add(new Observation(instr_2.getType(), OBSERVATION_TYPE.OPCODE));
        }
        if (!Objects.equals(instr_1.getRd(), instr_2.getRd())) {
            differences_1.add(new Observation(instr_1.getType(), OBSERVATION_TYPE.RD));
            differences_2.add(new Observation(instr_2.getType(), OBSERVATION_TYPE.RD));
        }
        if (!Objects.equals(instr_1.getRs1(), instr_2.getRs1())) {
            differences_1.add(new Observation(instr_1.getType(), OBSERVATION_TYPE.RS1));
            differences_2.add(new Observation(instr_2.getType(), OBSERVATION_TYPE.RS1));
        }
        if (!Objects.equals(instr_1.getRs2(), instr_2.getRs2())) {
            differences_1.add(new Observation(instr_1.getType(), OBSERVATION_TYPE.RS2));
            differences_2.add(new Observation(instr_2.getType(), OBSERVATION_TYPE.RS2));
        }
        if (instr_1.getRs1() < 8 && instr_2.getRs1() < 8 && !Objects.equals(getRegisterValue(ctx, 1, instr_1.getRs1(), fetch_1), getRegisterValue(ctx, 2, instr_2.getRs1(), fetch_2))) {
            differences_1.add(new Observation(instr_1.getType(), OBSERVATION_TYPE.REG_RS1));
            differences_2.add(new Observation(instr_2.getType(), OBSERVATION_TYPE.REG_RS1));
        }
        if (instr_1.getRs2() < 8 && instr_2.getRs2() < 8 && !Objects.equals(getRegisterValue(ctx, 1, instr_1.getRs2(), fetch_1), getRegisterValue(ctx, 2, instr_2.getRs2(), fetch_2))) {
            differences_1.add(new Observation(instr_1.getType(), OBSERVATION_TYPE.REG_RS2));
            differences_2.add(new Observation(instr_2.getType(), OBSERVATION_TYPE.REG_RS2));
        }
        synchronized (contract) {
            // update contract
            contract.add(new Counterexample(differences_1));
            contract.add(new Counterexample(differences_2));
            contract.updateContractPremium();
            System.out.println(contract);
            System.out.println("new Contract " + contract.printContract());
        }

    }

    private Pair<Pair<Instruction, Integer>, Pair<Instruction, Integer>> findInstructionsSequential(VcdFile ctx) {
        Integer violation = ctx.getTop().getWire("atk_equiv").getLastChangeTime();
        System.out.println("Violation at " + violation);
        Integer fetch_1 = ctx.getTop().getChild("control").getWire("fetch_1_count").getLastChangeBeforeTime(violation);
        Integer fetch_2 = ctx.getTop().getChild("control").getWire("fetch_2_count").getLastChangeBeforeTime(violation);
        System.out.println("Fetch of instructions at " + fetch_1 +" and " + fetch_2);
        String instr_1_b = ctx.getTop().getWire("instr_1").getValueAt(fetch_1);
        System.out.println("Instr 1: " + instr_1_b);
        Instruction instr_1 = Instruction.parseBinaryString(instr_1_b);
        System.out.println(instr_1);
        String instr_2_b = ctx.getTop().getWire("instr_2").getValueAt(fetch_2);
        System.out.println("Instr 2: " + instr_2_b);
        Instruction instr_2 = Instruction.parseBinaryString(instr_2_b);
        System.out.println(instr_2);
        return new Pair<>(new Pair<>(instr_1, fetch_1), new Pair<>(instr_2, fetch_2));
    }

    private Pair<Pair<Instruction, Integer>, Pair<Instruction, Integer>> findInstructionsPipeline(VcdFile ctx) {
        Integer violation = ctx.getTop().getWire("atk_equiv").getLastChangeTime();
        System.out.println("Violation at " + violation);
        int fetch_1_count = Integer.parseInt(ctx.getTop().getChild("control").getWire("fetch_1_count").getValueAt(violation), 2);
        int fetch_2_count = Integer.parseInt(ctx.getTop().getChild("control").getWire("fetch_2_count").getValueAt(violation), 2);
        int retire_count = Integer.parseInt(ctx.getTop().getChild("control").getWire("retire_count").getValueAt(violation), 2);
        int fetch = Integer.max(fetch_1_count, fetch_2_count);
        int responsible = -1;
        for (int i = retire_count + 1 ; i <= fetch; i++) {
            generateSources(Optional.of(i));
            String path = "verif_out/count_" + i;
            generateSBY(50, path);
            runScript(BASE_PATH + "/syn/verif.sh", true);
            if (Files.exists(Path.of(BASE_PATH + "/syn/" + path + "/verif/FAIL"))) {
                System.out.println("First violation with " + i + " instructions.");
                responsible = i;
                break;
            }
        }
        assert responsible != -1;
        Integer fetch_1 = ctx.getTop().getChild("control").getWire("fetch_1_count").getFirstTimeValue(Integer.toBinaryString(responsible));
        Integer fetch_2 = ctx.getTop().getChild("control").getWire("fetch_2_count").getFirstTimeValue(Integer.toBinaryString(responsible));
        System.out.println("Fetch of instructions at " + fetch_1 +" and " + fetch_2);
        String instr_1_b = ctx.getTop().getChild("core_1").getWire("instr_ex").getValueAt(fetch_1);
        System.out.println("Instr 1: " + instr_1_b);
        Instruction instr_1 = Instruction.parseBinaryString(instr_1_b);
        System.out.println(instr_1);
        String instr_2_b = ctx.getTop().getChild("core_2").getWire("instr_ex").getValueAt(fetch_2);
        System.out.println("Instr 2: " + instr_2_b);
        Instruction instr_2 = Instruction.parseBinaryString(instr_2_b);
        System.out.println(instr_2);
        return new Pair<>(new Pair<>(instr_1, fetch_1), new Pair<>(instr_2, fetch_2));
    }

    private String getRegisterValue(VcdFile ctx, int i, Integer register, Integer time) {
        if (i != 1 && i != 2)
            throw new IllegalArgumentException();
        Wire regfile = i == 1 ? ctx.getTop().getWire("regfile_1") : ctx.getTop().getWire("regfile_2");
        String value = regfile.getValueAt(time);
        // regfile is {r[7], r[6], r[5], r[4], r[3], r[2], r[1], r[0]}
        return value.substring((7 - register) * 32, (7 - register + 1) * 32);
    }

    private void generateCoverSBY(int steps, String path) {
        try {
            copyFile(Path.of(TEMPLATE_PATH + "syn/verif.sh").toFile(), Path.of(BASE_PATH + "syn/verif.sh").toFile(), REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        replaceString("syn/verif_cover.sby", "/* Number Steps */", String.valueOf(steps));
        replaceString("syn/verif.sh", "/* PATH */", BASE_PATH + "syn");
        replaceString("syn/verif.sh", "/* Output Directory */", path);
        replaceString("syn/verif.sh", "/* SBY File */", "verif_cover.sby");
    }

    private void generateSBY(int steps, String path) {
        try {
            copyFile(Path.of(TEMPLATE_PATH + "syn/verif.sh").toFile(), Path.of(BASE_PATH + "syn/verif.sh").toFile(), REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        replaceString("syn/verif.sby", "/* Number Steps */", String.valueOf(steps));
        replaceString("syn/verif.sh", "/* PATH */", BASE_PATH + "syn");
        replaceString("syn/verif.sh", "/* Output Directory */", path);
        replaceString("syn/verif.sh", "/* SBY File */", "verif.sby");
    }

    private void generateSources(Optional<Integer> max_count) {
        //copy source to target using Files Class
        try {
            copyFileOrFolder(Path.of(TEMPLATE_PATH).toFile(), Path.of(BASE_PATH).toFile(), REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // contract
        synchronized (contract) {
            replaceString("rtl/ctr.sv", "/* CONTRACT */", contract.printContract());
        }

        // instruction memory
        replaceString("rtl/instr_mem_1.sv", "/* Instruction Memory 1 - Variables */", testcase.getProgram1().printSymbolic());
        replaceString("rtl/instr_mem_1.sv", "/* Instruction Memory 1 - Instructions */", testcase.getProgram1().printProgram(Integer.parseInt("10", 16)));
        replaceString("rtl/instr_mem_2.sv", "/* Instruction Memory 2 - Variables */", testcase.getProgram2().printSymbolic());
        replaceString("rtl/instr_mem_2.sv", "/* Instruction Memory 2 - Instructions */", testcase.getProgram2().printProgram(Integer.parseInt("10", 16)));

        //control module
        replaceString("rtl/control.sv", "/* Max Instruction Count */", String.valueOf(max_count.orElse(testcase.getMaxInstructionCount())));
        replaceString("rtl/control.sv", "/* Max Instruction Address 1 */", "16'h" + Integer.toHexString(testcase.getProgram1().maxAddress(Integer.parseInt("10", 16))));
        replaceString("rtl/control.sv", "/* Max Instruction Address 2 */", "16'h" + Integer.toHexString(testcase.getProgram2().maxAddress(Integer.parseInt("10", 16))));

        // formal
        replaceString("syn/formal.sv", "/* Max Instruction Address 1 */", "16'h" + Integer.toHexString(testcase.getProgram1().maxAddress(Integer.parseInt("10", 16))));
        replaceString("syn/formal.sv", "/* Max Instruction Address 2 */", "16'h" + Integer.toHexString(testcase.getProgram2().maxAddress(Integer.parseInt("10", 16))));
    }


    private void replaceString(String filePath, String text, String replacement) {

        Path path = Paths.get(BASE_PATH + filePath);
        // Get all the lines
        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            // Do the replace operation
            List<String> list = stream.map(line -> line.replace(text, replacement)).collect(Collectors.toList());
            // Write the content back
            Files.write(path, list, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String runScript(String path, boolean silent) {
        Process p;
        try {
            StringBuilder sb = new StringBuilder();
            List<String> cmdList = new ArrayList<>();
            // adding command and args to the list
            cmdList.add(path);
            ProcessBuilder pb = new ProcessBuilder(cmdList);
            System.out.println("Starting Script...");
            p = pb.start();
            p.waitFor();
            if (!silent) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    System.out.println(line);
                }
            }
            return sb.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void copyFileOrFolder(File source, File dest, CopyOption...  options) throws IOException {
        if (source.isDirectory())
            copyFolder(source, dest, options);
        else {
            ensureParentFolder(dest);
            copyFile(source, dest, options);
        }
    }

    private void copyFolder(File source, File dest, CopyOption... options) throws IOException {
        if (!dest.exists())
            dest.mkdirs();
        File[] contents = source.listFiles();
        if (contents != null) {
            for (File f : contents) {
                File newFile = new File(dest.getAbsolutePath() + File.separator + f.getName());
                if (f.isDirectory())
                    copyFolder(f, newFile, options);
                else
                    copyFile(f, newFile, options);
            }
        }
    }

    private void copyFile(File source, File dest, CopyOption... options) throws IOException {
        Files.copy(source.toPath(), dest.toPath(), options);
    }

    private void ensureParentFolder(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists())
            parent.mkdirs();
    }
}

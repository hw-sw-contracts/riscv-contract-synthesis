package contractgen;

import contractgen.generator.iverilog.ParallelIverilogGenerator;
import contractgen.riscv.cva6.CVA6;
import contractgen.riscv.ibex.IBEX;
import contractgen.riscv.isa.tests.RISCVRandomTests;
import contractgen.updater.ILPUpdater;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ContractGen {

    public static void main(String[] args) throws IOException {
        Generator ibex_generator = new ParallelIverilogGenerator(new IBEX(new ILPUpdater(), new RISCVRandomTests(12345678, 10)), 12, false);
        String ibex_path = "contract-" + ibex_generator.MARCH.getName() + "-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".json";
        Evaluator ibex_eval = new Evaluator(new IBEX(new ILPUpdater(), new RISCVRandomTests(123456789, 100)), 12);

        Generator cva6_generator = new ParallelIverilogGenerator(new CVA6(new ILPUpdater(), new RISCVRandomTests(12345678, 10)), 12, false);
        String cva6_path = "contract-" + cva6_generator.MARCH.getName() + "-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".json";
        Evaluator cva6_eval = new Evaluator(new CVA6(new ILPUpdater(), new RISCVRandomTests(123456789, 100)), 12);

        generateAndEvaluate(ibex_generator, ibex_eval, ibex_path);
        //evaluate(cva6_eval, "contract-cva6-2023-02-28-16-04-53.json");
    }

    public static void generateAndEvaluate(Generator generator, Evaluator evaluator, String path) {
        generate(generator, path);
        evaluate(evaluator, path);
    }

    public static void generate(Generator generator, String path) {
        Contract contract = generator.generate();
        System.out.println(contract);
        try {
            Files.write(Path.of(path), contract.toJSON().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void evaluate(Evaluator evaluator, String path) {
        evaluator.evaluate(path);
    }

}

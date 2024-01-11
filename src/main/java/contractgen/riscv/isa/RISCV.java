package contractgen.riscv.isa;

import contractgen.Contract;
import contractgen.ISA;
import contractgen.TestCases;
import contractgen.Updater;
import contractgen.riscv.isa.contract.RISCVContract;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

/**
 * The RISC-V instruction set architecture.
 */
public class RISCV extends ISA {

    /**
     * The contract object.
     */
    private RISCVContract contract;

    /**
     * @param updater   The updater to be used to compute the contract.
     * @param testCases The test cases to be used for generation or evaluation.
     */
    public RISCV(Updater updater, TestCases testCases) {
        super(testCases);
        contract = new RISCVContract(updater);
    }

    @Override
    public Contract getContract() {
        return contract;
    }

    @Override
    public void loadContract(Path path) throws IOException {
        this.contract = RISCVContract.fromJSON(Files.readString(path));
        this.contract.update(true);
    }
}

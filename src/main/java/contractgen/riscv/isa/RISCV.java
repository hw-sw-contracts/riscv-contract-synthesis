package contractgen.riscv.isa;

import contractgen.Contract;
import contractgen.ISA;
import contractgen.TestCases;
import contractgen.Updater;
import contractgen.riscv.isa.contract.RISCVContract;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RISCV extends ISA {

    private RISCVContract contract;

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
    }
}

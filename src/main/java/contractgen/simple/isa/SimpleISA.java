package contractgen.simple.isa;

import contractgen.Contract;
import contractgen.ISA;
import contractgen.TestCases;
import contractgen.Updater;
import contractgen.simple.isa.contract.SimpleContract;

import java.nio.file.Path;

public class SimpleISA extends ISA {

    private final SimpleContract contract;

    public SimpleISA(Updater updater, TestCases testCases) {
        super(testCases);
        contract = new SimpleContract(updater);
    }

    @Override
    public Contract getContract() {
        return contract;
    }

    @Override
    public void loadContract(Path path) {
        throw new UnsupportedOperationException("Not implemented.");
    }
}

package contractgen.simple.isa;

import contractgen.Contract;
import contractgen.ISA;
import contractgen.TestCases;
import contractgen.Updater;
import contractgen.simple.isa.contract.SimpleContract;

import java.nio.file.Path;

/**
 * A simple ISA for the toy example.
 */
public class SimpleISA extends ISA {

    private final SimpleContract contract;

    /**
     * @param updater   The updater to be used.
     * @param testCases The test cases to be used.
     */
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

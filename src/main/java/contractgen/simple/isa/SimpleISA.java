package contractgen.simple.isa;

import contractgen.Contract;
import contractgen.ISA;
import contractgen.TestCases;
import contractgen.simple.isa.contract.SimpleContract;

public class SimpleISA extends ISA {

    private SimpleContract contract;

    public SimpleISA(TestCases testCases) {
        super(testCases);
        contract = new SimpleContract();
    }

    @Override
    public Contract getContract() {
        return contract;
    }
}

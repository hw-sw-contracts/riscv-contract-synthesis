package contractgen.riscv.isa;

import contractgen.Contract;
import contractgen.ISA;
import contractgen.TestCases;
import contractgen.riscv.isa.contract.RISCVContract;

public class RISCV extends ISA {

    private RISCVContract contract;

    public RISCV(TestCases testCases) {
        super(testCases);
        contract = new RISCVContract();
    }

    @Override
    public Contract getContract() {
        return contract;
    }
}

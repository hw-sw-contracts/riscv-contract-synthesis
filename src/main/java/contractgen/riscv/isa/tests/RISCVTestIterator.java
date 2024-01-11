package contractgen.riscv.isa.tests;

import contractgen.TestCase;
import contractgen.riscv.isa.RISCV_SUBSET;
import contractgen.riscv.isa.contract.RISCV_OBSERVATION_TYPE;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 */
public class RISCVTestIterator implements Iterator<TestCase> {

    /**
     * The test generator.
     */
    private final RISCVTestGenerator generator;
    /**
     * The total number of test cases to be evaluated.
     */
    private final int total;
    /**
     * The current count.
     */
    private int count = 0;
    /**
     * The currently generated chunk.
     */
    private List<TestCase> chunk;

    /**
     * @param subsets              the allowed ISA subsets.
     * @param allowed_observations the allowed observation types.
     * @param seed                 the random seed.
     * @param total                the total number of test cases to be generated.
     */
    public RISCVTestIterator(Set<RISCV_SUBSET> subsets, Set<RISCV_OBSERVATION_TYPE> allowed_observations, long seed, int total) {
        generator = new RISCVTestGenerator(subsets, allowed_observations, seed, 0);
        this.total = total;
        this.chunk = generator.nextRepetition(count);
    }

    @Override
    public boolean hasNext() {
        return total > count;
    }

    @Override
    public TestCase next() {
        if (total < count) return null;
        if (chunk.isEmpty())
            chunk = generator.nextRepetition(count + 1);
        count++;
        return chunk.remove(0);
    }
}
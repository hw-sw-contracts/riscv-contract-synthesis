package contractgen.riscv.isa.tests;

import contractgen.TestCase;
import contractgen.TestCases;
import contractgen.riscv.isa.RISCV_SUBSET;
import contractgen.riscv.isa.contract.RISCV_OBSERVATION_TYPE;

import java.util.*;

/**
 * An iterative test generation method.
 */
public class RISCVIterativeTests extends TestCases {
    /**
     * @param subsets              the ISA subsets to consider.
     * @param allowed_observations the observations under consideration.
     * @param seed                 a random seed.
     * @param THREADS              the number of threads that will be used for generation
     * @param count                the total number of testcases to be generated.
     * @return A list of testcase iterators.
     */
    private static List<Iterator<TestCase>> createIterators(Set<RISCV_SUBSET> subsets, Set<RISCV_OBSERVATION_TYPE> allowed_observations, long seed, int THREADS, int count) {
        List<Iterator<TestCase>> iterators = new ArrayList<>(THREADS);
        Random r = new Random(seed);
        for (int i = 0; i < THREADS; i++) {
            iterators.add(new RISCVTestIterator(subsets, allowed_observations, r.nextLong(), count / THREADS + (count % THREADS > i ? 1 : 0)));
        }
        return iterators;
    }

    /**
     * @param subsets              the ISA subsets to consider.
     * @param allowed_observations the observations under consideration.
     * @param seed                 a random seed.
     * @param THREADS              the number of threads that will be used for generation
     * @param count                the total number of testcases to be generated.
     */
    public RISCVIterativeTests(Set<RISCV_SUBSET> subsets, Set<RISCV_OBSERVATION_TYPE> allowed_observations, long seed, int THREADS, int count) {
        super(createIterators(subsets, allowed_observations, seed, THREADS, count), count);
    }
}
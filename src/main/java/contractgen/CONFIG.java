package contractgen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import contractgen.riscv.isa.RISCV_SUBSET;
import contractgen.riscv.isa.contract.RISCVContract;
import contractgen.riscv.isa.contract.RISCV_OBSERVATION_TYPE;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A configuration for the contract generation.
 */
@SuppressWarnings("MissingJavadoc")
public class CONFIG {
    public enum CONTRACT_SOURCE {NEW, EXISTING, PREDEFINED}

    public enum PROCESSOR {IBEX, CVA6}

    public final String NAME;
    public final PROCESSOR CORE;
    public final Set<RISCV_SUBSET> subsets;
    public final Set<RISCV_OBSERVATION_TYPE> allowed_observations;
    public final int THREADS;
    public final boolean DEBUG;

    public final CONTRACT_SOURCE TRAINING_SOURCE;
    public final int TRAINING_NEW_COUNT;
    public final long TRAINING_NEW_SEED;
    public final String TRAINING_EXISTING_NAME;
    public final boolean TRAINING_EXISTING_FLIP_T_E;
    public final RISCVContract TRAINING_PREDEFINED;

    public final CONTRACT_SOURCE EVAL_SOURCE;
    public final int EVAL_NEW_COUNT;
    public final long EVAL_NEW_SEED;
    public final String EVAL_EXISTING_NAME;
    public final boolean EVAL_EXISTING_FLIP_T_E;

    /**
     * @param file The file to write the config.
     * @throws IOException On filesystem errors.
     */
    public void toJSON(FileWriter file) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        gson.toJson(this, file);
        file.flush();
    }

    public String getPATH() {
        return "results/" + (switch (this.CORE) {
            case IBEX -> "ibex";
            case CVA6 -> "cva6";
        }) + "/";
    }

    /**
     * @param NAME                       The name of the config.
     * @param subsets                    The allowed ISA subsets.
     * @param allowed_observations       The allowed observations.
     * @param THREADS                    The number of threads.
     * @param TRAINING_SOURCE            The training data source.
     * @param TRAINING_NEW_COUNT         The number of testcases for training.
     * @param TRAINING_NEW_SEED          A seed for the training testcases.
     * @param TRAINING_EXISTING_NAME     If an existing training should be used, the name.
     * @param TRAINING_EXISTING_FLIP_T_E Whether the training or eval of the existing set should be used.
     * @param TRAINING_PREDEFINED        Use a predefined contract to evaluate performance.
     * @param EVAL_SOURCE                The eval data source.
     * @param EVAL_NEW_COUNT             The number of testcases for evaluation.
     * @param EVAL_NEW_SEED              A seed for the evaluation testcases.
     * @param EVAL_EXISTING_NAME         If an existing evaluation set should be used, the name.
     * @param EVAL_EXISTING_FLIP_T_E     Whether the training or eval of the existing set should be used.
     */
    private CONFIG(String NAME, PROCESSOR CORE, Set<RISCV_SUBSET> subsets, Set<RISCV_OBSERVATION_TYPE> allowed_observations, int THREADS, boolean DEBUG, CONTRACT_SOURCE TRAINING_SOURCE, int TRAINING_NEW_COUNT, long TRAINING_NEW_SEED, String TRAINING_EXISTING_NAME, boolean TRAINING_EXISTING_FLIP_T_E, RISCVContract TRAINING_PREDEFINED, CONTRACT_SOURCE EVAL_SOURCE, int EVAL_NEW_COUNT, long EVAL_NEW_SEED, String EVAL_EXISTING_NAME, boolean EVAL_EXISTING_FLIP_T_E) {
        this.NAME = NAME;
        this.CORE = CORE;
        this.subsets = subsets;
        this.allowed_observations = allowed_observations;
        this.THREADS = THREADS;
        this.DEBUG = DEBUG;
        this.TRAINING_SOURCE = TRAINING_SOURCE;
        this.TRAINING_NEW_COUNT = TRAINING_NEW_COUNT;
        this.TRAINING_NEW_SEED = TRAINING_NEW_SEED;
        this.TRAINING_EXISTING_NAME = TRAINING_EXISTING_NAME;
        this.TRAINING_EXISTING_FLIP_T_E = TRAINING_EXISTING_FLIP_T_E;
        this.TRAINING_PREDEFINED = TRAINING_PREDEFINED;
        this.EVAL_SOURCE = EVAL_SOURCE;
        this.EVAL_NEW_COUNT = EVAL_NEW_COUNT;
        this.EVAL_NEW_SEED = EVAL_NEW_SEED;
        this.EVAL_EXISTING_NAME = EVAL_EXISTING_NAME;
        this.EVAL_EXISTING_FLIP_T_E = EVAL_EXISTING_FLIP_T_E;
    }

    /**
     * @return The ibex_small config.
     */
    public static CONFIG ibex_small() {
        return new CONFIG(
                "ibex_small",
                PROCESSOR.IBEX,
                Set.of(RISCV_SUBSET.BASE, RISCV_SUBSET.M),
                Arrays.stream(RISCV_OBSERVATION_TYPE.values()).collect(Collectors.toSet()),
                126,
                true,
                CONTRACT_SOURCE.NEW,
                20000,
                123456789,
                "",
                false,
                null,
                CONTRACT_SOURCE.NEW,
                100000,
                987654321,
                "",
                false
        );
    }

    /**
     * @return The ibex_large config.
     */
    public static CONFIG ibex_large() {
        return new CONFIG(
                "ibex_large",
                PROCESSOR.IBEX,
                Set.of(RISCV_SUBSET.BASE, RISCV_SUBSET.M),
                Arrays.stream(RISCV_OBSERVATION_TYPE.values()).collect(Collectors.toSet()),
                126,
                true,
                CONTRACT_SOURCE.NEW,
                100000,
                123456789,
                "",
                false,
                null,
                CONTRACT_SOURCE.NEW,
                2000000,
                987654321,
                "",
                false
        );
    }

    /**
     * @return The cva6_small config.
     */
    public static CONFIG cva6_small() {
        return new CONFIG(
                "cva6_small",
                PROCESSOR.CVA6,
                Set.of(RISCV_SUBSET.BASE, RISCV_SUBSET.M),
                Arrays.stream(RISCV_OBSERVATION_TYPE.values()).collect(Collectors.toSet()),
                126,
                true,
                CONTRACT_SOURCE.NEW,
                20000,
                123456789,
                "",
                false,
                null,
                CONTRACT_SOURCE.NEW,
                100000,
                987654321,
                "",
                false
        );
    }

    /**
     * @return The cva6_large config.
     */
    public static CONFIG cva6_large() {
        return new CONFIG(
                "cva6_large",
                PROCESSOR.CVA6,
                Set.of(RISCV_SUBSET.BASE, RISCV_SUBSET.M),
                Arrays.stream(RISCV_OBSERVATION_TYPE.values()).collect(Collectors.toSet()),
                126,
                true,
                CONTRACT_SOURCE.NEW,
                100000,
                123456789,
                "",
                false,
                null,
                CONTRACT_SOURCE.NEW,
                2000000,
                987654321,
                "",
                false
        );
    }
}

package contractgen.riscv.isa.contract;

import com.google.gson.*;
import contractgen.Contract;
import contractgen.TestResult;
import contractgen.Observation;
import contractgen.Updater;
import contractgen.riscv.isa.RISCV_TYPE;
import contractgen.updater.ILPUpdater;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A contract for the RISC-V ISA.
 */
public class RISCVContract extends Contract {

    /**
     * @param updater The updater to be used to compute the contract.
     */
    public RISCVContract(Updater updater) {
        super(updater);
    }

    /**
     * @param initialResults Already available results to resume a computation.
     * @param updater        The updater to be used to compute the contract.
     */
    public RISCVContract(List<TestResult> initialResults, Updater updater) {
        super(initialResults, updater);
    }

    /**
     * @param allowed The set of allowed observations.
     */
    public void restrictObservations(Set<RISCV_OBSERVATION_TYPE> allowed) {
        getTestResults().forEach(res -> ((RISCVTestResult) res).restrictObservations(allowed));
    }

    /**
     * @return A contract that only contains the applicable observations.
     */
    public RISCVContract filterApplicable() {
        List<TestResult> filtered_res = new ArrayList<>(this.getTestResults().size());
        getTestResults().forEach(res -> {
                    Set<RISCVObservation> filered_obs = new HashSet<>();
                    res.getPossibleObservations().forEach(obs -> {
                        if (obs.isApplicable()) filered_obs.add((RISCVObservation) obs);
                    });
                    filtered_res.add(new RISCVTestResult(filered_obs, res.isAdversaryDistinguishable(), res.getIndex()));
                }
        );
        return new RISCVContract(filtered_res, this.updater);
    }

    @Override
    public String printContract() {
        Map<RISCV_TYPE, List<RISCVObservation>> observations_per_type =
                getCurrentContract().stream().map(obs -> (RISCVObservation) obs).collect(Collectors.groupingBy(RISCVObservation::type));

        StringBuilder sb = new StringBuilder();

        observations_per_type.forEach((type, observations) -> {
            sb.append(type.generateContract("1"));
            for (RISCV_OBSERVATION_TYPE observation_type : RISCV_OBSERVATION_TYPE.values()) {
                sb.append(observation_type.generateObservation("1", observations.stream().anyMatch(o -> o.observation() == observation_type)));
            }
            sb.append("end\n");
        });

        observations_per_type.forEach((type, observations) -> {
            sb.append(type.generateContract("2"));
            for (RISCV_OBSERVATION_TYPE observation_type : RISCV_OBSERVATION_TYPE.values()) {
                sb.append(observation_type.generateObservation("2", observations.stream().anyMatch(o -> o.observation() == observation_type)));
            }
            sb.append("end\n");
        });

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTotalStats());
        sb.append(getContractStats());
        sb.append("\tInferred contract: \n");
        getCurrentContract().stream().sorted(Comparator.comparing(Observation::toString)).forEach(obs -> sb.append(obs).append("\n"));
        return sb.toString();
    }

    /**
     * Reads a json string and instantiates the according contract.
     *
     * @param json A string with the serialized contract.
     * @return A new contract as specified in the json.
     */
    public static RISCVContract fromJSON(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(TestResult.class, new TestResultDeserializer());
        builder.registerTypeAdapter(Observation.class, new ObservationDeserializer());
        builder.registerTypeAdapter(Updater.class, new UpdaterDeserializer());
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        return gson.fromJson(json, RISCVContract.class);
    }

    /**
     * Reads a json string and instantiates the according contract.
     *
     * @param file The file that contains the dumped contract.
     * @return A new contract as specified in the file.
     * @throws IOException On filesystem errors.
     */
    public static RISCVContract fromJSON(FileReader file) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(TestResult.class, new TestResultDeserializer());
        builder.registerTypeAdapter(Observation.class, new ObservationDeserializer());
        builder.registerTypeAdapter(Updater.class, new UpdaterDeserializer());
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        return gson.fromJson(file, RISCVContract.class);
    }

    /**
     * Deserializes the class TestResult.
     */
    public static class TestResultDeserializer implements JsonDeserializer<TestResult> {

        @Override
        public TestResult deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            JsonArray jsonPossibilities = jsonObject.get("observations").getAsJsonArray();
            boolean adversaryDistinguishable = jsonObject.get("adversaryDistinguishable").getAsBoolean();
            int index = jsonObject.has("index") ? jsonObject.get("index").getAsInt() : 0;
            Set<RISCVObservation> possibilities = jsonPossibilities.asList().stream().map(jsonE -> gson.fromJson(jsonE, RISCVObservation.class)).collect(Collectors.toSet());
            return new RISCVTestResult(possibilities, adversaryDistinguishable, index);
        }
    }

    /**
     * Deserializes an observation.
     */
    public static class ObservationDeserializer implements JsonDeserializer<Observation> {

        @Override
        public Observation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            JsonElement jsonType = jsonObject.get("type");
            JsonElement jsonObservation = jsonObject.get("observation");
            return new RISCVObservation(gson.fromJson(jsonType, RISCV_TYPE.class), gson.fromJson(jsonObservation, RISCV_OBSERVATION_TYPE.class));
        }
    }

    /**
     * Deserializes the updater.
     */
    public static class UpdaterDeserializer implements JsonDeserializer<Updater> {

        @Override
        public Updater deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            // TODO Allow to specify the updater or read it from json.
            return new ILPUpdater();
        }
    }

    /**
     * @return A contract that leaks the instructions, branches, multiplication operands and memory addresses.
     */
    public static RISCVContract getIBMA() {
        List<TestResult> results = new ArrayList<>();
        int i = 0;
        for (RISCV_TYPE type : RISCV_TYPE.values()) {
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.NEW_PC)), true, i++));
        }
        for (RISCV_TYPE type : RISCV_TYPE.values()) {
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.BRANCH_TAKEN)), true, i++));
        }
        for (RISCV_TYPE type : RISCV_TYPE.values()) {
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.MEM_ADDR)), true, i++));
        }
        for (RISCV_TYPE type : RISCV_TYPE.values()) {
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.RAW_RS1_1)), true, i++));
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.RAW_RS1_2)), true, i++));
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.RAW_RS1_3)), true, i++));
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.RAW_RS1_4)), true, i++));
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.RAW_RS2_1)), true, i++));
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.RAW_RS2_2)), true, i++));
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.RAW_RS2_3)), true, i++));
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.RAW_RS2_4)), true, i++));
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.WAW_1)), true, i++));
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.WAW_2)), true, i++));
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.WAW_3)), true, i++));
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.WAW_4)), true, i++));
        }
        for (RISCV_TYPE type : Set.of(RISCV_TYPE.MUL, RISCV_TYPE.MULH, RISCV_TYPE.MULHU, RISCV_TYPE.MULHSU, RISCV_TYPE.DIV, RISCV_TYPE.DIVU, RISCV_TYPE.REM, RISCV_TYPE.REMU)) {
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.REG_RS1)), true, i++));
            results.add(new RISCVTestResult(Set.of(new RISCVObservation(type, RISCV_OBSERVATION_TYPE.REG_RS2)), true, i++));
        }
        return new RISCVContract(results, new ILPUpdater());
    }
}

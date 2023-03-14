package contractgen.riscv.isa.contract;

import com.google.gson.*;
import contractgen.Contract;
import contractgen.TestResult;
import contractgen.Observation;
import contractgen.Updater;
import contractgen.riscv.isa.RISCV_TYPE;
import contractgen.updater.ILPUpdater;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        StringBuilder sb = new StringBuilder("Current negative results: \n");
        getTestResults().stream().filter(res -> res.isAdversaryDistinguishable()).forEach(ctx ->sb.append(ctx).append("\n"));
        //sb.append("Current positive results: \n");
        //getTestResults().stream().filter(res -> !res.isAdversaryDistinguishable()).forEach(ctx ->sb.append(ctx).append("\n"));
        sb.append("Inferred contract: \n");
        getCurrentContract().forEach(obs -> sb.append(obs.toString()).append("\n"));
        return sb.toString();
    }

    /**
     * Reads a json string and instantiates the according contract.
     *
     * @param json A string with the serialized contract.
     * @return     A new contract as specified in the json.
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
}

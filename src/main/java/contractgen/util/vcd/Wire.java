package contractgen.util.vcd;

import contractgen.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A wire in a vcd trace.
 */
public class Wire {

    /**
     * The name of the wire.
     */
    private final String name;
    /**
     * The identifier used in the VCD file.
     */
    private final String internal_name;
    /**
     * The width of the wire.
     */
    private final int width;
    /**
     * The set of values, every value is valid until the next entry.
     */
    Map<Integer, String> values = new HashMap<>();

    /**
     * @param name          The name of the wire.
     * @param internal_name The internal name of the wire (in the trace).
     * @param width         The width of the wire.
     */
    public Wire(String name, String internal_name, int width) {
        this.name = name;
        this.internal_name = internal_name;
        this.width = width;
    }

    /**
     * @return The name of the wire.
     */
    public String getName() {
        return name;
    }

    /**
     * @param time The time.
     * @return The value at this time.
     */
    public String getValueAt(Integer time) {
        return values.get(values.keySet().stream().filter(key -> key <= time).max(Integer::compare).orElseThrow());
    }

    /**
     * @return The tima at which the value of this wire changed last.
     */
    public Integer getLastChangeTime() {
        return values.keySet().stream().filter(time -> !Objects.equals(values.get(time), values.get(values.keySet().stream().filter(key -> key < time).max(Integer::compare).orElse(0)))).max(Integer::compare).orElse(null);
    }

    /**
     * @param i The time.
     * @return The time at which the wire changed before the time i.
     */
    public Integer getLastChangeBeforeTime(Integer i) {
        return values.keySet().stream().filter(t -> t < i).filter(time -> !Objects.equals(values.get(time), values.get(values.keySet().stream().filter(key -> key < time).max(Integer::compare).orElse(0)))).max(Integer::compare).orElse(null);
    }

    /**
     * @param s The requested value.
     * @return The time at which the wire first had the requested value.
     */
    public Integer getFirstTimeValue(String s) {
        return values.keySet().stream().sorted().filter(t -> StringUtils.equalValue(values.get(t), s)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "Wire{" +
                "name='" + name + '\'' +
                ", internal_name='" + internal_name + '\'' +
                ", width=" + width +
                '}';
    }
}

package vcd;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Wire {

    private final String name;
    private final String internal_name;
    private final int width;
    Map<Integer, String> values = new HashMap<>();

    public Wire(String name, String internal_name, int width) {
        this.name = name;
        this.internal_name = internal_name;
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public String getValueAt(Integer time) {
        return values.get(values.keySet().stream().filter(key -> key <= time).max(Integer::compare).orElseThrow());
    }

    public Integer getLastChangeTime() {
        //values.keySet().stream().filter(t -> values.get(t) != null)
        return values.keySet().stream().filter(time -> !Objects.equals(values.get(time), values.get(values.keySet().stream().filter(key -> key < time).max(Integer::compare).orElse(0)))).max(Integer::compare).orElse(null);
    }

    public Integer getLastChangeBeforeTime(Integer i) {
        //values.keySet().stream().filter(t -> values.get(t) != null)
        return values.keySet().stream().filter(t -> t < i).filter(time -> !Objects.equals(values.get(time), values.get(values.keySet().stream().filter(key -> key < time).max(Integer::compare).orElse(0)))).max(Integer::compare).orElse(null);
    }

    public Integer getFirstTimeValue(String  s) {
        //values.keySet().stream().filter(t -> values.get(t) != null)
        return values.keySet().stream().sorted().filter(t -> values.get(t).endsWith(s)).findFirst().orElse(null);
        //return values.keySet().stream().filter(t -> t < i).filter(time -> !Objects.equals(values.get(time), values.get(values.keySet().stream().filter(key -> key < time).max(Integer::compare).orElse(0)))).max(Integer::compare).orElse(null);
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

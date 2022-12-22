package contractgen.util.vcd;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Module {

    private final String name;

    private final Module parent;
    private Map<String, Module> children = new HashMap<>();
    private Map<String, Wire> wireSet = new HashMap<>();

    public Module(Module parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addWire(Wire w) {
        wireSet.put(w.getName(), w);
    }

    public Wire getWire(String name) {
        return wireSet.get(name);
    }

    public void addChild(Module module) {
        this.children.put(module.getName(), module);
    }

    public Module getChild(String name) {
        return this.children.getOrDefault(name, null);
    }

    public Module getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "Module{" +
                "name='" + name + '\'' +
                ", wireSet=" + wireSet + '\'' +
                (parent != null ? ", parent=" + parent.name : ", top ") +
                "\n" + children.values().stream().map(Module::toString).collect(Collectors.joining("\n")) +
                '}';
    }
}

package contractgen.util.vcd;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A module in a vcd trace.
 */
public class Module {

    /**
     * The name of the module.
     */
    private final String name;

    /**
     * The parent module or null if toplevel.
     */
    private final Module parent;

    /**
     * The modules children identified by their name.
     */
    private final Map<String, Module> children = new HashMap<>();

    /**
     * The set of wires in this module identified by their name.
     */
    private final Map<String, Wire> wireSet = new HashMap<>();

    /**
     * @param parent Its parent module.
     * @param name   The name of the module.
     */
    public Module(Module parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    /**
     * @return The name of the module.
     */
    public String getName() {
        return name;
    }

    /**
     * @param w Add a wire to this module.
     */
    public void addWire(Wire w) {
        wireSet.put(w.getName(), w);
    }

    /**
     * @param name The name of the wire.
     * @return The wire.
     */
    public Wire getWire(String name) {
        return wireSet.get(name);
    }

    /**
     * @param module Add module as child module.
     */
    public void addChild(Module module) {
        this.children.put(module.getName(), module);
    }

    /**
     * @param name The name of the child.
     * @return The child.
     */
    public Module getChild(String name) {
        return this.children.getOrDefault(name, null);
    }

    /**
     * @return The parent module.
     */
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

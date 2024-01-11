package contractgen.util.vcd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A vcd trace.
 */
public class VcdFile {

    /**
     * The timesacle property of the VCD file.
     */
    private String timescale = null;
    /**
     * The date property of the VCD file.
     */
    private String date = null;
    /**
     * The version property of the VCD file.
     */
    private String version = null;
    /**
     * The top module.
     */
    private Module top = null;
    /**
     * The current module (used while parsing).
     */
    private Module current = null;

    /**
     * The current time (used while parsing).
     */
    private Integer time = null;

    /**
     * The pattern to identify variables.
     */
    private final Pattern varPattern = Pattern.compile("\\s?(.*?) (.*?) (.*?) (.*?)( .*|$)");

    /**
     * The set of wires in the vcd file.
     */
    private final Map<String, Wire> wires = new HashMap<>();

    /**
     * @param s The vcd trace to be parsed.
     */
    public VcdFile(String s) {
        String[] lines = s.split("\\$enddefinitions\\s*\\$end\n");
        assert lines.length == 2;
        String definitions = lines[0];
        String simulation = lines[1];
        parseDefinitions(definitions);
        parseSimulation(simulation);
    }

    /**
     * @return The top module.
     */
    public Module getTop() {
        return top;
    }


    /**
     * @param simulation the simulation part of the VCD file.
     */
    private void parseSimulation(String simulation) {
        List<String> lines = List.of(simulation.split("\n"));
        Pattern p = Pattern.compile("b([01xzXZ]*) ?(.*)|([01xzXZ]) ?(.*)");
        Pattern timePattern = Pattern.compile("#\\d*");
        Pattern infoStatement = Pattern.compile("\\$comment.*\\$end|\\$dumpall|\\$dumpvars|\\$end");
        for (String line : lines) {
            if (timePattern.matcher(line).matches()) {
                time = Integer.parseInt(line.substring(1));
            } else {
                Matcher m = p.matcher(line);
                if (!m.find()) {
                    if (infoStatement.matcher(line).matches()) continue;
                    System.out.println("Unexpected line " + line);
                    continue;
                }
                String value = m.group(1) == null ? m.group(3) : m.group(1);
                String name = m.group(2) == null ? m.group(4) : m.group(2);
                if (!wires.containsKey(name))
                    throw new IllegalStateException("Wire " + name + " not found.");
                wires.get(name).values.put(time, value);
            }
        }
    }

    /**
     * @param definitions the definition part of the VCD file.
     */
    private void parseDefinitions(String definitions) {
        Pattern p = Pattern.compile("\\$(.*?)(?: (.*?) | )\\$end\\n?");
        Matcher m = p.matcher(definitions);
        m.results().forEach(res -> parseDefinition(res.group(1), res.group(2)));
    }

    /**
     * @param name    The name of the definition, e.g. date, scope var etc.
     * @param content The content of this property.ti
     */
    private void parseDefinition(String name, String content) {
        if ("comment".equals(name)) {
            System.out.println("Found comment " + content);
            //ignoring
        } else if ("date".equals(name)) {
            this.date = content;
        } else if ("enddefinitions".equals(name)) {
            throw new IllegalStateException("Definitions should only end once.");
        } else if ("scope".equals(name)) {
            Pattern p = Pattern.compile("(.*) (.*)");
            Matcher m = p.matcher(content);
            if (m.find()) {
                if (m.group(1).equals("module") || m.group(1).equals("function") || m.group(1).equals("begin")) {
                    Module module = new Module(current, m.group(2));
                    if (current == null) {
                        if (top != null) {
                            module = top;
                        } else {
                            top = module;
                        }
                    } else {
                        current.addChild(module);
                    }
                    current = module;
                } else {
                    System.out.println("Ignoring " + content);
                }
                // ignoring all other types of scope
            }
        } else if ("timescale".equals(name)) {
            this.timescale = content;
        } else if ("upscope".equals(name)) {
            current = current.getParent();
        } else if ("var".equals(name)) {
            Matcher m = varPattern.matcher(content);
            if (m.find()) {
                switch (m.group(1)) {
                    case "wire", "reg", "parameter" -> {
                        if (current == null)
                            throw new IllegalStateException("Not in a scope.");
                        int width = Integer.parseInt(m.group(2));
                        String internal_name = m.group(3);
                        String wire_name = m.group(4);
                        Wire w = new Wire(wire_name, internal_name, width);
                        current.addWire(w);
                        wires.put(internal_name, w);
                    }
                    case "integer", "event" -> {
                        int width = Integer.parseInt(m.group(2));
                        String internal_name = m.group(3);
                        String wire_name = m.group(4);
                        Wire w = new Wire(wire_name, internal_name, width);
                        wires.put(internal_name, w);
                    }
                    default ->
                            throw new IllegalArgumentException("Unsupported var type " + m.group(1) + " in context " + content);
                }
                // ignoring all other types of scope
            }

        } else if ("version".equals(name)) {
            this.version = content;
        }

    }

    @Override
    public String toString() {
        return "VcdFile{" +
                "date='" + date + '\'' +
                ", version='" + version + '\'' +
                ", timescale='" + timescale + '\'' +
                ", top=" + top +
                ", wires=" + wires +
                '}';
    }
}

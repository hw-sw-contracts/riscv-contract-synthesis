package vcd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VcdFile {

    private String timescale = null;
    private String date = null;
    private String version = null;
    private Module top = null;
    private Module current = null;

    private Integer time = null;

    private Map<String, Wire> wires = new HashMap<>();

        public VcdFile(String s) {
            String[] lines = s.split("\\$enddefinitions\\s*\\$end\n");
            assert lines.length == 2;
            String definitions = lines[0];
            String simulation = lines[1];
            parseDefinitions(definitions);
            parseSimulation(simulation);
        }

    public Module getTop() {
        return top;
    }


    private void parseSimulation(String simulation) {
            List<String> lines = List.of(simulation.split("\n"));
            for (String line: lines) {
                if (line.matches("#\\d*")) {
                    time = Integer.parseInt(line.substring(1));
                } else {
                    Pattern p = Pattern.compile("b([01xzXZ]*) ?(.*)|([01xzXZ]) ?(.*)");
                    Matcher m = p.matcher(line);
                    if (!m.find()) {
                        System.out.println("Unexpected line " + line);
                        continue;
                    }
                    String value = m.group(1) == null ? m.group(3) : m.group(1);
                    String name = m.group(2) == null ? m.group(4): m.group(2);
                    if (!wires.containsKey(name))
                        throw new IllegalStateException("Wire " + name + " not found.");
                    wires.get(name).values.put(time, value);
                }
            }
        }

        private void parseDefinitions(String definitions) {
            Pattern p = Pattern.compile("\\$(.*?)(?: (.*?) | )\\$end\\n?");
            Matcher m = p.matcher(definitions);
            m.results().forEach(res -> parseDefinition(res.group(1), res.group(2)));
        }

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
                    if (m.group(1).equals("module")) {
                        Module module = new Module(current, m.group(2));
                        if (current == null) {
                            top = module;
                        } else {
                            current.addChild(module);
                        }
                        current = module;
                    }
                    // ignoring all other types of scope
                }
            } else if ("timescale".equals(name)) {
                this.timescale = content;
            } else if ("upscope".equals(name)) {
                current = current.getParent();
            } else if ("var".equals(name)) {
                Pattern p = Pattern.compile("(.*) (.*) (.*) (.*)");
                Matcher m = p.matcher(content);
                if (m.find()) {
                    if (m.group(1).equals("wire")) {
                        if (current == null)
                            throw new IllegalStateException("Not in a scope.");
                        int width = Integer.parseInt(m.group(2));
                        String internal_name = m.group(3);
                        String wire_name = m.group(4);
                        Wire w = new Wire(wire_name, internal_name, width);
                        current.addWire(w);
                        wires.put(internal_name, w);
                    } else if (m.group(1).equals("integer")) {
                        int width = Integer.parseInt(m.group(2));
                        String internal_name = m.group(3);
                        String wire_name = m.group(4);
                        Wire w = new Wire(wire_name, internal_name, width);
                        wires.put(internal_name, w);
                    } else if (m.group(1).equals("event")) {
                        int width = Integer.parseInt(m.group(2));
                        String internal_name = m.group(3);
                        String wire_name = m.group(4);
                        Wire w = new Wire(wire_name, internal_name, width);
                        wires.put(internal_name, w);
                    } else {
                        throw new IllegalArgumentException("Unsupported var type " + m.group(1));
                    }
                    // ignoring all other types of scope
                }

            } else if ("version".equals(name)) {
                this.version = content;
            }

        }

        public static void main(String[] args) throws IOException {
            Path p = Path.of("C:\\Users\\Gideon\\Documents\\Uni\\SS2022\\Bachelor\\simple_proc\\syn\\verif_out\\18_07_2022_13_50_08\\verif_cover\\engine_0\\trace0.vcd");
            String s = Files.readString(p);
            VcdFile vcd = new VcdFile(s);
            System.out.println(vcd);
            System.out.println(vcd.top.getWire("atk_equiv"));
            System.out.println(vcd.top.getChild("control").getWire("cycle_count").getValueAt(117));
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

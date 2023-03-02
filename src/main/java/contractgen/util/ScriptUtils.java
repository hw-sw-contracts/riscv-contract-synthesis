package contractgen.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ScriptUtils {

    public static String runScript(String path, boolean silent) {
        Process p = null;
        try {
            StringBuilder sb = new StringBuilder();
            // adding command and args to the list
            List<String> cmdList = new ArrayList<>(List.of(path.split(" +")));
            ProcessBuilder pb = new ProcessBuilder(cmdList);
            pb.directory(new File(path.split(" +")[0]).getParentFile());
            // System.out.println("Starting Script...");
            boolean success = false;
            while (!success) {
                try {
                    p = pb.start();
                    p.waitFor();
                    success = true;
                } catch (IOException e) { // binary may be busy

                }
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            if (!silent) {
                System.out.println(sb);
            }
            return sb.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}

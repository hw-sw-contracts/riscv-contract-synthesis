package contractgen.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Util methods related to the filesystem.
 */
public class FileUtils {
    /**
     * @param source  The source file or folder.
     * @param dest    The destination file or folder.
     * @param options The options used to copy the file or folder.
     * @throws IOException On filesystem errors.
     */
    public static void copyFileOrFolder(File source, File dest, CopyOption... options) throws IOException {
        if (source.isDirectory()) copyFolder(source, dest, options);
        else {
            ensureParentFolder(dest);
            copyFile(source, dest, options);
        }
    }

    /**
     * @param source  The source folder.
     * @param dest    The destination folder.
     * @param options The options used to copy the folder.
     * @throws IOException On filesystem errors.
     */
    private static void copyFolder(File source, File dest, CopyOption... options) throws IOException {
        if (!dest.exists()) {
            boolean success = dest.mkdirs();
            if (!success) throw new IOException("Failed to create directory");
        }
        File[] contents = source.listFiles();
        if (contents != null) {
            for (File f : contents) {
                File newFile = new File(dest.getAbsolutePath() + File.separator + f.getName());
                if (f.isDirectory()) copyFolder(f, newFile, options);
                else copyFile(f, newFile, options);
            }
        }
    }

    /**
     * @param source  The source file.
     * @param dest    The destination file.
     * @param options The options used to copy the file.
     * @throws IOException On filesystem errors.
     */
    private static void copyFile(File source, File dest, CopyOption... options) throws IOException {
        if (Files.isSymbolicLink(source.toPath()))
            return;
        Files.copy(source.toPath(), dest.toPath(), options);
    }

    /**
     * Creates the parent directory of a file if it does not exist.
     *
     * @param file The file.
     * @throws IOException On filesystem errors.
     */
    private static void ensureParentFolder(File file) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            boolean success = parent.mkdirs();
            if (!success) throw new IOException("Failed to create directory");
        }
    }

    /**
     * @param filePath    The file path.
     * @param text        The string to be replaced.
     * @param replacement The replacement string.
     */
    public static void replaceString(String filePath, String text, String replacement) {

        Path path = Paths.get(filePath);
        // Get all the lines
        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            // Do the replace operation
            List<String> list = stream.map(line -> line.replace(text, replacement)).collect(Collectors.toList());
            // Write the content back
            Files.write(path, list, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

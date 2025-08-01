package wget.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static List<String> readFile(String path) throws IOException {
        List<String> urls = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                urls.add(line.trim());
            }
        }
        return urls;
    }

    public static String extractFileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1).split("\\?")[0];
    }

    public static String parsePath(String path) throws IOException {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        String parsed = path.trim();

        if (parsed.startsWith("~/")) {
            String homeDir = System.getProperty("user.home");
            if (homeDir == null) {
                throw new IOException("Cannot determine user home directory");
            }
            parsed = homeDir + parsed.substring(1);
        }

        return parsed;
    }

    public static void ensureExists(String path) throws IOException {
        java.nio.file.Path p = java.nio.file.Paths.get(path);
        java.nio.file.Files.createDirectories(p);
    }

    public static String normalizePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        String normalized = path.trim();
        if (!normalized.endsWith("/")) {
            normalized += "/";
        }

        return normalized;
    }
}

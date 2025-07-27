package wget.download;

import java.io.IOException;

public class PathManager {

    public static String parsePath(String path) throws IOException {
        return path;
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

    public static void ensureExists(String path) throws IOException {
        java.nio.file.Path p = java.nio.file.Paths.get(path);
        java.nio.file.Files.createDirectories(p); // idempotent
    }
}

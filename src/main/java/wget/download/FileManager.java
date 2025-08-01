package wget.download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import wget.cli.ArgumentParser;
import wget.cli.OutputFormatter;
import wget.utils.FileUtils;
import wget.utils.TerminalUtils;

public class FileManager {
    private static final int BUFFER_SIZE = 8192;
    private final String fileName;
    private final String path;

    public FileManager(String fileName, String path) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        this.fileName = fileName;
        this.path = FileUtils.normalizePath(path);
    }

    public void save(HttpURLConnection conn, int contentLength, boolean inBackground, boolean inAsync,
            RateLimiter rateLimiter) throws IOException {
        try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(path + fileName)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long downloaded = 0;
            long startNano = System.nanoTime();

            final int barWidth = TerminalUtils.getTerminalWidth() - 65;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                downloaded += bytesRead;

                if (rateLimiter != null) {
                    try {
                        rateLimiter.throttle(bytesRead);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Download interrupted", e);
                    }
                }

                if (!inBackground && !inAsync) {
                    OutputFormatter.printProgressBar(downloaded, contentLength, barWidth, startNano);
                }
            }

            if (!inBackground && !inAsync) {
                System.out.print("\n\n");
            }
        }
    }

    public static String determineFileName(ArgumentParser parser, String url) {
        if (parser.hasOption("O")) {
            return parser.getOptionValue("O");
        }
        return FileUtils.extractFileName(url);
    }
}

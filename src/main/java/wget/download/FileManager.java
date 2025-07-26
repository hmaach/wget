package wget.download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import wget.utils.TerminalUtils;

public class FileManager {

    private static final int BUFFER_SIZE = 8192;

    public static void saveToFile(HttpURLConnection conn, String fileName, int contentLength) throws IOException {
        // validateFileName(fileName);
        try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(fileName)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            long downloaded = 0;
            long startNano = System.nanoTime();

            final int barWidth = TerminalUtils.getTerminalWidth() - 65; // keep spaces for more data estimated in 65 chars

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                downloaded += bytesRead;

                ProgressPrinter.printProgressBar(downloaded, contentLength, barWidth, startNano);
            }
            System.out.print("\n\n");
        }
    }

    // private void validateFileName(String fileName) {
    //     if (fileName == null || fileName.trim().isEmpty()) {
    //         throw new IllegalArgumentException("File name cannot be null or empty");
    //     }
    // }
}

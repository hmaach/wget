package wget.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import wget.download.PathManager;

public class LoggerUtils {

    private final String FILE_NAME = "wget";
    private final String PATH = "./logs/" + FILE_NAME + ".log";

    public LoggerUtils() throws IOException {
        try {
            PathManager.ensureExists("./logs/"); // Ensure the directory exists
        } catch (IOException e) {
            throw new IOException("Error creating logs directory: " + e.getMessage());
        }
    }

    public void log(String data) throws IOException {
        try (FileWriter fw = new FileWriter(PATH, true); // true = append mode
                 PrintWriter pw = new PrintWriter(fw)) {
            pw.println(data);
        }
    }

    public String getLogPath() {
        return PATH;
    }
}

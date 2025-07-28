package wget.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import wget.download.PathManager;

public class Logger {

    private final String FILE_NAME = "wget-log";
    private final String PATH = "./logs/" + FILE_NAME + ".log";

    public Logger() throws IOException {
        try {
            PathManager.ensureExists("./logs/");
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

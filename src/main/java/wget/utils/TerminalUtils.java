package wget.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TerminalUtils {

    public static int getTerminalWidth() {
        try {
            Process process = new ProcessBuilder("sh", "-c", "stty size < /dev/tty").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null && line.matches("\\d+\\s+\\d+")) {
                String[] parts = line.trim().split("\\s+");
                int cols = Integer.parseInt(parts[1]);
                return cols;
            }
        } catch (Exception e) {
        }
        // Default size if detection fails
        return 44;
    }
}

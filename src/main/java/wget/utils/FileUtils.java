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
}

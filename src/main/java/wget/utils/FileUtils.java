package wget.utils;

public class FileUtils {

    public static String extractFileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1).split("\\?")[0];
    }
}

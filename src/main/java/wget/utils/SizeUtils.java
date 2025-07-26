package wget.utils;

public class SizeUtils {

    public static String convertToMB(long bytes) {
        double mb = bytes / (1024.0 * 1024.0);
        return String.format("%.2f MB", mb);
    }
}

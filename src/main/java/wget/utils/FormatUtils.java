package wget.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtils {

    /**
     * Convert byte count into human-readable format.
     */
    public static String convertToMB(long bytes) {
        double mb = bytes / (1024.0 * 1024.0);
        return String.format("%.2f MB", mb);
    }

    /**
     * Extract file name from URL.
     */
    public static String extractFileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1).split("\\?")[0];
    }

    /**
     * Get current timestamp formatted as yyyy-MM-dd HH:mm:ss.
     */
    public static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * Format byte count into human-readable string with units. E.g. 1024 ->
     * "1.00 KB", 1536 -> "1.50 KB", 1048576 -> "1.00 MB"
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char unit = "KMGTPE".charAt(exp - 1);
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), unit);
    }
}

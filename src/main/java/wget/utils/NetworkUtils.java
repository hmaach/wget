package wget.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    public static HttpURLConnection createConnection(String urlStr, String method) throws IOException {
        if (urlStr == null || urlStr.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        return conn;
    }
}
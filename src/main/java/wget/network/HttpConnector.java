package wget.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpConnector {

    private final String method;
    private final URL url;

    public HttpConnector(String urlStr, String method) throws MalformedURLException {
        if (urlStr == null || urlStr.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }
        this.url = new URL(urlStr);
        this.method = method;
    }

    public HttpURLConnection connect() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) this.url.openConnection();
        conn.setRequestMethod(this.method);
        return conn;
    }
}

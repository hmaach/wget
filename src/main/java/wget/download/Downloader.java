package wget.download;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader {

    private static final String METHOD = "GET";

    public void downloadFile(String urlStr, String fileName) throws IOException {
        try {
            ProgressPrinter.printTime("start", urlStr);

            HttpURLConnection conn = openConnection(urlStr);

            ProgressPrinter.printConnectionInfo(conn);

            int contentLength = conn.getContentLength();
            String contentType = conn.getContentType();

            ProgressPrinter.printContentSize(contentLength, contentType);

            System.out.printf("saving file to: ./%s%n", fileName); // "./" will be replaced with path    

            FileManager.saveToFile(conn, fileName, contentLength);

            ProgressPrinter.printTime("finished", urlStr);

        } catch (IOException e) {
            throw e;
        }
    }

    private HttpURLConnection openConnection(String urlStr) throws IOException {
        if (urlStr == null || urlStr.trim().isEmpty()) {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(METHOD);
        return conn;
    }
}

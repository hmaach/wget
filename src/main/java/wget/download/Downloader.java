package wget.download;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import wget.utils.FormatUtils;
import wget.utils.TerminalUtils;

public class Downloader {

    /**
     * Main method to download a file from a URL with wget-like output.
     */
    public void downloadFile(String urlStr) {
        try {
            printTime("start", urlStr);

            HttpURLConnection conn = openHttpConnection(urlStr);

            printConnectionInfo(conn);

            int contentLength = conn.getContentLength();
            String contentType = conn.getContentType();

            printContentSize(contentLength, contentType);

            String fileName = FormatUtils.extractFileName(urlStr);
            System.out.printf("saving file to: ./%s%n", fileName); // "./" will be replaced with path    

            saveToFile(conn, fileName, contentLength);

            printTime("finished", urlStr);

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Print the initial timestamp header like wget.
     */
    private void printTime(String position, String url) {
        if (position.equals("finished")) {
            System.err.printf("Downloaded [%s]%n", url);
        }
        System.out.printf("%s at %s%n", position, FormatUtils.timestamp());
    }

    /**
     * Open HTTP connection to given URL.
     */
    private HttpURLConnection openHttpConnection(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return conn;
    }

    /**
     * Print resolving, connecting, and HTTP response status.
     */
    private void printConnectionInfo(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();
        System.out.printf("sending request, awaiting response... status %d %s%n", status, conn.getResponseMessage());

        if (status != HttpURLConnection.HTTP_OK) {
            throw new IOException("Download failed. Status: " + status);
        }
    }

    /**
     * Print content length info or 'unspecified' if unknown.
     */
    private void printContentSize(int contentLength, String contentType) {
        if (contentLength < 0) {
            System.out.printf("content size: unspecified [%s]%n", contentType);
        } else {
            System.out.printf("content size: %d [~%s]%n", contentLength, FormatUtils.convertToMB(contentLength));
        }
    }

    /**
     * Save the response input stream to a file while showing progress. Returns
     * total bytes downloaded.
     */
    private void saveToFile(HttpURLConnection conn, String fileName, int contentLength) throws IOException {
        try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(fileName)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            long downloaded = 0;
            long startNano = System.nanoTime();

            final int barWidth = TerminalUtils.getTerminalWidth() - 65; // keep spaces for more data estimated in 65 chars

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                downloaded += bytesRead;

                TerminalUtils.printProgressBar(downloaded, contentLength, barWidth, startNano);
            }
            System.out.print("\n\n");
        }
    }
}

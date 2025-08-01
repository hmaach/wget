package wget.download;

import java.io.IOException;
import java.net.HttpURLConnection;

import wget.cli.OutputFormatter;
import wget.utils.NetworkUtils;

public class Downloader {
    private final String url;
    private final String fileName;
    private final String path;
    private final String method;
    private final Boolean inBackground;
    private final RateLimiter rateLimiter;

    private final OutputFormatter formatter;

    public Downloader(String url, String fileName, String path, String method, OutputFormatter formatter,
            RateLimiter rateLimiter) {
        this.url = url;
        this.fileName = fileName;
        this.path = path;
        this.method = method;
        this.formatter = formatter;
        this.rateLimiter = rateLimiter;
        this.inBackground = this.formatter.parser.hasOption("background");
    }

    // Overloaded constructor for backward compatibility
    public Downloader(String url, String fileName, String path, String method, OutputFormatter formatter) {
        this(url, fileName, path, method, formatter, null);
    }

    public void download() throws IOException {
        formatter.printStart(url);

        HttpURLConnection conn = NetworkUtils.createConnection(url, method);

        formatter.printConnectionInfo(conn);

        int contentLength = conn.getContentLength();
        String contentType = conn.getContentType();
        formatter.printContentSize(contentLength, contentType);

        FileManager fileManager = new FileManager(fileName, path);
        final String message = String.format("Saving file to: %s%n", path + fileName);
        System.out.print(message);

        // Pass rate limiter to file manager
        fileManager.save(conn, contentLength, this.inBackground, this.rateLimiter);

        formatter.printEnd(url);
    }
}
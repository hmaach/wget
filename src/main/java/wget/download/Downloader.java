package wget.download;

import java.io.IOException;
import java.net.HttpURLConnection;

import wget.cli.OutputFormatter;
import wget.network.HttpConnector;

public class Downloader {

    private final String url;
    private final String fileName;
    private final String path;
    private final String method;
    private final Boolean in_async;
    private final Boolean in_background;
    private final RateLimiter rateLimiter;

    private final OutputFormatter formatter;

    public Downloader(String url, String fileName, String path, String method, OutputFormatter formatter, RateLimiter rateLimiter) {
        this.url = url;
        this.fileName = fileName;
        this.path = path;
        this.method = method;
        this.formatter = formatter;
        this.rateLimiter = rateLimiter;
        this.in_background = this.formatter.parser.hasOption("B");
        this.in_async = this.formatter.parser.hasOption("i");
    }

    // Overloaded constructor for backward compatibility
    public Downloader(String url, String fileName, String path, String method, OutputFormatter formatter) {
        this(url, fileName, path, method, formatter, null);
    }

    public void download() throws IOException {
        formatter.printStart(fileName);

        HttpConnector connector = new HttpConnector(url, method);
        HttpURLConnection conn = connector.connect();

        formatter.printConnectionInfo(conn, fileName);

        int contentLength = conn.getContentLength();
        String contentType = conn.getContentType();

        FileManager fileManager = new FileManager(fileName, path);
        if (!this.in_async) {
            formatter.printContentSize(contentLength, contentType);

            final String message = String.format("Saving file to: %s%n", fileManager.getFullPath());
            if (this.in_background) {
                this.formatter.logger.log(message);
            } else {
                System.out.print(message);
            }
        }

        fileManager.save(conn, contentLength, this.in_background, this.in_async, this.rateLimiter);

        formatter.printEnd(fileName, url);
    }
}

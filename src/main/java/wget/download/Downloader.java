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
    private final Boolean inAsync;
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
        this.inAsync = this.formatter.parser.hasOption("i");
        this.inBackground = this.formatter.parser.hasOption("background");
    }

    public void download() throws IOException {
        formatter.printStart(fileName);

        HttpURLConnection conn = NetworkUtils.createConnection(url, method);

        formatter.printConnectionInfo(conn, fileName);

        int contentLength = conn.getContentLength();
        String contentType = conn.getContentType();

        FileManager fileManager = new FileManager(fileName, path);

        if (!this.inAsync) {
            formatter.printContentSize(contentLength, contentType);
            final String message = String.format("Saving file to: %s%n", path + fileName);

            System.out.print(message);
        }

        fileManager.save(conn, contentLength, this.inBackground, this.inAsync, this.rateLimiter);

        formatter.printEnd(fileName, url);
    }
}

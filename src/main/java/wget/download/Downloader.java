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
    private final Boolean in_background;

    private final OutputFormatter formatter;

    public Downloader(String url, String fileName, String path, String method, OutputFormatter formatter) {
        this.url = url;
        this.fileName = fileName;
        this.path = path;
        this.method = method;
        this.formatter = formatter;
        this.in_background = this.formatter.parser.hasOption("B");
    }

    public void download() throws IOException {
        formatter.printStart(url);

        HttpConnector connector = new HttpConnector(url, method);
        HttpURLConnection conn = connector.connect();

        formatter.printConnectionInfo(conn);

        int contentLength = conn.getContentLength();
        String contentType = conn.getContentType();
        formatter.printContentSize(contentLength, contentType);

        FileManager fileManager = new FileManager(fileName, path);
        final String message = String.format("Saving file to: %s%n", fileManager.getFullPath());
        if (this.in_background) {
            this.formatter.logger.log(message);
        } else {
            System.out.print(message);
        }
        fileManager.save(conn, contentLength, this.in_background);

        formatter.printEnd(url);
    }
}

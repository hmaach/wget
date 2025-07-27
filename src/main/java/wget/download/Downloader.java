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

    private final OutputFormatter formatter;

    public Downloader(String url, String fileName, String path, String method, OutputFormatter formatter) {
        this.url = url;
        this.fileName = fileName;
        this.path = path;
        this.method = method;
        this.formatter = formatter;
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
        System.out.printf("Saving file to: %s%n", fileManager.getFullPath());
        fileManager.save(conn, contentLength);

        formatter.printEnd(url);
    }
}

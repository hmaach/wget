package wget.mirror;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import wget.download.FileManager;
import wget.utils.FileUtils;
import wget.utils.NetworkUtils;

public class WebsiteMirror {
    private final String baseUrl;
    private final String mirrorDirectory;
    private final HtmlParser htmlParser;
    private final boolean convertLinks;

    private final Queue<String> urlQueue = new LinkedList<>();
    private final Map<String, String> urlToLocalPathMap = new HashMap<>();

    public WebsiteMirror(String url,
            List<String> rejectedExtensions, List<String> excludedPaths,
            boolean convertLinks) throws MalformedURLException {
        this.baseUrl = normalizeUrl(url);
        this.mirrorDirectory = createMirrorDirectory(this.baseUrl);
        this.htmlParser = new HtmlParser(rejectedExtensions, excludedPaths);
        this.convertLinks = convertLinks;
    }

    public void mirror() throws IOException {
        System.out.printf("Starting mirror of %s%n", baseUrl);
        System.out.printf("Saving to directory: %s%n", mirrorDirectory);

        FileUtils.ensureExists(mirrorDirectory);

        urlQueue.add(baseUrl);

        while (!urlQueue.isEmpty()) {
            String currentUrl = urlQueue.poll();

            if (htmlParser.wasVisited(currentUrl)) {
                continue;
            }

            try {
                downloadAndProcess(currentUrl);
                htmlParser.markAsVisited(currentUrl);
            } catch (IOException e) {
                System.err.printf("Error processing %s: %s%n", currentUrl, e.getMessage());
            }
        }

        if (convertLinks) {
            System.out.println("Converting links for offline viewing...");
            LinkConverter linkConverter = new LinkConverter(urlToLocalPathMap);
            linkConverter.convertAllFiles();
        }

        System.out.printf("Mirror complete. %d files downloaded.%n", urlToLocalPathMap.size());
    }

    // URL → Download → Save to disk → Parse (if HTML) → Queue new URLs
    private void downloadAndProcess(String url) throws IOException {
        System.out.printf("Processing: %s%n", url);

        String localPath = downloadFile(url);
        if (localPath == null) {
            return;
        }

        urlToLocalPathMap.put(url, localPath);

        if (isHtmlFile(url) || isHtmlFile(localPath)) {
            try {
                String htmlContent = Files.readString(Paths.get(localPath));
                Set<String> discoveredUrls = htmlParser.parseDocument(htmlContent, url);

                for (String discoveredUrl : discoveredUrls) {
                    if (isSameDomain(discoveredUrl, baseUrl) && !htmlParser.wasVisited(discoveredUrl)) {
                        urlQueue.add(discoveredUrl);
                    }
                }
            } catch (IOException e) {
                System.err.printf("Warning: Could not parse HTML content from %s: %s%n",
                        localPath, e.getMessage());
            }
        }
    }

    private String downloadFile(String url) throws IOException {
        try {
            String localPath = generateLocalPath(url);

            Path localFilePath = Paths.get(localPath);
            Files.createDirectories(localFilePath.getParent());

            // Skip if file already exists
            if (Files.exists(localFilePath)) {
                System.out.printf("File already exists: %s%n", localPath);
                return localPath;
            }

            // Download the file
            HttpURLConnection conn = NetworkUtils.createConnection(url, "GET");

            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                System.err.printf("Warning: %s returned status %d%n", url, status);
                return null;
            }

            int contentLength = conn.getContentLength();
            String fileName = Paths.get(localPath).getFileName().toString();

            FileManager fileManager = new FileManager(fileName,
                    localFilePath.getParent().toString() + "/");
            fileManager.save(conn, contentLength, true, false, null);

            System.out.printf("Downloaded: %s -> %s%n", url, localPath);
            return localPath;

        } catch (Exception e) {
            throw new IOException("Failed to download " + url + ": " + e.getMessage(), e);
        }
    }

    private String generateLocalPath(String url) throws MalformedURLException {
        URL urlObj = new URL(url);
        String path = urlObj.getPath();

        if (path.isEmpty() || path.equals("/")) {
            path = "/index.html";
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        String localPath = mirrorDirectory + "/" + path;

        return localPath;
    }

    private String createMirrorDirectory(String url) throws MalformedURLException {
        URL urlObj = new URL(url);
        String host = urlObj.getHost();

        if (host.startsWith("www.")) {
            host = host.substring(4);
        }

        return "./downloads/" + host;
    }

    private String normalizeUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        return url;
    }

    private boolean isSameDomain(String url1, String url2) {
        try {
            URL u1 = new URL(url1);
            URL u2 = new URL(url2);
            return u1.getHost().equalsIgnoreCase(u2.getHost());
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private boolean isHtmlFile(String urlOrPath) {
        String lower = urlOrPath.toLowerCase();
        return lower.endsWith(".html") || lower.endsWith(".htm") ||
                lower.endsWith(".xhtml") || lower.endsWith(".shtml") ||
                lower.contains("text/html") ||
                (!lower.contains(".") && !lower.endsWith("/")); // Assume URLs without extension are HTML
    }

    public static List<String> parseCommaSeparatedList(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.asList(input.split(","));
    }
}
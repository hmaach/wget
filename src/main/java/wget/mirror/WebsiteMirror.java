package wget.mirror;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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
import java.util.regex.Pattern;

import wget.download.FileManager;
import wget.utils.NetworkUtils;

public class WebsiteMirror {
    private final URI baseUri;
    private final String baseDomain;
    private final Path mirrorDirectory;
    private final HtmlParser htmlParser;
    private final boolean convertLinks;

    private final Queue<String> urlQueue = new LinkedList<>();
    private final Map<String, String> urlToLocalPathMap = new HashMap<>();

    private static final Pattern HTML_PATTERN = Pattern.compile(
            "\\.(html?|xhtml|shtml)$", Pattern.CASE_INSENSITIVE);

    public WebsiteMirror(String url, List<String> rejectedExtensions,
            List<String> excludedPaths, boolean convertLinks) throws MalformedURLException {
        this.baseUri = normalizeToUri(url);
        this.baseDomain = this.baseUri.getHost().toLowerCase();
        this.mirrorDirectory = createMirrorDirectory();
        this.htmlParser = new HtmlParser(rejectedExtensions, excludedPaths);
        this.convertLinks = convertLinks;
    }

    public void mirror() throws IOException {
        System.out.printf("Starting mirror of %s%n", baseUri);
        System.out.printf("Saving to directory: %s%n", mirrorDirectory);

        Files.createDirectories(mirrorDirectory);
        urlQueue.add(baseUri.toString());

        while (!urlQueue.isEmpty()) {
            String currentUrl = urlQueue.poll();

            if (htmlParser.wasVisited(currentUrl)) {
                continue;
            }

            try {
                processUrl(currentUrl);
                htmlParser.markAsVisited(currentUrl);
            } catch (IOException e) {
                System.err.printf("Error processing %s: %s%n", currentUrl, e.getMessage());
            }
        }

        if (convertLinks) {
            System.out.println("Converting links for offline viewing...");
            new LinkConverter(urlToLocalPathMap).convertAllFiles();
        }

        System.out.printf("Mirror complete. %d files downloaded.%n", urlToLocalPathMap.size());
    }

    private void processUrl(String url) throws IOException {
        System.out.printf("Processing: %s%n", url);

        String localPath = downloadFile(url);
        if (localPath == null) {
            return;
        }

        urlToLocalPathMap.put(url, localPath);

        if (isHtmlFile(url, localPath)) {
            String content = Files.readString(Paths.get(localPath));
            Set<String> discoveredUrls = htmlParser.parseDocument(content, url);

            for (String discoveredUrl : discoveredUrls) {
                if (isSameDomain(discoveredUrl) && !htmlParser.wasVisited(discoveredUrl)) {
                    urlQueue.add(discoveredUrl);
                }
            }
        }
    }

    private String downloadFile(String url) throws IOException {
        String localPath = generateLocalPath(url);
        Path localFilePath = Paths.get(localPath);

        Files.createDirectories(localFilePath.getParent());

        if (Files.exists(localFilePath)) {
            System.out.printf("File already exists: %s%n", localPath);
            return localPath;
        }

        try {
            HttpURLConnection conn = NetworkUtils.createConnection(url, "GET");
            int status = conn.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK) {
                System.err.printf("Warning: %s returned status %d%n", url, status);
                return null;
            }

            String fileName = localFilePath.getFileName().toString();
            String directory = localFilePath.getParent().toString() + "/";

            FileManager fileManager = new FileManager(fileName, directory);
            fileManager.save(conn, conn.getContentLength(), true, false, null);

            System.out.printf("Downloaded: %s -> %s%n", url, localPath);
            return localPath;

        } catch (Exception e) {
            throw new IOException("Failed to download " + url + ": " + e.getMessage(), e);
        }
    }

    private String generateLocalPath(String url) throws MalformedURLException {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();

            if (path.isEmpty() || path.equals("/")) {
                path = "/index.html";
            }

            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            Path localPath = mirrorDirectory.resolve(path);
            return localPath.toString();

        } catch (URISyntaxException e) {
            throw new MalformedURLException("Invalid URL: " + url);
        }
    }

    private Path createMirrorDirectory() {
        String host = baseDomain;
        if (host.startsWith("www.")) {
            host = host.substring(4);
        }
        return Paths.get("./downloads", host);
    }

    private URI normalizeToUri(String url) throws MalformedURLException {
        try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }

            URI uri = new URI(url);

            String path = uri.getPath();
            if (path.endsWith("/") && path.length() > 1) {
                path = path.substring(0, path.length() - 1);
            }

            return new URI(uri.getScheme(), uri.getAuthority(), path, uri.getQuery(), uri.getFragment());

        } catch (URISyntaxException e) {
            throw new MalformedURLException("Invalid URL: " + url);
        }
    }

    private boolean isSameDomain(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            return host != null && host.toLowerCase().equals(baseDomain);
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private boolean isHtmlFile(String url, String localPath) {
        String lower = localPath.toLowerCase();
        return HTML_PATTERN.matcher(lower).find();
    }

    public static List<String> parseCommaSeparatedList(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.asList(input.split(","));
    }
}
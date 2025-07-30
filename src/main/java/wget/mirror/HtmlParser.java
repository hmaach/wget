package wget.mirror;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {

    private final Set<String> visitedUrls = new HashSet<>();
    private final List<String> rejectedExtensions;
    private final List<String> excludedPaths;

    public HtmlParser() {
        this.rejectedExtensions = new ArrayList<>();
        this.excludedPaths = new ArrayList<>();
    }

    public HtmlParser(List<String> rejectedExtensions, List<String> excludedPaths) {
        this.rejectedExtensions = rejectedExtensions != null ? rejectedExtensions : new ArrayList<>();
        this.excludedPaths = excludedPaths != null ? excludedPaths : new ArrayList<>();
    }

    public Set<String> parseDocument(String htmlContent, String baseUrl) throws IOException {
        Set<String> discoveredUrls = new HashSet<>();

        try {
            Document doc = Jsoup.parse(htmlContent);
            URL base = new URL(baseUrl);

            // Extract links from <a> tags with href attribute
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String href = link.attr("href");
                String absoluteUrl = resolveUrl(href, base);
                if (absoluteUrl != null && shouldIncludeUrl(absoluteUrl)) {
                    discoveredUrls.add(absoluteUrl);
                }
            }

            // Extract resources from <img> tags with src attribute
            Elements images = doc.select("img[src]");
            for (Element img : images) {
                String src = img.attr("src");
                String absoluteUrl = resolveUrl(src, base);
                if (absoluteUrl != null && shouldIncludeUrl(absoluteUrl)) {
                    discoveredUrls.add(absoluteUrl);
                }
            }

            // Extract resources from <link> tags with href attribute (CSS, favicons, etc.)
            Elements cssLinks = doc.select("link[href]");
            for (Element cssLink : cssLinks) {
                String href = cssLink.attr("href");
                String absoluteUrl = resolveUrl(href, base);
                if (absoluteUrl != null && shouldIncludeUrl(absoluteUrl)) {
                    discoveredUrls.add(absoluteUrl);
                }
            }

            // Extract resources from <script> tags with src attribute
            Elements scripts = doc.select("script[src]");
            for (Element script : scripts) {
                String src = script.attr("src");
                String absoluteUrl = resolveUrl(src, base);
                if (absoluteUrl != null && shouldIncludeUrl(absoluteUrl)) {
                    discoveredUrls.add(absoluteUrl);
                }
            }

        } catch (Exception e) {
            throw new IOException("Error parsing HTML document: " + e.getMessage(), e);
        }

        return discoveredUrls;
    }

    private String resolveUrl(String url, URL base) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        try {
            // Handle absolute URLs
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return url;
            }

            // Handle protocol-relative URLs
            if (url.startsWith("//")) {
                return base.getProtocol() + ":" + url;
            }

            // Handle absolute paths
            if (url.startsWith("/")) {
                return base.getProtocol() + "://" + base.getHost() +
                        (base.getPort() != -1 ? ":" + base.getPort() : "") + url;
            }

            // Handle relative URLs
            String basePath = base.getPath();
            if (!basePath.endsWith("/")) {
                basePath = basePath.substring(0, basePath.lastIndexOf('/') + 1);
            }

            String fullPath = basePath + url;
            return base.getProtocol() + "://" + base.getHost() +
                    (base.getPort() != -1 ? ":" + base.getPort() : "") + normalizePath(fullPath);

        } catch (Exception e) {
            System.err.println("Warning: Could not resolve URL: " + url);
            return null;
        }
    }

    private String normalizePath(String path) {
        String[] parts = path.split("/");
        List<String> normalizedParts = new ArrayList<>();

        for (String part : parts) {
            if (part.equals("..")) {
                if (!normalizedParts.isEmpty()) {
                    normalizedParts.remove(normalizedParts.size() - 1);
                }
            } else if (!part.equals(".") && !part.isEmpty()) {
                normalizedParts.add(part);
            }
        }

        return "/" + String.join("/", normalizedParts);
    }

    private boolean shouldIncludeUrl(String url) {
        if (url == null || visitedUrls.contains(url)) {
            return false;
        }

        // Check if URL should be excluded based on path
        for (String excludedPath : excludedPaths) {
            try {
                URL urlObj = new URL(url);
                if (urlObj.getPath().startsWith(excludedPath)) {
                    return false;
                }
            } catch (MalformedURLException e) {
                return false;
            }
        }

        // Check if URL should be rejected based on file extension
        for (String extension : rejectedExtensions) {
            if (url.toLowerCase().endsWith("." + extension.toLowerCase())) {
                return false;
            }
        }

        return true;
    }

    public void markAsVisited(String url) {
        visitedUrls.add(url);
    }

    public boolean wasVisited(String url) {
        return visitedUrls.contains(url);
    }

    public Set<String> getVisitedUrls() {
        return new HashSet<>(visitedUrls);
    }

    public void clearVisited() {
        visitedUrls.clear();
    }
}
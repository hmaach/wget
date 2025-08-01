package wget.mirror;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {
    private final Set<String> visitedUrls = new HashSet<>();
    private final List<String> rejectedExtensions;
    private final List<String> excludedPaths;

    private static final Pattern CSS_URL_PATTERN = Pattern.compile(
            "url\\s*\\(\\s*['\"]?([^'\"\\)\\s]+)['\"]?\\s*\\)",
            Pattern.CASE_INSENSITIVE);

    private static final String[][] ELEMENT_SELECTORS = {
            { "a[href]", "href" },
            { "img[src]", "src" },
            { "link[href]", "href" },
            { "script[src]", "src" }
    };

    public HtmlParser(List<String> rejectedExtensions, List<String> excludedPaths) {
        this.rejectedExtensions = rejectedExtensions != null ? rejectedExtensions : new ArrayList<>();
        this.excludedPaths = excludedPaths != null ? excludedPaths : new ArrayList<>();
    }

    public Set<String> parseDocument(String htmlContent, String baseUrl) throws IOException {
        Set<String> discoveredUrls = new HashSet<>();

        try {
            Document doc = Jsoup.parse(htmlContent);
            URL baseUrlObj = new URL(baseUrl);

            processHtmlElements(doc, baseUrlObj, discoveredUrls);
            processCssUrls(doc, baseUrlObj, discoveredUrls);

        } catch (MalformedURLException e) {
            throw new IOException("Invalid base URL: " + baseUrl, e);
        } catch (Exception e) {
            throw new IOException("Error parsing HTML document: " + e.getMessage(), e);
        }

        return discoveredUrls;
    }

    private void processHtmlElements(Document doc, URL baseUrl, Set<String> discoveredUrls) {
        for (String[] selectorConfig : ELEMENT_SELECTORS) {
            String selector = selectorConfig[0];
            String attribute = selectorConfig[1];

            Elements elements = doc.select(selector);
            for (Element element : elements) {
                String url = element.attr(attribute);
                addResolvedUrl(url, baseUrl, discoveredUrls);
            }
        }
    }

    private void processCssUrls(Document doc, URL baseUrl, Set<String> discoveredUrls) {
        // Process <style> tags
        Elements styleTags = doc.select("style");
        for (Element styleTag : styleTags) {
            extractUrlsFromCss(styleTag.html(), baseUrl, discoveredUrls);
        }

        // Process inline style attributes
        Elements elementsWithStyle = doc.select("[style]");
        for (Element element : elementsWithStyle) {
            extractUrlsFromCss(element.attr("style"), baseUrl, discoveredUrls);
        }
    }

    private void extractUrlsFromCss(String cssContent, URL baseUrl, Set<String> discoveredUrls) {
        if (cssContent == null || cssContent.trim().isEmpty()) {
            return;
        }

        Matcher matcher = CSS_URL_PATTERN.matcher(cssContent);
        while (matcher.find()) {
            String url = matcher.group(1).replaceAll("^['\"]|['\"]$", "").trim();
            if (!url.isEmpty()) {
                addResolvedUrl(url, baseUrl, discoveredUrls);
            }
        }
    }

    private void addResolvedUrl(String url, URL baseUrl, Set<String> discoveredUrls) {
        String absoluteUrl = resolveUrl(url, baseUrl);
        if (absoluteUrl != null && shouldIncludeUrl(absoluteUrl)) {
            discoveredUrls.add(absoluteUrl);
        }
    }

    private String resolveUrl(String url, URL baseUrl) {
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
                return baseUrl.getProtocol() + ":" + url;
            }

            URI baseUri = baseUrl.toURI();
            URI resolvedUri = baseUri.resolve(url);
            return resolvedUri.toString();

        } catch (URISyntaxException | IllegalArgumentException e) {
            // Log warning but don't fail the entire parsing process
            System.err.println("Warning: Could not resolve URL: " + url + " - " + e.getMessage());
            return null;
        }
    }

    private boolean shouldIncludeUrl(String url) {
        if (url == null || visitedUrls.contains(url)) {
            return false;
        }

        // Check rejected extensions
        for (String extension : rejectedExtensions) {
            if (url.toLowerCase().endsWith("." + extension.toLowerCase())) {
                return false;
            }
        }

        // Check excluded paths
        if (!excludedPaths.isEmpty()) {
            try {
                String path = new URL(url).getPath();
                for (String excludedPath : excludedPaths) {
                    if (path.startsWith(excludedPath)) {
                        return false;
                    }
                }
            } catch (MalformedURLException e) {
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
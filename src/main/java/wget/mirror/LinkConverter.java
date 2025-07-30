package wget.mirror;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LinkConverter {
    private final Map<String, String> urlToLocalPathMap;

    // Pattern to match CSS url() references
    private static final Pattern CSS_URL_PATTERN = Pattern.compile(
            "url\\s*\\(\\s*['\"]?([^'\"\\)\\s]+)['\"]?\\s*\\)",
            Pattern.CASE_INSENSITIVE);

    public LinkConverter(Map<String, String> urlToLocalPathMap) {
        this.urlToLocalPathMap = urlToLocalPathMap;
    }

    public void convertLinksInFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !isHtmlFile(filePath)) {
            return;
        }

        String content = Files.readString(file.toPath());
        String convertedContent = convertLinksInHtml(content, filePath);

        if (!content.equals(convertedContent)) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(convertedContent);
            }
        }
    }

    private String convertLinksInHtml(String htmlContent, String currentFilePath) {
        try {
            Document doc = Jsoup.parse(htmlContent);
            boolean modified = false;

            // Convert <a> tag href attributes
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String href = link.attr("href");
                String convertedHref = convertUrl(href, currentFilePath);
                if (!href.equals(convertedHref)) {
                    link.attr("href", convertedHref);
                    modified = true;
                }
            }

            // Convert <img> tag src attributes
            Elements images = doc.select("img[src]");
            for (Element img : images) {
                String src = img.attr("src");
                String convertedSrc = convertUrl(src, currentFilePath);
                if (!src.equals(convertedSrc)) {
                    img.attr("src", convertedSrc);
                    modified = true;
                }
            }

            // Convert <link> tag href attributes (CSS, etc.)
            Elements cssLinks = doc.select("link[href]");
            for (Element cssLink : cssLinks) {
                String href = cssLink.attr("href");
                String convertedHref = convertUrl(href, currentFilePath);
                if (!href.equals(convertedHref)) {
                    cssLink.attr("href", convertedHref);
                    modified = true;
                }
            }

            // Convert <script> tag src attributes
            Elements scripts = doc.select("script[src]");
            for (Element script : scripts) {
                String src = script.attr("src");
                String convertedSrc = convertUrl(src, currentFilePath);
                if (!src.equals(convertedSrc)) {
                    script.attr("src", convertedSrc);
                    modified = true;
                }
            }

            // Convert URLs in <style> tags
            Elements styleTags = doc.select("style");
            for (Element styleTag : styleTags) {
                String cssContent = styleTag.html();
                String convertedCss = convertUrlsInCss(cssContent, currentFilePath);
                if (!cssContent.equals(convertedCss)) {
                    styleTag.html(convertedCss);
                    modified = true;
                }
            }

            // Convert URLs in inline style attributes
            Elements elementsWithStyle = doc.select("[style]");
            for (Element element : elementsWithStyle) {
                String styleContent = element.attr("style");
                String convertedStyle = convertUrlsInCss(styleContent, currentFilePath);
                if (!styleContent.equals(convertedStyle)) {
                    element.attr("style", convertedStyle);
                    modified = true;
                }
            }

            return modified ? doc.outerHtml() : htmlContent;

        } catch (Exception e) {
            System.err.println("Warning: Could not convert links in HTML: " + e.getMessage());
            return htmlContent;
        }
    }

    private String convertUrlsInCss(String cssContent, String currentFilePath) {
        if (cssContent == null || cssContent.trim().isEmpty()) {
            return cssContent;
        }

        StringBuffer result = new StringBuffer();
        Matcher matcher = CSS_URL_PATTERN.matcher(cssContent);

        while (matcher.find()) {
            String originalUrl = matcher.group(1);
            String cleanUrl = originalUrl.trim();

            String convertedUrl = convertUrl(cleanUrl, currentFilePath);
            String replacement = "url(" + convertedUrl + ")";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private String convertUrl(String url, String currentFilePath) {
        if (url == null || url.trim().isEmpty()) {
            return url;
        }

        // Skip data URLs, javascript URLs, and anchors
        if (url.startsWith("data:") || url.startsWith("javascript:") || url.startsWith("#")) {
            return url;
        }

        String localPath = findLocalPath(url);
        if (localPath != null) {
            return makeRelativePath(currentFilePath, localPath);
        }

        return url;
    }

    private String findLocalPath(String url) {
        if (urlToLocalPathMap.containsKey(url)) {
            return urlToLocalPathMap.get(url);
        }

        // Look for files that end with this path
        for (Map.Entry<String, String> entry : urlToLocalPathMap.entrySet()) {
            if (entry.getKey().endsWith(url) && !url.isEmpty()) {
                return entry.getValue();
            }
        }

        return null;
    }

    private String makeRelativePath(String fromPath, String toPath) {
        try {
            Path from = Paths.get(fromPath).getParent();
            Path to = Paths.get(toPath);

            if (from == null) {
                return toPath;
            }

            Path relativePath = from.relativize(to);
            return "'" + relativePath.toString() + "'";
        } catch (Exception e) {
            return toPath;
        }
    }

    private boolean isHtmlFile(String filePath) {
        String lowerPath = filePath.toLowerCase();
        return lowerPath.endsWith(".html") || lowerPath.endsWith(".htm") ||
                lowerPath.endsWith(".xhtml") || lowerPath.endsWith(".shtml");
    }

    public void convertAllFiles() throws IOException {
        for (String localPath : urlToLocalPathMap.values()) {
            if (isHtmlFile(localPath)) {
                convertLinksInFile(localPath);
            }
        }
    }
}
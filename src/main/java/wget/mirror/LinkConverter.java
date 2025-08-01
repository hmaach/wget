package wget.mirror;

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

    private static final Pattern CSS_URL_PATTERN = Pattern.compile(
            "url\\s*\\(\\s*['\"]?([^'\"\\)\\s]+)['\"]?\\s*\\)",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern HTML_FILE_PATTERN = Pattern.compile(
            "\\.(html?|htm|xhtml|shtml)$", Pattern.CASE_INSENSITIVE);

    private static final String[][] ELEMENT_CONFIGS = {
            { "a[href]", "href" },
            { "img[src]", "src" },
            { "link[href]", "href" },
            { "script[src]", "src" }
    };

    public LinkConverter(Map<String, String> urlToLocalPathMap) {
        this.urlToLocalPathMap = urlToLocalPathMap;
    }

    public void convertLinksInFile(String filePath) throws IOException {
        if (!isHtmlFile(filePath) || !Files.exists(Paths.get(filePath))) {
            return;
        }

        String content = Files.readString(Paths.get(filePath));
        String convertedContent = convertLinksInHtml(content, filePath);

        if (!content.equals(convertedContent)) {
            Files.writeString(Paths.get(filePath), convertedContent);
        }
    }

    private String convertLinksInHtml(String htmlContent, String currentFilePath) {
        try {
            Document doc = Jsoup.parse(htmlContent);
            boolean modified = false;

            modified |= processHtmlElements(doc, currentFilePath);

            modified |= processCssUrls(doc, currentFilePath);

            return modified ? doc.outerHtml() : htmlContent;

        } catch (Exception e) {
            System.err.println("Warning: Could not convert links in HTML: " + e.getMessage());
            return htmlContent;
        }
    }

    private boolean processHtmlElements(Document doc, String currentFilePath) {
        boolean modified = false;

        for (String[] config : ELEMENT_CONFIGS) {
            String selector = config[0];
            String attribute = config[1];

            Elements elements = doc.select(selector);
            for (Element element : elements) {
                String originalUrl = element.attr(attribute);
                String convertedUrl = convertUrl(originalUrl, currentFilePath);

                if (!originalUrl.equals(convertedUrl)) {
                    element.attr(attribute, convertedUrl);
                    modified = true;
                }
            }
        }

        return modified;
    }

    private boolean processCssUrls(Document doc, String currentFilePath) {
        boolean modified = false;

        // Process <style> tags
        Elements styleTags = doc.select("style");
        for (Element styleTag : styleTags) {
            String originalCss = styleTag.html();
            String convertedCss = convertCssUrls(originalCss, currentFilePath);
            if (!originalCss.equals(convertedCss)) {
                styleTag.html(convertedCss);
                modified = true;
            }
        }

        // Process inline style attributes
        Elements elementsWithStyle = doc.select("[style]");
        for (Element element : elementsWithStyle) {
            String originalStyle = element.attr("style");
            String convertedStyle = convertCssUrls(originalStyle, currentFilePath);
            if (!originalStyle.equals(convertedStyle)) {
                element.attr("style", convertedStyle);
                modified = true;
            }
        }

        return modified;
    }

    private String convertCssUrls(String cssContent, String currentFilePath) {
        if (cssContent == null || cssContent.trim().isEmpty()) {
            return cssContent;
        }

        StringBuffer result = new StringBuffer();
        Matcher matcher = CSS_URL_PATTERN.matcher(cssContent);

        while (matcher.find()) {
            String originalUrl = matcher.group(1).trim();
            String convertedUrl = convertUrl(originalUrl, currentFilePath);

            // Build replacement with proper quoting
            String replacement = "url('" + convertedUrl + "')";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private String convertUrl(String url, String currentFilePath) {
        if (url == null || url.trim().isEmpty() ||
                url.startsWith("data:") || url.startsWith("javascript:") || url.startsWith("#")) {
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

        if (!url.isEmpty()) {
            for (Map.Entry<String, String> entry : urlToLocalPathMap.entrySet()) {
                if (entry.getKey().endsWith(url)) {
                    return entry.getValue();
                }
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
            return relativePath.toString();
        } catch (Exception e) {
            return toPath;
        }
    }

    private boolean isHtmlFile(String filePath) {
        return HTML_FILE_PATTERN.matcher(filePath.toLowerCase()).find();
    }

    public void convertAllFiles() throws IOException {
        for (String localPath : urlToLocalPathMap.values()) {
            if (isHtmlFile(localPath)) {
                try {
                    convertLinksInFile(localPath);
                } catch (IOException e) {
                    System.err.printf("Warning: Could not convert links in file %s: %s%n",
                            localPath, e.getMessage());
                }
            }
        }
    }
}
package wget;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import wget.cli.ArgumentParser;
import wget.cli.OutputFormatter;
import wget.download.Downloader;
import wget.download.FileManager;
import wget.download.RateLimiter;
import wget.mirror.WebsiteMirror;
import wget.utils.FileUtils;

public class WgetApplication {
    private String path = "./downloads/";
    private OutputFormatter formatter;
    private RateLimiter rateLimiter;
    private ArgumentParser parser;

    public void run(String[] args) {
        if (!initialize(args))
            return;

        if (parser.hasOption("mirror")) {
            handleMirroring();
        } else if (parser.hasOption("B")) {
            handleBackgroundDownload();
        } else {
            handleRegularDownloads();
        }
    }

    private boolean initialize(String[] args) {
        try {
            this.parser = new ArgumentParser(args);
            this.formatter = new OutputFormatter(this.parser);
            handlePath();
            handleRateLimit();
            return true;
        } catch (Exception e) {
            System.err.printf("Error: %s%n", e.getMessage());
            return false;
        }
    }

    private void handleMirroring() {
        String[] urls = parser.getUrls();
        if (urls.length != 1) {
            System.err.println("Mirroring requires exactly one URL.");
            return;
        }

        String url = urls[0];

        try {
            // Parse rejected extensions
            List<String> rejectedExtensions = null;
            if (parser.hasOption("R")) {
                rejectedExtensions = WebsiteMirror.parseCommaSeparatedList(parser.getOptionValue("R"));
                System.out.printf("Rejecting file types: %s%n", rejectedExtensions);
            }

            // Parse excluded paths
            List<String> excludedPaths = null;
            if (parser.hasOption("X")) {
                excludedPaths = WebsiteMirror.parseCommaSeparatedList(parser.getOptionValue("X"));
                System.out.printf("Excluding paths: %s%n", excludedPaths);
            }

            // Check if link conversion is requested
            boolean convertLinks = parser.hasOption("convert-links");
            if (convertLinks) {
                System.out.println("Link conversion enabled for offline viewing.");
            }

            // Create and run the mirror
            WebsiteMirror mirror = new WebsiteMirror(url,
                    rejectedExtensions, excludedPaths, convertLinks);
            mirror.mirror();

        } catch (Exception e) {
            System.err.printf("ERROR: mirroring '%s': %s%n", url, e.getMessage());
        }
    }

    private void handleBackgroundDownload() {
        String[] urls = parser.getUrls();
        if (urls.length != 1) {
            System.err.println("The -B flag supports only one URL.");
            return;
        }

        String url = urls[0];
        System.out.println("Output will be written to \"wget-log\".");

        try {
            List<String> command = new ArrayList<>();
            command.add("./wget");
            command.add("--background");

            if (parser.hasOption("O")) {
                command.add("-O=" + parser.getOptionValue("O"));
            }
            if (parser.hasOption("P")) {
                command.add("-P=" + parser.getOptionValue("P"));
            }
            if (parser.hasOption("rate-limit")) {
                command.add("--rate-limit=" + parser.getOptionValue("rate-limit"));
            }
            command.add(url);

            ProcessBuilder pb = new ProcessBuilder(command);

            File logFile = new File("wget-log");
            pb.redirectOutput(ProcessBuilder.Redirect.to(logFile));
            pb.redirectError(ProcessBuilder.Redirect.to(logFile));

            pb.start();

            System.exit(0);
        } catch (IOException e) {
            System.err.printf("ERROR: starting background download: %s%n", e.getMessage());
        }
    }

    private void handleRegularDownloads() {
        String[] urls = getUrls();
        if (urls == null || urls.length == 0) {
            parser.printHelp();
            return;
        }

        for (String url : urls) {
            try {
                String fileName = FileManager.determineFileName(parser, url);
                Downloader downloader = new Downloader(url, fileName, path, "GET", formatter, rateLimiter);
                downloader.download();
            } catch (IOException e) {
                System.err.printf("ERROR: downloading '%s': %s%n", url, e.getMessage());
            }
        }
    }

    private void handlePath() {
        if (parser.hasOption("P")) {
            try {
                path = FileUtils.parsePath(parser.getOptionValue("P"));
            } catch (IOException e) {
                System.err.printf("Error parsing directory path: %s%n", e.getMessage());
            }
        }

        try {
            FileUtils.ensureExists(path);
        } catch (IOException e) {
            System.err.printf("Error creating directory '%s': %s%n", path, e.getMessage());
        }
    }

    private void handleRateLimit() {
        if (parser.hasOption("rate-limit")) {
            try {
                String rateLimitStr = parser.getOptionValue("rate-limit");
                rateLimiter = new RateLimiter(rateLimitStr);
                System.out.printf("Rate limiting enabled: %d bytes/sec%n", rateLimiter.getBytesPerSecond());
            } catch (IllegalArgumentException e) {
                System.err.printf("Error parsing rate limit: %s%n", e.getMessage());
            }
        }
    }

    private String[] getUrls() {
        if (parser.hasOption("i")) {
            try {
                List<String> urlsFromFile = FileUtils.readFile(parser.getOptionValue("i"));
                return urlsFromFile.toArray(new String[0]);
            } catch (IOException e) {
                System.err.printf("ERROR: reading file '%s': %s%n", parser.getOptionValue("i"), e.getMessage());
                return null;
            }
        } else {
            return parser.getUrls();
        }
    }
}
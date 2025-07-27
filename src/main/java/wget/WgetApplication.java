package wget;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.ParseException;

import wget.cli.ArgumentParser;
import wget.cli.OutputFormatter;
import wget.download.Downloader;
import wget.download.FileManager;
import wget.download.PathManager;
import wget.utils.FileUtils;

public class WgetApplication {

    private String path = "./downloads/";
    private OutputFormatter formatter;

    public void run(String[] args) {
        ArgumentParser parser = parseArguments(args);
        if (parser == null) {
            return;
        }

        formatter = new OutputFormatter(parser);

        if (parser.hasOption("B")) {
            String[] urls = parser.getUrls();
            if (urls.length != 1) {
                System.err.println("The -B flag supports only one URL.");
                return;
            }
            String url = urls[0];
            String fileName = FileManager.determineFileName(parser, url);

            System.out.println("Output will be written to \"wget-log\".");

            new Thread(() -> {

            }).start();

            return;
        }

        if (!parser.hasOption("i") && parser.getUrls().length == 0) {
            parser.printHelp();
            return;
        }

        handlePath(parser);

        String[] urls = getUrls(parser);
        if (urls == null || urls.length == 0) {
            return;
        }

        for (String url : urls) {
            String fileName = FileManager.determineFileName(parser, url);
            Downloader downloader = new Downloader(url, fileName, path, "GET", formatter);
            try {
                downloader.download();
            } catch (IOException e) {
                System.err.printf("ERROR: downloading '%s': %s%n", url, e.getMessage());
            }
        }
    }

    private ArgumentParser parseArguments(String[] args) {
        try {
            return new ArgumentParser(args);
        } catch (ParseException e) {
            System.err.printf("Error parsing arguments: %s%n", e.getMessage());
            return null;
        }
    }

    private void handlePath(ArgumentParser parser) {
        if (parser.hasOption("P")) {
            try {
                path = PathManager.parsePath(parser.getOptionValue("P"));
            } catch (IOException e) {
                System.err.printf("Error parsing directory path: %s%n", e.getMessage());
            }
        }

        try {
            PathManager.ensureExists(path);
        } catch (IOException e) {
            System.err.printf("Error creating directory '%s': %s%n", path, e.getMessage());
        }
    }

    private String[] getUrls(ArgumentParser parser) {
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

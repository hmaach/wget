package wget;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.ParseException;

import wget.cli.ArgumentParser;
import wget.download.Downloader;
import wget.download.PathManager;
import wget.utils.FileUtils;

public class WgetApplication {

    private String path = "./downloads/";

    public void run(String[] args) {
        ArgumentParser parser;
        try {
            parser = new ArgumentParser(args);
        } catch (ParseException e) {
            System.out.println("Error parsing arguments: " + e.getMessage());
            return;
        }

        // No URL or input file? Show help.
        if (!parser.hasOption("i") && parser.getUrls().length == 0) {
            parser.printHelp();
            return;
        }

        try {
            if (parser.hasOption("P")) {
                path = PathManager.parsePath(parser.getOptionValue("P"));
            }
            PathManager.ensureExists(path);

        } catch (Exception e) {
            System.err.printf("Error: parsing saving Directory '%s': %s", path, e.getMessage());
        }

        Downloader downloader = new Downloader();

        if (parser.hasOption("i")) {
            // TODO: I need to download asynchronously
            List<String> urls;
            try {
                urls = FileUtils.readFile(parser.getOptionValue("i"));
                parser.setUrls(urls.toArray(new String[0])); // Creates an empty array to let Java know the target type
            } catch (IOException e) {
                System.err.printf("ERROR: reading file content: %s", e.getMessage());
            }
            for (String url : parser.getUrls()) {
                String fileName = FileUtils.extractFileName(url);
                try {
                    downloader.downloadFile(url, fileName, path);
                } catch (IOException e) {
                    System.err.printf("ERROR: downloading file '%s': %s", fileName, e.getMessage());
                }
            }
        } else {
            for (String url : parser.getUrls()) {
                String fileName;
                if (parser.hasOption("O")) {
                    fileName = parser.getOptionValue("O");
                } else {
                    fileName = FileUtils.extractFileName(url);
                }
                try {
                    downloader.downloadFile(url, fileName, path);
                } catch (IOException e) {
                    System.err.printf("ERROR: downloading file '%s': %s", fileName, e.getMessage());
                }
            }
        }
    }
}

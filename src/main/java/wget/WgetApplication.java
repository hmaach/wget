package wget;

import java.util.List;

import org.apache.commons.cli.ParseException;

import wget.cli.ArgumentParser;
import wget.download.Downloader;
import wget.utils.FileUtils;

public class WgetApplication {

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
        Downloader downloader = new Downloader();

        if (parser.hasOption("i")) {
            // TODO: I need to download asynchronously
            List<String> urls;
            try {
                urls = FileUtils.readFile(parser.getOptionValue("i"));
                parser.setUrls(urls.toArray(new String[0])); // Creates an empty array to let Java know the target type
            } catch (Exception e) {
                System.err.printf("ERROR: reading file content: %s", e.getMessage());
            }
        }

        for (String url : parser.getUrls()) {
            downloader.downloadFile(url);
        }
    }
}

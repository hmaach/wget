package wget;

import wget.download.Downloader;
// import org.apache.commons.cli.ParseException;

public class WgetApplication {

    public void run(String[] args) {
        // ArgumentParser parser;
        // try {
        //     parser = new ArgumentParser(args);
        // } catch (ParseException e) {
        //     System.out.println("Error parsing arguments: " + e.getMessage());
        //     return;
        // }

        // // No URL or input file? Show help.
        // if (!parser.hasOption("i") && parser.getUrls().length == 0) {
        //     parser.printHelp();
        //     return;
        // }
        Downloader downloader = new Downloader();

        // if (parser.hasOption("i")) {
        //     // TODO: read URLs from input file
        //     System.out.println("Input file parsing not implemented yet.");
        // }
        // for (String url : parser.getUrls()) {
        //     downloader.downloadFile(url);
        // }
        for (String url : args) {
            downloader.downloadFile(url);
        }
    }
}

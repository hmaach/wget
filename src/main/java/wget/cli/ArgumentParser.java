package wget.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ArgumentParser {
    private final CommandLine cmd;
    private final Options options;
    private String[] urls;

    public ArgumentParser(String[] args) throws ParseException {
        this.options = defineOptions();
        CommandLineParser parser = new DefaultParser();
        this.cmd = parser.parse(options, args);
        this.urls = cmd.getArgs();
    }

    private Options defineOptions() {
        Options opts = new Options();

        // Basic download options
        opts.addOption("B", false, "Download in background and write output to wget-log (only 1 URL supported)");
        opts.addOption("O", true, "Output filename");
        opts.addOption("P", true, "Output directory");
        opts.addOption("i", true, "Input file with URLs");
        opts.addOption(null, "rate-limit", true, "Limit download speed (e.g., 500k, 2M)");

        // Mirroring options
        opts.addOption(null, "mirror", false, "Mirror entire website");
        opts.addOption("R", "reject", true, "Comma-separated list of file suffixes to reject (e.g., jpg,gif,png)");
        opts.addOption("X", "exclude", true, "Comma-separated list of paths to exclude (e.g., /js,/css)");
        opts.addOption(null, "convert-links", false, "Convert links for offline viewing");

        return opts;
    }

    public boolean hasOption(String opt) {
        return cmd.hasOption(opt);
    }

    public String getOptionValue(String opt) {
        return cmd.getOptionValue(opt);
    }

    public String[] getUrls() {
        return this.urls;
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("./wget [OPTION]... [URL]...",
                "\nDownload files from the web with support for HTTP/HTTPS protocols.\n\n",
                options,
                "\nExamples:\n" +
                        "  ./wget https://example.com/file.zip\n" +
                        "  ./wget -O=myfile.zip https://example.com/file.zip\n" +
                        "  ./wget -P=/downloads/ https://example.com/file.zip\n" +
                        "  ./wget -B https://example.com/file.zip\n" +
                        "  ./wget --rate-limit=500k https://example.com/file.zip\n" +
                        "  ./wget -i=urls.txt\n" +
                        "  ./wget --mirror https://example.com\n" +
                        "  ./wget --mirror -R=jpg,gif,png https://example.com\n" +
                        "  ./wget --mirror -X=/js,/css https://example.com\n" +
                        "  ./wget --mirror --convert-links https://example.com\n");
    }
}
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
        opts.addOption("B", false, "Download in background and write output to wget-log (only 1 URL supported)");
        opts.addOption("O", true, "Output filename");
        opts.addOption("P", true, "Output directory");
        opts.addOption("i", true, "Input file with URLs");
        opts.addOption(null, "rate-limit", true, "Limit download speed (e.g., 500k, 2M)");
        opts.addOption(null, "mirror", false, "shortcut for -N -r -l inf --no-remove-listing");
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

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("./wget [OPTION]... [URL]...", options);
    }
}

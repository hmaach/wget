package wget.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;

import wget.utils.FormatUtils;
import wget.utils.TimeUtils;

public class OutputFormatter {

    private final PrintStream out;
    private final PrintStream err;

    public OutputFormatter() {
        this(System.out, System.err);
    }

    public OutputFormatter(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    public void printStart(String url) {
        out.printf("Start at %s%n", TimeUtils.timestamp());
    }

    public void printEnd(String url) {
        err.printf("Downloaded [%s]%n", url);
        out.printf("Finished at %s%n", TimeUtils.timestamp());
    }

    public void printConnectionInfo(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();
        out.printf("Sending request, awaiting response... Status %d %s%n", status, conn.getResponseMessage());

        if (status != HttpURLConnection.HTTP_OK) {
            throw new IOException("Download failed. Status: " + status);
        }
    }

    public void printContentSize(int contentLength, String contentType) {
        if (contentLength < 0) {
            out.printf("Content size: unspecified [%s]%n", contentType);
        } else {
            out.printf("Content size: %d [~%s]%n", contentLength, FormatUtils.convertToMB(contentLength));
        }
    }

    public static void printProgressBar(long downloaded, long contentLength, int barWidth, long startNano) {
        long elapsedNs = System.nanoTime() - startNano;
        double elapsedSec = elapsedNs / 1_000_000_000.0;

        double speedBytesPerSec = downloaded / elapsedSec;
        double speedMiB = speedBytesPerSec / (1024 * 1024);

        double progress = contentLength > 0 ? (double) downloaded / contentLength : 0;
        int filled = (int) (barWidth * progress);

        StringBuilder bar = new StringBuilder();
        bar.append('[');
        for (int i = 0; i < filled; i++) {
            bar.append('=');
        }
        for (int i = filled; i < barWidth; i++) {
            bar.append(' ');
        }
        bar.append(']');

        String downloadedStr = FormatUtils.formatBytes(downloaded);
        String totalStr = contentLength > 0 ? FormatUtils.formatBytes(contentLength) : "Unknown";
        double percent = progress * 100;

        long remainingSec = (speedBytesPerSec > 0 && contentLength > downloaded)
                ? (long) ((contentLength - downloaded) / speedBytesPerSec)
                : 0;

        System.out.printf("\r %s / %s %s %6.2f%% %4.2f MiB/s %ds",
                downloadedStr,
                totalStr,
                bar.toString(),
                percent,
                speedMiB,
                remainingSec);
    }
}

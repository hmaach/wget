package wget.cli;

import java.io.IOException;
import java.net.HttpURLConnection;

import wget.utils.FormatUtils;
import wget.utils.Logger;
import wget.utils.TimeUtils;

public class OutputFormatter {

    public final ArgumentParser parser;
    public final Logger logger;

    public OutputFormatter(ArgumentParser parser) throws IOException {
        this.parser = parser;
        this.logger = new Logger();
    }

    public void printStart(String url) throws IOException {
        final String message = String.format("Start at %s%n", TimeUtils.timestamp());
        if (this.parser.hasOption("-B")) {
            logger.log(message);
            return;
        }
        System.out.print(message);
    }

    public void printEnd(String url) throws IOException {
        final String message = String.format("Downloaded [%s]%nFinished at %s%n", url, TimeUtils.timestamp());
        if (this.parser.hasOption("-B")) {
            logger.log(message);
            logger.log("------------------------------------------------\n");
            return;
        }
        System.out.print(message);
    }

    public void printConnectionInfo(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();
        final String message = String.format(
                "Sending request, awaiting response... Status %d %s%n",
                status,
                conn.getResponseMessage());

        if (this.parser.hasOption("-B")) {
            logger.log(message);
            return;
        }
        System.out.print(message);

        if (status != HttpURLConnection.HTTP_OK) {
            throw new IOException("Download failed. Status: " + status);
        }
    }

    public void printContentSize(int contentLength, String contentType) throws IOException {
        final String message;
        if (contentLength < 0) {
            message = String.format("Content size: unspecified [%s]%n", contentType);
        } else {
            message = String.format("Content size: %d [~%s]%n", contentLength, FormatUtils.convertToMB(contentLength));
        }

        if (this.parser.hasOption("-B")) {
            logger.log(message);
            return;
        }
        System.out.print(message);
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

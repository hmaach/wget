package wget.cli;

import java.io.IOException;
import java.net.HttpURLConnection;

import wget.utils.FormatUtils;
import wget.utils.TimeUtils;

public class OutputFormatter {
    public final ArgumentParser parser;

    public OutputFormatter(ArgumentParser parser) {
        this.parser = parser;
    }

    public void printStart(String url) {
        System.out.printf("Start downloading [%s] at %s%n", url, TimeUtils.timestamp());
    }

    public void printEnd(String fileName, String url) {
        System.out.printf("Finished Downloading [%s] at %s%n", fileName, TimeUtils.timestamp());
    }

    public void printConnectionInfo(HttpURLConnection conn, String fileName) throws IOException {
        int status = conn.getResponseCode();
        System.out.printf("%n--%s: Sending request, awaiting response... Status %d %s%n", fileName,
                status, conn.getResponseMessage());

        if (status != HttpURLConnection.HTTP_OK) {
            throw new IOException("Download failed. Status: " + status);
        }
    }

    public void printContentSize(int contentLength, String contentType) {
        if (contentLength < 0) {
            System.out.printf("Content size: unspecified [%s]%n", contentType);
        } else {
            System.out.printf("Content size: %d [~%s]%n", contentLength, FormatUtils.convertToMB(contentLength));
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

        System.out.printf("\r %s / %s %s %6.2f%% %4.2f MiB/s %ds  ",
                downloadedStr,
                totalStr,
                bar.toString(),
                percent,
                speedMiB,
                remainingSec);
    }
}

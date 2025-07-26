package wget.cli;

import wget.utils.FormatUtils;

public class OutputFormatter {

    /**
     * Print the progress bar with speed and downloaded size.
     */
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

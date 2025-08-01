package wget.download;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import wget.cli.OutputFormatter;

public class AsyncDownloader {

    private final ExecutorService executor;
    private static final int MAX_CONCURRENT_DOWNLOADS = 5;

    public AsyncDownloader() {
        this.executor = Executors.newFixedThreadPool(MAX_CONCURRENT_DOWNLOADS);
    }

    public void downloadAsync(String url, String fileName, String path, String method, OutputFormatter formatter, RateLimiter rateLimiter) {
        executor.submit(() -> {
            Downloader downloader = new Downloader(url, fileName, path, method, formatter, rateLimiter);
            try {
                downloader.download();
            } catch (IOException e) {
                System.err.printf("ERROR: downloading '%s': %s%n", url, e.getMessage());
            }
        });
    }

    public void shutdownAndAwaitTermination() {
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

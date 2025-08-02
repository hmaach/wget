package wget.download;

import java.util.concurrent.TimeUnit;

public class RateLimiter {

    private final long bytesPerSecond;
    private long nextAllowedTime = System.nanoTime();

    public RateLimiter(String rateLimitStr) {
        this.bytesPerSecond = parseRateLimit(rateLimitStr);
    }

    private long parseRateLimit(String rateLimitStr) {
        if (rateLimitStr == null || rateLimitStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Rate limit cannot be null or empty");
        }

        String cleaned = rateLimitStr.trim().toLowerCase();
        long multiplier = 1;

        if (cleaned.endsWith("k")) {
            multiplier = 1024;
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        } else if (cleaned.endsWith("m")) {
            multiplier = 1024 * 1024;
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }

        try {
            long baseValue = Long.parseLong(cleaned);
            return baseValue * multiplier;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid rate limit format: " + rateLimitStr);
        }
    }

    public void throttle(int bytesRead) throws InterruptedException {
        if (bytesPerSecond <= 0) {
            return;
        }

        long nanosPerByte = 1_000_000_000L / bytesPerSecond;
        long waitTime = bytesRead * nanosPerByte;

        nextAllowedTime += waitTime;
        long now = System.nanoTime();
        long sleepNanos = nextAllowedTime - now;

        if (sleepNanos > 0) {
            TimeUnit.NANOSECONDS.sleep(sleepNanos);
        } else {
            nextAllowedTime = now;
        }
    }

    public long getBytesPerSecond() {
        return bytesPerSecond;
    }
}

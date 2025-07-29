package wget.download;

public class RateLimiter {
    
    private final long bytesPerSecond;
    private long lastCheckTime;
    private long bytesInCurrentSecond;
    
    public RateLimiter(String rateLimitStr) {
        this.bytesPerSecond = parseRateLimit(rateLimitStr);
        this.lastCheckTime = System.currentTimeMillis();
        this.bytesInCurrentSecond = 0;
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
        
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastCheckTime;
        
        if (timeDiff >= 1000) {
            lastCheckTime = currentTime;
            bytesInCurrentSecond = bytesRead;
            return;
        }
        
        bytesInCurrentSecond += bytesRead;
        
        long expectedTimeMs = (bytesInCurrentSecond * 1000) / bytesPerSecond;
        
        if (timeDiff < expectedTimeMs) {
            long sleepTime = expectedTimeMs - timeDiff;
            Thread.sleep(sleepTime);
        }
    }
    
    public long getBytesPerSecond() {
        return bytesPerSecond;
    }
}
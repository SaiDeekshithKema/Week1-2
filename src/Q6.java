import java.util.*;
import java.util.concurrent.*;

public class Q6 {
    private static class TokenBucket {
        private int tokens;
        private long lastRefillTime;
        private final int maxTokens;
        private final int refillRate;

        public TokenBucket(int maxTokens, int refillRate) {
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        public synchronized boolean allowRequest() {
            refill();
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime;
            long tokensToAdd = (elapsed / 3600000L) * refillRate;
            if (tokensToAdd > 0) {
                tokens = Math.min(maxTokens, tokens + (int) tokensToAdd);
                lastRefillTime = now;
            }
        }

        public synchronized int getRemainingTokens() {
            refill();
            return tokens;
        }
    }

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final int maxTokens;
    private final int refillRate;

    public Q6(int maxTokens, int refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
    }

    public String checkRateLimit(String clientId) {
        TokenBucket bucket = buckets.computeIfAbsent(clientId, k -> new TokenBucket(maxTokens, refillRate));
        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
        } else {
            return "Denied (0 requests remaining, retry after " + getRetryAfterSeconds(clientId) + "s)";
        }
    }

    public Map<String, Object> getRateLimitStatus(String clientId) {
        TokenBucket bucket = buckets.computeIfAbsent(clientId, k -> new TokenBucket(maxTokens, refillRate));
        Map<String, Object> status = new HashMap<>();
        status.put("used", maxTokens - bucket.getRemainingTokens());
        status.put("limit", maxTokens);
        status.put("reset", bucket.lastRefillTime + 3600000L);
        return status;
    }

    private long getRetryAfterSeconds(String clientId) {
        TokenBucket bucket = buckets.get(clientId);
        if (bucket == null) return 0;
        return (bucket.lastRefillTime + 3600000L - System.currentTimeMillis()) / 1000;
    }

    public static void main(String[] args) {
        Q6 limiter = new Q6(1000, 1000);
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.checkRateLimit("abc123"));
        System.out.println(limiter.getRateLimitStatus("abc123"));
    }
}

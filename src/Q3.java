import java.util.*;

public class Q3 {

    public static void main(String[] args) throws InterruptedException {

        DNSCache cache = new DNSCache(3); // max cache size

        System.out.println(cache.resolve("google.com"));
        System.out.println(cache.resolve("google.com"));

        Thread.sleep(4000); // simulate TTL expiry

        System.out.println(cache.resolve("google.com"));

        System.out.println(cache.getCacheStats());
    }
}

class DNSEntry {

    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {

    private final int capacity;

    private Map<String, DNSEntry> cache;

    private int hits = 0;
    private int misses = 0;

    public DNSCache(int capacity) {

        this.capacity = capacity;

        // LinkedHashMap for LRU eviction
        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {

            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };

        startCleanupThread();
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        long start = System.nanoTime();

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                long time = System.nanoTime() - start;

                return "Cache HIT → " + entry.ipAddress +
                        " (retrieved in " + time / 1_000_000.0 + " ms)";
            } else {
                cache.remove(domain);
                System.out.println("Cache EXPIRED for " + domain);
            }
        }

        // Cache miss
        misses++;

        String ip = queryUpstreamDNS(domain);
        DNSEntry entry = new DNSEntry(domain, ip, 3); // TTL = 3 seconds
        cache.put(domain, entry);

        return "Cache MISS → Query upstream → " + ip + " (TTL: 3s)";
    }

    // Simulated upstream DNS
    private String queryUpstreamDNS(String domain) {

        Random r = new Random();
        return "172.217.14." + (100 + r.nextInt(100));
    }

    // Cache statistics
    public String getCacheStats() {

        int total = hits + misses;

        double hitRate = total == 0 ? 0 : (hits * 100.0) / total;

        return "Hit Rate: " + hitRate + "% (Hits=" + hits + ", Misses=" + misses + ")";
    }

    // Background cleanup thread
    private void startCleanupThread() {

        Thread cleaner = new Thread(() -> {

            while (true) {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (this) {

                    Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry<String, DNSEntry> entry = it.next();

                        if (entry.getValue().isExpired()) {
                            it.remove();
                        }
                    }
                }
            }
        });

        cleaner.setDaemon(true);
        cleaner.start();
    }
}
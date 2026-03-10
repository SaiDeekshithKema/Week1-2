import java.util.*;

public class Q10 {
    static class VideoData {
        String videoId;
        String content;
        VideoData(String videoId, String content) {
            this.videoId = videoId;
            this.content = content;
        }
    }

    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;
        LRUCache(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    private final LRUCache<String, VideoData> L1;
    private final LRUCache<String, VideoData> L2;
    private final Map<String, VideoData> L3;
    private final Map<String, Integer> accessCount;
    private int L1Hits = 0, L2Hits = 0, L3Hits = 0, totalRequests = 0;

    public Q10() {
        L1 = new LRUCache<>(10000);
        L2 = new LRUCache<>(100000);
        L3 = new HashMap<>();
        accessCount = new HashMap<>();
    }

    public VideoData getVideo(String videoId) {
        totalRequests++;
        if (L1.containsKey(videoId)) {
            L1Hits++;
            return L1.get(videoId);
        }
        if (L2.containsKey(videoId)) {
            L2Hits++;
            VideoData video = L2.get(videoId);
            accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);
            if (accessCount.get(videoId) > 5) {
                L1.put(videoId, video);
            }
            return video;
        }
        if (L3.containsKey(videoId)) {
            L3Hits++;
            VideoData video = L3.get(videoId);
            L2.put(videoId, video);
            accessCount.put(videoId, 1);
            return video;
        }
        return null;
    }

    public void addVideoToDB(String videoId, String content) {
        L3.put(videoId, new VideoData(videoId, content));
    }

    public void invalidate(String videoId) {
        L1.remove(videoId);
        L2.remove(videoId);
        L3.remove(videoId);
        accessCount.remove(videoId);
    }

    public String getStatistics() {
        double L1Rate = totalRequests == 0 ? 0 : (L1Hits * 100.0) / totalRequests;
        double L2Rate = totalRequests == 0 ? 0 : (L2Hits * 100.0) / totalRequests;
        double L3Rate = totalRequests == 0 ? 0 : (L3Hits * 100.0) / totalRequests;
        return "L1: Hit Rate " + String.format("%.2f", L1Rate) + "%\n" +
                "L2: Hit Rate " + String.format("%.2f", L2Rate) + "%\n" +
                "L3: Hit Rate " + String.format("%.2f", L3Rate) + "%\n" +
                "Overall: Hit Rate " + String.format("%.2f", (L1Rate+L2Rate+L3Rate)) + "%";
    }

    public static void main(String[] args) {
        Q10 cacheSystem = new Q10();
        cacheSystem.addVideoToDB("video_123", "Breaking News Content");
        cacheSystem.addVideoToDB("video_999", "Movie Trailer Content");

        System.out.println(cacheSystem.getVideo("video_123"));
        System.out.println(cacheSystem.getVideo("video_123"));
        System.out.println(cacheSystem.getVideo("video_999"));
        System.out.println(cacheSystem.getStatistics());
    }
}

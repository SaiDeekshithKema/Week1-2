import java.util.*;

public class Q5 {
    private Map<String, Integer> pageViews = new HashMap<>();
    private Map<String, Set<String>> uniqueVisitors = new HashMap<>();
    private Map<String, Integer> sourceCount = new HashMap<>();

    public void processEvent(String url, String userId, String source) {
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);
        uniqueVisitors.computeIfAbsent(url, k -> new HashSet<>()).add(userId);
        sourceCount.put(source, sourceCount.getOrDefault(source, 0) + 1);
    }

    public List<String> getTopPages(int n) {
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());
        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {
            pq.offer(entry);
            if (pq.size() > n) pq.poll();
        }
        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            Map.Entry<String, Integer> e = pq.poll();
            result.add(e.getKey() + " - " + e.getValue() + " views (" +
                    uniqueVisitors.getOrDefault(e.getKey(), Collections.emptySet()).size() + " unique)");
        }
        Collections.reverse(result);
        return result;
    }

    public Map<String, Integer> getSourceCounts() {
        return new HashMap<>(sourceCount);
    }

    public static void main(String[] args) {
        Q5 dashboard = new Q5();
        dashboard.processEvent("/article/breaking-news", "user_123", "google");
        dashboard.processEvent("/article/breaking-news", "user_456", "facebook");
        dashboard.processEvent("/sports/championship", "user_789", "direct");

        System.out.println("Top Pages:");
        for (String page : dashboard.getTopPages(10)) {
            System.out.println(page);
        }

        System.out.println("\nTraffic Sources:");
        for (Map.Entry<String, Integer> entry : dashboard.getSourceCounts().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}

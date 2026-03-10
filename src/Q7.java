import java.util.*;

public class Q7 {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        Map<String, Integer> queries = new HashMap<>();
    }

    private final TrieNode root = new TrieNode();
    private final Map<String, Integer> globalFrequency = new HashMap<>();

    public void insert(String query) {
        globalFrequency.put(query, globalFrequency.getOrDefault(query, 0) + 1);
        TrieNode node = root;
        for (char c : query.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
            node.queries.put(query, globalFrequency.get(query));
        }
    }

    public List<String> search(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) return Collections.emptyList();
            node = node.children.get(c);
        }
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());
        for (Map.Entry<String, Integer> entry : node.queries.entrySet()) {
            pq.offer(entry);
            if (pq.size() > 10) pq.poll();
        }
        List<String> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            Map.Entry<String, Integer> e = pq.poll();
            result.add(e.getKey() + " (" + e.getValue() + " searches)");
        }
        Collections.reverse(result);
        return result;
    }

    public void updateFrequency(String query) {
        insert(query);
    }

    public static void main(String[] args) {
        Q7 autocomplete = new Q7();
        autocomplete.insert("java tutorial");
        autocomplete.insert("javascript");
        autocomplete.insert("java download");
        autocomplete.insert("java features");
        autocomplete.updateFrequency("java features");
        autocomplete.updateFrequency("java features");

        System.out.println("Suggestions for 'jav':");
        for (String suggestion : autocomplete.search("jav")) {
            System.out.println(suggestion);
        }
    }
}

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Q4 {


    private Map<String, Set<String>> ngramIndex = new HashMap<>();
    private int n = 5; // default n-gram size

    public Q4(int n) {
        this.n = n;
    }


    private String normalize(String text) {
        return text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
    }


    private List<String> extractNgrams(String text) {
        String[] words = normalize(text).split("\\s+");
        List<String> ngrams = new ArrayList<>();
        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append(words[i + j]).append(" ");
            }
            ngrams.add(sb.toString().trim());
        }
        return ngrams;
    }


    public void indexDocument(String docId, String text) {
        List<String> ngrams = extractNgrams(text);
        for (String ng : ngrams) {
            ngramIndex.computeIfAbsent(ng, k -> new HashSet<>()).add(docId);
        }
    }


    public void analyzeDocument(String docId, String text) {
        List<String> ngrams = extractNgrams(text);
        Map<String, Integer> matchCount = new HashMap<>();

        for (String ng : ngrams) {
            if (ngramIndex.containsKey(ng)) {
                for (String otherDoc : ngramIndex.get(ng)) {
                    matchCount.put(otherDoc, matchCount.getOrDefault(otherDoc, 0) + 1);
                }
            }
        }

        System.out.println("→ Extracted " + ngrams.size() + " n-grams");
        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {
            double similarity = (entry.getValue() * 100.0) / ngrams.size();
            System.out.printf("→ Found %d matching n-grams with \"%s\"%n", entry.getValue(), entry.getKey());
            System.out.printf("→ Similarity: %.2f%% %s%n", similarity,
                    similarity > 50 ? "(PLAGIARISM DETECTED)" : (similarity > 10 ? "(suspicious)" : ""));
        }
    }

    public static String loadFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }
    public static void main(String[] args) throws IOException {
        Q4 detector = new Q4(5);


        detector.indexDocument("essay_089.txt", loadFile("essay_089.txt"));
        detector.indexDocument("essay_092.txt", loadFile("essay_092.txt"));


        detector.analyzeDocument("essay_123.txt", loadFile("essay_123.txt"));
    }
}

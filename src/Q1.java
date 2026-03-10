import java.util.*;

public class Q1 {

    public static void main(String[] args) {

        UsernameService service = new UsernameService();


        service.registerUser("john_doe", 101);
        service.registerUser("admin", 102);
        service.registerUser("alex", 103);

        System.out.println("Check Availability:");

        System.out.println("john_doe → " + service.checkAvailability("john_doe"));
        System.out.println("jane_smith → " + service.checkAvailability("jane_smith"));

        System.out.println("\nSuggestions for john_doe:");
        System.out.println(service.suggestAlternatives("john_doe"));

        System.out.println("\nMost Attempted Username:");
        System.out.println(service.getMostAttempted());
    }
}

class UsernameService {


    private HashMap<String, Integer> usernameMap = new HashMap<>();


    private HashMap<String, Integer> attemptCount = new HashMap<>();



    public void registerUser(String username, int userId) {
        usernameMap.put(username, userId);
    }



    public boolean checkAvailability(String username) {


        attemptCount.put(username, attemptCount.getOrDefault(username, 0) + 1);

        return !usernameMap.containsKey(username);
    }



    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!usernameMap.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        String dotVersion = username.replace("_", ".");
        if (!usernameMap.containsKey(dotVersion)) {
            suggestions.add(dotVersion);
        }

        return suggestions;
    }



    public String getMostAttempted() {

        String maxUser = null;
        int maxCount = 0;

        for (Map.Entry<String, Integer> entry : attemptCount.entrySet()) {

            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxUser = entry.getKey();
            }
        }

        return maxUser + " (" + maxCount + " attempts)";
    }
}
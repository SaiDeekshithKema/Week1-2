import java.util.*;

public class Q9 {
    static class Transaction {
        int id;
        int amount;
        String merchant;
        String account;
        long time;

        Transaction(int id, int amount, String merchant, String account, long time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.time = time;
        }
    }

    private final List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public List<List<Transaction>> findTwoSum(int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<List<Transaction>> result = new ArrayList<>();
        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                result.add(Arrays.asList(map.get(complement), t));
            }
            map.put(t.amount, t);
        }
        return result;
    }

    public List<List<Transaction>> findTwoSumWithinHour(int target) {
        List<List<Transaction>> result = new ArrayList<>();
        Map<Integer, List<Transaction>> map = new HashMap<>();
        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                for (Transaction other : map.get(complement)) {
                    if (Math.abs(t.time - other.time) <= 3600000) {
                        result.add(Arrays.asList(other, t));
                    }
                }
            }
            map.computeIfAbsent(t.amount, k -> new ArrayList<>()).add(t);
        }
        return result;
    }

    public List<List<Transaction>> findKSum(int k, int target) {
        List<List<Transaction>> result = new ArrayList<>();
        backtrack(transactions, k, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(List<Transaction> trans, int k, int target, int start,
                           List<Transaction> current, List<List<Transaction>> result) {
        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }
        if (k == 0 || target < 0) return;
        for (int i = start; i < trans.size(); i++) {
            current.add(trans.get(i));
            backtrack(trans, k - 1, target - trans.get(i).amount, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public List<Map<String, Object>> detectDuplicates() {
        Map<String, Map<Integer, List<Transaction>>> map = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Transaction t : transactions) {
            map.computeIfAbsent(t.merchant, m -> new HashMap<>())
                    .computeIfAbsent(t.amount, a -> new ArrayList<>())
                    .add(t);
        }
        for (String merchant : map.keySet()) {
            for (int amount : map.get(merchant).keySet()) {
                List<Transaction> list = map.get(merchant).get(amount);
                if (list.size() > 1) {
                    Map<String, Object> dup = new HashMap<>();
                    dup.put("amount", amount);
                    dup.put("merchant", merchant);
                    List<String> accounts = new ArrayList<>();
                    for (Transaction t : list) accounts.add(t.account);
                    dup.put("accounts", accounts);
                    result.add(dup);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Q9 fraudDetector = new Q9();
        fraudDetector.addTransaction(new Transaction(1, 500, "Store A", "acc1", 1000));
        fraudDetector.addTransaction(new Transaction(2, 300, "Store B", "acc2", 2000));
        fraudDetector.addTransaction(new Transaction(3, 200, "Store C", "acc3", 2500));
        fraudDetector.addTransaction(new Transaction(4, 500, "Store A", "acc4", 3000));

        System.out.println("Two-Sum (target=500):");
        for (List<Transaction> pair : fraudDetector.findTwoSum(500)) {
            System.out.println("(" + pair.get(0).id + ", " + pair.get(1).id + ")");
        }

        System.out.println("\nTwo-Sum within 1 hour (target=500):");
        for (List<Transaction> pair : fraudDetector.findTwoSumWithinHour(500)) {
            System.out.println("(" + pair.get(0).id + ", " + pair.get(1).id + ")");
        }

        System.out.println("\nK-Sum (k=3, target=1000):");
        for (List<Transaction> group : fraudDetector.findKSum(3, 1000)) {
            System.out.print("(");
            for (Transaction t : group) System.out.print(t.id + " ");
            System.out.println(")");
        }

        System.out.println("\nDuplicates:");
        for (Map<String, Object> dup : fraudDetector.detectDuplicates()) {
            System.out.println(dup);
        }
    }
}

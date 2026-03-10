import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Q2 {

    public static void main(String[] args) {

        InventoryService service = new InventoryService();

        // Add product with stock
        service.addProduct("IPHONE15_256GB", 100);

        System.out.println(service.checkStock("IPHONE15_256GB"));

        // Simulate purchases
        for (int i = 1; i <= 105; i++) {
            System.out.println(service.purchaseItem("IPHONE15_256GB", i));
        }

    }
}

class InventoryService {

    // productId -> stock count
    private Map<String, Integer> stockMap = new ConcurrentHashMap<>();

    // productId -> waiting list
    private Map<String, Queue<Integer>> waitingList = new ConcurrentHashMap<>();


    // Add product
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingList.put(productId, new LinkedList<>());
    }


    // Check stock availability
    public String checkStock(String productId) {

        Integer stock = stockMap.get(productId);

        if (stock == null)
            return "Product not found";

        return productId + " → " + stock + " units available";
    }


    // Purchase item (thread-safe)
    public synchronized String purchaseItem(String productId, int userId) {

        if (!stockMap.containsKey(productId))
            return "Product not found";

        int stock = stockMap.get(productId);

        if (stock > 0) {

            stock--;
            stockMap.put(productId, stock);

            return "Success for user " + userId +
                    ", " + stock + " units remaining";

        } else {

            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);

            return "Added to waiting list, position #" + queue.size();
        }
    }
}
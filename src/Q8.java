import java.util.*;

public class Q8 {
    private static class Spot {
        String licensePlate;
        long entryTime;
        boolean occupied;

        Spot() {
            this.licensePlate = null;
            this.entryTime = 0;
            this.occupied = false;
        }
    }

    private final Spot[] spots;
    private int occupiedCount = 0;
    private int totalProbes = 0;
    private int probeCount = 0;
    private final int capacity;

    public Q8(int capacity) {
        this.capacity = capacity;
        this.spots = new Spot[capacity];
        for (int i = 0; i < capacity; i++) {
            spots[i] = new Spot();
        }
    }

    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    public String parkVehicle(String licensePlate) {
        int index = hash(licensePlate);
        int probes = 0;
        while (spots[index].occupied) {
            index = (index + 1) % capacity;
            probes++;
        }
        spots[index].licensePlate = licensePlate;
        spots[index].entryTime = System.currentTimeMillis();
        spots[index].occupied = true;
        occupiedCount++;
        totalProbes += probes;
        probeCount++;
        return "Assigned spot #" + index + " (" + probes + " probes)";
    }

    public String exitVehicle(String licensePlate) {
        int index = hash(licensePlate);
        while (spots[index].occupied && !spots[index].licensePlate.equals(licensePlate)) {
            index = (index + 1) % capacity;
        }
        if (spots[index].occupied && spots[index].licensePlate.equals(licensePlate)) {
            long duration = System.currentTimeMillis() - spots[index].entryTime;
            double hours = duration / 3600000.0;
            double fee = hours * 5.0;
            spots[index].occupied = false;
            spots[index].licensePlate = null;
            occupiedCount--;
            return "Spot #" + index + " freed, Duration: " + (int)(hours*60) + "m, Fee: $" + String.format("%.2f", fee);
        }
        return "Vehicle not found";
    }

    public String getStatistics() {
        double occupancy = (occupiedCount * 100.0) / capacity;
        double avgProbes = probeCount == 0 ? 0 : (double) totalProbes / probeCount;
        return "Occupancy: " + String.format("%.2f", occupancy) + "%, Avg Probes: " + String.format("%.2f", avgProbes);
    }

    public static void main(String[] args) throws InterruptedException {
        Q8 parkingLot = new Q8(500);
        System.out.println(parkingLot.parkVehicle("ABC-1234"));
        System.out.println(parkingLot.parkVehicle("ABC-1235"));
        System.out.println(parkingLot.parkVehicle("XYZ-9999"));
        Thread.sleep(2000);
        System.out.println(parkingLot.exitVehicle("ABC-1234"));
        System.out.println(parkingLot.getStatistics());
    }
}

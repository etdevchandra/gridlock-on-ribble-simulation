public class CarPark extends Thread {
    private String name;
    private Road inputRoad;
    private int capacity;
    private Vehicle[] parkedVehicles;
    private int count = 0;

    public CarPark(String name, Road inputRoad, int capacity) {
        this.name = name;
        this.inputRoad = inputRoad;
        this.capacity = capacity;
        parkedVehicles = new Vehicle[capacity];
    }

    @Override
    public void run() {
        long shutdownDeadline = System.currentTimeMillis() + 15000; // Max 15 seconds after Main.running = false

        // Continue accepting vehicles while simulation is running OR until flush deadline
        while (Main.running || (!inputRoad.isEmpty() && System.currentTimeMillis() < shutdownDeadline)) {
            if (!inputRoad.isEmpty() && count < capacity) {
                Vehicle v = inputRoad.removeVehicle();
                if (v != null) {
                    try {
                        Thread.sleep(1200); // Simulated delay: 12 simulated seconds
                    } catch (InterruptedException e) {
                        // Ignore interruption
                    }
                    int currentTime = Main.clock.getSimulatedTime();
                    v.setParkedTime(currentTime);
                    parkedVehicles[count++] = v;
                    Main.incrementVehiclesParked();
                    // Optional: uncomment below for debug logging
                    // System.out.println("Time: " + formatTime(currentTime) + " - CarPark " + name + " admitted Vehicle " + v.getId());
                }
            } else {
                try {
                    Thread.sleep(200); // Short wait when no car to park
                } catch (InterruptedException e) {
                    // Ignore
                }
            }
        }
    }

    public int getParkedCount() {
        return count;
    }

    public int getAverageJourneyTime() {
        if (count == 0) return 0;
        int total = 0;
        for (int i = 0; i < count; i++) {
            total += (parkedVehicles[i].getParkedTime() - parkedVehicles[i].getEntryTime());
        }
        return total / count;
    }

    public int getRemainingSpaces() {
        return capacity - count;
    }

    // Converts simulated seconds to readable mm:ss format
    private String formatTime(int simulatedSeconds) {
        int minutes = simulatedSeconds / 60;
        int seconds = simulatedSeconds % 60;
        return minutes + "m" + seconds + "s";
    }
}

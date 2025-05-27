public class EntryPoint extends Thread {
    private String name;
    private int carsPerHour; // Vehicles per hour

    public EntryPoint(String name, int carsPerHour) {
        this.name = name;
        this.carsPerHour = carsPerHour;
    }

    @Override
    public void run() {
        // Convert cars/hour to vehicle generation interval in ms (scaled to 1 sim second = 100ms)
        long intervalMillis = (long) ((3600.0 / carsPerHour) / 10 * 1000);

        while (Main.running) {
            int currentTime = Main.clock.getSimulatedTime();

            // Stop generating vehicles near end of simulation to allow smooth completion
            if (currentTime >= 3580) break;  // No new vehicles after 59m40s

            // Assign destination using weighted probabilities
            double r = Math.random();
            String destination;
            if (r < 0.10)
                destination = "University";
            else if (r < 0.30)
                destination = "Station";
            else if (r < 0.60)
                destination = "Shopping Centre";
            else
                destination = "Industrial Park";

            Vehicle v = new Vehicle(destination, currentTime);

            // Assign vehicle to appropriate entry road
            boolean added = false;
            if (name.equalsIgnoreCase("North")) {
                added = tryAddToRoad(Main.roadC, v);
            } else if (name.equalsIgnoreCase("East")) {
                added = tryAddToRoad(Main.roadB, v);
            } else if (name.equalsIgnoreCase("South")) {
                added = tryAddToRoad(Main.roadA, v);
            }

            if (added) {
                Main.incrementVehiclesCreated();
                // Optional debug logging
                //System.out.println("Time: " + formatTime(currentTime) + " - EntryPoint " + name + " created Vehicle " + v.getId() + " destined for " + destination);
            }

            try {
                Thread.sleep(intervalMillis); // Wait before next vehicle creation
            } catch (InterruptedException e) {
                // Safe exit if thread is interrupted
                break;
            }
        }
    }

    // Safely attempt to add a vehicle to the road if there is space
    private boolean tryAddToRoad(Road road, Vehicle v) {
        if (!road.isFull()) {
            road.addVehicle(v);
            return true;
        }
        return false;
    }

    // Converts simulated seconds to a readable time format
    private String formatTime(int simulatedSeconds) {
        int minutes = simulatedSeconds / 60;
        int seconds = simulatedSeconds % 60;
        return minutes + "m" + seconds + "s";
    }
}

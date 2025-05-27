public class Clock extends Thread {
    private int tick = 0;
    private int maxTicks; // Number of ticks to simulate (e.g., 360 ticks = 1 hour = 3600 simulated seconds)

    public Clock(int maxTicks) {
        this.maxTicks = maxTicks;
    }

    @Override
    public void run() {
        while (tick < maxTicks && Main.running) {
            try {
                Thread.sleep(1000); // 1 real second = 10 simulated seconds
            } catch (InterruptedException e) {
                // Ignore interruption.
            }
            tick++;
            int simulatedTime = tick * 10;

            // Optional: uncomment below for debug logging
            //System.out.println("Clock: Simulated time is " + formatTime(simulatedTime));

            // Every 10 simulated minutes (600 seconds), print car park signboard
            if (simulatedTime % 600 == 0) {
                reportCarParkStatus();
            }
        }
        // Signal end of simulation
        Main.running = false;
        System.out.println("Clock: Simulation ended.");
    }

    public int getSimulatedTime() {
        return tick * 10;
    }

    // Format seconds to mm:ss string for consistent timestamp output
    private String formatTime(int simulatedSeconds) {
        int minutes = simulatedSeconds / 60;
        int seconds = simulatedSeconds % 60;
        return minutes + "m" + seconds + "s";
    }

    // Console display for car park availability (signboard output)
    private void reportCarParkStatus() {
        int time = getSimulatedTime();
        System.out.println("\n---------- Sign Board at Time: " + formatTime(time) + " ----------");
        System.out.println("University: " + Main.carParkUniversity.getRemainingSpaces() + " Spaces");
        System.out.println("Station: " + Main.carParkStation.getRemainingSpaces() + " Spaces");
        System.out.println("Shopping Centre: " + Main.carParkShopping.getRemainingSpaces() + " Spaces");
        System.out.println("Industrial Park: " + Main.carParkIndustrial.getRemainingSpaces() + " Spaces");
        System.out.println("----------------------------------------------\n");
    }
}

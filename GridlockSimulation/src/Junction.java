import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class Junction extends Thread {
    private String name;
    private List<Road> inputRoads;                   // Multiple entry roads
    private Map<String, Road> exitRoads;             // Destination name → exit road
    private int greenDuration;                       // Green light duration (simulated seconds)
    private List<String> entryDirections;            // Entry directions for logging
    private PrintWriter logWriter;

    public Junction(String name, List<Road> inputRoads, Map<String, Road> exitRoads,
                    int greenDuration, List<String> entryDirections) {
        this.name = name;
        this.inputRoads = inputRoads;
        this.exitRoads = exitRoads;
        this.greenDuration = greenDuration;
        this.entryDirections = entryDirections;
        try {
            // Log files will be created in the root-level /upload folder
            File folder = new File(System.getProperty("user.dir") + File.separator + ".." + File.separator + "upload");
            if (!folder.exists()) folder.mkdirs();
            logWriter = new PrintWriter(new FileWriter(new File(folder, "Junction_" + name + ".log")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int index = 0;
        while (Main.running) {
            Road inputRoad = inputRoads.get(index);
            String direction = entryDirections.get(index);
            int startTime = Main.clock.getSimulatedTime();
            int carsThrough = 0;

            while (Main.clock.getSimulatedTime() - startTime < greenDuration && Main.running) {
                if (!inputRoad.isEmpty()) {
                    Vehicle v = inputRoad.removeVehicle();
                    if (v == null) continue;

                    String destination = v.getDestination();
                    Road exitRoad = exitRoads.get(destination);

                    if (exitRoad == null) {
                        log("Vehicle " + v.getId() + " has unknown destination: " + destination);
                        inputRoad.addVehicle(v); // Put back to retry later
                        sleepQuiet(200);
                        continue;
                    }

                    if (exitRoad.isFull()) {
                        log("GRIDLOCK for Vehicle " + v.getId() + " to " + destination);
                        inputRoad.addVehicle(v); // Try again in next round
                        sleepQuiet(500);
                    } else {
                        sleepQuiet(500); // Simulate movement delay
                        exitRoad.addVehicle(v);
                        carsThrough++;
                        log("Vehicle " + v.getId() + " moved to " + destination);
                    }
                } else {
                    sleepQuiet(200); // Wait briefly if no cars
                }
            }
            // End of green phase — log summary
            int queueLength = inputRoad.getCount();
            String logMessage = "Green phase ended: " + carsThrough + " cars through from " + direction + ", " + queueLength + " waiting.";
            if (carsThrough == 0 && queueLength > 0) logMessage += " GRIDLOCK";
            log(logMessage);
            // Move to next direction
            index = (index + 1) % inputRoads.size();
        }
        logWriter.close(); // Finalize log file
    }

    // Helper method for non-blocking sleep
    private void sleepQuiet(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }

    // Formats and writes a message to the log file
    private void log(String message) {
        int time = Main.clock.getSimulatedTime();
        String logMessage = "Time: " + formatTime(time) + " - Junction " + name + ": " + message;
        //System.out.println(logMessage);
        logWriter.println(logMessage);
        logWriter.flush();
    }

    // Converts simulated seconds to mm:ss forma
    private String formatTime(int simulatedSeconds) {
        int minutes = simulatedSeconds / 60;
        int seconds = simulatedSeconds % 60;
        return minutes + "m" + seconds + "s";
    }
}

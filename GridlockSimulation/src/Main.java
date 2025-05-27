import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    // Shared running flag to coordinate thread execution
    public static volatile boolean running = true;

    // Simulation clock instance
    public static Clock clock;

    // Counters for data consistency tracking
    public static int totalVehiclesCreated = 0;
    public static int totalVehiclesParked = 0;

    // Thread-safe increment methods for global counters
    public static synchronized void incrementVehiclesCreated() {
        totalVehiclesCreated++;
    }

    public static synchronized void incrementVehiclesParked() {
        totalVehiclesParked++;
    }

    // Configuration variables for entry rates and junction timings
    public static int epNorthRate;
    public static int epEastRate;
    public static int epSouthRate;
    public static int jA_green;
    public static int jB_green;
    public static int jC_green;
    public static int jD_green;

    // Primary entry roads into the town
    public static Road roadA;
    public static Road roadB;
    public static Road roadC;
    public static Road roadD;  // Optional/placeholder

    // Intermediate connecting roads between junctions
    public static Road roadCtoD;
    public static Road roadBtoA;
    public static Road roadAtoB;
    public static Road roadCtoB;
    public static Road roadBtoC;

    // Exit roads leading into car parks
    public static Road exitUniversity;
    public static Road exitStation;
    public static Road exitShopping;
    public static Road exitIndustrial;

    // CarPark instances
    public static CarPark carParkUniversity;
    public static CarPark carParkStation;
    public static CarPark carParkShopping;
    public static CarPark carParkIndustrial;

    // Reads and loads configuration from scenario file
    public static void loadConfiguration(String configFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line;
            boolean isEntryPoints = false;
            boolean isJunctions = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                // Parse sections: ENTRYPOINTS or JUNCTIONS
                if (line.equalsIgnoreCase("ENTRYPOINTS")) {
                    isEntryPoints = true;
                    isJunctions = false;
                    continue;
                }
                if (line.equalsIgnoreCase("JUNCTIONS")) {
                    isEntryPoints = false;
                    isJunctions = true;
                    continue;
                }
                String[] parts = line.split("\\s+");
                if (parts.length < 2) continue;

                String key = parts[0];
                int value = Integer.parseInt(parts[1]);

                // Set entry rates or junction timings based on section
                if (isEntryPoints) {
                    if (key.equalsIgnoreCase("North")) epNorthRate = value;
                    else if (key.equalsIgnoreCase("East")) epEastRate = value;
                    else if (key.equalsIgnoreCase("south")) epSouthRate = value;
                } else if (isJunctions) {
                    if (key.equalsIgnoreCase("A")) jA_green = value;
                    else if (key.equalsIgnoreCase("B")) jB_green = value;
                    else if (key.equalsIgnoreCase("C")) jC_green = value;
                    else if (key.equalsIgnoreCase("D")) jD_green = value;
                }
            }
        } catch (IOException e) {
            System.err.println("Configuration file " + configFile + " not found or could not be read.");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        // Load scenario config file
        String configFile = (args.length > 0) ? args[0] : "Scenario5.txt";
        loadConfiguration(configFile);

        // Output initial configuration summary
        System.out.println("Using configuration file: " + configFile);
        System.out.println("ENTRYPOINTS: North " + epNorthRate + ", East " + epEastRate + ", South " + epSouthRate);
        System.out.println("JUNCTIONS: A " + jA_green + ", B " + jB_green + ", C " + jC_green + ", D " + jD_green);
        System.out.println("---------------------------------------");

        // Initialize road network
        roadA = new Road(60);   // South → A
        roadB = new Road(30);   // East → B
        roadC = new Road(50);   // North → C
        roadD = new Road(30);   // Placeholder

        // Intermediate connecting roads
        roadCtoD = new Road(10);
        roadBtoA = new Road(7);
        roadAtoB = new Road(7);
        roadCtoB = new Road(10);
        roadBtoC = new Road(10);

        // Exit roads leading to car parks
        exitUniversity = new Road(15);
        exitStation = new Road(15);
        exitShopping = new Road(7);
        exitIndustrial = new Road(15);

        // EntryPoints 
        EntryPoint epNorth = new EntryPoint("North", epNorthRate);
        EntryPoint epEast = new EntryPoint("East", epEastRate);
        EntryPoint epSouth = new EntryPoint("South", epSouthRate);


        // Junction Configurations
        Junction jA = new Junction("A", List.of(roadA, roadBtoA), Map.of(
            "Industrial Park", exitIndustrial,
            "University", roadAtoB,
            "Station", roadAtoB,
            "Shopping Centre", roadAtoB
        ), jA_green, List.of("From South", "From B"));

        Junction jB = new Junction("B", List.of(roadAtoB, roadB, roadCtoB), Map.of(
            "Station", roadBtoC,
            "Shopping Centre", roadBtoC,
            "University", roadBtoC,
            "Industrial Park", roadBtoA
        ), jB_green, List.of("From A", "From East", "From C"));

        Junction jC = new Junction("C", List.of(roadBtoC, roadC), Map.of(
            "Shopping Centre", exitShopping,
            "University", roadCtoD,
            "Station", roadCtoD,
            "Industrial Park", roadCtoB
        ), jC_green, List.of("From B", "From North"));

        Junction jD = new Junction("D", List.of(roadCtoD), Map.of(
            "University", exitUniversity,
            "Station", exitStation
        ), jD_green, List.of("From C"));


        // Set up car parks
        carParkUniversity = new CarPark("University", exitUniversity, 100);
        carParkStation = new CarPark("Station", exitStation, 150);
        carParkShopping = new CarPark("Shopping Centre", exitShopping, 400);
        carParkIndustrial = new CarPark("Industrial Park", exitIndustrial, 1000);

        // Start the simulation clock (6 minutes → 360 simulated seconds)
        clock = new Clock(360);
        clock.start();

        // Start all concurrent system threads
        epNorth.start();
        epEast.start();
        epSouth.start();
        jA.start();
        jB.start();
        jC.start();
        jD.start();
        carParkUniversity.start();
        carParkStation.start();
        carParkShopping.start();
        carParkIndustrial.start();

        try {
            // Start all concurrent system threads
            clock.join();
            Main.running = false; // Signal all threads to stop

            // Ensure all threads finish processing
            epNorth.join();
            epEast.join();
            epSouth.join();
            jA.join();
            jB.join();
            jC.join();
            jD.join();
            carParkUniversity.join();
            carParkStation.join();
            carParkShopping.join();
            carParkIndustrial.join();

            // Ensure all threads finish processing
            Thread.sleep(8000);

            // FINAL REPORT
            System.out.println("\n----- FINAL REPORT -----");
            System.out.println("CarPark University: " + carParkUniversity.getParkedCount() +
                    " cars parked, average journey time: " + carParkUniversity.getAverageJourneyTime() + " simulated sec");
            System.out.println("CarPark Station: " + carParkStation.getParkedCount() +
                    " cars parked, average journey time: " + carParkStation.getAverageJourneyTime() + " simulated sec");
            System.out.println("CarPark Shopping Centre: " + carParkShopping.getParkedCount() +
                    " cars parked, average journey time: " + carParkShopping.getAverageJourneyTime() + " simulated sec");
            System.out.println("CarPark Industrial Park: " + carParkIndustrial.getParkedCount() +
                    " cars parked, average journey time: " + carParkIndustrial.getAverageJourneyTime() + " simulated sec");

            // Count vehicles still on roads
            int queuedVehicles = roadA.getCount() + roadB.getCount() + roadC.getCount() + roadD.getCount() +
                    roadCtoD.getCount() + roadBtoA.getCount() + roadAtoB.getCount() +
                    roadCtoB.getCount() + roadBtoC.getCount() +
                    exitUniversity.getCount() + exitStation.getCount() +
                    exitShopping.getCount() + exitIndustrial.getCount();

            // Output vehicle tracking summary
            System.out.println("Total vehicles created: " + totalVehiclesCreated);
            System.out.println("Total vehicles parked: " + totalVehiclesParked);
            System.out.println("Total vehicles queued on roads: " + queuedVehicles);

            int accountedFor = totalVehiclesParked + queuedVehicles;
            if (Math.abs(totalVehiclesCreated - accountedFor) <= 2) {
                System.out.println("Data consistency check passed (±2 tolerance).");
            } else {
                System.out.println("Data consistency check FAILED.");
                System.out.println("  Mismatch: Created=" + totalVehiclesCreated +
                        ", Accounted=" + accountedFor + " (Parked + Queued)");
            }

            System.out.println("----- Simulation Ended -----");


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Vehicle {
    private static int nextId = 1;
    private int id;
    private String destination;
    private int entryTime;   // Simulated entry time.
    private int parkedTime;  // Simulated parked time.

    public Vehicle(String destination, int entryTime) {
        this.id = nextId++;
        this.destination = destination;
        this.entryTime = entryTime;
        this.parkedTime = -1;
    }

    public String getDestination() {
        return destination;
    }

    public int getEntryTime() {
        return entryTime;
    }

    public void setParkedTime(int time) {
        this.parkedTime = time;
    }

    public int getParkedTime() {
        return parkedTime;
    }

    public int getId() {
        return id;
    }
}

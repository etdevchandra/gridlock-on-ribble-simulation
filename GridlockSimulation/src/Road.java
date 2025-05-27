public class Road {
    private Vehicle[] buffer;
    private int capacity;
    private int head = 0;
    private int tail = 0;
    private int count = 0;

    public Road(int capacity) {
        this.capacity = capacity;
        buffer = new Vehicle[capacity];
    }

    // Check if the road buffer is full
    public synchronized boolean isFull() {
        return count == capacity;
    }

    // Check if the road buffer is empty
    public synchronized boolean isEmpty() {
        return count == 0;
    }

    // Get current number of vehicles on the road
    public synchronized int getCount() {
        return count;
    }

    // Add a vehicle to the road buffer, with wait if full
    public synchronized void addVehicle(Vehicle v) {
        while (count == capacity && Main.running) {
            try {
                wait(100); // Wait and retry if buffer full and simulation still running
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interruption state
                return; 
            }
        }
    
        // If simulation ended or still full after waiting, skip adding
        if (!Main.running || count == capacity) return;
    
        buffer[tail] = v;
        tail = (tail + 1) % capacity;
        count++;
        notifyAll(); 
    }
    
    // Remove and return the next vehicle from the buffer, with wait if empty
    public synchronized Vehicle removeVehicle() {
        while (count == 0 && Main.running) {
            try {
                wait(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); 
                return null;
            }
        }
    
        if (count == 0) return null; // Return null if still empty after wait
    
        Vehicle v = buffer[head];
        head = (head + 1) % capacity;
        count--;
        notifyAll();
        return v;
    }
    
}

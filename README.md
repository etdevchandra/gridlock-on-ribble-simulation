# Gridlock-on-Ribble: Java Traffic Simulation

This repository contains a Java-based concurrency simulation for modelling urban traffic flow and car park management.

---

## Project Overview

- **Goal:** Simulate and optimize traffic flow in a congested urban network using Java concurrency.
- **Key Topics:** Concurrency, synchronization, deadlock avoidance, traffic simulation, fixed-size buffers, multi-threaded design.

---

## Java Traffic Simulation

- Models a network of entry points, single-lane roads (circular buffers), traffic-light controlled junctions, and car parks.
- Implements concurrency using Java threads, `synchronized`, `wait`, and `notify`.
- Entry rates and traffic light timings are configurable via scenario files.
- Reports statistics, journey times, gridlock events, and performs consistency checks.

---

## How to Run

1. **Compile all Java files:**
    ```bash
    javac src/*.java
    ```

2. **Run the simulation with a scenario config:**
    ```bash
    java -cp src Main config/Scenario1.txt
    ```

3. **Check the `upload/` directory** for simulation logs and output.

---

## Highlights

- Full traffic network and concurrency logic
- Deadlock- and livelock-avoiding design
- Configurable via external files for experimentation and optimization
- Detailed console and log reporting

---

## Author

**Enoshan Devchandra**  
BSc (Hons) Software Engineering  
University of Central Lancashire

---

## License

This project is for academic use as part of CO3408 at UCLan.

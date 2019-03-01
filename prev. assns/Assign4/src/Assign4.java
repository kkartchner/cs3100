import java.util.ArrayList;

/**
 * Spawns and starts a number of threads equivalent to the number of avaible CPU cores of the
 * current system; Each thread pops and executes tasks from a TaskQueue, and then stores
 * the results into a ResultTable in order to calculate the first 1000 digits of pi concurrently.
 *
 * @author Ky Kartchner
 */
public class Assign4 {
    public static void main(String[] args) {
        try {
            int threadCount = Runtime.getRuntime().availableProcessors(); // Get number of CPU cores for current
            Thread[] threads = new Thread[threadCount];                   // system and create that many threads.

            final int NUM_OF_DIGITS = 1000;

            TaskQueue taskQueue = generatePiComputerQueue(NUM_OF_DIGITS);
            ResultTable<Integer, Integer> resultTable = new ResultTable<>();

            System.out.println("Calculating pi...");

            long startTime = System.currentTimeMillis(); // Start a timer
            for (int i = 0; i < threadCount; ++i) {
                threads[i] = new Thread(new PiComputer(taskQueue, resultTable));
                threads[i].start();
            }

            for (Thread t : threads) { // Wait until all threads are done before continuing
                t.join();
            }

            long endTime = System.currentTimeMillis(); // End timer

            System.out.println("\n" + "3." + resultTable.toString()); // resultTable only contains decimal part of pi
            System.out.printf("\nPi Computation took %d ms\n", endTime - startTime);

        } catch (Exception e) {
            System.out.println("Exception thrown: " + e);
        }
    }

    /**
     * Generate the TaskQueue to be used for computing n digits of pi;
     * Each task computes the digit of pi specified by its id.
     *
     * @param digitNum The number of digits to create
     */
    private static TaskQueue generatePiComputerQueue(int digitNum) {
        TaskQueue queue = new TaskQueue();
        ArrayList<Task> arrayList = new ArrayList<>(); // Temp container for shuffling
        for (int i = 1; i <= digitNum; ++i) { // Create 'size' number of tasks 
            Task<Integer> t = new Task<>(i);
            t.setToDo(() -> Bpp.getDecimal(t.getId())); // Set the tasks "toDo" function to be getting the idth digit
            arrayList.add(t);
        }
        java.util.Collections.shuffle(arrayList); // Shuffle array to make task execution times more diverse

        queue.addAll(arrayList); // Add random order tasks the actual task queue

        return queue;
    }
}


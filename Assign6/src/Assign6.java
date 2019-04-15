import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Assign6 {
    static final int MAX_MEMORY_FRAMES = 100;
    static final int MAX_PAGE_REFERENCE = 250;
    static final int SIMULATION_COUNT = 100;

    public static void main(String[] args) {
        /*
         * Create a threadPool with the number of workers equal to the number of
         * processors
         */
        int numberOfProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService threadPool = Executors.newFixedThreadPool(numberOfProcessors);

        /* Create 2D arrays for storing simulation results */
        int[][] resultsFIFO = new int[SIMULATION_COUNT][];
        int[][] resultsLRU = new int[SIMULATION_COUNT][];
        int[][] resultsMRU = new int[SIMULATION_COUNT][];
        int[] minCounts = new int[3];

        /* Run SIMULATION_COUNT number of simulations */
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < SIMULATION_COUNT; ++i) {
            resultsFIFO[i] = new int[MAX_MEMORY_FRAMES + 1]; // +1 to allow for results[i][MAX] to be legal
            resultsLRU[i] = new int[MAX_MEMORY_FRAMES + 1];
            resultsMRU[i] = new int[MAX_MEMORY_FRAMES + 1];
            runSimulation(threadPool, resultsFIFO[i], resultsLRU[i], resultsMRU[i], minCounts);
        }

        /* Shutdown the threadPool and wait until all task are complete before printing results */
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        long endTime = System.currentTimeMillis();

        /* Print total simulation time */
        System.out.printf("Simulation took %d ms\n\n", endTime - startTime);

        /* Print number of times each algorithm had minimum number of page faults */
        System.out.printf("FIFO min PF : %d\n", minCounts[0]);
        System.out.printf("LRU min PF : %d\n", minCounts[1]);
        System.out.printf("MRU min PF : %d\n\n", minCounts[2]);

        /* Print Belady's Anomaly reports */
        reportBeladys(resultsFIFO, "FIFO");
        reportBeladys(resultsLRU, "LRU");
        reportBeladys(resultsMRU, "MRU");

//        Tests.validateAlgorithms();
    }

    /**
     * Runs the FIFO, LRU, and MRU page replacement algorithms on a randomly
     * generated sequence using from one to 100 memory frames.
     *
     * @param threadPool     The threadpool to execute the tasks with.
     * @param pageFaultsFIFO The array to store the FIFO page fault info in.
     * @param pageFaultsLRU  The array to store the LRU page fault info in.
     * @param pageFaultsMRU  The array to store the MRU page fault info in.
     * @return The number of times each algorithm had the lowest number of page faults
     */
    static void runSimulation(ExecutorService threadPool, int[] pageFaultsFIFO, int[] pageFaultsLRU,
                              int[] pageFaultsMRU, int[] minCounts) {
        int[] sequence = generateRandomSequence();

        class AlgResult {
            int id;
            int[] pageFaults;

            AlgResult(int id) {
                this.id = id;
            }
        }
        AlgResult[] algResults = new AlgResult[]{
                new AlgResult(0),
                new AlgResult(1),
                new AlgResult(2)
        };

        for (int maxFrames = 1; maxFrames <= MAX_MEMORY_FRAMES; ++maxFrames) {
            TaskFIFO task1 = new TaskFIFO(sequence, maxFrames, MAX_PAGE_REFERENCE, pageFaultsFIFO);
            TaskLRU task2 = new TaskLRU(sequence, maxFrames, MAX_PAGE_REFERENCE, pageFaultsLRU);
            TaskMRU task3 = new TaskMRU(sequence, maxFrames, MAX_PAGE_REFERENCE, pageFaultsMRU);

            threadPool.execute(task1);
            threadPool.execute(task2);
            threadPool.execute(task3);

            Arrays.sort(algResults, Comparator.comparing(i ->);

            ++minCounts[algResults[0].id];
            if (algResults[0].pageFaults == algResults[1].pageFaults) {
                ++minCounts[algResults[1].id];
                if (algResults[1].pageFaults == algResults[2].pageFaults) {
                    ++minCounts[algResults[2].id];
                }
            }
        }
    }

    /**
     * Check for and report on occurences of Belady's Anamoly in the provided results array.
     *
     * @param results The simulation results to be reported on.
     * @param name    The name of the simulation to be printed.
     */
    static void reportBeladys(int[][] results, String name) {
        int occurences = 0;
        int maxDifference = 0;

        System.out.printf("Belady's Anamoly Report for %s\n", name);
        for (int[] pageFaults : results) {
            for (int i = 2; i < pageFaults.length; ++i) {
                if (pageFaults[i] > pageFaults[i - 1]) {
                    int difference = pageFaults[i] - pageFaults[i - 1];
                    System.out.printf("\tdetected - Previous %d : Current %d (%d)\n", pageFaults[i - 1], pageFaults[i],
                            difference);

                    if (difference > maxDifference) {
                        maxDifference = difference;
                    }
                    ++occurences;
                }
            }
        }
        System.out.printf("\t Anomaly detected %d times with a max difference of %d\n\n", occurences, maxDifference);
    }

    /**
     * Generate a random sequence of 1000 page reference numbers that range from 1 to MAX_PAGE_REFERENCE.
     *
     * @return The random sequence.
     */
    static int[] generateRandomSequence() {
        int[] sequence = new int[1000];
        for (int i = 0; i < sequence.length; ++i) {
            sequence[i] = (int) (1 + Math.random() * MAX_PAGE_REFERENCE);
        }
        return sequence;
    }
}

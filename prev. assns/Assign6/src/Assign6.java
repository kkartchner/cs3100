import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Main class for simulating page replacement algorithms of First-In-First-Out,
 * Least-Recently Used, and Most-Recently Used, and printing the results.
 *
 * @author Ky Kartchner
 */
class Assign6 {
    static final int MAX_MEMORY_FRAMES = 100;
    static final int MAX_PAGE_REFERENCE = 250;
    static final int SIMULATION_COUNT = 1000;

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

        /* Run SIMULATION_COUNT number of simulations */
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < SIMULATION_COUNT; ++i) {
            resultsFIFO[i] = new int[MAX_MEMORY_FRAMES + 1]; // +1 to allow for results[i][MAX] to be legal
            resultsLRU[i] = new int[MAX_MEMORY_FRAMES + 1];
            resultsMRU[i] = new int[MAX_MEMORY_FRAMES + 1];
            runSimulation(threadPool, resultsFIFO[i], resultsLRU[i], resultsMRU[i]);
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
        int[] minCounts = new int[3];
        getMinCounts(resultsFIFO, resultsLRU, resultsMRU, minCounts);

        System.out.printf("FIFO min PF : %d\n", minCounts[0]);
        System.out.printf("LRU min PF : %d\n", minCounts[1]);
        System.out.printf("MRU min PF : %d\n\n", minCounts[2]);

        /* Print Belady's Anomaly reports */
        reportBeladys(resultsFIFO, "FIFO");
        reportBeladys(resultsLRU, "LRU");
        reportBeladys(resultsMRU, "MRU");
    }

    /**
     * Runs the FIFO, LRU, and MRU page replacement algorithms on a randomly
     * generated sequence using from one to 100 memory frames.
     *
     * @param threadPool     The threadpool to execute the tasks with.
     * @param pageFaultsFIFO The array to store the FIFO page fault info in.
     * @param pageFaultsLRU  The array to store the LRU page fault info in.
     * @param pageFaultsMRU  The array to store the MRU page fault info in.
     * @return The number of times each algorithm had the lowest number of page faults.
     */
    static void runSimulation(ExecutorService threadPool, int[] pageFaultsFIFO, int[] pageFaultsLRU,
            int[] pageFaultsMRU) {
        int[] sequence = generateRandomSequence();

        for (int maxFrames = 1; maxFrames <= MAX_MEMORY_FRAMES; ++maxFrames) {
            TaskFIFO task1 = new TaskFIFO(sequence, maxFrames, MAX_PAGE_REFERENCE, pageFaultsFIFO);
            TaskLRU task2 = new TaskLRU(sequence, maxFrames, MAX_PAGE_REFERENCE, pageFaultsLRU);
            TaskMRU task3 = new TaskMRU(sequence, maxFrames, MAX_PAGE_REFERENCE, pageFaultsMRU);

            threadPool.execute(task1);
            threadPool.execute(task2);
            threadPool.execute(task3);
        }
    }

    /**
     * Add up the number of times that each algorithm was the lowest or tied for the lowest 
     * number of page faults.
     *
     * @param resultsFIFO Simulation results for FIFO algorithm.
     * @param resultsLRU Simulation results for LRU algorithm.
     * @param resultsMRU Simulation results for MRU algorithm.
     * @param minCounts Array that minCounts are stored in.
     */
    static void getMinCounts (int[][] resultsFIFO, int[][] resultsLRU, 
            int[][] resultsMRU, int[] minCounts){
        /* For keeping algorithm id paired with its number of pageFaults 
         * at specified index */
        class AlgResult {

            private int id;
            private int[] pageFaults;

            AlgResult(int id, int[] pageFaults) {
                this.id = id;
                this.pageFaults = pageFaults;
            }

            int faultsAt(int index){
                return pageFaults[index];
            }
        }  

        for (int j = 0; j < SIMULATION_COUNT; ++j){
            /* Create array for keeping number of page faults paired
             * with the associated algorithm */
            AlgResult[] algResults = new AlgResult[]{
                new AlgResult(0, resultsFIFO[j]),
                    new AlgResult(1, resultsLRU[j]),
                    new AlgResult(2, resultsMRU[j])
            };
            for (int i = 0; i < MAX_MEMORY_FRAMES; ++i){ 
                /* Order the algorithm results for current frame in order of 
                 * ascending number of page faults */
                final int index = i;
                Arrays.sort(algResults, Comparator.comparing(alg -> alg.faultsAt(index)));

                /* The algorithm at algResults[0] is the lowest (or tied for lowest)
                 * so increment it. Also increment algResults[1] and algResults[2] if
                 * they are equal to (tie with) algResults[0] */
                ++minCounts[algResults[0].id]; 
                if (algResults[1].faultsAt(index) == algResults[0].faultsAt(index)) {
                    ++minCounts[algResults[1].id];
                }
                if (algResults[2].faultsAt(index) == algResults[0].faultsAt(index)) {
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

                    /* Update maxDifference if needed */
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

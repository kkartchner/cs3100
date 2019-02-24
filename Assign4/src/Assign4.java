/**
 * @author Ky Kartchner
 */
public class Assign4 {
    public static void main(String[] args) {
        try {
            int threadCount = Runtime.getRuntime().availableProcessors();
            Thread[] threads = new Thread[threadCount];

            long startTime = System.currentTimeMillis();
            for (int i = 0; i < threadCount; ++i) {
                threads[i] = new Thread(new PiComputer());
                threads[i].start();
            }

            for (Thread t : threads) {
                t.join();
            }

            long endTime = System.currentTimeMillis();
            System.out.println("\n" + PiComputer.piString());
            System.out.printf("\nPi Computation took %d ms\n", endTime - startTime);

        } catch (Exception e) {
            System.out.println("Exception thrown: " + e);
        }
    }
}


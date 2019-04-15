class Tests {
    public static void main (String[] args){
         
    }
    public static void validateAlgorithms() {
        // These shadows the global constants of the same name.
        final int MAX_PAGE_REFERENCE = 25;
        final int MAX_MEMORY_FRAMES = 10;

        // Sequence length: 200
        // Pages: [0, 25)
        int[] sequence = {20, 16, 9, 6, 21, 23, 21, 2, 24, 5, 15, 13, 5, 6, 3, 17, 16, 6, 18, 22, 22, 7, 12, 18, 12, 7, 8, 3, 22, 3, 20, 0, 13, 6, 8, 18, 14, 11, 20, 18, 2, 12, 9, 24, 7, 3, 9, 8, 24, 10, 2, 5, 9, 8, 4, 12, 20, 10, 5, 22, 17, 6, 3, 23, 6, 7, 6, 23, 14, 8, 5, 7, 0, 3, 7, 8, 24, 14, 7, 7, 21, 4, 19, 15, 20, 23, 21, 1, 21, 18, 1, 19, 9, 22, 17, 5, 11, 3, 19, 20, 6, 22, 9, 24, 21, 3, 14, 7, 11, 4, 12, 1, 23, 6, 14, 12, 21, 21, 11, 12, 21, 9, 21, 14, 0, 23, 7, 14, 7, 19, 11, 23, 22, 6, 20, 19, 14, 21, 9, 8, 19, 23, 19, 20, 24, 4, 20, 14, 9, 3, 24, 6, 23, 13, 13, 6, 23, 3, 19, 1, 11, 15, 24, 8, 1, 14, 3, 5, 6, 2, 18, 20, 0, 16, 16, 2, 15, 5, 18, 15, 12, 11, 20, 15, 7, 9, 24, 3, 20, 2, 19, 22, 11, 2, 0, 18, 11, 11, 16, 11};

        // First entry in each of these arrays is 0, because we don't simulate 0 frames of memory
        final int[] expectedFIFO = {0, 194, 184, 174, 163, 155, 148, 140, 133, 120, 115};
        final int[] expectedLRU = {0, 194, 185, 173, 164, 155, 147, 140, 126, 117, 110};
        final int[] expectedMRU = {0, 194, 188, 179, 170, 167, 157, 152, 147, 136, 126};

        // This array is used to store the page faults for each of the memory frame sizes
        int[] pageFaults = new int[MAX_MEMORY_FRAMES + 1];

        (new TaskFIFO(sequence, 1, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 2, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 3, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 4, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 5, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 6, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 7, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 8, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 9, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskFIFO(sequence, 10, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.println("Actual: " + java.util.Arrays.toString(pageFaults));
        System.out.println("Expected: " + java.util.Arrays.toString(expectedFIFO));
        if (java.util.Arrays.equals(expectedFIFO, pageFaults)) {
            System.out.println("FIFO Passed");
        }
        else {
            System.out.println("FIFO Failed");
        }

        (new TaskLRU(sequence, 1, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 2, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 3, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 4, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 5, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 6, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 7, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 8, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 9, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskLRU(sequence, 10, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.println("Actual: " + java.util.Arrays.toString(pageFaults));
        System.out.println("Expected: " + java.util.Arrays.toString(expectedLRU));
        if (java.util.Arrays.equals(expectedLRU, pageFaults)) {
            System.out.println("LRU Passed");
        }
        else {
            System.out.println("LRU Failed");
        }

        (new TaskMRU(sequence, 1, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 2, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 3, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 4, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 5, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 6, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 7, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 8, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 9, MAX_PAGE_REFERENCE, pageFaults)).run();
        (new TaskMRU(sequence, 10, MAX_PAGE_REFERENCE, pageFaults)).run();
        System.out.println("Actual: " + java.util.Arrays.toString(pageFaults));
        System.out.println("Expected: " + java.util.Arrays.toString(expectedMRU));
        if (java.util.Arrays.equals(expectedMRU, pageFaults)) {
            System.out.println("MRU Passed");
        }
        else {
            System.out.println("MRU Failed");
        }
    }
}

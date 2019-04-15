class TaskMRU implements Runnable {
    private int[] sequence;
    private int maxMemoryFrames;
    private int maxPageReference;
    private int[] pageFaults;

    public TaskMRU(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults) {
        this.maxMemoryFrames = maxMemoryFrames;
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.maxPageReference = maxPageReference;
        this.pageFaults = pageFaults;
    }

    /**
     * Add the page reference sequence using the most recently used (MRU) replacement algorithm.
     */
    @Override
    public void run() {
        boolean[] inMemory = new boolean[maxPageReference + 1];
        byte framesInUse = 0;

        int mostRecentlyUsed = -1; // -1 to distinguish from when it actually gets assigned

        pageFaults[maxMemoryFrames] = 0; // Ensure that page fault count starts at 0

        /* For each page reference in the sequence */
        for (int pageRef : sequence) {
            if (!inMemory[pageRef]) {
                /* If current page reference is not already in a memory frame */
                ++pageFaults[maxMemoryFrames]; // A page fault has occurred, so increment

                if (framesInUse == maxMemoryFrames) {
                    /* If no more room in memory */
                    inMemory[mostRecentlyUsed] = false; // Remove the most recently used page reference
                } else {
                    ++framesInUse;
                }
                inMemory[pageRef] = true; // Add current page reference to frames
            }
            mostRecentlyUsed = pageRef; // Set current page reference as most recently used
        }
    }
}

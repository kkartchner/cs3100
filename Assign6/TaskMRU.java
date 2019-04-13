class TaskMRU implements Runnable {
    private int maxMemoryFrames;
    int mostRecent;

    public TaskMRU(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults) {
        this.maxMemoryFrames = maxMemoryFrames;
    }

    @Override
    public void run() {
        // System.out.printf("TaskMRU for %s frames complete\n", maxMemoryFrames);
    }

}
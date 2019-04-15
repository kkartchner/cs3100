import java.util.Set;
import java.util.HashSet;

class TaskMRU implements Runnable {
    private int[] sequence;
    private int maxMemoryFrames;
    private int maxPageReference;
    private int[] pageFaults;
    int mostRecentlyUsed;

    public TaskMRU(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults) {
        this.maxMemoryFrames = maxMemoryFrames;
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.maxPageReference = maxPageReference;
        this.pageFaults = pageFaults;
    }

    @Override
    public void run() {
        Set<Integer> memoryFrames = new HashSet<Integer>(maxPageReference);
        for (int i = 0; i < sequence.length; ++i) {
            // If current page reference is not already in a memory frame
            if (!memoryFrames.contains(sequence[i])) {
                ++pageFaults[maxMemoryFrames]; // A page fault has occurred

                if (memoryFrames.size() == maxMemoryFrames) { // If no more room in memory
                    /* Remove the most recently used page reference */
                    memoryFrames.remove(mostRecentlyUsed);
                } 
                memoryFrames.add(sequence[i]);
                mostRecentlyUsed = sequence[i];
            }         
        }
    }
}

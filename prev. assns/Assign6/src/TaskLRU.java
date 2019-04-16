import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Completes a simulation of page reference sequences from 1 to 250,
 * using from 1 to 100 frames, incorporating the Least-Recently Removed
 * (LRU) page replacement algorithm.
 *
 * @author Ky Kartchner
 */
class TaskLRU implements Runnable {
    private int[] sequence;
    private int maxMemoryFrames;
    private int maxPageReference;
    private int[] pageFaults;

    public TaskLRU(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults) {
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.maxPageReference = maxPageReference;
        this.pageFaults = pageFaults;
    }

    /**
     * Add the page reference sequence using the least-recently used (LRU) replacement
     * algorithm to replace frames as needed to make room.
     */
    @Override
    public void run() {
        Set<Integer> memoryFrames = Collections.newSetFromMap(new LinkedHashMap<>(maxMemoryFrames + 1) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Boolean> eldest) {
                return size() > maxMemoryFrames; // Cause eldest to be removed if size is greater than maxMemoryFrames
            }

        });
        pageFaults[maxMemoryFrames] = 0; // Ensure page faults count starts at 0

        for (int value : sequence) {
            /* If current page reference is not already in a memory frame */
            if (!memoryFrames.contains(value)) {
                ++pageFaults[maxMemoryFrames]; // A page fault has occurred, so increment
            } else {
                /* Remove page reference so it can be reinserted as most recently used */
                memoryFrames.remove(value);
            }
            memoryFrames.add(value); // Insert or reinsert page reference into memory
        }
    }
}

import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Iterator;

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

    @Override
    public void run() {
        Set<Integer> memoryFrames = new LinkedHashSet<Integer>(maxPageReference);
        for (int i = 0; i < sequence.length; ++i) {
            // If current page reference is not already in a memory frame
            if (!memoryFrames.contains(sequence[i])) {
                ++pageFaults[maxMemoryFrames]; // A page fault has occurred

                if (memoryFrames.size() == maxMemoryFrames) {
                    /* Remove the front (oldest) entryr of memoryFrames' internal list to make room */
                    Iterator<Integer> it = memoryFrames.iterator();
                    if (it.hasNext()) {
                        it.next();
                        it.remove();
                    }
                } 
            } else {
                /* Remove sequence[i] so it can be reinserted at the end of memoryFrames internal list */ 
                memoryFrames.remove(sequence[i]);
            }
            memoryFrames.add(sequence[i]);
        }
    }
}

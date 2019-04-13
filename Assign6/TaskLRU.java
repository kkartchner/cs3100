import java.util.LinkedHashMap;
import java.util.Map;

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
        Map<Integer, Integer> memoryFrames = new LinkedHashMap<Integer, Integer>(maxPageReference) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > maxMemoryFrames; // Remove eldest if size() is great than max
            }
        };

        for (int i = 0; i < sequence.length; ++i) {
            // If current page reference is not already in a memory frame
            if (!memoryFrames.containsKey(sequence[i])) {
                ++pageFaults[maxMemoryFrames]; // A page fault has occurred

                // Replace appropriate page reference with current page reference
                memoryFrames.put(sequence[i], sequence[i]);
            }
        }
    }
}
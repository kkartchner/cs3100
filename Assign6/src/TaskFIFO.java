import java.util.ArrayDeque;
import java.util.Queue;

class TaskFIFO implements Runnable {
    private int[] sequence;
    private int maxMemoryFrames;
    private int maxPageReference;
    private int[] pageFaults;

    public TaskFIFO(int[] sequence, int maxMemoryFrames, int maxPageReference, int[] pageFaults) {
        this.sequence = sequence;
        this.maxMemoryFrames = maxMemoryFrames;
        this.maxPageReference = maxPageReference;
        this.pageFaults = pageFaults;
    }

    @Override
    public void run() {
        boolean[] inMemory = new boolean[maxPageReference + 1];
        byte framesInUse = 0;

        Queue<Integer> replacementQueue = new ArrayDeque<>();

        pageFaults[maxMemoryFrames] = 0; // Ensure that page fault count starts at 0

        for (int pageRef : sequence) {
            // If current page reference is not already in a memory frame
            if (!inMemory[pageRef]) {
                ++pageFaults[maxMemoryFrames]; // A page fault has occurred

                if (framesInUse == maxMemoryFrames) { // If frames are full
                    // Replace next in line page reference with current page reference
                    inMemory[replacementQueue.poll()] = false;
                } else {
                    ++framesInUse;
                }

                inMemory[pageRef] = true;

                replacementQueue.offer(pageRef);
            }
        }
    }
}

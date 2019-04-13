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

        for (int i = 0; i < sequence.length; ++i) {
            // If current page reference is not already in a memory frame
            if (!inMemory[sequence[i]]) {
                ++pageFaults[maxMemoryFrames]; // A page fault has occurred

                // Replace appropriate page reference with current page reference
                if (framesInUse == maxMemoryFrames) {
                    inMemory[replacementQueue.poll()] = false;
                } else {
                    ++framesInUse;
                }

                inMemory[sequence[i]] = true;

                replacementQueue.offer(sequence[i]);
            }
        }

    }
}
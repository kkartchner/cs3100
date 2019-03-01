/**
 * Class description for a Pi Computer used for spawning as a thread to retrieve and
 * execute tasks from the task queue.
 *
 * @author Ky Kartchner
 */
public class PiComputer implements Runnable {
    /**
     * For keeping track of how many tasks have been executed among all the threads
     */
    private static int completionCount = 0;

    private TaskQueue taskQueue;
    private ResultTable resultTable;


    PiComputer(TaskQueue queue, ResultTable table) {
        this.taskQueue = queue;
        this.resultTable = table;
    }

    /**
     * Pops a task from the task queue, executes it, then stores the result into the result table;
     * Prints a period whenever the number of total tasks completed (completionCount) is a
     * multiple of 10, and a newline every time it is a multiple of 200.
     */
    @Override
    public void run() {
        final int PROGRESS_MULTIPLE = 10;
        final int BREAK_MULTIPLE = 200;

        Task task;
        while ((task = taskQueue.pop()) != null) { // While the task that was popped is not null
            try {
                resultTable.storeValue(task.getId(), task.perform()); // Compute and store value
                if (++completionCount % PROGRESS_MULTIPLE == 0) { // Print period every multiple of 10
                    System.out.print(".");
                    if (completionCount % BREAK_MULTIPLE == 0) { // New line every multiple of 200
                        System.out.println();
                    }
                }
                System.out.flush(); // Help proceeding prints be faster

            } catch (Exception e) {
                System.out.println("Error with popping or storing value: " + e);

            }
        }
    }
}

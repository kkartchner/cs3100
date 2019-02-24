public class PiComputer implements Runnable {
    private static final int NUM_OF_DIGITS = 1000;
    private static TaskQueue taskQueue = new TaskQueue(NUM_OF_DIGITS);
    private static ResultTable resultTable = new ResultTable();

    private static int completionCount = 0;

    private static int count = 0;
    private int id;

    PiComputer() {
        id = ++count;
    }

    @Override
    public void run() {
        Task task;
        final int PROGRESS_MULTIPLE = 10;
        final int BREAK_MULTIPLE = 200;
        while ((task = taskQueue.pop()) != null) {
            try {
                resultTable.storeValue(task.getId(), task.perform());
                if (++completionCount % PROGRESS_MULTIPLE == 0) {
                    System.out.print(".");
                    if (completionCount % BREAK_MULTIPLE == 0) {
                        System.out.println();
                    }
                }
                System.out.flush();

            } catch (Exception e) {
                System.out.println("Error with popping: " + e);

            }
        }
    }

    static String piString() {
        return "3." + resultTable.toString();
    }

}

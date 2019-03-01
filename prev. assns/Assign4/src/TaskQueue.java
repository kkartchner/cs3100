import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

/**
 * Describes the TaskQueue with an underlying queue LinkedList structure;
 * Used for storing tasks for later execution.
 *
 * @author Ky Kartchner
 */
class TaskQueue {
    private Queue<Task> queue = new LinkedList<>();

    /**
     * Returns the frontmost task to be executed.
     */
    synchronized Task pop() {
        return queue.poll();
    }

    /**
     * Adds the specified task the queue.
     *
     * @param task The task to add
     */
    synchronized void add(Task task) {
        queue.add(task);
    }

    /**
     * Adds the entire specified collection of tasks to the queue.
     *
     * @param tasks The collection of tasks to add
     */
    synchronized void addAll(Collection<Task> tasks) {
        queue.addAll(tasks);
    }
}

/**
 * For storing a generic return type function to be executed at a later time.
 *
 * @author Ky Kartchner
 */
class Task<R> { // R is the return type to use with the Supplier functional interface
    private int id;
    private Supplier<R> toDo;

    Task(int id) {
        this.id = id;
    }

    /**
     * Performs the function stored in toDo and returns the return value of it, if
     * there is one.
     */
    R perform() {
        return toDo.get();
    }

    /**
     * Returns the task's stored id value.
     */
    int getId() {
        return id;
    }

    /**
     * Sets the toDo Supplier object to the specified functional interface object.
     *
     * @param toDo Supplier that contains the function to execute
     */
    void setToDo(Supplier<R> toDo) {
        this.toDo = toDo;
    }
}


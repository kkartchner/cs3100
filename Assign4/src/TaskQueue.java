import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

class TaskQueue {
    private Queue<Task> queue = new LinkedList<>();

    TaskQueue(int size) {
        ArrayList<Task> arrayList = new ArrayList<>();
        for (int i = 1; i <= size; ++i) {
            Task t = new Task(i);
            t.setToDo(() -> Bpp.getDecimal(t.getId()));
            arrayList.add(t);
        }
        java.util.Collections.shuffle(arrayList);

        queue.addAll(arrayList);
    }

    synchronized Task pop() {
        return queue.poll();
    }
}

class Task {
    private int id;
    private Supplier<Integer> toDo;

    Task(int id) {
        this.id = id;
    }

    Integer perform() {
        return toDo.get();
    }

    int getId() {
        return id;
    }

    void setToDo(Supplier<Integer> toDo) {
        this.toDo = toDo;
    }
}


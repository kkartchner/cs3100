import java.util.HashMap;

public class ResultTable {
    private HashMap<Integer, Integer> table = new HashMap<>();

    synchronized void storeValue(int key, int val) {
        table.put(key, val);
    }

    synchronized Integer getValue(int key) {
        return table.get(key);
    }

    @Override
    public String toString() {
        StringBuilder valString = new StringBuilder();
        for (var entry : table.entrySet()) {
            valString.append(entry.getValue());
        }
        return valString.toString();
    }
}

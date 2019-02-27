import java.util.HashMap;

/**
 * Used to store the value of the nth digit of pi using n as the key and the computed value as the value. 
 *
 * @author Ky Kartchner
 */
public class ResultTable<K, V> {
    private HashMap<K, V> table = new HashMap<>();

    /**
     * Store specified value at specified key.
     *
     * @param key The key to store at
     * @param val The value to store
     */
    synchronized void storeValue(K key, V val) { 
        table.put(key, val);
    }

    /**
     * Get the value stored at the specified key.
     *
     * @param key The key whose value to get
     */
    synchronized V getValue(K key) { // Get the value stored at the key
        return table.get(key);
    }

    /**
     * String together the digits stored in the table (i.e '14159...' etc.).
     *
     * @return Returns the stringed together digits
     */
    @Override
    public String toString() { 
        StringBuilder valString = new StringBuilder();
        table.forEach((key, value) -> valString.append(value));

        return valString.toString();
    }
}

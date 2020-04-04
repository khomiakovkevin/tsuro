import java.util.HashMap;

/**
 * Represents a bidirectional HashMap.
 * @param <K> The key type for the forwards HashMap.
 * @param <V> The value type for the forwards HashMap.
 */
public class BiMap<K, V> {
  private HashMap<K, V> forwards;
  private HashMap<V, K> backwards;
  private Class<V> v;
  private Class<K> k;
  
  /**
   * Standard constructor.
   */
  public BiMap(Class<K> k, Class<V> v) {
    this.forwards = new HashMap<K, V>();
    this.backwards = new HashMap<V, K>();
    this.k = k;
    this.v = v;
  }
  
  public V getForwards(K key) {
    return this.forwards.get(key);
  }
  
  public K getBackwards(V value) {
    return this.backwards.get(value);
  }
  
  
  /**
   * Retrieves the corresponding key or value of the passed object.
   * @param object The object whose pair is being sought.
   * @return The corresponding key or value of the passed object, or null if it
   *         was in neither the forwards or backwards HashMaps.
   */
  public Object get(Object object) {
    if (this.k.isInstance(object)) {
      return this.getForwards((K) object);
    } else if (this.v.isInstance(object)) {
      return this.getBackwards((V) object);
    } else {
      return null;
    }
  }
  
  /**
   * Inserts the key/value pair into both the forwards and backwards HashMaps.
   * @param key The key for the forwards HashMap.
   * @param value The value for the forwards HashMap.
   */
  public void put(K key, V value) {
    this.forwards.put(key, value);
    this.backwards.put(value, key);
  }
  
}

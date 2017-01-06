package lambda;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BindMap<K, V> {
    
    final Map<K, V> map;
    
    protected BindMap(Map<K, V> map) {
        this.map = map;
    }

    public int size() {
        return map.size();
    }

    public V get(K key) {
        return map.get(key);
    }
    
    public V getOrDefault(K key, V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }
    
    public V define(K key, V value) {
        map.put(key, value);
        return value;
    }
    
    public Restorable put(K key, V value) {
        V old = map.get(key);
        map.put(key, value);
        return old != null ?
            () -> map.put(key, old) :
            () -> map.remove(key);
    }

    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }
    
    @Override
    public String toString() {
        return map.toString();
    }
}

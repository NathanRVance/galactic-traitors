package net.traitors.util;

import java.util.HashMap;
import java.util.Map;

public class BiMap<T, V> {

    private Map<T, V> tvMap = new HashMap<>();
    private Map<V, T> vtMap = new HashMap<>();

    public void put(T t, V v) {
        tvMap.put(t, v);
        vtMap.put(v, t);
    }

    public V get(T t) {
        return tvMap.get(t);
    }

    public T getReverse(V v) {
        return vtMap.get(v);
    }

}

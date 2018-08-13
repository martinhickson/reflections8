package org.reflections.util;

import java.util.*;

public class Multimap<T,V> extends HashMap<T, List<V>> {
    public Multimap(Map<? extends T, ? extends List<V>> m) {
        super(m);
    }

    public void putSingle(T key, V value) {
        List<V> vs = super.get(key);
        if (vs != null) {
            vs.add(value);
        } else {
            super.put(key, Arrays.asList(value));
        }
    }

    public boolean removeSingle(Object key, V value) {
        List<V> vs = super.get(key);
        if (vs == null)
            return false;
        else {
            boolean res = vs.remove(value);
            if (vs.isEmpty())
                super.remove(key);
            return res;
        }
    }
}

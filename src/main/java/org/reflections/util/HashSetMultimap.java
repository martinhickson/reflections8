package org.reflections.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class HashSetMultimap<T,V> extends HashMap<T, Set<V>> implements SetMultimap<T, V> {
    private static final long serialVersionUID = 140511307437539771L;
    private final Supplier<Set<V>> setSupplier;

    public HashSetMultimap() {
        setSupplier = () -> new HashSet<>();
    }

    public HashSetMultimap(Supplier<Set<V>> setSupplier) {
        this.setSupplier = setSupplier;
    }

    @Override
    public boolean putSingle(T key, V value) {
        Set<V> vs = super.get(key);
        if (vs != null) {
            return vs.add(value);
        } else {
            Set<V> setValue = setSupplier.get();
            setValue.add(value);
            super.put(key, setValue);
            return true;
        }
    }

    @Override
    public boolean removeSingle(Object key, V value) {
        Set<V> vs = super.get(key);
        if (vs == null)
            return false;
        else {
            boolean res = vs.remove(value);
            if (vs.isEmpty()) {
                super.remove(key);
            }
            return res;
        }
    }

    @Override
    public Collection<V> flatValues() {
        ArrayList<V> res = new ArrayList<>();
        for (Set<V> s: values()) {
            res.addAll(s);
        }
        return res;
    }

    @Override
    public Set<V> flatValuesAsSet() {
        HashSet<V> res = new HashSet<>();
        for (Set<V> s: values()) {
            res.addAll(s);
        }
        return res;
    }

    @Override
    public Map<T,Set<V>> asMap() {
        Map<T,Set<V>> result = new HashMap<>();
        for (Entry<T,Set<V>> e: entrySet()) {
            if (e.getValue() != null && !e.getValue().isEmpty())
                result.put(e.getKey(), e.getValue());
        }
        return result;
    }
}

package org.reflections.util;

import java.util.*;
import java.util.function.Supplier;

public class SetMultimap<T,V> extends HashMap<T, Set<V>> {
    private static final long serialVersionUID = 140511307437539771L;
    private final Supplier<Set<V>> setSupplier;

    public SetMultimap() {
        super();
        setSupplier = new Supplier<Set<V>>() {
            @Override
            public Set<V> get() {
                return new HashSet<>();
            }
        };

    }

    public SetMultimap(Supplier<Set<V>> setSupplier) {
        this.setSupplier = setSupplier;

    }

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

    public Collection<V> flatValues() {
        ArrayList<V> res = new ArrayList<>();
        for (Set<V> s: values()) {
            res.addAll(s);
        }
        return res;
    }

    public Set<V> flatValuesAsSet() {
        HashSet<V> res = new HashSet<>();
        for (Set<V> s: values()) {
            res.addAll(s);
        }
        return res;
    }
}

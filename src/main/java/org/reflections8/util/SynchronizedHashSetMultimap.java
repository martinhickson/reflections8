package org.reflections8.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Helper class used to avoid guava
 *
 * @author aschoerk
 */
public class SynchronizedHashSetMultimap<K, V> extends HashSetMultimap<K, V> implements SetMultimap<K,V> {

    private static final long serialVersionUID = -907195260984577390L;

    private final Object mutex;
    private final SetMultimap delegate;

    public SynchronizedHashSetMultimap(final SetMultimap delegate) {
        this.delegate = delegate;
        this.mutex = this;
    }

    @Override
    public boolean putSingle(final Object key, final Object value) {
        synchronized (mutex) {
            return delegate.putSingle(key, value);
        }
    }


    @Override
    public void putAllSingles(final SetMultimap<K,V> m) {
        synchronized (mutex) {
            delegate.putAllSingles(m);
        }
    }

    public boolean removeSingle(final Object key, final Object value) {
        synchronized (mutex) {
            return delegate.removeSingle(key, value);
        }
    }

    @Override
    public Collection flatValues() {
        synchronized (mutex) {
            return delegate.flatValues();
        }
    }

    @Override
    public Set flatValuesAsSet() {
        synchronized (mutex) {
            return delegate.flatValuesAsSet();
        }
    }

    @Override
    public Map asMap() {
        synchronized (mutex) {
            return delegate.asMap();
        }
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        synchronized (mutex) {
            return delegate.size();
        }
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        synchronized (mutex) {
            return delegate.isEmpty();
        }
    }


    @Override
    public Set<V> get(final Object key) {
        synchronized (mutex) {
            return (Set<V>) delegate.get(key);
        }
    }


    @Override
    public boolean containsKey(final Object key) {
        synchronized (mutex) {
            return delegate.containsKey(key);
        }
    }


    public Set<V> put(final K key, final Set<V> value) {
        synchronized (mutex) {
            return (Set<V>)delegate.put(key, value);
        }
    }


    public void putAll(final Map m) {
        synchronized (mutex) {
            delegate.putAll(m);
        }
    }



    @Override
    public Set<V> remove(final Object key) {
        synchronized (mutex) {
            return (Set<V>) delegate.remove(key);
        }
    }


    @Override
    public void clear() {
        synchronized (mutex) {
            delegate.clear();
        }
    }


    @Override
    public boolean containsValue(final Object value) {
        synchronized (mutex) {
            return delegate.containsValue(value);
        }
    }


    @Override
    public Set keySet() {
        synchronized (mutex) {
            return delegate.keySet();
        }
    }


    @Override
    public Collection values() {
        synchronized (mutex) {
            return delegate.values();
        }
    }


    @Override
    public Set<Entry<K, Set<V>>> entrySet() {
        synchronized (mutex) {
            return delegate.entrySet();
        }
    }

    public Set<V> getOrDefault(final Object key, final Set<V> defaultValue) {
        synchronized (mutex) {
            return (Set<V>)delegate.getOrDefault(key, defaultValue);
        }
    }

    public Set<V> putIfAbsent(final Object key, final Set<V> value) {
        synchronized (mutex) {
            return (Set<V>)delegate.putIfAbsent(key, value);
        }
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        synchronized (mutex) {
            return delegate.remove(key, value);
        }
    }

    public boolean replace(final Object key, final Set<V> oldValue, final Set<V> newValue) {
        synchronized (mutex) {
            return delegate.replace(key, oldValue, newValue);
        }
    }

    public Set<V> replace(final Object key, final Set<V> value) {
        synchronized (mutex) {
            return (Set<V>)delegate.replace(key, value);
        }
    }

    public Set<V> computeIfAbsent(final Object key, final Function mappingFunction) {
        synchronized (mutex) {
            return (Set<V>)delegate.computeIfAbsent(key, mappingFunction);
        }
    }

    public Set<V> computeIfPresent(final Object key, final BiFunction remappingFunction) {
        synchronized (mutex) {
            return (Set<V>)delegate.computeIfPresent(key, remappingFunction);
        }
    }

    public Set<V> compute(final Object key, final BiFunction remappingFunction) {
        synchronized (mutex) {
            return (Set<V>) delegate.compute(key, remappingFunction);
        }
    }

    public Set<V> merge(final Object key, final Set<V> value, final BiFunction remappingFunction) {
        synchronized (mutex) {
            return (Set<V>)delegate.merge(key, value, remappingFunction);
        }
    }

    public void forEach(final BiConsumer action) {
        synchronized (mutex) {
            delegate.forEach(action);
        }
    }

    public void replaceAll(final BiFunction function) {
        synchronized (mutex) {
            delegate.replaceAll(function);
        }
    }


    @Override
    public boolean equals(final Object o) {
        synchronized (mutex) {
            return delegate.equals(o);
        }
    }


    @Override
    public int hashCode() {
        synchronized (mutex) {
            return delegate.hashCode();
        }
    }

    @Override
    public String toString() {
        synchronized (mutex) {
            return delegate.toString();
        }
    }
}

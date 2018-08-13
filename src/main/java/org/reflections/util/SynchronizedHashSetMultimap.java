package org.reflections.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author aschoerk
 */
public class SynchronizedHashSetMultimap<K, V> extends HashSetMultimap<K, V> implements SetMultimap<K,V> {

    private static final long serialVersionUID = -907195260984577390L;

    Object mutex;
    SetMultimap delegate;

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

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     *
     * @see #put(Object, Object)
     */

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     *
     * @see #put(Object, Object)
     * @param key
     */
    @Override
    public Set<V> get(final Object key) {
        synchronized (mutex) {
            return (Set<V>) delegate.get(key);
        }
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the
     * specified key.
     *
     * @param   key   The key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     * key.
     */
    @Override
    public boolean containsKey(final Object key) {
        synchronized (mutex) {
            return delegate.containsKey(key);
        }
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    public Set<V> put(final K key, final Set<V> value) {
        synchronized (mutex) {
            return (Set<V>)delegate.put(key, value);
        }
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     * These mappings will replace any mappings that this map had for
     * any of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map is null
     */
    public void putAll(final Map m) {
        synchronized (mutex) {
            delegate.putAll(m);
        }
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     *
     * @param  key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    @Override
    public Set<V> remove(final Object key) {
        synchronized (mutex) {
            return (Set<V>) delegate.remove(key);
        }
    }

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    @Override
    public void clear() {
        synchronized (mutex) {
            delegate.clear();
        }
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value
     */
    @Override
    public boolean containsValue(final Object value) {
        synchronized (mutex) {
            return delegate.containsValue(value);
        }
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     *
     * @return a set view of the keys contained in this map
     */
    @Override
    public Set keySet() {
        synchronized (mutex) {
            return delegate.keySet();
        }
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a view of the values contained in this map
     */
    @Override
    public Collection values() {
        synchronized (mutex) {
            return delegate.values();
        }
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation, or through the
     * <tt>setValue</tt> operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations.  It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map
     */
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

    /**
     * Compares the specified object with this map for equality.  Returns
     * <tt>true</tt> if the given object is also a map and the two maps
     * represent the same mappings.  More formally, two maps <tt>m1</tt> and
     * <tt>m2</tt> represent the same mappings if
     * <tt>m1.entrySet().equals(m2.entrySet())</tt>.  This ensures that the
     * <tt>equals</tt> method works properly across different implementations
     * of the <tt>Map</tt> interface.
     *
     * @implSpec
     * This implementation first checks if the specified object is this map;
     * if so it returns <tt>true</tt>.  Then, it checks if the specified
     * object is a map whose size is identical to the size of this map; if
     * not, it returns <tt>false</tt>.  If so, it iterates over this map's
     * <tt>entrySet</tt> collection, and checks that the specified map
     * contains each mapping that this map contains.  If the specified map
     * fails to contain such a mapping, <tt>false</tt> is returned.  If the
     * iteration completes, <tt>true</tt> is returned.
     *
     * @param o object to be compared for equality with this map
     * @return <tt>true</tt> if the specified object is equal to this map
     */
    @Override
    public boolean equals(final Object o) {
        synchronized (mutex) {
            return delegate.equals(o);
        }
    }

    /**
     * Returns the hash code value for this map.  The hash code of a map is
     * defined to be the sum of the hash codes of each entry in the map's
     * <tt>entrySet()</tt> view.  This ensures that <tt>m1.equals(m2)</tt>
     * implies that <tt>m1.hashCode()==m2.hashCode()</tt> for any two maps
     * <tt>m1</tt> and <tt>m2</tt>, as required by the general contract of
     * {@link Object#hashCode}.
     *
     * @implSpec
     * This implementation iterates over <tt>entrySet()</tt>, calling
     * {@link Entry#hashCode hashCode()} on each element (entry) in the
     * set, and adding up the results.
     *
     * @return the hash code value for this map
     * @see Entry#hashCode()
     * @see Object#equals(Object)
     * @see Set#equals(Object)
     */
    @Override
    public int hashCode() {
        synchronized (mutex) {
            return delegate.hashCode();
        }
    }

    /**
     * Returns a string representation of this map.  The string representation
     * consists of a list of key-value mappings in the order returned by the
     * map's <tt>entrySet</tt> view's iterator, enclosed in braces
     * (<tt>"{}"</tt>).  Adjacent mappings are separated by the characters
     * <tt>", "</tt> (comma and space).  Each key-value mapping is rendered as
     * the key followed by an equals sign (<tt>"="</tt>) followed by the
     * associated value.  Keys and values are converted to strings as by
     * {@link String#valueOf(Object)}.
     *
     * @return a string representation of this map
     */
    @Override
    public String toString() {
        synchronized (mutex) {
            return delegate.toString();
        }
    }
}

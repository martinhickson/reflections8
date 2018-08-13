package org.reflections.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author aschoerk
 */
public interface SetMultimap<T, V> extends Map<T, Set<V>> {
    boolean putSingle(T key, V value);

    boolean removeSingle(Object key, V value);

    Collection<V> flatValues();

    Set<V> flatValuesAsSet();

    Map<T,Set<V>> asMap();
}

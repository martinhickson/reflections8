package org.reflections8.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

/**
 * @author aschoerk
 */
public class ReflectionsIterables {

    public static boolean isEmpty(Iterable<?> iterable) {
        return iterable == null || !iterable.iterator().hasNext();
    }

    public static <T> T getOnlyElement(Iterable<T> iterable) {
        if (iterable == null)
            throw new NoSuchElementException();
        final Iterator<T> iterator = iterable.iterator();
        if (!iterator.hasNext())
            throw new NoSuchElementException();
        T res = iterator.next();
        if (iterator.hasNext())
            throw new IllegalArgumentException();
        return res;
    }

    public static <T> Iterable<T> concat(Iterable<? extends T> a,
                                         Iterable<? extends T> b) {
        List<T> res = new ArrayList<>();
        for (T aEl: a) {
            res.add(aEl);
        }
        for (T bEl: b) {
            res.add(bEl);
        }
        return res;
    }

    public static <T> Set<T> makeSetOf(Iterable<? extends T> a) {
        HashSet<T> res = new HashSet<>();

        for (T aEl: a) {
            res.add(aEl);
        }
        return res;
    }


    public static boolean 	contains(Iterable<?> iterable, Object element) {
        for (Object el: iterable) {
            if (el.equals(element))
                return true;
        }
        return false;
    }

    public static <F,T> Iterable<T> transform(Iterable<F> fromIterable,
                                              Function<? super F,? extends T> function) {
        final ArrayList<T> res = new ArrayList<>();
        if (fromIterable != null) {
            for (F el : fromIterable) {
                res.add(function.apply(el));
            }
        }
        return res;
    }

    public static <F,T> Set<T> transformToSet(Iterable<F> fromIterable,
                                              Function<? super F,? extends T> function) {
        Set<T> res = new HashSet<>();
        if (fromIterable != null) {
            for (F el : fromIterable) {
                res.add(function.apply(el));
            }
        }
        return res;
    }
}

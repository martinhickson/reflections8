package org.reflections.util;

import java.util.function.Predicate;

public class AlwaysTruePredicate<T> implements Predicate<T> {

    @Override
    public boolean test(T t) {
        return true;
    }
}

package org.reflections8.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractIterator<T> implements Iterator<T> {

    private T tmp = null;

    private boolean endOfData = false;

    protected T endOfData() {
        endOfData = true;
        return null;
    }

    protected abstract T computeNext();

    public T next() {
        if (endOfData || !hasNext()) {
            throw new NoSuchElementException("nothing left");
        }
        T res = tmp;
        tmp = null;
        return res;
    }

    @Override
    public boolean hasNext() {
        if (tmp != null)
            return true;
        tmp = computeNext();
        return !endOfData;
    }
}

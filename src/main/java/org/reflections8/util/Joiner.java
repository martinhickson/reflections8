package org.reflections8.util;

import java.util.StringJoiner;

public class Joiner {

    StringJoiner j;

    public Joiner(String separator) {
        this.j = new StringJoiner(separator);
    }

    public static Joiner on(String separator) {
        return new Joiner(separator);
    }

    public String join(Object first, Object ... rest) {
        j.add(first.toString());
        for (Object o: rest) {
            j.add(o.toString());
        }
        return j.toString();
    }

    public String join(Iterable<?> parts) {
        for (Object o: parts) {
            j.add(o.toString());
        }
        return j.toString();
    }

}

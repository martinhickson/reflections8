package org.reflections;

import org.reflections.util.Multimap;
import org.reflections.util.SetMultimap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * stores metadata information in multimaps
 * <p>use the different query methods (getXXX) to query the metadata
 * <p>the query methods are string based, and does not cause the class loader to define the types
 * <p>use {@link org.reflections.Reflections#getStore()} to access this store
 */
public class Store {

    private transient boolean concurrent;
    private final Map<String, SetMultimap<String, String>> storeMap;

    //used via reflection
    @SuppressWarnings("UnusedDeclaration")
    protected Store() {
        storeMap = new HashMap<String, SetMultimap<String, String>>();
        concurrent = false;
    }

    public Store(Configuration configuration) {
        storeMap = new HashMap<String, SetMultimap<String, String>>();
        concurrent = configuration.getExecutorService().isPresent();
    }

    /** return all indices */
    public Set<String> keySet() {
        return storeMap.keySet();
    }

    /** get or create the multimap object for the given {@code index} */
    public SetMultimap<String, String> getOrCreate(String index) {
        SetMultimap<String, String> mmap = storeMap.get(index);
        if (mmap == null) {
            SetMultimap<String, String> multimap =
                new SetMultimap(
                        new Supplier<Set<String>>() {
                            public Set<String> get() {
                                return Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
                            }
                        });
            if (concurrent)
                throw new NotImplementedException();
            else {
                mmap = multimap;
            }
            // map = concurrent ? new SynchronizedSetMultimap<String, String>(multimap) : multimap;
            storeMap.put(index,mmap);
        }
        return mmap;
    }

    /** get the multimap object for the given {@code index}, otherwise throws a {@link org.reflections.ReflectionsException} */
    public SetMultimap<String, String> get(String index) {
        SetMultimap<String, String> mmap = storeMap.get(index);
        if (mmap == null) {
            throw new ReflectionsException("Scanner " + index + " was not configured");
        }
        return mmap;
    }

    /** get the values stored for the given {@code index} and {@code keys} */
    public Iterable<String> get(String index, String... keys) {
        return get(index, Arrays.asList(keys));
    }

    /** get the values stored for the given {@code index} and {@code keys} */
    public Iterable<String> get(String index, Iterable<String> keys) {
        SetMultimap<String, String> mmap = get(index);
        IterableChain<String> result = new IterableChain<String>();
        for (String key : keys) {
            result.addAll(mmap.get(key));
        }
        return result;
    }

    /** recursively get the values stored for the given {@code index} and {@code keys}, including keys */
    private Iterable<String> getAllIncluding(String index, Iterable<String> keys, IterableChain<String> result) {
        result.addAll(keys);
        for (String key : keys) {
            Iterable<String> values = get(index, key);
            if (values.iterator().hasNext()) {
                getAllIncluding(index, values, result);
            }
        }
        return result;
    }

    /** recursively get the values stored for the given {@code index} and {@code keys}, not including keys */
    public Iterable<String> getAll(String index, String key) {
        return getAllIncluding(index, get(index, key), new IterableChain<String>());
    }

    /** recursively get the values stored for the given {@code index} and {@code keys}, not including keys */
    public Iterable<String> getAll(String index, Iterable<String> keys) {
        return getAllIncluding(index, get(index, keys), new IterableChain<String>());
    }

    private static class IterableChain<T> implements Iterable<T> {
        private final List<Iterable<T>> chain = new ArrayList();

        private void addAll(Iterable<T> iterable) { chain.add(iterable); }

        public Iterator<T> iterator() {
            List<T> result = new ArrayList<>();
            chain.forEach(iterable -> { if (iterable != null) iterable.forEach(element -> result.add(element));});
            return result.iterator();
        }

        // public Iterator<T> iterator() { return Iterables.concat(chain).iterator(); }
    }
}

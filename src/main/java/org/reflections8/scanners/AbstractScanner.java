package org.reflections8.scanners;

import java.util.Optional;
import java.util.function.Predicate;

import org.reflections8.Configuration;
import org.reflections8.ReflectionsException;
import org.reflections8.adapters.MetadataAdapter;
import org.reflections8.util.AlwaysTruePredicate;
import org.reflections8.util.SetMultimap;
import org.reflections8.vfs.Vfs;

/**
 *
 */
@SuppressWarnings({"RawUseOfParameterizedType", "unchecked"})
public abstract class AbstractScanner implements Scanner {

	private Configuration configuration;
	private SetMultimap<String, String> store;
	private Predicate<String> resultFilter = new AlwaysTruePredicate(); //accept all by default

    public boolean acceptsInput(String file) {
        return getMetadataAdapter().acceptsInput(file);
    }

    public Object scan(Vfs.File file, Optional<Object> classObject) {
        if (!classObject.isPresent()) {
            try {
                classObject = Optional.of(configuration.getMetadataAdapter().getOrCreateClassObject(file));
            } catch (Exception e) {
                throw new ReflectionsException("could not create class object from file " + file.getRelativePath(), e);
            }
        }
        scan(classObject.get());
        return classObject.get();
    }

    public abstract void scan(Object cls);

    //
    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final Configuration configuration) {
        this.configuration = configuration;
    }

    public SetMultimap<String, String> getStore() {
        return store;
    }

    public void setStore(final SetMultimap<String, String> store) {
        this.store = store;
    }

    public Predicate<String> getResultFilter() {
        return resultFilter;
    }

    public void setResultFilter(Predicate<String> resultFilter) {
        this.resultFilter = resultFilter;
    }

    public Scanner filterResultsBy(Predicate<String> filter) {
        this.setResultFilter(filter); return this;
    }

    //
    public boolean acceptResult(final String fqn) {
		return fqn != null && resultFilter.test(fqn);
	}

	protected MetadataAdapter getMetadataAdapter() {
		return configuration.getMetadataAdapter();
	}

    //
    @Override public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass();
    }

    @Override public int hashCode() {
        return getClass().hashCode();
    }
}

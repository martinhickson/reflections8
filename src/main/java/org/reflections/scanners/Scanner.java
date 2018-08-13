package org.reflections.scanners;

import java.util.Optional;
import java.util.function.Predicate;

import org.reflections.Configuration;
import org.reflections.util.SetMultimap;
import org.reflections.vfs.Vfs;

/**
 *
 */
public interface Scanner {

    void setConfiguration(Configuration configuration);

    SetMultimap<String, String> getStore();

    void setStore(SetMultimap<String, String> store);

    Scanner filterResultsBy(Predicate<String> filter);

    boolean acceptsInput(String file);

    Object scan(Vfs.File file, Optional<Object> classObject);

    boolean acceptResult(String fqn);
}

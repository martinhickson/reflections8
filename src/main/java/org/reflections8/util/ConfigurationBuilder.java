package org.reflections8.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import org.reflections8.Configuration;
import org.reflections8.Reflections;
import org.reflections8.ReflectionsException;
import org.reflections8.adapters.JavaReflectionAdapter;
import org.reflections8.adapters.JavassistAdapter;
import org.reflections8.adapters.MetadataAdapter;
import org.reflections8.scanners.Scanner;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.reflections8.serializers.Serializer;
import org.reflections8.serializers.XmlSerializer;

/**
 * a fluent builder for {@link org.reflections8.Configuration}, to be used for constructing a {@link org.reflections8.Reflections} instance
 * <p>usage:
 * <pre>
 *      new Reflections(
 *          new ConfigurationBuilder()
 *              .filterInputsBy(new FilterBuilder().include("your project's common package prefix here..."))
 *              .setUrls(ClasspathHelper.forClassLoader())
 *              .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner().filterResultsBy(myClassAnnotationsFilter)));
 * </pre>
 * <br>{@link #executorService} is used optionally used for parallel scanning. if value is null then scanning is done in a simple for loop
 * <p>defaults: accept all for {@link #inputsFilter},
 * {@link #executorService} is null,
 * {@link #serializer} is {@link org.reflections8.serializers.XmlSerializer}
 */
public class ConfigurationBuilder implements Configuration {
    private Set<Scanner> scanners;
    private Set<URL> urls;
    /*lazy*/ protected MetadataAdapter metadataAdapter;
    private Optional<Predicate<String>> inputsFilter = Optional.empty();
    /*lazy*/ private Serializer serializer;
    private Optional<ExecutorService> executorService = Optional.empty();
    private Optional<ClassLoader[]> classLoaders = Optional.empty();
    private boolean expandSuperTypes = true;

    public ConfigurationBuilder() {
        scanners = new HashSet();
        scanners.add(new TypeAnnotationsScanner());
        scanners.add(new SubTypesScanner());
        urls = new HashSet();
    }

    /**
     * constructs a {@link ConfigurationBuilder} using the given parameters, in a non statically typed way.
     * that is, each element in {@code params} is guessed by it's type and populated into the configuration.
     * <ul>
     * <li>{@link String} - add urls using {@link ClasspathHelper#forPackage(String, ClassLoader...)} ()}</li>
     * <li>{@link Class} - add urls using {@link ClasspathHelper#forClass(Class, ClassLoader...)} </li>
     * <li>{@link ClassLoader} - use these classloaders in order to find urls in ClasspathHelper.forPackage(), ClasspathHelper.forClass() and for resolving types</li>
     * <li>{@link Scanner} - use given scanner, overriding the default scanners</li>
     * <li>{@link URL} - add the given url for scanning</li>
     * <li>{@code Object[]} - flatten and use each element as above</li>
     * </ul>
     * <p>
     * an input {@link FilterBuilder} will be set according to given packages.
     * <p>use any parameter type in any order. this constructor uses instanceof on each param and instantiate a {@link ConfigurationBuilder} appropriately.
     */
    @SuppressWarnings("unchecked")
    public static ConfigurationBuilder build(final Object... params) {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        //flatten
        List<Object> parameters = new ArrayList();
        if (params != null) {
            for (Object param : params) {
                if (param != null) {
                    if (param.getClass().isArray()) {
                        for (Object p : (Object[]) param) if (p != null) parameters.add(p);
                    } else if (param instanceof Iterable) {
                        for (Object p : (Iterable) param) if (p != null) parameters.add(p);
                    } else parameters.add(param);
                }
            }
        }

        List<ClassLoader> loaders = new ArrayList();
        for (Object param : parameters) if (param instanceof ClassLoader) loaders.add((ClassLoader) param);

        ClassLoader[] classLoaders = loaders.isEmpty() ? null : loaders.toArray(new ClassLoader[loaders.size()]);
        FilterBuilder filter = new FilterBuilder();
        List<Scanner> scanners = new ArrayList();

        for (Object param : parameters) {
            if (param instanceof String) {
                builder.addUrls(ClasspathHelper.forPackage((String) param, classLoaders));
                filter.includePackage((String) param);
            } else if (param instanceof Class) {
                if (Scanner.class.isAssignableFrom((Class) param)) {
                    try {
                        builder.addScanners(((Scanner) ((Class) param).newInstance()));
                    } catch (Exception e) { /*fallback*/ }
                }
                builder.addUrls(ClasspathHelper.forClass((Class) param, classLoaders));
                filter.includePackage(((Class) param));
            } else if (param instanceof Scanner) {
                scanners.add((Scanner) param);
            } else if (param instanceof URL) {
                builder.addUrls((URL) param);
            } else if (param instanceof ClassLoader) { /* already taken care */ } else if (param instanceof Predicate) {
                filter.add((Predicate<String>) param);
            } else if (param instanceof ExecutorService) {
                builder.setExecutorService((ExecutorService) param);
            } else if (Reflections.log.isPresent()) {
                throw new ReflectionsException("could not use param " + param);
            }
        }

        if (builder.getUrls().isEmpty()) {
            if (classLoaders != null) {
                builder.addUrls(ClasspathHelper.forClassLoader(classLoaders)); //default urls getResources("")
            } else {
                builder.addUrls(ClasspathHelper.forClassLoader()); //default urls getResources("")
            }
        }

        builder.filterInputsBy(filter);
        if (!scanners.isEmpty()) {
            builder.setScanners(scanners.toArray(new Scanner[scanners.size()]));
        }
        if (!loaders.isEmpty()) {
            builder.addClassLoaders(loaders);
        }

        return builder;
    }

    public ConfigurationBuilder forPackages(String... packages) {
        for (String pkg : packages) {
            addUrls(ClasspathHelper.forPackage(pkg));
        }
        return this;
    }

    public Set<Scanner> getScanners() {
        return scanners;
    }

    /**
     * set the scanners instances for scanning different metadata
     */
    public ConfigurationBuilder setScanners(final Scanner... scanners) {
        this.scanners.clear();
        return addScanners(scanners);
    }

    /**
     * set the scanners instances for scanning different metadata
     */
    public ConfigurationBuilder addScanners(final Scanner... scanners) {
        HashSet<Scanner> set = new HashSet();
        set.addAll(Arrays.asList(scanners));
        this.scanners.addAll(set);
        return this;
    }

    public Set<URL> getUrls() {
        return urls;
    }

    /**
     * set the urls to be scanned
     * <p>use {@link org.reflections8.util.ClasspathHelper} convenient methods to get the relevant urls
     */
    public ConfigurationBuilder setUrls(final Collection<URL> urls) {
        this.urls = new HashSet(urls);
        return this;
    }

    /**
     * set the urls to be scanned
     * <p>use {@link org.reflections8.util.ClasspathHelper} convenient methods to get the relevant urls
     */
    public ConfigurationBuilder setUrls(final URL... urls) {
        this.urls = new HashSet(Arrays.asList(urls));
        return this;
    }

    /**
     * add urls to be scanned
     * <p>use {@link org.reflections8.util.ClasspathHelper} convenient methods to get the relevant urls
     */
    public ConfigurationBuilder addUrls(final Collection<URL> urls) {
        this.urls.addAll(urls);
        return this;
    }

    /**
     * add urls to be scanned
     * <p>use {@link org.reflections8.util.ClasspathHelper} convenient methods to get the relevant urls
     */
    public ConfigurationBuilder addUrls(final URL... urls) {
        this.urls.addAll(Arrays.asList(urls));
        return this;
    }

    /**
     * returns the metadata adapter.
     * if javassist library exists in the classpath, this method returns {@link JavassistAdapter} otherwise defaults to {@link JavaReflectionAdapter}.
     * <p>the {@link JavassistAdapter} is preferred in terms of performance and class loading.
     */
    public MetadataAdapter getMetadataAdapter() {
        if (metadataAdapter != null) return metadataAdapter;
        else {
            try {
                return (metadataAdapter = new JavassistAdapter());
            } catch (Throwable e) {
                if (Reflections.log.isPresent())
                    Reflections.log.get().warn("could not create JavassistAdapter, using JavaReflectionAdapter", e);
                return (metadataAdapter = new JavaReflectionAdapter());
            }
        }
    }

    /**
     * sets the metadata adapter used to fetch metadata from classes
     */
    public ConfigurationBuilder setMetadataAdapter(final MetadataAdapter metadataAdapter) {
        this.metadataAdapter = metadataAdapter;
        return this;
    }

    public Optional<Predicate<String>> getInputsFilter() {
        return inputsFilter;
    }

    /**
     * sets the input filter for all resources to be scanned.
     * <p> supply a {@link Predicate} or use the {@link FilterBuilder}
     */
    public void setInputsFilter(Predicate<String> inputsFilter) {
        this.inputsFilter = Optional.of(inputsFilter);
    }

    /**
     * sets the input filter for all resources to be scanned.
     * <p> supply a {@link Predicate} or use the {@link FilterBuilder}
     */
    public ConfigurationBuilder filterInputsBy(Predicate<String> inputsFilter) {
        this.inputsFilter = Optional.of(inputsFilter);
        return this;
    }


    public Optional<ExecutorService> getExecutorService() {
        return executorService;
    }

    /**
     * sets the executor service used for scanning.
     */
    public ConfigurationBuilder setExecutorService(ExecutorService executorService) {
        this.executorService = Optional.of(executorService);
        return this;
    }

    /**
     * sets the executor service used for scanning to ThreadPoolExecutor with core size as {@link java.lang.Runtime#availableProcessors()}
     * <p>default is ThreadPoolExecutor with a single core
     */
    public ConfigurationBuilder useParallelExecutor() {
        return useParallelExecutor(Runtime.getRuntime().availableProcessors());
    }

    /**
     * sets the executor service used for scanning to ThreadPoolExecutor with core size as the given availableProcessors parameter.
     * the executor service spawns daemon threads by default.
     * <p>default is ThreadPoolExecutor with a single core
     */
    public ConfigurationBuilder useParallelExecutor(final int availableProcessors) {
        setExecutorService(Executors.newFixedThreadPool(availableProcessors));
        return this;
    }

    public Serializer getSerializer() {
        return serializer != null ? serializer : (serializer = new XmlSerializer()); //lazily defaults to XmlSerializer
    }

    /**
     * sets the serializer used when issuing {@link org.reflections8.Reflections#save}
     */
    public ConfigurationBuilder setSerializer(Serializer serializer) {
        this.serializer = serializer;
        return this;
    }

    /**
     * get class loader, might be used for scanning or resolving methods/fields
     */

    public Optional<ClassLoader[]> getClassLoaders() {
        return classLoaders;
    }

    @Override
    public boolean shouldExpandSuperTypes() {
        return expandSuperTypes;
    }

    /**
     * if set to true, Reflections will expand super types after scanning.
     * <p>see {@link org.reflections8.Reflections#expandSuperTypes()}
     */
    public ConfigurationBuilder setExpandSuperTypes(boolean expandSuperTypes) {
        this.expandSuperTypes = expandSuperTypes;
        return this;
    }

    /**
     * set class loader, might be used for resolving methods/fields
     */
    public void setClassLoaders(Optional<ClassLoader[]> classLoaders) {
        this.classLoaders = classLoaders;
    }

    /**
     * add class loader, might be used for resolving methods/fields
     */
    public ConfigurationBuilder addClassLoader(ClassLoader classLoader) {
        return addClassLoaders(classLoader);
    }

    /**
     * add class loader, might be used for resolving methods/fields
     */
    public ConfigurationBuilder addClassLoaders(ClassLoader... classLoaders) {
        if (this.classLoaders.isPresent()) {
            ClassLoader[] existing = this.classLoaders.get();
            int existingLength = existing.length;
            ClassLoader[] tmpArray = Arrays.copyOf(existing, existingLength + classLoaders.length);
            System.arraycopy(classLoaders, 0, tmpArray, existingLength, classLoaders.length);
            this.classLoaders = Optional.of(tmpArray);
        } else {
            this.classLoaders = Optional.of(classLoaders);
        }
        return this;
    }

    /**
     * add class loader, might be used for resolving methods/fields
     */
    public ConfigurationBuilder addClassLoaders(Collection<ClassLoader> classLoaders) {
        return addClassLoaders(classLoaders.toArray(new ClassLoader[classLoaders.size()]));
    }
}

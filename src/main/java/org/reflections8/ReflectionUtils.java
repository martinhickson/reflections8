package org.reflections8;

import static org.reflections8.util.Utils.isEmpty;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.reflections8.util.AlwaysTruePredicate;
import org.reflections8.util.ClasspathHelper;
import org.slf4j.Logger;

/** convenient java reflection helper methods
 * <p>
 *     1. some helper methods to get type by name: {@link #forName(String, ClassLoader...)} and {@link #forNames(Iterable, ClassLoader...)}
 * <p>
 *     2. some helper methods to get all types/methods/fields/constructors/properties matching some predicates, generally:
 *     <pre> Set&lt;?&gt; result = getAllXXX(type/s, withYYY) </pre>
 *     <p>where get methods are:
 *     <ul>
 *         <li>{@link #getAllSuperTypes(Class, Predicate...)}
 *         <li>{@link #getAllFields(Class, Predicate...)}
 *         <li>{@link #getAllMethods(Class, Predicate...)}
 *         <li>{@link #getAllConstructors(Class,Predicate...)}
 *     </ul>
 *     <p>and predicates included here all starts with "with", such as
 *     <ul>
 *         <li>{@link #withAnnotation(java.lang.annotation.Annotation)}
 *         <li>{@link #withModifier(int)}
 *         <li>{@link #withName(String)}
 *         <li>{@link #withParameters(Class[])}
 *         <li>{@link #withAnyParameterAnnotation(Class)}
 *         <li>{@link #withParametersAssignableTo(Class[])}
 *         <li>{@link #withParametersAssignableFrom(Class[])}
 *         <li>{@link #withPrefix(String)}
 *         <li>{@link #withReturnType(Class)}
 *         <li>{@link #withType(Class)}
 *         <li>{@link #withTypeAssignableTo}
 *     </ul>
 *
 *     <p><br>
 *      for example, getting all getters would be:
 *     <pre>
 *      Set&lt;Method&gt; getters = getAllMethods(someClasses,
 *              Predicates.and(
 *                      withModifier(Modifier.PUBLIC),
 *                      withPrefix("get"),
 *                      withParametersCount(0)));
 *     </pre>
 * */
@SuppressWarnings("unchecked")
public abstract class ReflectionUtils {

    private static final String SEMICOLON = ";";

    private static final String L = "L";

    private static final String EMPTY_STRING = "";

    private static final String CLOSE_SQUARE_BRACKET = "]";

    private static final String OPEN_SQUARE_BRACKET = "[";

    private static final String TYPE_UNAVAILABLE_SHORT = "Type: %s unavailable";

    private static final String TYPE_UNAVAILABLE = "Type: %s unavailable from any class loader";

    /** would include {@code Object.class} when {@link #getAllSuperTypes(Class, Predicate[])}. default is false. */
    public static final boolean includeObject = false;

    /** get all super types of given {@code type}, including, optionally filtered by {@code predicates}
     * <p> include {@code Object.class} if {@link #includeObject} is true */
    public static Set<Class<?>> getAllSuperTypes(final Class<?> type, Predicate<? super Class<?>>... predicates) {
        Set<Class<?>> result = new LinkedHashSet<Class<?>>();
        if (type != null && (includeObject || !type.equals(Object.class))) {
            result.add(type);
            for (Class<?> supertype : getSuperTypes(type)) {
                result.addAll(getAllSuperTypes(supertype));
            }
        }
        return filter(result, predicates);
    }

    /** get the immediate supertype and interfaces of the given {@code type} */
    public static Set<Class<?>> getSuperTypes(Class<?> type) {
        Set<Class<?>> result = new LinkedHashSet<>();
        Class<?> superclass = type.getSuperclass();
        Class<?>[] interfaces = type.getInterfaces();
        if (superclass != null && (includeObject || !superclass.equals(Object.class))) result.add(superclass);
        if (interfaces != null && interfaces.length > 0) result.addAll(Arrays.asList(interfaces));
        return result;
    }

    /** get all methods of given {@code type}, up the super class hierarchy, optionally filtered by {@code predicates} */
    public static Set<Method> getAllMethods(final Class<?> type, Predicate<? super Method>... predicates) {
        Set<Method> result = new HashSet<Method>();
        for (Class<?> t : getAllSuperTypes(type)) {
            result.addAll(getMethods(t, predicates));
        }
        return result;
    }

    /** get methods of given {@code type}, optionally filtered by {@code predicates} */
    public static Set<Method> getMethods(Class<?> t, Predicate<? super Method>... predicates) {
        return filter(t.isInterface() ? t.getMethods() : t.getDeclaredMethods(), predicates);
    }

    /** get all constructors of given {@code type}, up the super class hierarchy, optionally filtered by {@code predicates} */
    public static Set<Constructor> getAllConstructors(final Class<?> type, Predicate<? super Constructor>... predicates) {
        Set<Constructor> result = new HashSet<Constructor>();
        for (Class<?> t : getAllSuperTypes(type)) {
            result.addAll(getConstructors(t, predicates));
        }
        return result;
    }

    /** get constructors of given {@code type}, optionally filtered by {@code predicates} */
    public static Set<Constructor> getConstructors(Class<?> t, Predicate<? super Constructor>... predicates) {
        return ReflectionUtils.<Constructor>filter(t.getDeclaredConstructors(), predicates); //explicit needed only for jdk1.5
    }

    /** get all fields of given {@code type}, up the super class hierarchy, optionally filtered by {@code predicates} */
    public static Set<Field> getAllFields(final Class<?> type, Predicate<? super Field>... predicates) {
        Set<Field> result = new HashSet<Field>();
        for (Class<?> t : getAllSuperTypes(type)) result.addAll(getFields(t, predicates));
        return result;
    }

    /** get fields of given {@code type}, optionally filtered by {@code predicates} */
    public static Set<Field> getFields(Class<?> type, Predicate<? super Field>... predicates) {
        return filter(type.getDeclaredFields(), predicates);
    }

    /** get all annotations of given {@code type}, up the super class hierarchy, optionally filtered by {@code predicates} */
    public static <T extends AnnotatedElement> Set<Annotation>  getAllAnnotations(T type, Predicate<Annotation>... predicates) {
        Set<Annotation> result = new HashSet<Annotation>();
        if (type instanceof Class) {
            for (Class<?> t : getAllSuperTypes((Class<?>) type)) {
                result.addAll(getAnnotations(t, predicates));
            }
        } else {
            result.addAll(getAnnotations(type, predicates));
        }
        return result;
    }

    /** get annotations of given {@code type}, optionally honorInherited, optionally filtered by {@code predicates} */
    public static <T extends AnnotatedElement> Set<Annotation> getAnnotations(T type, Predicate<Annotation>... predicates) {
        return filter(type.getDeclaredAnnotations(), predicates);
    }

    /** filter all given {@code elements} with {@code predicates}, if given */
    public static <T extends AnnotatedElement> Set<T> getAll(final Set<T> elements, Predicate<? super T>... predicates) {
        Predicate<? super T> p = new AlwaysTruePredicate<>();
        for (Predicate x: predicates) {
            p = p.and(x);
        }
        return isEmpty(predicates) ? elements : elements.stream().filter(p).collect(Collectors.toSet());
    }

    //predicates
    /** where member name equals given {@code name} */
    public static <T extends Member> Predicate<T> withName(final String name) {
        return new Predicate<T>() {
            public boolean test(T input) {
                return input != null && input.getName().equals(name);
            }
        };
    }

    /** where member name startsWith given {@code prefix} */
    public static <T extends Member> Predicate<T> withPrefix(final String prefix) {
        return new Predicate<T>() {
            public boolean test(T input) {
                return input != null && input.getName().startsWith(prefix);
            }
        };
    }

    /** where member's {@code toString} matches given {@code regex}
     * <p>for example:
     * <pre>
     *  getAllMethods(someClass, withPattern("public void .*"))
     * </pre>
     * */
    public static <T extends AnnotatedElement> Predicate<T> withPattern(final String regex) {
        return new Predicate<T>() {
            public boolean test(T input) {
                return Pattern.matches(regex, input.toString());
            }
        };
    }

    /** where element is annotated with given {@code annotation} */
    public static <T extends AnnotatedElement> Predicate<T> withAnnotation(final Class<? extends Annotation> annotation) {
        return new Predicate<T>() {
            public boolean test(T input) {
                return input != null && input.isAnnotationPresent(annotation);
            }
        };
    }

    /** where element is annotated with given {@code annotations} */
    public static <T extends AnnotatedElement> Predicate<T> withAnnotations(final Class<? extends Annotation>... annotations) {
        return new Predicate<T>() {
            public boolean test(T input) {
                return input != null && Arrays.equals(annotations, annotationTypes(input.getAnnotations()));
            }
        };
    }

    /** where element is annotated with given {@code annotation}, including member matching */
    public static <T extends AnnotatedElement> Predicate<T> withAnnotation(final Annotation annotation) {
        return new Predicate<T>() {
            public boolean test(T input) {
                return input != null && input.isAnnotationPresent(annotation.annotationType()) &&
                        areAnnotationMembersMatching(input.getAnnotation(annotation.annotationType()), annotation);
            }
        };
    }

    /** where element is annotated with given {@code annotations}, including member matching */
    public static <T extends AnnotatedElement> Predicate<T> withAnnotations(final Annotation... annotations) {
        return new Predicate<T>() {
            public boolean test(T input) {
                if (input != null) {
                    Annotation[] inputAnnotations = input.getAnnotations();
                    if (inputAnnotations.length == annotations.length) {
                        for (int i = 0; i < inputAnnotations.length; i++) {
                            if (!areAnnotationMembersMatching(inputAnnotations[i], annotations[i])) return false;
                        }
                    }
                }
                return true;
            }
        };
    }

    /** when method/constructor parameter types equals given {@code types} */
    public static Predicate<Member> withParameters(final Class<?>... types) {
        return new Predicate<Member>() {
            public boolean test(Member input) {
                return Arrays.equals(parameterTypes(input), types);
            }
        };
    }

    /** when member parameter types assignable to given {@code types} */
    public static Predicate<Member> withParametersAssignableTo(final Class... types) {
        return new Predicate<Member>() {
            public boolean test(Member input) {
                return isAssignable(types, parameterTypes(input));
            }
        };
    }

    /** when method/constructor parameter types assignable from given {@code types} */
    public static Predicate<Member> withParametersAssignableFrom(final Class... types) {
        return new Predicate<Member>() {
            public boolean test(Member input) {
                return isAssignable(parameterTypes(input), types);
            }
        };
    }

    /** when method/constructor parameters count equal given {@code count} */
    public static Predicate<Member> withParametersCount(final int count) {
        return new Predicate<Member>() {
            public boolean test(Member input) {
                return input != null && parameterTypes(input).length == count;
            }
        };
    }

    /** when method/constructor has any parameter with an annotation matches given {@code annotations} */
    public static Predicate<Member> withAnyParameterAnnotation(final Class<? extends Annotation> annotationClass) {
        return new Predicate<Member>() {
            public boolean test(Member input) {
                return input != null && annotationTypes(parameterAnnotations(input)).stream()
                        .anyMatch(new Predicate<Class<? extends Annotation>>() {
                            public boolean test(Class<? extends Annotation> predicateInput) {
                                return predicateInput.equals(annotationClass);
                            };
                        });
            }
        };
    }

    /** when method/constructor has any parameter with an annotation matches given {@code annotations}, including member matching */
    public static Predicate<Member> withAnyParameterAnnotation(final Annotation annotation) {
        return new Predicate<Member>() {
            public boolean test(Member input) {
                return input != null && parameterAnnotations(input).stream().anyMatch(new Predicate<Annotation>() {
                    public boolean test(Annotation innerInput) {
                        return areAnnotationMembersMatching(annotation, innerInput);
                    }
                });
            }
        };
    }

    /** when field type equal given {@code type} */
    public static <T> Predicate<Field> withType(final Class<T> type) {
        return new Predicate<Field>() {
            public boolean test(Field input) {
                return input != null && input.getType().equals(type);
            }
        };
    }

    /** when field type assignable to given {@code type} */
    public static <T> Predicate<Field> withTypeAssignableTo(final Class<T> type) {
        return new Predicate<Field>() {
            public boolean test(Field input) {
                return input != null && type.isAssignableFrom(input.getType());
            }
        };
    }

    /** when method return type equal given {@code type} */
    public static <T> Predicate<Method> withReturnType(final Class<T> type) {
        return new Predicate<Method>() {
            public boolean test(Method input) {
                return input != null && input.getReturnType().equals(type);
            }
        };
    }

    /** when method return type assignable from given {@code type} */
    public static <T> Predicate<Method> withReturnTypeAssignableTo(final Class<T> type) {
        return new Predicate<Method>() {
            public boolean test(Method input) {
                return input != null && type.isAssignableFrom(input.getReturnType());
            }
        };
    }

    /** when member modifier matches given {@code mod}
     * <p>for example:
     * <pre>
     * withModifier(Modifier.PUBLIC)
     * </pre>
     */
    public static <T extends Member> Predicate<T> withModifier(final int mod) {
        return new Predicate<T>() {
            public boolean test(T input) {
                return input != null && (input.getModifiers() & mod) != 0;
            }
        };
    }

    /** when class modifier matches given {@code mod}
     * <p>for example:
     * <pre>
     * withModifier(Modifier.PUBLIC)
     * </pre>
     */
    public static Predicate<Class<?>> withClassModifier(final int mod) {
        return new Predicate<Class<?>>() {
            public boolean test(Class<?> input) {
                return input != null && (input.getModifiers() & mod) != 0;
            }
        };
    }

    public static Class<?> forName(String typeName, ClassLoader ... classLoaders) {
        if (classLoaders == null) {
            return forName(typeName, Optional.empty());
        } else {
            return forName(typeName, Optional.of(classLoaders));
        }
    }

    /**
     * Tries to resolve a java type name to a Class
     * <p>if optional {@link ClassLoader}s are not specified, then both {@link org.reflections8.util.ClasspathHelper#contextClassLoader()}
     * and {@link org.reflections8.util.ClasspathHelper#staticClassLoader()} are used
     */
    public static Class<?> forName(String typeName, Optional<ClassLoader[]> classLoaders) {
        if (getPrimitiveNames().contains(typeName)) {
            return getPrimitiveTypes().get(getPrimitiveNames().indexOf(typeName));
        } else {
            String type;
            if (typeName.contains(OPEN_SQUARE_BRACKET)) {
                int i = typeName.indexOf(OPEN_SQUARE_BRACKET);
                type = typeName.substring(0, i);
                String array = typeName.substring(i).replace(CLOSE_SQUARE_BRACKET, EMPTY_STRING);
                if (getPrimitiveNames().contains(type)) {
                    type = getPrimitiveDescriptors().get(getPrimitiveNames().indexOf(type));
                } else {
                    type = L + type + SEMICOLON;
                }
                type = array + type;
            } else {
                type = typeName;
            }
            List<ReflectionsException> reflectionsExceptions = new ArrayList<ReflectionsException>();
            for (ClassLoader classLoader : ClasspathHelper.classLoaders(classLoaders).get()) {
                if (type.contains(OPEN_SQUARE_BRACKET)) {
                    try {
                        return Class.forName(type, false, classLoader);
                    } catch (Throwable e) {
                        reflectionsExceptions.add(new ReflectionsException(String.format(TYPE_UNAVAILABLE_SHORT, typeName), e));
                    }
                }
                try {
                    return classLoader.loadClass(type);
                } catch (Throwable e) {
                    reflectionsExceptions.add(new ReflectionsException(String.format(TYPE_UNAVAILABLE_SHORT, typeName), e));
                }
            }
            if (Reflections.log.isPresent()) {
                Logger logger = Reflections.log.get();
                for (ReflectionsException reflectionsException : reflectionsExceptions) {
                    boolean traceEnabled = logger.isTraceEnabled();
                    if (Reflections.REFLECTIONS_VERBOSE_SCANNING || traceEnabled) {
                        String message = String.format(TYPE_UNAVAILABLE, typeName);
                        if (traceEnabled) {
                            logger.trace(message, reflectionsException);
                        } else {
                            logger.warn(message, reflectionsException);
                        }
                    }
                }
            }
            return null;
        }
    }

    public static <T> List<Class<? extends T>> forNames(final Iterable<String> classes, Optional<ClassLoader[]> classLoaders) {
        List<Class<? extends T>> result = new ArrayList<Class<? extends T>>();
        for (String className : classes) {
            Class<?> type = forName(className, classLoaders);
            if (type != null) {
                result.add((Class<? extends T>) type);
            }
        }
        return result;
    }


        /** try to resolve all given string representation of types to a list of java types */
    public static <T> List<Class<? extends T>> forNames(final Iterable<String> classes, ClassLoader... classLoaders) {
        if (classLoaders == null)
            return forNames(classes, Optional.empty());
        else
            return forNames(classes, Optional.of(classLoaders));
    }

    private static Class[] parameterTypes(Member member) {
        return member != null ?
                (member.getClass() == Method.class ? ((Method) member).getParameterTypes() :
                         (member.getClass() == Constructor.class ? ((Constructor) member).getParameterTypes() : null)) : null;
    }

    private static Set<Annotation> parameterAnnotations(Member member) {
        Set<Annotation> result = new HashSet<Annotation>();
        Annotation[][] annotations =
                member instanceof Method ? ((Method) member).getParameterAnnotations() :
                member instanceof Constructor ? ((Constructor) member).getParameterAnnotations() : null;
        if (annotations != null) {
            for (Annotation[] annotation : annotations) Collections.addAll(result, annotation);
        }
        return result;
    }

    private static Set<Class<? extends Annotation>> annotationTypes(Iterable<Annotation> annotations) {
        Set<Class<? extends Annotation>> result = new HashSet<Class<? extends Annotation>>();
        for (Annotation annotation : annotations) result.add(annotation.annotationType());
        return result;
    }

    private static Class<? extends Annotation>[] annotationTypes(Annotation[] annotations) {
        Class<? extends Annotation>[] result = new Class[annotations.length];
        for (int i = 0; i < annotations.length; i++) result[i] = annotations[i].annotationType();
        return result;
    }

    //
    private static List<String> primitiveNames;
    private static List<Class> primitiveTypes;
    private static List<String> primitiveDescriptors;

    private static void initPrimitives() {
        if (primitiveNames == null) {
            primitiveNames = Arrays.asList("boolean", "char", "byte", "short", "int", "long", "float", "double", "void");
            primitiveTypes = Arrays.asList(boolean.class, char.class, byte.class, short.class, int.class, long.class, float.class, double.class, void.class);
            primitiveDescriptors = Arrays.asList("Z", "C", "B", "S", "I", "J", "F", "D", "V");
        }
    }

    private static List<String> getPrimitiveNames() { initPrimitives(); return primitiveNames; }
    private static List<Class> getPrimitiveTypes() { initPrimitives(); return primitiveTypes; }
    private static List<String> getPrimitiveDescriptors() { initPrimitives(); return primitiveDescriptors; }

    static <T> Predicate<? super T> andPredicateArray(Predicate<? super T>[] predicates) {
        Predicate<? super T> p = new AlwaysTruePredicate<>();
        for (Predicate x: predicates) {
            p = p.and(x);
        }
        return p;

    }

    //
    static <T> Set<T> filter(final T[] elements, Predicate<? super T>... predicates) {
        Stream<T> elemStream = Arrays.stream(elements);

        return (isEmpty(predicates) ? elemStream : elemStream.filter(andPredicateArray(predicates)))
                .collect(Collectors.toSet());
    }

    static <T> Set<T> filter(final Iterable<T> elements, Predicate<? super T>... predicates) {
        Stream<T> elemStream = StreamSupport.stream(elements.spliterator(), false);
        return (isEmpty(predicates) ? elemStream : elemStream.filter(andPredicateArray(predicates)))
                .collect(Collectors.toSet());
    }

    private static boolean areAnnotationMembersMatching(Annotation annotation1, Annotation annotation2) {
        if (annotation2 != null && annotation1.annotationType() == annotation2.annotationType()) {
            for (Method method : annotation1.annotationType().getDeclaredMethods()) {
                try {
                    if (!method.invoke(annotation1).equals(method.invoke(annotation2))) return false;
                } catch (Exception e) {
                    throw new ReflectionsException(String.format("could not invoke method %s on annotation %s", method.getName(), annotation1.annotationType()), e);
                }
            }
            return true;
        }
        return false;
    }


    private static boolean isAssignable(Class[] childClasses, Class[] parentClasses) {
        if (childClasses == null) {
            return parentClasses == null || parentClasses.length == 0;
        }
        if (childClasses.length != parentClasses.length) {
            return false;
        }
        for (int i = 0; i < childClasses.length; i++) {
            if (!parentClasses[i].isAssignableFrom(childClasses[i]) ||
                    (parentClasses[i] == Object.class && childClasses[i] != Object.class)) {
                return false;
            }
        }
        return true;
    }
}

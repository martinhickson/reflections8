package org.reflections8;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.reflections8.util.Utils.index;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reflections8.TestModel.AC1;
import org.reflections8.TestModel.AC1n;
import org.reflections8.TestModel.AC2;
import org.reflections8.TestModel.AC3;
import org.reflections8.TestModel.AF1;
import org.reflections8.TestModel.AI1;
import org.reflections8.TestModel.AI2;
import org.reflections8.TestModel.AM1;
import org.reflections8.TestModel.C1;
import org.reflections8.TestModel.C2;
import org.reflections8.TestModel.C3;
import org.reflections8.TestModel.C4;
import org.reflections8.TestModel.C5;
import org.reflections8.TestModel.C6;
import org.reflections8.TestModel.C7;
import org.reflections8.TestModel.C8;
import org.reflections8.TestModel.I1;
import org.reflections8.TestModel.I2;
import org.reflections8.TestModel.I3;
import org.reflections8.TestModel.MAI1;
import org.reflections8.TestModel.Usage;
import org.reflections8.scanners.FieldAnnotationsScanner;
import org.reflections8.scanners.MemberUsageScanner;
import org.reflections8.scanners.MethodAnnotationsScanner;
import org.reflections8.scanners.MethodParameterNamesScanner;
import org.reflections8.scanners.MethodParameterScanner;
import org.reflections8.scanners.ResourcesScanner;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.reflections8.util.ClasspathHelper;
import org.reflections8.util.ConfigurationBuilder;
import org.reflections8.util.FilterBuilder;
import org.reflections8.util.ReflectionsIterables;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ReflectionsTest {
    public static final FilterBuilder TestModelFilter = new FilterBuilder().include("org.reflections8.TestModel\\$.*");
    static Reflections reflections8;

    @BeforeClass
    public static void init() {
        reflections8 = new Reflections(new ConfigurationBuilder()
                .setUrls(asList(ClasspathHelper.forClass(TestModel.class))).filterInputsBy(TestModelFilter)
                .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner(), new FieldAnnotationsScanner(),
                        new MethodAnnotationsScanner(), new MethodParameterScanner(), new MethodParameterNamesScanner(),
                        new MemberUsageScanner()));
    }

    @Test
    public void testSubTypesOf() {
        assertThat(reflections8.getSubTypesOf(I1.class), are(I2.class, C1.class, C2.class, C3.class, C5.class));
        assertThat(reflections8.getSubTypesOf(C1.class), are(C2.class, C3.class, C5.class));

        assertFalse("getAllTypes should not be empty when Reflections is configured with SubTypesScanner(false)",
                reflections8.getAllTypes().isEmpty());
    }

    @Test
    public void testTypesAnnotatedWith() {
        assertThat(reflections8.getTypesAnnotatedWith(MAI1.class, true), are(AI1.class));
        assertThat(reflections8.getTypesAnnotatedWith(MAI1.class, true), annotatedWith(MAI1.class));

        assertThat(reflections8.getTypesAnnotatedWith(AI2.class, true), are(I2.class));
        assertThat(reflections8.getTypesAnnotatedWith(AI2.class, true), annotatedWith(AI2.class));

        assertThat(reflections8.getTypesAnnotatedWith(AC1.class, true), are(C1.class, C2.class, C3.class, C5.class));
        assertThat(reflections8.getTypesAnnotatedWith(AC1.class, true), annotatedWith(AC1.class));

        assertThat(reflections8.getTypesAnnotatedWith(AC1n.class, true), are(C1.class));
        assertThat(reflections8.getTypesAnnotatedWith(AC1n.class, true), annotatedWith(AC1n.class));

        assertThat(reflections8.getTypesAnnotatedWith(MAI1.class),
                are(AI1.class, I1.class, I2.class, C1.class, C2.class, C3.class, C5.class));
        assertThat(reflections8.getTypesAnnotatedWith(MAI1.class), metaAnnotatedWith(MAI1.class));

        assertThat(reflections8.getTypesAnnotatedWith(AI1.class),
                are(I1.class, I2.class, C1.class, C2.class, C3.class, C5.class));
        assertThat(reflections8.getTypesAnnotatedWith(AI1.class), metaAnnotatedWith(AI1.class));

        assertThat(reflections8.getTypesAnnotatedWith(AI2.class),
                are(I2.class, C1.class, C2.class, C3.class, C5.class));
        assertThat(reflections8.getTypesAnnotatedWith(AI2.class), metaAnnotatedWith(AI2.class));

        assertThat(reflections8.getTypesAnnotatedWith(AM1.class), isEmpty);

        // annotation member value matching
        AC2 ac2 = new AC2() {
            @Override
            public String value() {
                return "ugh?!";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return AC2.class;
            }
        };

        assertThat(reflections8.getTypesAnnotatedWith(ac2),
                are(C3.class, C5.class, I3.class, C6.class, AC3.class, C7.class));

        assertThat(reflections8.getTypesAnnotatedWith(ac2, true), are(C3.class, I3.class, AC3.class));
    }

    @Test
    public void testMethodsAnnotatedWith() {
        try {
            assertThat(reflections8.getMethodsAnnotatedWith(AM1.class),
                    are(C4.class.getDeclaredMethod("m1"), C4.class.getDeclaredMethod("m1", int.class, String[].class),
                            C4.class.getDeclaredMethod("m1", int[][].class, String[][].class),
                            C4.class.getDeclaredMethod("m3")));

            AM1 am1 = new AM1() {
                @Override
                public String value() {
                    return "1";
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return AM1.class;
                }
            };
            assertThat(reflections8.getMethodsAnnotatedWith(am1),
                    are(C4.class.getDeclaredMethod("m1"), C4.class.getDeclaredMethod("m1", int.class, String[].class),
                            C4.class.getDeclaredMethod("m1", int[][].class, String[][].class)));
        } catch (NoSuchMethodException e) {
            fail();
        }
    }

    @Test
    public void testConstructorsAnnotatedWith() {
        try {
            assertThat(reflections8.getConstructorsAnnotatedWith(AM1.class),
                    are(C4.class.getDeclaredConstructor(String.class)));

            AM1 am1 = new AM1() {
                @Override
                public String value() {
                    return "1";
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return AM1.class;
                }
            };
            assertThat(reflections8.getConstructorsAnnotatedWith(am1),
                    are(C4.class.getDeclaredConstructor(String.class)));
        } catch (NoSuchMethodException e) {
            fail();
        }
    }

    @Test
    public void testFieldsAnnotatedWith() {
        try {
            assertThat(reflections8.getFieldsAnnotatedWith(AF1.class),
                    are(C4.class.getDeclaredField("f1"), C4.class.getDeclaredField("f2")));

            assertThat(reflections8.getFieldsAnnotatedWith(new AF1() {
                @Override
                public String value() {
                    return "2";
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return AF1.class;
                }
            }), are(C4.class.getDeclaredField("f2")));
        } catch (NoSuchFieldException e) {
            fail();
        }
    }

    @Test
    public void testMethodParameter() {
        try {
            assertThat(reflections8.getMethodsMatchParams(String.class),
                    are(C4.class.getDeclaredMethod("m4", String.class),
                            Usage.C1.class.getDeclaredMethod("method", String.class)));

            assertThat(reflections8.getMethodsMatchParams(),
                    are(C4.class.getDeclaredMethod("m1"), C4.class.getDeclaredMethod("m3"),
                            AC2.class.getMethod("value"), AF1.class.getMethod("value"), AM1.class.getMethod("value"),
                            Usage.C1.class.getDeclaredMethod("method"), Usage.C2.class.getDeclaredMethod("method"),
                            C8.class.getDeclaredMethod("print"), C8.class.getDeclaredMethod("lambda$0")));

            assertThat(reflections8.getMethodsMatchParams(int[][].class, String[][].class),
                    are(C4.class.getDeclaredMethod("m1", int[][].class, String[][].class)));

            assertThat(reflections8.getMethodsReturn(int.class),
                    are(C4.class.getDeclaredMethod("add", int.class, int.class)));

            assertThat(reflections8.getMethodsReturn(String.class),
                    are(C4.class.getDeclaredMethod("m3"), C4.class.getDeclaredMethod("m4", String.class),
                            AC2.class.getMethod("value"), AF1.class.getMethod("value"), AM1.class.getMethod("value")));

            assertThat(reflections8.getMethodsReturn(void.class),
                    are(C4.class.getDeclaredMethod("m1"), C4.class.getDeclaredMethod("m1", int.class, String[].class),
                            C4.class.getDeclaredMethod("m1", int[][].class, String[][].class),
                            Usage.C1.class.getDeclaredMethod("method"),
                            Usage.C1.class.getDeclaredMethod("method", String.class),
                            Usage.C2.class.getDeclaredMethod("method"), C8.class.getDeclaredMethod("print"),
                            C8.class.getDeclaredMethod("lambda$0")));

            assertThat(reflections8.getMethodsWithAnyParamAnnotated(AM1.class),
                    are(C4.class.getDeclaredMethod("m4", String.class)));

            assertThat(reflections8.getMethodsWithAnyParamAnnotated(new AM1() {
                @Override
                public String value() {
                    return "2";
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return AM1.class;
                }
            }), are(C4.class.getDeclaredMethod("m4", String.class)));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testConstructorParameter() throws NoSuchMethodException {
        assertThat(reflections8.getConstructorsMatchParams(String.class),
                are(C4.class.getDeclaredConstructor(String.class)));

        assertThat(reflections8.getConstructorsMatchParams(),
                are(C1.class.getDeclaredConstructor(), C2.class.getDeclaredConstructor(),
                        C3.class.getDeclaredConstructor(), C4.class.getDeclaredConstructor(),
                        C5.class.getDeclaredConstructor(), C6.class.getDeclaredConstructor(),
                        C7.class.getDeclaredConstructor(), Usage.C1.class.getDeclaredConstructor(),
                        Usage.C2.class.getDeclaredConstructor(), C8.class.getDeclaredConstructor()));

        assertThat(reflections8.getConstructorsWithAnyParamAnnotated(AM1.class),
                are(C4.class.getDeclaredConstructor(String.class)));

        assertThat(reflections8.getConstructorsWithAnyParamAnnotated(new AM1() {
            @Override
            public String value() {
                return "1";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return AM1.class;
            }
        }), are(C4.class.getDeclaredConstructor(String.class)));
    }

    @Test
    public void testResourcesScanner() {
        Predicate<String> filter = new FilterBuilder().include(".*\\.xml").exclude(".*testModel-reflections.*\\.xml");
        Reflections localReflections8 = new Reflections(new ConfigurationBuilder().filterInputsBy(filter)
                .setScanners(new ResourcesScanner()).setUrls(asList(ClasspathHelper.forClass(TestModel.class))));

        Set<String> resolved = localReflections8.getResources(Pattern.compile(".*resource1-reflections\\.xml"));
        assertThat(resolved, are("META-INF/reflections/resource1-reflections.xml"));

        Set<String> resources = localReflections8.getStore().get(index(ResourcesScanner.class)).keySet();
        assertThat(resources, are("resource1-reflections.xml", "resource2-reflections.xml"));
    }

    @Test
    public void testMethodParameterNames() throws NoSuchMethodException {
        assertEquals(reflections8.getMethodParamNames(C4.class.getDeclaredMethod("m3")), new ArrayList());

        assertEquals(reflections8.getMethodParamNames(C4.class.getDeclaredMethod("m4", String.class)),
                Arrays.asList("string"));

        assertEquals(reflections8.getMethodParamNames(C4.class.getDeclaredMethod("add", int.class, int.class)),
                Arrays.asList("i1", "i2"));

        assertEquals(reflections8.getConstructorParamNames(C4.class.getDeclaredConstructor(String.class)),
                Arrays.asList("f1"));
    }

    @Test
    public void testMemberUsageScanner() throws NoSuchFieldException, NoSuchMethodException {
        // field usage
        assertThat(reflections8.getFieldUsage(Usage.C1.class.getDeclaredField("c2")),
                are(Usage.C1.class.getDeclaredConstructor(), Usage.C1.class.getDeclaredConstructor(Usage.C2.class),
                        Usage.C1.class.getDeclaredMethod("method"),
                        Usage.C1.class.getDeclaredMethod("method", String.class)));

        // method usage
        assertThat(reflections8.getMethodUsage(Usage.C1.class.getDeclaredMethod("method")),
                are(Usage.C2.class.getDeclaredMethod("method")));

        assertThat(reflections8.getMethodUsage(Usage.C1.class.getDeclaredMethod("method", String.class)),
                are(Usage.C2.class.getDeclaredMethod("method")));

        // constructor usage
        assertThat(reflections8.getConstructorUsage(Usage.C1.class.getDeclaredConstructor()),
                are(Usage.C2.class.getDeclaredConstructor(), Usage.C2.class.getDeclaredMethod("method")));

        assertThat(reflections8.getConstructorUsage(Usage.C1.class.getDeclaredConstructor(Usage.C2.class)),
                are(Usage.C2.class.getDeclaredMethod("method")));
    }

    @Test
    public void testScannerNotConfigured() {
        try {
            new Reflections(TestModel.class, TestModelFilter).getMethodsAnnotatedWith(AC1.class);
            fail();
        } catch (ReflectionsException e) {
            assertEquals(e.getMessage(),
                    "Scanner " + MethodAnnotationsScanner.class.getSimpleName() + " was not configured");
        }
    }

    //
    public static String getUserDir() {
        File file = new File(System.getProperty("user.dir"));
        // a hack to fix user.dir issue(?) in surfire
        if (Arrays.asList(file.list()).contains("reflections")) {
            file = new File(file, "reflections");
        }
        return file.getAbsolutePath();
    }

    private final BaseMatcher<Set<Class<?>>> isEmpty = new BaseMatcher<Set<Class<?>>>() {
        @Override
        public boolean matches(Object o) {
            return ((Collection<?>) o).isEmpty();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("empty collection");
        }
    };

    private abstract static class Match<T> extends BaseMatcher<T> {
        @Override
        public void describeTo(Description description) {
        }
    }

    public static <T> Matcher<Set<? super T>> are(final T... ts) {
        final Collection<?> c1 = Arrays.asList(ts);
        return new Match<Set<? super T>>() {
            @Override
            public boolean matches(Object o) {
                Collection<?> c2 = (Collection<?>) o;
                return c1.containsAll(c2) && c2.containsAll(c1);
            }
        };
    }

    private Matcher<Set<Class<?>>> annotatedWith(final Class<? extends Annotation> annotation) {
        return new Match<Set<Class<?>>>() {
            @Override
            public boolean matches(Object o) {
                for (Class<?> c : (Iterable<Class<?>>) o) {
                    if (!ReflectionsIterables.contains(annotationTypes(Arrays.asList(c.getAnnotations())), annotation))
                        return false;
                }
                return true;
            }
        };
    }

    private Matcher<Set<Class<?>>> metaAnnotatedWith(final Class<? extends Annotation> annotation) {
        return new Match<Set<Class<?>>>() {
            @Override
            public boolean matches(Object o) {
                for (Class<?> c : (Iterable<Class<?>>) o) {
                    Set<Class> result = new HashSet();
                    List<Class> stack = new ArrayList<>();
                    stack.addAll(ReflectionUtils.getAllSuperTypes(c));
                    while (!stack.isEmpty()) {
                        Class next = stack.remove(0);
                        if (result.add(next)) {
                            for (Class<? extends Annotation> ac : annotationTypes(
                                    Arrays.asList(next.getDeclaredAnnotations()))) {
                                if (!result.contains(ac) && !stack.contains(ac))
                                    stack.add(ac);
                            }
                        }
                    }
                    if (!result.contains(annotation))
                        return false;
                }
                return true;
            }
        };
    }

    private Iterable<Class<? extends Annotation>> annotationTypes(Iterable<Annotation> annotations) {
        return ReflectionsIterables.transform(annotations, new Function<Annotation, Class<? extends Annotation>>() {
            @Override
            public Class<? extends Annotation> apply(Annotation input) {
                return input != null ? input.annotationType() : null;
            }
        });
    }
}

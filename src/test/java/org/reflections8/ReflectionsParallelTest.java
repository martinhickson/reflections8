package org.reflections8;

import static java.util.Arrays.asList;

import org.junit.BeforeClass;
import org.reflections8.scanners.FieldAnnotationsScanner;
import org.reflections8.scanners.MemberUsageScanner;
import org.reflections8.scanners.MethodAnnotationsScanner;
import org.reflections8.scanners.MethodParameterNamesScanner;
import org.reflections8.scanners.MethodParameterScanner;
import org.reflections8.scanners.SubTypesScanner;
import org.reflections8.scanners.TypeAnnotationsScanner;
import org.reflections8.util.ClasspathHelper;
import org.reflections8.util.ConfigurationBuilder;

/** */
public class ReflectionsParallelTest extends ReflectionsTest {

    @BeforeClass
    public static void init() {
        reflections8 = new Reflections(new ConfigurationBuilder()
                .setUrls(asList(ClasspathHelper.forClass(TestModel.class)))
                .filterInputsBy(TestModelFilter)
                .setScanners(
                        new SubTypesScanner(false),
                        new TypeAnnotationsScanner(),
                        new FieldAnnotationsScanner(),
                        new MethodAnnotationsScanner(),
                        new MethodParameterScanner(),
                        new MethodParameterNamesScanner(),
                        new MemberUsageScanner())
                .useParallelExecutor());
    }
}

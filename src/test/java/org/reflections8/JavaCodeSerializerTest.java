package org.reflections8;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.reflections8.TestModel.AC2;
import static org.reflections8.TestModel.C1;
import static org.reflections8.TestModel.C2;
import static org.reflections8.TestModel.C4;

import java.util.function.Predicate;

import org.junit.BeforeClass;
import org.junit.Test;
import org.reflections8.scanners.TypeElementsScanner;
import org.reflections8.serializers.JavaCodeSerializer;
import org.reflections8.util.ClasspathHelper;
import org.reflections8.util.ConfigurationBuilder;
import org.reflections8.util.FilterBuilder;

/** */
public class JavaCodeSerializerTest {

    @BeforeClass
    public static void generateAndSave() {
        Predicate<String> filter = new FilterBuilder().include("org.reflections8.TestModel\\$.*");

        Reflections reflections8 = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(filter)
                .setScanners(new TypeElementsScanner().includeFields().publicOnly(false))
                .setUrls(asList(ClasspathHelper.forClass(TestModel.class))));

        //save
        String filename = ReflectionsTest.getUserDir() + "/src/test/java/org.reflections8.MyTestModelStore";
        reflections8.save(filename, new JavaCodeSerializer());
    }

    @Test
    public void resolve() throws NoSuchMethodException, NoSuchFieldException {
        //class
        assertEquals(C1.class,
                JavaCodeSerializer.resolveClass(MyTestModelStore.org.reflections8.TestModel$C1.class));

        //method
        assertEquals(C4.class.getDeclaredMethod("m1"),
                JavaCodeSerializer.resolveMethod(MyTestModelStore.org.reflections8.TestModel$C4.methods.m1.class));

        //overloaded method with parameters
        assertEquals(C4.class.getDeclaredMethod("m1", int.class, String[].class),
                JavaCodeSerializer.resolveMethod(MyTestModelStore.org.reflections8.TestModel$C4.methods.m1_int__java_lang_String$$.class));

        //overloaded method with parameters and multi dimensional array
        assertEquals(C4.class.getDeclaredMethod("m1", int[][].class, String[][].class),
                JavaCodeSerializer.resolveMethod(MyTestModelStore.org.reflections8.TestModel$C4.methods.m1_int$$$$__java_lang_String$$$$.class));

        //field
        assertEquals(C4.class.getDeclaredField("f1"),
                JavaCodeSerializer.resolveField(MyTestModelStore.org.reflections8.TestModel$C4.fields.f1.class));

        //annotation
        assertEquals(C2.class.getAnnotation(AC2.class),
                JavaCodeSerializer.resolveAnnotation(MyTestModelStore.org.reflections8.TestModel$C2.annotations.org_reflections8_TestModel$AC2.class));
    }
}

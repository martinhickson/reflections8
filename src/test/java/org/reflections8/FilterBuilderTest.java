package org.reflections8;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.reflections8.util.FilterBuilder;

/**
 * Test filtering
 */
public class FilterBuilderTest {

  @Test
  public void test_include() {
      FilterBuilder filter = new FilterBuilder().include("org\\.reflections8.*");
      assertTrue(filter.test("org.reflections8.Reflections"));
      assertTrue(filter.test("org.reflections8.foo.Reflections"));
      assertFalse(filter.test("org.foobar.Reflections"));
  }

    @Test
    public void test_includePackage() {
        FilterBuilder filter = new FilterBuilder().includePackage("org.reflections8");
        assertTrue(filter.test("org.reflections8.Reflections"));
        assertTrue(filter.test("org.reflections8.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
    }

    @Test
    public void test_includePackageMultiple() {
        FilterBuilder filter = new FilterBuilder().includePackage("org.reflections8", "org.foo");
        assertTrue(filter.test("org.reflections8.Reflections"));
        assertTrue(filter.test("org.reflections8.foo.Reflections"));
        assertTrue(filter.test("org.foo.Reflections"));
        assertTrue(filter.test("org.foo.bar.Reflections"));
        assertFalse(filter.test("org.bar.Reflections"));
    }

    @Test
    public void test_includePackagebyClass() {
        FilterBuilder filter = new FilterBuilder().includePackage(Reflections.class);
        assertTrue(filter.test("org.reflections8.Reflections"));
        assertTrue(filter.test("org.reflections8.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_exclude() {
        FilterBuilder filter = new FilterBuilder().exclude("org\\.reflections8.*");
        assertFalse(filter.test("org.reflections8.Reflections"));
        assertFalse(filter.test("org.reflections8.foo.Reflections"));
        assertTrue(filter.test("org.foobar.Reflections"));
    }

    @Test
    public void test_excludePackage() {
        FilterBuilder filter = new FilterBuilder().excludePackage("org.reflections8");
        assertFalse(filter.test("org.reflections8.Reflections"));
        assertFalse(filter.test("org.reflections8.foo.Reflections"));
        assertTrue(filter.test("org.foobar.Reflections"));
    }

    @Test
    public void test_excludePackageByClass() {
        FilterBuilder filter = new FilterBuilder().excludePackage(Reflections.class);
        assertFalse(filter.test("org.reflections8.Reflections"));
        assertFalse(filter.test("org.reflections8.foo.Reflections"));
        assertTrue(filter.test("org.foobar.Reflections"));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_parse_include() {
        FilterBuilder filter = FilterBuilder.parse("+org.reflections8.*");
        assertTrue(filter.test("org.reflections8.Reflections"));
        assertTrue(filter.test("org.reflections8.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
        assertTrue(filter.test("org.reflections8plus.Reflections"));
    }

    @Test
    public void test_parse_include_notRegex() {
        FilterBuilder filter = FilterBuilder.parse("+org.reflections8");
        assertFalse(filter.test("org.reflections8.Reflections"));
        assertFalse(filter.test("org.reflections8.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
        assertFalse(filter.test("org.reflectionsplus.Reflections"));
    }

    @Test
    public void test_parse_exclude() {
        FilterBuilder filter = FilterBuilder.parse("-org.reflections8.*");
        assertFalse(filter.test("org.reflections8.Reflections"));
        assertFalse(filter.test("org.reflections8.foo.Reflections"));
        assertTrue(filter.test("org.foobar.Reflections"));
        assertFalse(filter.test("org.reflections8plus.Reflections"));
    }

    @Test
    public void test_parse_exclude_notRegex() {
        FilterBuilder filter = FilterBuilder.parse("-org.reflections8");
        assertTrue(filter.test("org.reflections8.Reflections"));
        assertTrue(filter.test("org.reflections8.foo.Reflections"));
        assertTrue(filter.test("org.foobar.Reflections"));
        assertTrue(filter.test("org.reflections8plus.Reflections"));
    }

    @Test
    public void test_parse_include_exclude() {
        FilterBuilder filter = FilterBuilder.parse("+org.reflections8.*, -org.reflections8.foo.*");
        assertTrue(filter.test("org.reflections8.Reflections"));
        assertFalse(filter.test("org.reflections8.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
    }

    //-----------------------------------------------------------------------
    @Test
    public void test_parsePackages_include() {
        FilterBuilder filter = FilterBuilder.parsePackages("+org.reflections8");
        assertTrue(filter.test("org.reflections8.Reflections"));
        assertTrue(filter.test("org.reflections8.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
        assertFalse(filter.test("org.reflectionsplus.Reflections"));
    }

    @Test
    public void test_parsePackages_include_trailingDot() {
        FilterBuilder filter = FilterBuilder.parsePackages("+org.reflections8.");
        assertTrue(filter.test("org.reflections8.Reflections"));
        assertTrue(filter.test("org.reflections8.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
        assertFalse(filter.test("org.reflectionsplus.Reflections"));
    }

    @Test
    public void test_parsePackages_exclude() {
        FilterBuilder filter = FilterBuilder.parsePackages("-org.reflections8");
        assertFalse(filter.test("org.reflections8.Reflections"));
        assertFalse(filter.test("org.reflections8.foo.Reflections"));
        assertTrue(filter.test("org.foobar.Reflections"));
        assertTrue(filter.test("org.reflectionsplus.Reflections"));
    }

    @Test
    public void test_parsePackages_exclude_trailingDot() {
        FilterBuilder filter = FilterBuilder.parsePackages("-org.reflections8.");
        assertFalse(filter.test("org.reflections8.Reflections"));
        assertFalse(filter.test("org.reflections8.foo.Reflections"));
        assertTrue(filter.test("org.foobar.Reflections"));
        assertTrue(filter.test("org.reflectionsplus.Reflections"));
    }

    @Test
    public void test_parsePackages_include_exclude() {
        FilterBuilder filter = FilterBuilder.parsePackages("+org.reflections8, -org.reflections8.foo");
        assertTrue(filter.test("org.reflections8.Reflections"));
        assertFalse(filter.test("org.reflections8.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
    }

}

package org.reflections8.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

public class ConfigurationBuilderTest {
    @Test
    public void shouldAddNewClassloaderToEmptyConfiguration() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        Assert.assertEquals("Should be empty", configurationBuilder.getClassLoaders(), Optional.empty());
        Assert.assertFalse("Should not present", configurationBuilder.getClassLoaders().isPresent());

        configurationBuilder.addClassLoader(this.getClass().getClassLoader());

        Assert.assertTrue("Should be present", configurationBuilder.getClassLoaders().isPresent());
        Assert.assertEquals("Should be one", 1, configurationBuilder.getClassLoaders().get().length);
        Assert.assertArrayEquals("Should be equals", Optional.of(new ClassLoader[]{this.getClass().getClassLoader()}).get(), configurationBuilder.getClassLoaders().get());
    }

    @Test
    public void shouldAddNewTwoClassloaderToEmptyConfiguration() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        Assert.assertEquals("Should be empty", configurationBuilder.getClassLoaders(), Optional.empty());
        Assert.assertFalse("Should not present", configurationBuilder.getClassLoaders().isPresent());

        configurationBuilder.addClassLoader(this.getClass().getClassLoader());
        configurationBuilder.addClassLoader(this.getClass().getClassLoader().getParent());

        Assert.assertTrue("Should be present", configurationBuilder.getClassLoaders().isPresent());
        Assert.assertEquals("Should be two", 2, configurationBuilder.getClassLoaders().get().length);
        Assert.assertArrayEquals("Should be equals", Optional.of(new ClassLoader[]{this.getClass().getClassLoader(), this.getClass().getClassLoader().getParent()}).get(), configurationBuilder.getClassLoaders().get());
    }

    @Test
    public void shouldAddNewTwoClassloaderAsListToEmptyConfiguration() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

        Assert.assertEquals("Should be empty", configurationBuilder.getClassLoaders(), Optional.empty());
        Assert.assertFalse("Should not present", configurationBuilder.getClassLoaders().isPresent());

        configurationBuilder.addClassLoaders(Arrays.asList(this.getClass().getClassLoader(), this.getClass().getClassLoader().getParent()));

        Assert.assertTrue("Should be present", configurationBuilder.getClassLoaders().isPresent());
        Assert.assertEquals("Should be two", 2, configurationBuilder.getClassLoaders().get().length);
        Assert.assertArrayEquals("Should be equals", Optional.of(new ClassLoader[]{this.getClass().getClassLoader(), this.getClass().getClassLoader().getParent()}).get(), configurationBuilder.getClassLoaders().get());
    }
}

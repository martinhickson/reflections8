package org.reflections.vfs;

import org.reflections.ReflectionsException;
import org.reflections.util.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

/**
 *
 */
public class JarInputDir implements Vfs.Dir {
    private final URL url;
    JarInputStream jarInputStream;
    long cursor = 0;
    long nextCursor = 0;

    public JarInputDir(URL url) {
        this.url = url;
    }

    public String getPath() {
        return url.getPath();
    }

    public Iterable<Vfs.File> getFiles() {
        return new Iterable<Vfs.File>() {
            public Iterator<Vfs.File> iterator() {
                return new Iterator<Vfs.File>() {
                    ZipEntry entry = null;

                    {
                        try { jarInputStream = new JarInputStream(url.openConnection().getInputStream()); }
                        catch (Exception e) { throw new ReflectionsException("Could not open url connection", e); }
                    }

                    public Vfs.File next() {
                        while (true) {

                            if (entry == null) {
                                if (!hasNext()) {
                                    throw new NoSuchElementException("nothing left");
                                }
                            }
                            ZipEntry tmp = entry;
                            entry = null;
                            long size = tmp.getSize();
                            if (size < 0) size = 0xffffffffl + size; //JDK-6916399
                            nextCursor += size;
                            if (!tmp.isDirectory()) {
                                return new JarInputFile(tmp, JarInputDir.this, cursor, nextCursor);
                            }
                        }
                    }

                    @Override
                    public boolean hasNext() {
                        if (entry == null) {
                            try {
                                entry = jarInputStream.getNextJarEntry();
                            } catch (IOException e) {
                                throw new ReflectionsException("could not get next zip entry", e);
                            }
                        }
                        return entry != null;
                    }
                };
            }
        };
    }

    public void close() {
        Utils.close(jarInputStream);
    }
}

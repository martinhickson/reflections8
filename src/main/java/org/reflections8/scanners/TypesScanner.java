package org.reflections8.scanners;

import java.util.Optional;

import org.reflections8.vfs.Vfs;

/** scans classes and stores fqn as key and full path as value.
 * <p>Deprecated. use {@link org.reflections8.scanners.TypeElementsScanner} */
@Deprecated
public class TypesScanner extends AbstractScanner {

    @Override
    public Object scan(Vfs.File file, Optional<Object> classObject) {
        Object tmpClassObject = super.scan(file, classObject);
        String className = getMetadataAdapter().getClassName(tmpClassObject);
        getStore().putSingle(className, className);
        return tmpClassObject;
    }

    @Override
    public void scan(Object cls) {
        throw new UnsupportedOperationException("should not get here");
    }
}
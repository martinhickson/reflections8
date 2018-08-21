package org.reflections8.serializers;

import java.io.File;
import java.io.InputStream;

import org.reflections8.Reflections;

/** Serilizer of a {@link org.reflections8.Reflections} instance */
public interface Serializer {
    /** reads the input stream into a new Reflections instance, populating it's store */
    Reflections read(InputStream inputStream);

    /** saves a Reflections instance into the given filename */
    File save(Reflections reflections, String filename);

    /** returns a string serialization of the given Reflections instance */
    String toString(Reflections reflections);
}

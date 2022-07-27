package org.reflections8.vfs;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import org.reflections8.ReflectionsException;
import org.reflections8.vfs.Vfs.Dir;

public class JrtDir implements Dir {

  private final Path path;

  public JrtDir(URL url) throws URISyntaxException {
    Path p = Path.of(url.toURI());
    if (!Files.exists(p)) {
      path = p.resolve("/modules").resolve(p.getRoot().relativize(p));
    } else {
      path = p;
    }
  }

  @Override
  public String getPath() {
    return path.toString();
  }

  @Override
  public Iterable<Vfs.File> getFiles() {
    if (path == null) {
      return Collections.emptyList();
    }
    return () -> {
      try {
        return Files.walk(path)
                .filter(Files::isRegularFile)
                .map(currentPath -> (Vfs.File) new JrtFile(currentPath))
                .iterator();
      } catch (IOException e) {
        throw new ReflectionsException("could not get files for " + path, e);
      }
    };

  }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.reflections8.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import org.reflections8.vfs.Vfs.File;

/**
 *
 * @author lnavrat
 */
public class JrtFile implements File {

  private final Path path;

  public JrtFile(Path path) {
    this.path = path;
  }

  @Override
  public String getName() {
    return path.getFileName().toString();
  }

  @Override
  public String getRelativePath() {
    return path.toString();
  }

  @Override
  public InputStream openInputStream() throws IOException {
    return Files.newInputStream(path);
  }
}

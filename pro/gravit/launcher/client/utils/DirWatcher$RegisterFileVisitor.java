package pro.gravit.launcher.client.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

final class RegisterFileVisitor extends SimpleFileVisitor<Path> {
  public FileVisitResult preVisitDirectory(Path paramPath, BasicFileAttributes paramBasicFileAttributes) throws IOException {
    FileVisitResult fileVisitResult = super.preVisitDirectory(paramPath, paramBasicFileAttributes);
    paramPath.register(DirWatcher.this.service, DirWatcher.KINDS);
    return fileVisitResult;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\clien\\utils\DirWatcher$RegisterFileVisitor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
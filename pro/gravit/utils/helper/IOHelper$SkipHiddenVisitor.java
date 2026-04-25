package pro.gravit.utils.helper;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

final class SkipHiddenVisitor implements FileVisitor<Path> {
  private final FileVisitor<Path> visitor;
  
  private SkipHiddenVisitor(FileVisitor<Path> paramFileVisitor) {
    this.visitor = paramFileVisitor;
  }
  
  public FileVisitResult postVisitDirectory(Path paramPath, IOException paramIOException) throws IOException {
    return Files.isHidden(paramPath) ? FileVisitResult.CONTINUE : this.visitor.postVisitDirectory(paramPath, paramIOException);
  }
  
  public FileVisitResult preVisitDirectory(Path paramPath, BasicFileAttributes paramBasicFileAttributes) throws IOException {
    return Files.isHidden(paramPath) ? FileVisitResult.SKIP_SUBTREE : this.visitor.preVisitDirectory(paramPath, paramBasicFileAttributes);
  }
  
  public FileVisitResult visitFile(Path paramPath, BasicFileAttributes paramBasicFileAttributes) throws IOException {
    return Files.isHidden(paramPath) ? FileVisitResult.CONTINUE : this.visitor.visitFile(paramPath, paramBasicFileAttributes);
  }
  
  public FileVisitResult visitFileFailed(Path paramPath, IOException paramIOException) throws IOException {
    return this.visitor.visitFileFailed(paramPath, paramIOException);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\IOHelper$SkipHiddenVisitor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
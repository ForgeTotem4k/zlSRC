package pro.gravit.utils.helper;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;

class MoveFileVisitor implements FileVisitor<Path> {
  private final Path from;
  
  private final Path to;
  
  private MoveFileVisitor(Path paramPath1, Path paramPath2) {
    this.from = paramPath1;
    this.to = paramPath2;
  }
  
  public FileVisitResult preVisitDirectory(Path paramPath, BasicFileAttributes paramBasicFileAttributes) throws IOException {
    Path path = this.to.resolve(this.from.relativize(paramPath));
    if (!IOHelper.isDir(path))
      Files.createDirectories(path, (FileAttribute<?>[])new FileAttribute[0]); 
    return FileVisitResult.CONTINUE;
  }
  
  public FileVisitResult visitFile(Path paramPath, BasicFileAttributes paramBasicFileAttributes) throws IOException {
    Files.move(paramPath, this.to.resolve(this.from.relativize(paramPath)), IOHelper.COPY_OPTIONS);
    return FileVisitResult.CONTINUE;
  }
  
  public FileVisitResult visitFileFailed(Path paramPath, IOException paramIOException) throws IOException {
    throw paramIOException;
  }
  
  public FileVisitResult postVisitDirectory(Path paramPath, IOException paramIOException) throws IOException {
    if (paramIOException != null)
      throw paramIOException; 
    if (!this.from.equals(paramPath))
      Files.delete(paramPath); 
    return FileVisitResult.CONTINUE;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\IOHelper$MoveFileVisitor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
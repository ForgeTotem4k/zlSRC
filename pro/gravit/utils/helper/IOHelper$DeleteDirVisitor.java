package pro.gravit.utils.helper;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

final class DeleteDirVisitor extends SimpleFileVisitor<Path> {
  private final Path dir;
  
  private final boolean self;
  
  private DeleteDirVisitor(Path paramPath, boolean paramBoolean) {
    this.dir = paramPath;
    this.self = paramBoolean;
  }
  
  public FileVisitResult postVisitDirectory(Path paramPath, IOException paramIOException) throws IOException {
    FileVisitResult fileVisitResult = super.postVisitDirectory(paramPath, paramIOException);
    if (this.self || !this.dir.equals(paramPath))
      Files.delete(paramPath); 
    return fileVisitResult;
  }
  
  public FileVisitResult visitFile(Path paramPath, BasicFileAttributes paramBasicFileAttributes) throws IOException {
    Files.delete(paramPath);
    return super.visitFile(paramPath, paramBasicFileAttributes);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\IOHelper$DeleteDirVisitor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
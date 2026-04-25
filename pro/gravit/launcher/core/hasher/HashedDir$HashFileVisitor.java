package pro.gravit.launcher.core.hasher;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Deque;
import java.util.LinkedList;
import pro.gravit.utils.helper.IOHelper;

final class HashFileVisitor extends SimpleFileVisitor<Path> {
  private final Path dir;
  
  private final FileNameMatcher matcher;
  
  private final boolean allowSymlinks;
  
  private final boolean digest;
  
  private final Deque<String> path = new LinkedList<>();
  
  private final Deque<HashedDir> stack = new LinkedList<>();
  
  private HashedDir current = HashedDir.this;
  
  private HashFileVisitor(Path paramPath, FileNameMatcher paramFileNameMatcher, boolean paramBoolean1, boolean paramBoolean2) {
    this.dir = paramPath;
    this.matcher = paramFileNameMatcher;
    this.allowSymlinks = paramBoolean1;
    this.digest = paramBoolean2;
  }
  
  public FileVisitResult postVisitDirectory(Path paramPath, IOException paramIOException) throws IOException {
    FileVisitResult fileVisitResult = super.postVisitDirectory(paramPath, paramIOException);
    if (this.dir.equals(paramPath))
      return fileVisitResult; 
    HashedDir hashedDir = this.stack.removeLast();
    hashedDir.map.put(this.path.removeLast(), this.current);
    this.current = hashedDir;
    return fileVisitResult;
  }
  
  public FileVisitResult preVisitDirectory(Path paramPath, BasicFileAttributes paramBasicFileAttributes) throws IOException {
    FileVisitResult fileVisitResult = super.preVisitDirectory(paramPath, paramBasicFileAttributes);
    if (this.dir.equals(paramPath))
      return fileVisitResult; 
    if (!this.allowSymlinks && paramBasicFileAttributes.isSymbolicLink())
      throw new SecurityException("Symlinks are not allowed"); 
    this.stack.add(this.current);
    this.current = new HashedDir();
    this.path.add(IOHelper.getFileName(paramPath));
    return fileVisitResult;
  }
  
  public FileVisitResult visitFile(Path paramPath, BasicFileAttributes paramBasicFileAttributes) throws IOException {
    if (!this.allowSymlinks && paramBasicFileAttributes.isSymbolicLink())
      throw new SecurityException("Symlinks are not allowed"); 
    this.path.add(IOHelper.getFileName(paramPath));
    boolean bool = (this.digest && (this.matcher == null || this.matcher.shouldUpdate(this.path))) ? true : false;
    this.current.map.put(this.path.removeLast(), new HashedFile(paramPath, paramBasicFileAttributes.size(), bool));
    return super.visitFile(paramPath, paramBasicFileAttributes);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\hasher\HashedDir$HashFileVisitor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
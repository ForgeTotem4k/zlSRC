package pro.gravit.launcher.client;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import pro.gravit.utils.helper.IOHelper;

final class ClassPathFileVisitor extends SimpleFileVisitor<Path> {
  private final List<Path> result;
  
  private ClassPathFileVisitor(List<Path> paramList) {
    this.result = paramList;
  }
  
  public FileVisitResult visitFile(Path paramPath, BasicFileAttributes paramBasicFileAttributes) throws IOException {
    if (IOHelper.hasExtension(paramPath, "jar") || IOHelper.hasExtension(paramPath, "zip"))
      this.result.add(paramPath); 
    return super.visitFile(paramPath, paramBasicFileAttributes);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\ClientLauncherEntryPoint$ClassPathFileVisitor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
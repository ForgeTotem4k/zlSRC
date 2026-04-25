package pro.gravit.launcher.base.modules.impl;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public final class ModulesVisitor extends SimpleFileVisitor<Path> {
  public FileVisitResult visitFile(Path paramPath, BasicFileAttributes paramBasicFileAttributes) throws IOException {
    if (paramPath.toFile().getName().endsWith(".jar"))
      SimpleModuleManager.this.loadModule(paramPath); 
    return super.visitFile(paramPath, paramBasicFileAttributes);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\impl\SimpleModuleManager$ModulesVisitor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
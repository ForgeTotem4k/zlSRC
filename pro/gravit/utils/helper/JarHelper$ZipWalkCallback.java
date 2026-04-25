package pro.gravit.utils.helper;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@FunctionalInterface
public interface ZipWalkCallback {
  void process(ZipInputStream paramZipInputStream, ZipEntry paramZipEntry);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\JarHelper$ZipWalkCallback.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.base;

import java.nio.file.Path;

public interface DownloadCallback {
  void apply(long paramLong);
  
  void onComplete(Path paramPath);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\Downloader$DownloadCallback.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.gui.scenes.update;

import java.nio.file.Path;
import pro.gravit.launcher.base.Downloader;

class null implements Downloader.DownloadCallback {
  public void apply(long paramLong) {
    long l = VisualDownloader.this.totalDownloaded.getAndAdd(paramLong);
    VisualDownloader.this.updateProgress(l, l + paramLong);
  }
  
  public void onComplete(Path paramPath) {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scene\\update\VisualDownloader$1.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
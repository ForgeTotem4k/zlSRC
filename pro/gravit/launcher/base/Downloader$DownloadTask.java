package pro.gravit.launcher.base;

import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class DownloadTask {
  public final Downloader.ProgressTrackingBodyHandler<Path> bodyHandler;
  
  public CompletableFuture<HttpResponse<Path>> completableFuture;
  
  public DownloadTask(Downloader.ProgressTrackingBodyHandler<Path> paramProgressTrackingBodyHandler, CompletableFuture<HttpResponse<Path>> paramCompletableFuture) {
    this.bodyHandler = paramProgressTrackingBodyHandler;
    this.completableFuture = paramCompletableFuture;
  }
  
  public boolean isCompleted() {
    return this.completableFuture.isDone() | this.completableFuture.isCompletedExceptionally();
  }
  
  public void cancel() {
    this.bodyHandler.cancel();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\Downloader$DownloadTask.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
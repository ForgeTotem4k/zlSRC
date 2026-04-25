package io.sentry;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

@Internal
public final class SentryExecutorService implements ISentryExecutorService {
  @NotNull
  private final ScheduledExecutorService executorService;
  
  @TestOnly
  SentryExecutorService(@NotNull ScheduledExecutorService paramScheduledExecutorService) {
    this.executorService = paramScheduledExecutorService;
  }
  
  public SentryExecutorService() {
    this(Executors.newSingleThreadScheduledExecutor(new SentryExecutorServiceThreadFactory(null)));
  }
  
  @NotNull
  public Future<?> submit(@NotNull Runnable paramRunnable) {
    return this.executorService.submit(paramRunnable);
  }
  
  @NotNull
  public <T> Future<T> submit(@NotNull Callable<T> paramCallable) {
    return this.executorService.submit(paramCallable);
  }
  
  @NotNull
  public Future<?> schedule(@NotNull Runnable paramRunnable, long paramLong) {
    return this.executorService.schedule(paramRunnable, paramLong, TimeUnit.MILLISECONDS);
  }
  
  public void close(long paramLong) {
    synchronized (this.executorService) {
      if (!this.executorService.isShutdown()) {
        this.executorService.shutdown();
        try {
          if (!this.executorService.awaitTermination(paramLong, TimeUnit.MILLISECONDS))
            this.executorService.shutdownNow(); 
        } catch (InterruptedException interruptedException) {
          this.executorService.shutdownNow();
          Thread.currentThread().interrupt();
        } 
      } 
    } 
  }
  
  public boolean isClosed() {
    synchronized (this.executorService) {
      return this.executorService.isShutdown();
    } 
  }
  
  private static final class SentryExecutorServiceThreadFactory implements ThreadFactory {
    private int cnt;
    
    private SentryExecutorServiceThreadFactory() {}
    
    @NotNull
    public Thread newThread(@NotNull Runnable param1Runnable) {
      Thread thread = new Thread(param1Runnable, "SentryExecutorServiceThreadFactory-" + this.cnt++);
      thread.setDaemon(true);
      return thread;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryExecutorService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
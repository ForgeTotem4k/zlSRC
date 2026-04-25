package io.sentry;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.jetbrains.annotations.NotNull;

final class NoOpSentryExecutorService implements ISentryExecutorService {
  private static final NoOpSentryExecutorService instance = new NoOpSentryExecutorService();
  
  @NotNull
  public static ISentryExecutorService getInstance() {
    return instance;
  }
  
  @NotNull
  public Future<?> submit(@NotNull Runnable paramRunnable) {
    return new FutureTask(() -> null);
  }
  
  @NotNull
  public <T> Future<T> submit(@NotNull Callable<T> paramCallable) {
    return new FutureTask<>(() -> null);
  }
  
  @NotNull
  public Future<?> schedule(@NotNull Runnable paramRunnable, long paramLong) {
    return new FutureTask(() -> null);
  }
  
  public void close(long paramLong) {}
  
  public boolean isClosed() {
    return false;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpSentryExecutorService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
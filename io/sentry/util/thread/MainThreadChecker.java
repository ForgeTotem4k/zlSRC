package io.sentry.util.thread;

import io.sentry.protocol.SentryThread;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class MainThreadChecker implements IMainThreadChecker {
  private static final long mainThreadId = Thread.currentThread().getId();
  
  private static final MainThreadChecker instance = new MainThreadChecker();
  
  public static MainThreadChecker getInstance() {
    return instance;
  }
  
  public boolean isMainThread(long paramLong) {
    return (mainThreadId == paramLong);
  }
  
  public boolean isMainThread(@NotNull Thread paramThread) {
    return isMainThread(paramThread.getId());
  }
  
  public boolean isMainThread() {
    return isMainThread(Thread.currentThread());
  }
  
  public boolean isMainThread(@NotNull SentryThread paramSentryThread) {
    Long long_ = paramSentryThread.getId();
    return (long_ != null && isMainThread(long_.longValue()));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\thread\MainThreadChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
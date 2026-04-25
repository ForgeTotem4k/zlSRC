package io.sentry.util.thread;

import io.sentry.protocol.SentryThread;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class NoOpMainThreadChecker implements IMainThreadChecker {
  private static final NoOpMainThreadChecker instance = new NoOpMainThreadChecker();
  
  public static NoOpMainThreadChecker getInstance() {
    return instance;
  }
  
  public boolean isMainThread(long paramLong) {
    return false;
  }
  
  public boolean isMainThread(@NotNull Thread paramThread) {
    return false;
  }
  
  public boolean isMainThread() {
    return false;
  }
  
  public boolean isMainThread(@NotNull SentryThread paramSentryThread) {
    return false;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\thread\NoOpMainThreadChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
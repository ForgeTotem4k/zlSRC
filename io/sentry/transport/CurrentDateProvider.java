package io.sentry.transport;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class CurrentDateProvider implements ICurrentDateProvider {
  private static final ICurrentDateProvider instance = new CurrentDateProvider();
  
  public static ICurrentDateProvider getInstance() {
    return instance;
  }
  
  public final long getCurrentTimeMillis() {
    return System.currentTimeMillis();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\CurrentDateProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
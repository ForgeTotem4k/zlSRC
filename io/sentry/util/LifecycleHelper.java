package io.sentry.util;

import io.sentry.ISentryLifecycleToken;
import org.jetbrains.annotations.Nullable;

public final class LifecycleHelper {
  public static void close(@Nullable Object paramObject) {
    if (paramObject != null && paramObject instanceof ISentryLifecycleToken) {
      ISentryLifecycleToken iSentryLifecycleToken = (ISentryLifecycleToken)paramObject;
      iSentryLifecycleToken.close();
    } 
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\LifecycleHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
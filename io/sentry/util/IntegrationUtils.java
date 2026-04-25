package io.sentry.util;

import io.sentry.SentryIntegrationPackageStorage;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class IntegrationUtils {
  public static void addIntegrationToSdkVersion(@NotNull Class<?> paramClass) {
    String str = paramClass.getSimpleName().replace("Sentry", "").replace("Integration", "").replace("Interceptor", "").replace("EventProcessor", "");
    addIntegrationToSdkVersion(str);
  }
  
  public static void addIntegrationToSdkVersion(@NotNull String paramString) {
    SentryIntegrationPackageStorage.getInstance().addIntegration(paramString);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\IntegrationUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry.util;

import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import java.util.List;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DebugMetaPropertiesApplier {
  @NotNull
  public static String DEBUG_META_PROPERTIES_FILENAME = "sentry-debug-meta.properties";
  
  public static void applyToOptions(@NotNull SentryOptions paramSentryOptions, @Nullable List<Properties> paramList) {
    if (paramList != null) {
      applyBundleIds(paramSentryOptions, paramList);
      applyProguardUuid(paramSentryOptions, paramList);
    } 
  }
  
  private static void applyBundleIds(@NotNull SentryOptions paramSentryOptions, @NotNull List<Properties> paramList) {
    if (paramSentryOptions.getBundleIds().isEmpty())
      for (Properties properties : paramList) {
        String str = properties.getProperty("io.sentry.bundle-ids");
        paramSentryOptions.getLogger().log(SentryLevel.DEBUG, "Bundle IDs found: %s", new Object[] { str });
        if (str != null) {
          String[] arrayOfString = str.split(",", -1);
          for (String str1 : arrayOfString)
            paramSentryOptions.addBundleId(str1); 
        } 
      }  
  }
  
  private static void applyProguardUuid(@NotNull SentryOptions paramSentryOptions, @NotNull List<Properties> paramList) {
    if (paramSentryOptions.getProguardUuid() == null)
      for (Properties properties : paramList) {
        String str = getProguardUuid(properties);
        if (str != null) {
          paramSentryOptions.getLogger().log(SentryLevel.DEBUG, "Proguard UUID found: %s", new Object[] { str });
          paramSentryOptions.setProguardUuid(str);
          break;
        } 
      }  
  }
  
  @Nullable
  public static String getProguardUuid(@NotNull Properties paramProperties) {
    return paramProperties.getProperty("io.sentry.ProguardUuids");
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\DebugMetaPropertiesApplier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
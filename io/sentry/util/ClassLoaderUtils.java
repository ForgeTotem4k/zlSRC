package io.sentry.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClassLoaderUtils {
  @NotNull
  public static ClassLoader classLoaderOrDefault(@Nullable ClassLoader paramClassLoader) {
    return (paramClassLoader == null) ? ClassLoader.getSystemClassLoader() : paramClassLoader;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\ClassLoaderUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry.util;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class Platform {
  static boolean isAndroid;
  
  static boolean isJavaNinePlus;
  
  public static boolean isAndroid() {
    return isAndroid;
  }
  
  public static boolean isJvm() {
    return !isAndroid;
  }
  
  public static boolean isJavaNinePlus() {
    return isJavaNinePlus;
  }
  
  static {
    try {
      isAndroid = "The Android Project".equals(System.getProperty("java.vendor"));
    } catch (Throwable throwable) {
      isAndroid = false;
    } 
    try {
      String str = System.getProperty("java.specification.version");
      if (str != null) {
        double d = Double.valueOf(str).doubleValue();
        isJavaNinePlus = (d >= 9.0D);
      } else {
        isJavaNinePlus = false;
      } 
    } catch (Throwable throwable) {
      isJavaNinePlus = false;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\Platform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
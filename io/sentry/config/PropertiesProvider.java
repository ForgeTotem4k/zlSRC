package io.sentry.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PropertiesProvider {
  @Nullable
  String getProperty(@NotNull String paramString);
  
  @NotNull
  Map<String, String> getMap(@NotNull String paramString);
  
  @NotNull
  default List<String> getList(@NotNull String paramString) {
    String str = getProperty(paramString);
    return (str != null) ? Arrays.<String>asList(str.split(",")) : Collections.<String>emptyList();
  }
  
  @NotNull
  default String getProperty(@NotNull String paramString1, @NotNull String paramString2) {
    String str = getProperty(paramString1);
    return (str != null) ? str : paramString2;
  }
  
  @Nullable
  default Boolean getBooleanProperty(@NotNull String paramString) {
    String str = getProperty(paramString);
    return (str != null) ? Boolean.valueOf(str) : null;
  }
  
  @Nullable
  default Double getDoubleProperty(@NotNull String paramString) {
    String str = getProperty(paramString);
    Double double_ = null;
    if (str != null)
      try {
        double_ = Double.valueOf(str);
      } catch (NumberFormatException numberFormatException) {} 
    return double_;
  }
  
  @Nullable
  default Long getLongProperty(@NotNull String paramString) {
    String str = getProperty(paramString);
    Long long_ = null;
    if (str != null)
      try {
        long_ = Long.valueOf(str);
      } catch (NumberFormatException numberFormatException) {} 
    return long_;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\config\PropertiesProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
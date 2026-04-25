package io.sentry.config;

import io.sentry.util.StringUtils;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class EnvironmentVariablePropertiesProvider implements PropertiesProvider {
  private static final String PREFIX = "SENTRY";
  
  @Nullable
  public String getProperty(@NotNull String paramString) {
    return StringUtils.removeSurrounding(System.getenv(propertyToEnvironmentVariableName(paramString)), "\"");
  }
  
  @NotNull
  public Map<String, String> getMap(@NotNull String paramString) {
    String str = propertyToEnvironmentVariableName(paramString) + "_";
    ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();
    for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
      String str1 = (String)entry.getKey();
      if (str1.startsWith(str)) {
        String str2 = StringUtils.removeSurrounding((String)entry.getValue(), "\"");
        if (str2 != null)
          concurrentHashMap.put(str1.substring(str.length()).toLowerCase(Locale.ROOT), str2); 
      } 
    } 
    return (Map)concurrentHashMap;
  }
  
  @NotNull
  private String propertyToEnvironmentVariableName(@NotNull String paramString) {
    return "SENTRY_" + paramString.replace(".", "_").replace("-", "_").toUpperCase(Locale.ROOT);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\config\EnvironmentVariablePropertiesProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
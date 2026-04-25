package io.sentry.config;

import io.sentry.util.Objects;
import io.sentry.util.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class AbstractPropertiesProvider implements PropertiesProvider {
  @NotNull
  private final String prefix;
  
  @NotNull
  private final Properties properties;
  
  protected AbstractPropertiesProvider(@NotNull String paramString, @NotNull Properties paramProperties) {
    this.prefix = (String)Objects.requireNonNull(paramString, "prefix is required");
    this.properties = (Properties)Objects.requireNonNull(paramProperties, "properties are required");
  }
  
  protected AbstractPropertiesProvider(@NotNull Properties paramProperties) {
    this("", paramProperties);
  }
  
  @Nullable
  public String getProperty(@NotNull String paramString) {
    return StringUtils.removeSurrounding(this.properties.getProperty(this.prefix + paramString), "\"");
  }
  
  @NotNull
  public Map<String, String> getMap(@NotNull String paramString) {
    String str = this.prefix + paramString + ".";
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (Map.Entry<Object, Object> entry : this.properties.entrySet()) {
      if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
        String str1 = (String)entry.getKey();
        if (str1.startsWith(str)) {
          String str2 = StringUtils.removeSurrounding((String)entry.getValue(), "\"");
          hashMap.put(str1.substring(str.length()), str2);
        } 
      } 
    } 
    return (Map)hashMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\config\AbstractPropertiesProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
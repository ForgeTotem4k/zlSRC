package io.sentry.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CompositePropertiesProvider implements PropertiesProvider {
  @NotNull
  private final List<PropertiesProvider> providers;
  
  public CompositePropertiesProvider(@NotNull List<PropertiesProvider> paramList) {
    this.providers = paramList;
  }
  
  @Nullable
  public String getProperty(@NotNull String paramString) {
    for (PropertiesProvider propertiesProvider : this.providers) {
      String str = propertiesProvider.getProperty(paramString);
      if (str != null)
        return str; 
    } 
    return null;
  }
  
  @NotNull
  public Map<String, String> getMap(@NotNull String paramString) {
    ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();
    for (PropertiesProvider propertiesProvider : this.providers)
      concurrentHashMap.putAll(propertiesProvider.getMap(paramString)); 
    return (Map)concurrentHashMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\config\CompositePropertiesProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
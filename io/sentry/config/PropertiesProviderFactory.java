package io.sentry.config;

import io.sentry.ILogger;
import io.sentry.SystemOutLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class PropertiesProviderFactory {
  @NotNull
  public static PropertiesProvider create() {
    SystemOutLogger systemOutLogger = new SystemOutLogger();
    ArrayList<SystemPropertyPropertiesProvider> arrayList = new ArrayList();
    arrayList.add(new SystemPropertyPropertiesProvider());
    arrayList.add(new EnvironmentVariablePropertiesProvider());
    String str1 = System.getProperty("sentry.properties.file");
    if (str1 != null) {
      Properties properties = (new FilesystemPropertiesLoader(str1, (ILogger)systemOutLogger)).load();
      if (properties != null)
        arrayList.add(new SimplePropertiesProvider(properties)); 
    } 
    String str2 = System.getenv("SENTRY_PROPERTIES_FILE");
    if (str2 != null) {
      Properties properties = (new FilesystemPropertiesLoader(str2, (ILogger)systemOutLogger)).load();
      if (properties != null)
        arrayList.add(new SimplePropertiesProvider(properties)); 
    } 
    Properties properties1 = (new ClasspathPropertiesLoader((ILogger)systemOutLogger)).load();
    if (properties1 != null)
      arrayList.add(new SimplePropertiesProvider(properties1)); 
    Properties properties2 = (new FilesystemPropertiesLoader("sentry.properties", (ILogger)systemOutLogger)).load();
    if (properties2 != null)
      arrayList.add(new SimplePropertiesProvider(properties2)); 
    return new CompositePropertiesProvider((List)arrayList);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\config\PropertiesProviderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
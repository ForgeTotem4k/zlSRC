package io.sentry;

import io.sentry.util.LoadClass;
import java.lang.reflect.InvocationTargetException;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class ScopesStorageFactory {
  private static final String OTEL_SCOPES_STORAGE = "io.sentry.opentelemetry.OtelContextScopesStorage";
  
  @NotNull
  public static IScopesStorage create(@NotNull LoadClass paramLoadClass, @NotNull ILogger paramILogger) {
    if (paramLoadClass.isClassAvailable("io.sentry.opentelemetry.OtelContextScopesStorage", paramILogger)) {
      Class<Object> clazz = paramLoadClass.loadClass("io.sentry.opentelemetry.OtelContextScopesStorage", paramILogger);
      if (clazz != null)
        try {
          IScopesStorage iScopesStorage = (IScopesStorage)clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
          if (iScopesStorage != null && iScopesStorage instanceof IScopesStorage)
            return iScopesStorage; 
        } catch (InstantiationException instantiationException) {
        
        } catch (IllegalAccessException illegalAccessException) {
        
        } catch (InvocationTargetException invocationTargetException) {
        
        } catch (NoSuchMethodException noSuchMethodException) {} 
    } 
    return new DefaultScopesStorage();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\ScopesStorageFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public interface JsonDeserializer<T> {
  @NotNull
  T deserialize(@NotNull JsonObjectReader paramJsonObjectReader, @NotNull ILogger paramILogger) throws Exception;
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\JsonDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
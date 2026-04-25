package io.sentry;

import java.io.IOException;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public enum SentryLevel implements JsonSerializable {
  DEBUG, INFO, WARNING, ERROR, FATAL;
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.value(name().toLowerCase(Locale.ROOT));
  }
  
  static final class Deserializer implements JsonDeserializer<SentryLevel> {
    @NotNull
    public SentryLevel deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      return SentryLevel.valueOf(param1JsonObjectReader.nextString().toUpperCase(Locale.ROOT));
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryLevel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
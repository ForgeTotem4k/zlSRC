package io.sentry.instrumentation.file;

import io.sentry.IScopes;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import org.jetbrains.annotations.NotNull;

public final class SentryFileReader extends InputStreamReader {
  public SentryFileReader(@NotNull String paramString) throws FileNotFoundException {
    super(new SentryFileInputStream(paramString));
  }
  
  public SentryFileReader(@NotNull File paramFile) throws FileNotFoundException {
    super(new SentryFileInputStream(paramFile));
  }
  
  public SentryFileReader(@NotNull FileDescriptor paramFileDescriptor) {
    super(new SentryFileInputStream(paramFileDescriptor));
  }
  
  SentryFileReader(@NotNull File paramFile, @NotNull IScopes paramIScopes) throws FileNotFoundException {
    super(new SentryFileInputStream(paramFile, paramIScopes));
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\instrumentation\file\SentryFileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
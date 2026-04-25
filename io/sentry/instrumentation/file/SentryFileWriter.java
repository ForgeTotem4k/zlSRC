package io.sentry.instrumentation.file;

import io.sentry.IScopes;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import org.jetbrains.annotations.NotNull;

public final class SentryFileWriter extends OutputStreamWriter {
  public SentryFileWriter(@NotNull String paramString) throws FileNotFoundException {
    super(new SentryFileOutputStream(paramString));
  }
  
  public SentryFileWriter(@NotNull String paramString, boolean paramBoolean) throws FileNotFoundException {
    super(new SentryFileOutputStream(paramString, paramBoolean));
  }
  
  public SentryFileWriter(@NotNull File paramFile) throws FileNotFoundException {
    super(new SentryFileOutputStream(paramFile));
  }
  
  public SentryFileWriter(@NotNull File paramFile, boolean paramBoolean) throws FileNotFoundException {
    super(new SentryFileOutputStream(paramFile, paramBoolean));
  }
  
  public SentryFileWriter(@NotNull FileDescriptor paramFileDescriptor) {
    super(new SentryFileOutputStream(paramFileDescriptor));
  }
  
  SentryFileWriter(@NotNull File paramFile, boolean paramBoolean, @NotNull IScopes paramIScopes) throws FileNotFoundException {
    super(new SentryFileOutputStream(paramFile, paramBoolean, paramIScopes));
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\instrumentation\file\SentryFileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
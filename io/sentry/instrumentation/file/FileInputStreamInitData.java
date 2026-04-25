package io.sentry.instrumentation.file;

import io.sentry.ISpan;
import io.sentry.SentryOptions;
import java.io.File;
import java.io.FileInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FileInputStreamInitData {
  @Nullable
  final File file;
  
  @Nullable
  final ISpan span;
  
  @NotNull
  final FileInputStream delegate;
  
  @NotNull
  final SentryOptions options;
  
  FileInputStreamInitData(@Nullable File paramFile, @Nullable ISpan paramISpan, @NotNull FileInputStream paramFileInputStream, @NotNull SentryOptions paramSentryOptions) {
    this.file = paramFile;
    this.span = paramISpan;
    this.delegate = paramFileInputStream;
    this.options = paramSentryOptions;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\instrumentation\file\FileInputStreamInitData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
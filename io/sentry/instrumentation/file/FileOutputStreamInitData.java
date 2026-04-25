package io.sentry.instrumentation.file;

import io.sentry.ISpan;
import io.sentry.SentryOptions;
import java.io.File;
import java.io.FileOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FileOutputStreamInitData {
  @Nullable
  final File file;
  
  @Nullable
  final ISpan span;
  
  final boolean append;
  
  @NotNull
  final FileOutputStream delegate;
  
  @NotNull
  final SentryOptions options;
  
  FileOutputStreamInitData(@Nullable File paramFile, boolean paramBoolean, @Nullable ISpan paramISpan, @NotNull FileOutputStream paramFileOutputStream, @NotNull SentryOptions paramSentryOptions) {
    this.file = paramFile;
    this.append = paramBoolean;
    this.span = paramISpan;
    this.delegate = paramFileOutputStream;
    this.options = paramSentryOptions;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\instrumentation\file\FileOutputStreamInitData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
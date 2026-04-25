package io.sentry.instrumentation.file;

import io.sentry.IScopes;
import io.sentry.ISpan;
import io.sentry.SentryIntegrationPackageStorage;
import io.sentry.SentryOptions;
import io.sentry.SentryStackTraceFactory;
import io.sentry.SpanStatus;
import io.sentry.util.Platform;
import io.sentry.util.StringUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FileIOSpanManager {
  @Nullable
  private final ISpan currentSpan;
  
  @Nullable
  private final File file;
  
  @NotNull
  private final SentryOptions options;
  
  @NotNull
  private SpanStatus spanStatus = SpanStatus.OK;
  
  private long byteCount;
  
  @NotNull
  private final SentryStackTraceFactory stackTraceFactory;
  
  @Nullable
  static ISpan startSpan(@NotNull IScopes paramIScopes, @NotNull String paramString) {
    ISpan iSpan = (ISpan)(Platform.isAndroid() ? paramIScopes.getTransaction() : paramIScopes.getSpan());
    return (iSpan != null) ? iSpan.startChild(paramString) : null;
  }
  
  FileIOSpanManager(@Nullable ISpan paramISpan, @Nullable File paramFile, @NotNull SentryOptions paramSentryOptions) {
    this.currentSpan = paramISpan;
    this.file = paramFile;
    this.options = paramSentryOptions;
    this.stackTraceFactory = new SentryStackTraceFactory(paramSentryOptions);
    SentryIntegrationPackageStorage.getInstance().addIntegration("FileIO");
  }
  
  <T> T performIO(@NotNull FileIOCallable<T> paramFileIOCallable) throws IOException {
    try {
      T t = paramFileIOCallable.call();
      if (t instanceof Integer) {
        int i = ((Integer)t).intValue();
        if (i != -1)
          this.byteCount += i; 
      } else if (t instanceof Long) {
        long l = ((Long)t).longValue();
        if (l != -1L)
          this.byteCount += l; 
      } 
      return t;
    } catch (IOException iOException) {
      this.spanStatus = SpanStatus.INTERNAL_ERROR;
      if (this.currentSpan != null)
        this.currentSpan.setThrowable(iOException); 
      throw iOException;
    } 
  }
  
  void finish(@NotNull Closeable paramCloseable) throws IOException {
    try {
      paramCloseable.close();
    } catch (IOException iOException) {
      this.spanStatus = SpanStatus.INTERNAL_ERROR;
      if (this.currentSpan != null)
        this.currentSpan.setThrowable(iOException); 
      throw iOException;
    } finally {
      finishSpan();
    } 
  }
  
  private void finishSpan() {
    if (this.currentSpan != null) {
      String str = StringUtils.byteCountToString(this.byteCount);
      if (this.file != null) {
        String str1 = this.file.getName() + " (" + str + ")";
        this.currentSpan.setDescription(str1);
        if (Platform.isAndroid() || this.options.isSendDefaultPii())
          this.currentSpan.setData("file.path", this.file.getAbsolutePath()); 
      } else {
        this.currentSpan.setDescription(str);
      } 
      this.currentSpan.setData("file.size", Long.valueOf(this.byteCount));
      boolean bool = this.options.getMainThreadChecker().isMainThread();
      this.currentSpan.setData("blocked_main_thread", Boolean.valueOf(bool));
      if (bool)
        this.currentSpan.setData("call_stack", this.stackTraceFactory.getInAppCallStack()); 
      this.currentSpan.finish(this.spanStatus);
    } 
  }
  
  @FunctionalInterface
  static interface FileIOCallable<T> {
    T call() throws IOException;
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\instrumentation\file\FileIOSpanManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
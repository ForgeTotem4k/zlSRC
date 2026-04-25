package io.sentry;

import io.sentry.hints.Flushable;
import io.sentry.hints.Retryable;
import io.sentry.util.HintUtils;
import io.sentry.util.Objects;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class EnvelopeSender extends DirectoryProcessor implements IEnvelopeSender {
  @NotNull
  private final IScopes scopes;
  
  @NotNull
  private final ISerializer serializer;
  
  @NotNull
  private final ILogger logger;
  
  public EnvelopeSender(@NotNull IScopes paramIScopes, @NotNull ISerializer paramISerializer, @NotNull ILogger paramILogger, long paramLong, int paramInt) {
    super(paramIScopes, paramILogger, paramLong, paramInt);
    this.scopes = (IScopes)Objects.requireNonNull(paramIScopes, "Scopes are required.");
    this.serializer = (ISerializer)Objects.requireNonNull(paramISerializer, "Serializer is required.");
    this.logger = (ILogger)Objects.requireNonNull(paramILogger, "Logger is required.");
  }
  
  protected void processFile(@NotNull File paramFile, @NotNull Hint paramHint) {
    if (!paramFile.isFile()) {
      this.logger.log(SentryLevel.DEBUG, "'%s' is not a file.", new Object[] { paramFile.getAbsolutePath() });
      return;
    } 
    if (!isRelevantFileName(paramFile.getName())) {
      this.logger.log(SentryLevel.DEBUG, "File '%s' doesn't match extension expected.", new Object[] { paramFile.getAbsolutePath() });
      return;
    } 
    if (!paramFile.getParentFile().canWrite()) {
      this.logger.log(SentryLevel.WARNING, "File '%s' cannot be deleted so it will not be processed.", new Object[] { paramFile.getAbsolutePath() });
      return;
    } 
    try {
      BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(paramFile));
      try {
        SentryEnvelope sentryEnvelope = this.serializer.deserializeEnvelope(bufferedInputStream);
        if (sentryEnvelope == null) {
          this.logger.log(SentryLevel.ERROR, "Failed to deserialize cached envelope %s", new Object[] { paramFile.getAbsolutePath() });
        } else {
          this.scopes.captureEnvelope(sentryEnvelope, paramHint);
        } 
        HintUtils.runIfHasTypeLogIfNot(paramHint, Flushable.class, this.logger, paramFlushable -> {
              if (!paramFlushable.waitFlush())
                this.logger.log(SentryLevel.WARNING, "Timed out waiting for envelope submission.", new Object[0]); 
            });
        bufferedInputStream.close();
      } catch (Throwable throwable) {
        try {
          bufferedInputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (FileNotFoundException fileNotFoundException) {
      this.logger.log(SentryLevel.ERROR, fileNotFoundException, "File '%s' cannot be found.", new Object[] { paramFile.getAbsolutePath() });
    } catch (IOException iOException) {
      this.logger.log(SentryLevel.ERROR, iOException, "I/O on file '%s' failed.", new Object[] { paramFile.getAbsolutePath() });
    } catch (Throwable throwable) {
      this.logger.log(SentryLevel.ERROR, throwable, "Failed to capture cached envelope %s", new Object[] { paramFile.getAbsolutePath() });
      HintUtils.runIfHasTypeLogIfNot(paramHint, Retryable.class, this.logger, paramRetryable -> {
            paramRetryable.setRetry(false);
            this.logger.log(SentryLevel.INFO, paramThrowable, "File '%s' won't retry.", new Object[] { paramFile.getAbsolutePath() });
          });
    } finally {
      HintUtils.runIfHasTypeLogIfNot(paramHint, Retryable.class, this.logger, paramRetryable -> {
            if (!paramRetryable.isRetry()) {
              safeDelete(paramFile, "after trying to capture it");
              this.logger.log(SentryLevel.DEBUG, "Deleted file %s.", new Object[] { paramFile.getAbsolutePath() });
            } else {
              this.logger.log(SentryLevel.INFO, "File not deleted since retry was marked. %s.", new Object[] { paramFile.getAbsolutePath() });
            } 
          });
    } 
  }
  
  protected boolean isRelevantFileName(@NotNull String paramString) {
    return paramString.endsWith(".envelope");
  }
  
  public void processEnvelopeFile(@NotNull String paramString, @NotNull Hint paramHint) {
    Objects.requireNonNull(paramString, "Path is required.");
    processFile(new File(paramString), paramHint);
  }
  
  private void safeDelete(@NotNull File paramFile, @NotNull String paramString) {
    try {
      if (!paramFile.delete())
        this.logger.log(SentryLevel.ERROR, "Failed to delete '%s' %s", new Object[] { paramFile.getAbsolutePath(), paramString }); 
    } catch (Throwable throwable) {
      this.logger.log(SentryLevel.ERROR, throwable, "Failed to delete '%s' %s", new Object[] { paramFile.getAbsolutePath(), paramString });
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\EnvelopeSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
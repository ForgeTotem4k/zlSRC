package io.sentry;

import io.sentry.hints.Cached;
import io.sentry.hints.Enqueable;
import io.sentry.hints.Flushable;
import io.sentry.hints.Retryable;
import io.sentry.hints.SubmissionResult;
import io.sentry.transport.RateLimiter;
import io.sentry.util.HintUtils;
import java.io.File;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

abstract class DirectoryProcessor {
  private static final long ENVELOPE_PROCESSING_DELAY = 100L;
  
  @NotNull
  private final IScopes scopes;
  
  @NotNull
  private final ILogger logger;
  
  private final long flushTimeoutMillis;
  
  private final Queue<String> processedEnvelopes;
  
  DirectoryProcessor(@NotNull IScopes paramIScopes, @NotNull ILogger paramILogger, long paramLong, int paramInt) {
    this.scopes = paramIScopes;
    this.logger = paramILogger;
    this.flushTimeoutMillis = paramLong;
    this.processedEnvelopes = SynchronizedQueue.synchronizedQueue(new CircularFifoQueue<>(paramInt));
  }
  
  public void processDirectory(@NotNull File paramFile) {
    try {
      this.logger.log(SentryLevel.DEBUG, "Processing dir. %s", new Object[] { paramFile.getAbsolutePath() });
      if (!paramFile.exists()) {
        this.logger.log(SentryLevel.WARNING, "Directory '%s' doesn't exist. No cached events to send.", new Object[] { paramFile.getAbsolutePath() });
        return;
      } 
      if (!paramFile.isDirectory()) {
        this.logger.log(SentryLevel.ERROR, "Cache dir %s is not a directory.", new Object[] { paramFile.getAbsolutePath() });
        return;
      } 
      File[] arrayOfFile1 = paramFile.listFiles();
      if (arrayOfFile1 == null) {
        this.logger.log(SentryLevel.ERROR, "Cache dir %s is null.", new Object[] { paramFile.getAbsolutePath() });
        return;
      } 
      File[] arrayOfFile2 = paramFile.listFiles((paramFile, paramString) -> isRelevantFileName(paramString));
      this.logger.log(SentryLevel.DEBUG, "Processing %d items from cache dir %s", new Object[] { Integer.valueOf((arrayOfFile2 != null) ? arrayOfFile2.length : 0), paramFile.getAbsolutePath() });
      for (File file : arrayOfFile1) {
        if (!file.isFile()) {
          this.logger.log(SentryLevel.DEBUG, "File %s is not a File.", new Object[] { file.getAbsolutePath() });
        } else {
          String str = file.getAbsolutePath();
          if (this.processedEnvelopes.contains(str)) {
            this.logger.log(SentryLevel.DEBUG, "File '%s' has already been processed so it will not be processed again.", new Object[] { str });
          } else {
            RateLimiter rateLimiter = this.scopes.getRateLimiter();
            if (rateLimiter != null && rateLimiter.isActiveForCategory(DataCategory.All)) {
              this.logger.log(SentryLevel.INFO, "DirectoryProcessor, rate limiting active.", new Object[0]);
              return;
            } 
            this.logger.log(SentryLevel.DEBUG, "Processing file: %s", new Object[] { str });
            SendCachedEnvelopeHint sendCachedEnvelopeHint = new SendCachedEnvelopeHint(this.flushTimeoutMillis, this.logger, str, this.processedEnvelopes);
            Hint hint = HintUtils.createWithTypeCheckHint(sendCachedEnvelopeHint);
            processFile(file, hint);
            Thread.sleep(100L);
          } 
        } 
      } 
    } catch (Throwable throwable) {
      this.logger.log(SentryLevel.ERROR, throwable, "Failed processing '%s'", new Object[] { paramFile.getAbsolutePath() });
    } 
  }
  
  protected abstract void processFile(@NotNull File paramFile, @NotNull Hint paramHint);
  
  protected abstract boolean isRelevantFileName(String paramString);
  
  private static final class SendCachedEnvelopeHint implements Cached, Retryable, SubmissionResult, Flushable, Enqueable {
    boolean retry = false;
    
    boolean succeeded = false;
    
    private final CountDownLatch latch;
    
    private final long flushTimeoutMillis;
    
    @NotNull
    private final ILogger logger;
    
    @NotNull
    private final String filePath;
    
    @NotNull
    private final Queue<String> processedEnvelopes;
    
    public SendCachedEnvelopeHint(long param1Long, @NotNull ILogger param1ILogger, @NotNull String param1String, @NotNull Queue<String> param1Queue) {
      this.flushTimeoutMillis = param1Long;
      this.filePath = param1String;
      this.processedEnvelopes = param1Queue;
      this.latch = new CountDownLatch(1);
      this.logger = param1ILogger;
    }
    
    public boolean isRetry() {
      return this.retry;
    }
    
    public void setRetry(boolean param1Boolean) {
      this.retry = param1Boolean;
    }
    
    public boolean waitFlush() {
      try {
        return this.latch.await(this.flushTimeoutMillis, TimeUnit.MILLISECONDS);
      } catch (InterruptedException interruptedException) {
        Thread.currentThread().interrupt();
        this.logger.log(SentryLevel.ERROR, "Exception while awaiting on lock.", interruptedException);
        return false;
      } 
    }
    
    public void setResult(boolean param1Boolean) {
      this.succeeded = param1Boolean;
      this.latch.countDown();
    }
    
    public boolean isSuccess() {
      return this.succeeded;
    }
    
    public void markEnqueued() {
      this.processedEnvelopes.add(this.filePath);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\DirectoryProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry.transport;

import io.sentry.DateUtils;
import io.sentry.Hint;
import io.sentry.ILogger;
import io.sentry.RequestDetails;
import io.sentry.SentryDate;
import io.sentry.SentryDateProvider;
import io.sentry.SentryEnvelope;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.UncaughtExceptionHandlerIntegration;
import io.sentry.cache.IEnvelopeCache;
import io.sentry.clientreport.DiscardReason;
import io.sentry.hints.Cached;
import io.sentry.hints.DiskFlushNotification;
import io.sentry.hints.Enqueable;
import io.sentry.hints.Retryable;
import io.sentry.hints.SubmissionResult;
import io.sentry.util.HintUtils;
import io.sentry.util.LogUtils;
import io.sentry.util.Objects;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AsyncHttpTransport implements ITransport {
  @NotNull
  private final QueuedThreadPoolExecutor executor;
  
  @NotNull
  private final IEnvelopeCache envelopeCache;
  
  @NotNull
  private final SentryOptions options;
  
  @NotNull
  private final RateLimiter rateLimiter;
  
  @NotNull
  private final ITransportGate transportGate;
  
  @NotNull
  private final HttpConnection connection;
  
  @Nullable
  private volatile Runnable currentRunnable = null;
  
  public AsyncHttpTransport(@NotNull SentryOptions paramSentryOptions, @NotNull RateLimiter paramRateLimiter, @NotNull ITransportGate paramITransportGate, @NotNull RequestDetails paramRequestDetails) {
    this(initExecutor(paramSentryOptions.getMaxQueueSize(), paramSentryOptions.getEnvelopeDiskCache(), paramSentryOptions.getLogger(), paramSentryOptions.getDateProvider()), paramSentryOptions, paramRateLimiter, paramITransportGate, new HttpConnection(paramSentryOptions, paramRequestDetails, paramRateLimiter));
  }
  
  public AsyncHttpTransport(@NotNull QueuedThreadPoolExecutor paramQueuedThreadPoolExecutor, @NotNull SentryOptions paramSentryOptions, @NotNull RateLimiter paramRateLimiter, @NotNull ITransportGate paramITransportGate, @NotNull HttpConnection paramHttpConnection) {
    this.executor = (QueuedThreadPoolExecutor)Objects.requireNonNull(paramQueuedThreadPoolExecutor, "executor is required");
    this.envelopeCache = (IEnvelopeCache)Objects.requireNonNull(paramSentryOptions.getEnvelopeDiskCache(), "envelopeCache is required");
    this.options = (SentryOptions)Objects.requireNonNull(paramSentryOptions, "options is required");
    this.rateLimiter = (RateLimiter)Objects.requireNonNull(paramRateLimiter, "rateLimiter is required");
    this.transportGate = (ITransportGate)Objects.requireNonNull(paramITransportGate, "transportGate is required");
    this.connection = (HttpConnection)Objects.requireNonNull(paramHttpConnection, "httpConnection is required");
  }
  
  public void send(@NotNull SentryEnvelope paramSentryEnvelope, @NotNull Hint paramHint) throws IOException {
    IEnvelopeCache iEnvelopeCache = this.envelopeCache;
    boolean bool = false;
    if (HintUtils.hasType(paramHint, Cached.class)) {
      iEnvelopeCache = NoOpEnvelopeCache.getInstance();
      bool = true;
      this.options.getLogger().log(SentryLevel.DEBUG, "Captured Envelope is already cached", new Object[0]);
    } 
    SentryEnvelope sentryEnvelope = this.rateLimiter.filter(paramSentryEnvelope, paramHint);
    if (sentryEnvelope == null) {
      if (bool)
        this.envelopeCache.discard(paramSentryEnvelope); 
    } else {
      SentryEnvelope sentryEnvelope1;
      if (HintUtils.hasType(paramHint, UncaughtExceptionHandlerIntegration.UncaughtExceptionHint.class)) {
        sentryEnvelope1 = this.options.getClientReportRecorder().attachReportToEnvelope(sentryEnvelope);
      } else {
        sentryEnvelope1 = sentryEnvelope;
      } 
      Future<?> future = this.executor.submit(new EnvelopeSender(sentryEnvelope1, paramHint, iEnvelopeCache));
      if (future != null && future.isCancelled()) {
        this.options.getClientReportRecorder().recordLostEnvelope(DiscardReason.QUEUE_OVERFLOW, sentryEnvelope1);
      } else {
        HintUtils.runIfHasType(paramHint, Enqueable.class, paramEnqueable -> {
              paramEnqueable.markEnqueued();
              this.options.getLogger().log(SentryLevel.DEBUG, "Envelope enqueued", new Object[0]);
            });
      } 
    } 
  }
  
  public void flush(long paramLong) {
    this.executor.waitTillIdle(paramLong);
  }
  
  private static QueuedThreadPoolExecutor initExecutor(int paramInt, @NotNull IEnvelopeCache paramIEnvelopeCache, @NotNull ILogger paramILogger, @NotNull SentryDateProvider paramSentryDateProvider) {
    RejectedExecutionHandler rejectedExecutionHandler = (paramRunnable, paramThreadPoolExecutor) -> {
        if (paramRunnable instanceof EnvelopeSender) {
          EnvelopeSender envelopeSender = (EnvelopeSender)paramRunnable;
          if (!HintUtils.hasType(envelopeSender.hint, Cached.class))
            paramIEnvelopeCache.store(envelopeSender.envelope, envelopeSender.hint); 
          markHintWhenSendingFailed(envelopeSender.hint, true);
          paramILogger.log(SentryLevel.WARNING, "Envelope rejected", new Object[0]);
        } 
      };
    return new QueuedThreadPoolExecutor(1, paramInt, new AsyncConnectionThreadFactory(), rejectedExecutionHandler, paramILogger, paramSentryDateProvider);
  }
  
  @NotNull
  public RateLimiter getRateLimiter() {
    return this.rateLimiter;
  }
  
  public boolean isHealthy() {
    boolean bool1 = this.rateLimiter.isAnyRateLimitActive();
    boolean bool2 = this.executor.didRejectRecently();
    return (!bool1 && !bool2);
  }
  
  public void close() throws IOException {
    close(false);
  }
  
  public void close(boolean paramBoolean) throws IOException {
    this.executor.shutdown();
    this.options.getLogger().log(SentryLevel.DEBUG, "Shutting down", new Object[0]);
    try {
      long l = paramBoolean ? 0L : this.options.getFlushTimeoutMillis();
      if (!this.executor.awaitTermination(l, TimeUnit.MILLISECONDS)) {
        this.options.getLogger().log(SentryLevel.WARNING, "Failed to shutdown the async connection async sender  within " + l + " ms. Trying to force it now.", new Object[0]);
        this.executor.shutdownNow();
        if (this.currentRunnable != null)
          this.executor.getRejectedExecutionHandler().rejectedExecution(this.currentRunnable, this.executor); 
      } 
    } catch (InterruptedException interruptedException) {
      this.options.getLogger().log(SentryLevel.DEBUG, "Thread interrupted while closing the connection.", new Object[0]);
      Thread.currentThread().interrupt();
    } 
  }
  
  private static void markHintWhenSendingFailed(@NotNull Hint paramHint, boolean paramBoolean) {
    HintUtils.runIfHasType(paramHint, SubmissionResult.class, paramSubmissionResult -> paramSubmissionResult.setResult(false));
    HintUtils.runIfHasType(paramHint, Retryable.class, paramRetryable -> paramRetryable.setRetry(paramBoolean));
  }
  
  private final class EnvelopeSender implements Runnable {
    @NotNull
    private final SentryEnvelope envelope;
    
    @NotNull
    private final Hint hint;
    
    @NotNull
    private final IEnvelopeCache envelopeCache;
    
    private final TransportResult failedResult = TransportResult.error();
    
    EnvelopeSender(@NotNull SentryEnvelope param1SentryEnvelope, @NotNull Hint param1Hint, IEnvelopeCache param1IEnvelopeCache) {
      this.envelope = (SentryEnvelope)Objects.requireNonNull(param1SentryEnvelope, "Envelope is required.");
      this.hint = param1Hint;
      this.envelopeCache = (IEnvelopeCache)Objects.requireNonNull(param1IEnvelopeCache, "EnvelopeCache is required.");
    }
    
    public void run() {
      AsyncHttpTransport.this.currentRunnable = this;
      TransportResult transportResult = this.failedResult;
      try {
        TransportResult transportResult1;
        transportResult = flush();
        AsyncHttpTransport.this.options.getLogger().log(SentryLevel.DEBUG, "Envelope flushed", new Object[0]);
      } catch (Throwable throwable) {
        AsyncHttpTransport.this.options.getLogger().log(SentryLevel.ERROR, throwable, "Envelope submission failed", new Object[0]);
        throw throwable;
      } finally {
        TransportResult transportResult1 = transportResult;
        HintUtils.runIfHasType(this.hint, SubmissionResult.class, param1SubmissionResult -> {
              AsyncHttpTransport.this.options.getLogger().log(SentryLevel.DEBUG, "Marking envelope submission result: %s", new Object[] { Boolean.valueOf(param1TransportResult.isSuccess()) });
              param1SubmissionResult.setResult(param1TransportResult.isSuccess());
            });
        AsyncHttpTransport.this.currentRunnable = null;
      } 
    }
    
    @NotNull
    private TransportResult flush() {
      TransportResult transportResult = this.failedResult;
      this.envelope.getHeader().setSentAt(null);
      this.envelopeCache.store(this.envelope, this.hint);
      HintUtils.runIfHasType(this.hint, DiskFlushNotification.class, param1DiskFlushNotification -> {
            if (param1DiskFlushNotification.isFlushable(this.envelope.getHeader().getEventId())) {
              param1DiskFlushNotification.markFlushed();
              AsyncHttpTransport.this.options.getLogger().log(SentryLevel.DEBUG, "Disk flush envelope fired", new Object[0]);
            } else {
              AsyncHttpTransport.this.options.getLogger().log(SentryLevel.DEBUG, "Not firing envelope flush as there's an ongoing transaction", new Object[0]);
            } 
          });
      if (AsyncHttpTransport.this.transportGate.isConnected()) {
        SentryEnvelope sentryEnvelope = AsyncHttpTransport.this.options.getClientReportRecorder().attachReportToEnvelope(this.envelope);
        try {
          SentryDate sentryDate = AsyncHttpTransport.this.options.getDateProvider().now();
          sentryEnvelope.getHeader().setSentAt(DateUtils.nanosToDate(sentryDate.nanoTimestamp()));
          transportResult = AsyncHttpTransport.this.connection.send(sentryEnvelope);
          if (transportResult.isSuccess()) {
            this.envelopeCache.discard(this.envelope);
          } else {
            String str = "The transport failed to send the envelope with response code " + transportResult.getResponseCode();
            AsyncHttpTransport.this.options.getLogger().log(SentryLevel.ERROR, str, new Object[0]);
            if (transportResult.getResponseCode() >= 400 && transportResult.getResponseCode() != 429)
              HintUtils.runIfDoesNotHaveType(this.hint, Retryable.class, param1Object -> AsyncHttpTransport.this.options.getClientReportRecorder().recordLostEnvelope(DiscardReason.NETWORK_ERROR, param1SentryEnvelope)); 
            throw new IllegalStateException(str);
          } 
        } catch (IOException iOException) {
          HintUtils.runIfHasType(this.hint, Retryable.class, param1Retryable -> param1Retryable.setRetry(true), (param1Object, param1Class) -> {
                LogUtils.logNotInstanceOf(param1Class, param1Object, AsyncHttpTransport.this.options.getLogger());
                AsyncHttpTransport.this.options.getClientReportRecorder().recordLostEnvelope(DiscardReason.NETWORK_ERROR, param1SentryEnvelope);
              });
          throw new IllegalStateException("Sending the event failed.", iOException);
        } 
      } else {
        HintUtils.runIfHasType(this.hint, Retryable.class, param1Retryable -> param1Retryable.setRetry(true), (param1Object, param1Class) -> {
              LogUtils.logNotInstanceOf(param1Class, param1Object, AsyncHttpTransport.this.options.getLogger());
              AsyncHttpTransport.this.options.getClientReportRecorder().recordLostEnvelope(DiscardReason.NETWORK_ERROR, this.envelope);
            });
      } 
      return transportResult;
    }
  }
  
  private static final class AsyncConnectionThreadFactory implements ThreadFactory {
    private int cnt;
    
    private AsyncConnectionThreadFactory() {}
    
    @NotNull
    public Thread newThread(@NotNull Runnable param1Runnable) {
      Thread thread = new Thread(param1Runnable, "SentryAsyncConnection-" + this.cnt++);
      thread.setDaemon(true);
      return thread;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\AsyncHttpTransport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
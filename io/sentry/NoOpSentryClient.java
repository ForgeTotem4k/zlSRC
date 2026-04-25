package io.sentry;

import io.sentry.metrics.NoopMetricsAggregator;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.transport.RateLimiter;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class NoOpSentryClient implements ISentryClient {
  private static final NoOpSentryClient instance = new NoOpSentryClient();
  
  public static NoOpSentryClient getInstance() {
    return instance;
  }
  
  public boolean isEnabled() {
    return false;
  }
  
  @NotNull
  public SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable IScope paramIScope, @Nullable Hint paramHint) {
    return SentryId.EMPTY_ID;
  }
  
  public void close(boolean paramBoolean) {}
  
  public void close() {}
  
  public void flush(long paramLong) {}
  
  public void captureUserFeedback(@NotNull UserFeedback paramUserFeedback) {}
  
  public void captureSession(@NotNull Session paramSession, @Nullable Hint paramHint) {}
  
  public SentryId captureEnvelope(@NotNull SentryEnvelope paramSentryEnvelope, @Nullable Hint paramHint) {
    return SentryId.EMPTY_ID;
  }
  
  @NotNull
  public SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction, @Nullable TraceContext paramTraceContext, @Nullable IScope paramIScope, @Nullable Hint paramHint, @Nullable ProfilingTraceData paramProfilingTraceData) {
    return SentryId.EMPTY_ID;
  }
  
  @Experimental
  @NotNull
  public SentryId captureCheckIn(@NotNull CheckIn paramCheckIn, @Nullable IScope paramIScope, @Nullable Hint paramHint) {
    return SentryId.EMPTY_ID;
  }
  
  @Nullable
  public RateLimiter getRateLimiter() {
    return null;
  }
  
  @NotNull
  public IMetricsAggregator getMetricsAggregator() {
    return (IMetricsAggregator)NoopMetricsAggregator.getInstance();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpSentryClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
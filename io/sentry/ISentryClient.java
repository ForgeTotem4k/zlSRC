package io.sentry;

import io.sentry.protocol.Message;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.transport.RateLimiter;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ISentryClient {
  boolean isEnabled();
  
  @NotNull
  SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable IScope paramIScope, @Nullable Hint paramHint);
  
  void close();
  
  void close(boolean paramBoolean);
  
  void flush(long paramLong);
  
  @NotNull
  default SentryId captureEvent(@NotNull SentryEvent paramSentryEvent) {
    return captureEvent(paramSentryEvent, null, null);
  }
  
  @NotNull
  default SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable IScope paramIScope) {
    return captureEvent(paramSentryEvent, paramIScope, null);
  }
  
  @NotNull
  default SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint) {
    return captureEvent(paramSentryEvent, null, paramHint);
  }
  
  @NotNull
  default SentryId captureMessage(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel, @Nullable IScope paramIScope) {
    SentryEvent sentryEvent = new SentryEvent();
    Message message = new Message();
    message.setFormatted(paramString);
    sentryEvent.setMessage(message);
    sentryEvent.setLevel(paramSentryLevel);
    return captureEvent(sentryEvent, paramIScope);
  }
  
  @NotNull
  default SentryId captureMessage(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel) {
    return captureMessage(paramString, paramSentryLevel, null);
  }
  
  @NotNull
  default SentryId captureException(@NotNull Throwable paramThrowable) {
    return captureException(paramThrowable, null, null);
  }
  
  @NotNull
  default SentryId captureException(@NotNull Throwable paramThrowable, @Nullable IScope paramIScope, @Nullable Hint paramHint) {
    SentryEvent sentryEvent = new SentryEvent(paramThrowable);
    return captureEvent(sentryEvent, paramIScope, paramHint);
  }
  
  @NotNull
  default SentryId captureException(@NotNull Throwable paramThrowable, @Nullable Hint paramHint) {
    return captureException(paramThrowable, null, paramHint);
  }
  
  @NotNull
  default SentryId captureException(@NotNull Throwable paramThrowable, @Nullable IScope paramIScope) {
    return captureException(paramThrowable, paramIScope, null);
  }
  
  void captureUserFeedback(@NotNull UserFeedback paramUserFeedback);
  
  void captureSession(@NotNull Session paramSession, @Nullable Hint paramHint);
  
  default void captureSession(@NotNull Session paramSession) {
    captureSession(paramSession, null);
  }
  
  @Nullable
  SentryId captureEnvelope(@NotNull SentryEnvelope paramSentryEnvelope, @Nullable Hint paramHint);
  
  @Nullable
  default SentryId captureEnvelope(@NotNull SentryEnvelope paramSentryEnvelope) {
    return captureEnvelope(paramSentryEnvelope, null);
  }
  
  @NotNull
  default SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction, @Nullable IScope paramIScope, @Nullable Hint paramHint) {
    return captureTransaction(paramSentryTransaction, null, paramIScope, paramHint);
  }
  
  @NotNull
  default SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction, @Nullable TraceContext paramTraceContext, @Nullable IScope paramIScope, @Nullable Hint paramHint) {
    return captureTransaction(paramSentryTransaction, paramTraceContext, paramIScope, paramHint, null);
  }
  
  @NotNull
  @Internal
  SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction, @Nullable TraceContext paramTraceContext, @Nullable IScope paramIScope, @Nullable Hint paramHint, @Nullable ProfilingTraceData paramProfilingTraceData);
  
  @Internal
  @NotNull
  default SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction, @Nullable TraceContext paramTraceContext) {
    return captureTransaction(paramSentryTransaction, paramTraceContext, null, null);
  }
  
  @NotNull
  default SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction) {
    return captureTransaction(paramSentryTransaction, null, null, null);
  }
  
  @NotNull
  @Experimental
  SentryId captureCheckIn(@NotNull CheckIn paramCheckIn, @Nullable IScope paramIScope, @Nullable Hint paramHint);
  
  @Internal
  @Nullable
  RateLimiter getRateLimiter();
  
  @Internal
  default boolean isHealthy() {
    return true;
  }
  
  @Internal
  @NotNull
  IMetricsAggregator getMetricsAggregator();
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\ISentryClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
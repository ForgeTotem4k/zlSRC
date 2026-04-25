package io.sentry;

import io.sentry.metrics.MetricsApi;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.User;
import io.sentry.transport.RateLimiter;
import java.util.List;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IScopes {
  boolean isEnabled();
  
  @NotNull
  SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint);
  
  @NotNull
  default SentryId captureEvent(@NotNull SentryEvent paramSentryEvent) {
    return captureEvent(paramSentryEvent, new Hint());
  }
  
  @NotNull
  default SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @NotNull ScopeCallback paramScopeCallback) {
    return captureEvent(paramSentryEvent, new Hint(), paramScopeCallback);
  }
  
  @NotNull
  SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint, @NotNull ScopeCallback paramScopeCallback);
  
  @NotNull
  default SentryId captureMessage(@NotNull String paramString) {
    return captureMessage(paramString, SentryLevel.INFO);
  }
  
  @NotNull
  SentryId captureMessage(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel);
  
  @NotNull
  SentryId captureMessage(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel, @NotNull ScopeCallback paramScopeCallback);
  
  @NotNull
  default SentryId captureMessage(@NotNull String paramString, @NotNull ScopeCallback paramScopeCallback) {
    return captureMessage(paramString, SentryLevel.INFO, paramScopeCallback);
  }
  
  @NotNull
  SentryId captureEnvelope(@NotNull SentryEnvelope paramSentryEnvelope, @Nullable Hint paramHint);
  
  @NotNull
  default SentryId captureEnvelope(@NotNull SentryEnvelope paramSentryEnvelope) {
    return captureEnvelope(paramSentryEnvelope, new Hint());
  }
  
  @NotNull
  SentryId captureException(@NotNull Throwable paramThrowable, @Nullable Hint paramHint);
  
  @NotNull
  default SentryId captureException(@NotNull Throwable paramThrowable) {
    return captureException(paramThrowable, new Hint());
  }
  
  @NotNull
  default SentryId captureException(@NotNull Throwable paramThrowable, @NotNull ScopeCallback paramScopeCallback) {
    return captureException(paramThrowable, new Hint(), paramScopeCallback);
  }
  
  @NotNull
  SentryId captureException(@NotNull Throwable paramThrowable, @Nullable Hint paramHint, @NotNull ScopeCallback paramScopeCallback);
  
  void captureUserFeedback(@NotNull UserFeedback paramUserFeedback);
  
  void startSession();
  
  void endSession();
  
  void close();
  
  void close(boolean paramBoolean);
  
  void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb, @Nullable Hint paramHint);
  
  void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb);
  
  default void addBreadcrumb(@NotNull String paramString) {
    addBreadcrumb(new Breadcrumb(paramString));
  }
  
  default void addBreadcrumb(@NotNull String paramString1, @NotNull String paramString2) {
    Breadcrumb breadcrumb = new Breadcrumb(paramString1);
    breadcrumb.setCategory(paramString2);
    addBreadcrumb(breadcrumb);
  }
  
  void setLevel(@Nullable SentryLevel paramSentryLevel);
  
  void setTransaction(@Nullable String paramString);
  
  void setUser(@Nullable User paramUser);
  
  void setFingerprint(@NotNull List<String> paramList);
  
  void clearBreadcrumbs();
  
  void setTag(@NotNull String paramString1, @NotNull String paramString2);
  
  void removeTag(@NotNull String paramString);
  
  void setExtra(@NotNull String paramString1, @NotNull String paramString2);
  
  void removeExtra(@NotNull String paramString);
  
  @NotNull
  SentryId getLastEventId();
  
  @NotNull
  ISentryLifecycleToken pushScope();
  
  @NotNull
  ISentryLifecycleToken pushIsolationScope();
  
  @Deprecated
  void popScope();
  
  void withScope(@NotNull ScopeCallback paramScopeCallback);
  
  void withIsolationScope(@NotNull ScopeCallback paramScopeCallback);
  
  default void configureScope(@NotNull ScopeCallback paramScopeCallback) {
    configureScope(null, paramScopeCallback);
  }
  
  void configureScope(@Nullable ScopeType paramScopeType, @NotNull ScopeCallback paramScopeCallback);
  
  void bindClient(@NotNull ISentryClient paramISentryClient);
  
  boolean isHealthy();
  
  void flush(long paramLong);
  
  @Deprecated
  @NotNull
  IHub clone();
  
  @NotNull
  IScopes forkedScopes(@NotNull String paramString);
  
  @NotNull
  IScopes forkedCurrentScope(@NotNull String paramString);
  
  @NotNull
  IScopes forkedRootScopes(@NotNull String paramString);
  
  @NotNull
  ISentryLifecycleToken makeCurrent();
  
  @Internal
  @NotNull
  IScope getScope();
  
  @Internal
  @NotNull
  IScope getIsolationScope();
  
  @Internal
  @NotNull
  IScope getGlobalScope();
  
  @Internal
  @Nullable
  IScopes getParentScopes();
  
  @Internal
  boolean isAncestorOf(@Nullable IScopes paramIScopes);
  
  @Internal
  @NotNull
  SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction, @Nullable TraceContext paramTraceContext, @Nullable Hint paramHint, @Nullable ProfilingTraceData paramProfilingTraceData);
  
  @Internal
  @NotNull
  default SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction, @Nullable TraceContext paramTraceContext, @Nullable Hint paramHint) {
    return captureTransaction(paramSentryTransaction, paramTraceContext, paramHint, null);
  }
  
  @Internal
  @NotNull
  default SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction, @Nullable Hint paramHint) {
    return captureTransaction(paramSentryTransaction, null, paramHint);
  }
  
  @Internal
  @NotNull
  default SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction, @Nullable TraceContext paramTraceContext) {
    return captureTransaction(paramSentryTransaction, paramTraceContext, null);
  }
  
  @NotNull
  default ITransaction startTransaction(@NotNull TransactionContext paramTransactionContext) {
    return startTransaction(paramTransactionContext, new TransactionOptions());
  }
  
  @NotNull
  default ITransaction startTransaction(@NotNull String paramString1, @NotNull String paramString2) {
    return startTransaction(paramString1, paramString2, new TransactionOptions());
  }
  
  @NotNull
  default ITransaction startTransaction(@NotNull String paramString1, @NotNull String paramString2, @NotNull TransactionOptions paramTransactionOptions) {
    return startTransaction(new TransactionContext(paramString1, paramString2), paramTransactionOptions);
  }
  
  @NotNull
  ITransaction startTransaction(@NotNull TransactionContext paramTransactionContext, @NotNull TransactionOptions paramTransactionOptions);
  
  @Deprecated
  @Nullable
  SentryTraceHeader traceHeaders();
  
  @Internal
  void setSpanContext(@NotNull Throwable paramThrowable, @NotNull ISpan paramISpan, @NotNull String paramString);
  
  @Nullable
  ISpan getSpan();
  
  @Internal
  void setActiveSpan(@Nullable ISpan paramISpan);
  
  @Internal
  @Nullable
  ITransaction getTransaction();
  
  @NotNull
  SentryOptions getOptions();
  
  @Nullable
  Boolean isCrashedLastRun();
  
  void reportFullyDisplayed();
  
  @Deprecated
  default void reportFullDisplayed() {
    reportFullyDisplayed();
  }
  
  @Nullable
  TransactionContext continueTrace(@Nullable String paramString, @Nullable List<String> paramList);
  
  @Nullable
  SentryTraceHeader getTraceparent();
  
  @Nullable
  BaggageHeader getBaggage();
  
  @Experimental
  @NotNull
  SentryId captureCheckIn(@NotNull CheckIn paramCheckIn);
  
  @Internal
  @Nullable
  RateLimiter getRateLimiter();
  
  @Experimental
  @NotNull
  MetricsApi metrics();
  
  default boolean isNoOp() {
    return false;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\IScopes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry;

import io.sentry.metrics.MetricsApi;
import io.sentry.metrics.NoopMetricsAggregator;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.User;
import io.sentry.transport.RateLimiter;
import java.util.List;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public final class NoOpHub implements IHub {
  private static final NoOpHub instance = new NoOpHub();
  
  @NotNull
  private final SentryOptions emptyOptions = SentryOptions.empty();
  
  @NotNull
  private final MetricsApi metricsApi = new MetricsApi((MetricsApi.IMetricsInterface)NoopMetricsAggregator.getInstance());
  
  @Deprecated
  public static NoOpHub getInstance() {
    return instance;
  }
  
  public boolean isEnabled() {
    return false;
  }
  
  @NotNull
  public SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint) {
    return SentryId.EMPTY_ID;
  }
  
  @NotNull
  public SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint, @NotNull ScopeCallback paramScopeCallback) {
    return SentryId.EMPTY_ID;
  }
  
  @NotNull
  public SentryId captureMessage(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel) {
    return SentryId.EMPTY_ID;
  }
  
  @NotNull
  public SentryId captureMessage(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel, @NotNull ScopeCallback paramScopeCallback) {
    return SentryId.EMPTY_ID;
  }
  
  @NotNull
  public SentryId captureEnvelope(@NotNull SentryEnvelope paramSentryEnvelope, @Nullable Hint paramHint) {
    return SentryId.EMPTY_ID;
  }
  
  @NotNull
  public SentryId captureException(@NotNull Throwable paramThrowable, @Nullable Hint paramHint) {
    return SentryId.EMPTY_ID;
  }
  
  @NotNull
  public SentryId captureException(@NotNull Throwable paramThrowable, @Nullable Hint paramHint, @NotNull ScopeCallback paramScopeCallback) {
    return SentryId.EMPTY_ID;
  }
  
  public void captureUserFeedback(@NotNull UserFeedback paramUserFeedback) {}
  
  public void startSession() {}
  
  public void endSession() {}
  
  public void close() {}
  
  public void close(boolean paramBoolean) {}
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb, @Nullable Hint paramHint) {}
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb) {}
  
  public void setLevel(@Nullable SentryLevel paramSentryLevel) {}
  
  public void setTransaction(@Nullable String paramString) {}
  
  public void setUser(@Nullable User paramUser) {}
  
  public void setFingerprint(@NotNull List<String> paramList) {}
  
  public void clearBreadcrumbs() {}
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {}
  
  public void removeTag(@NotNull String paramString) {}
  
  public void setExtra(@NotNull String paramString1, @NotNull String paramString2) {}
  
  public void removeExtra(@NotNull String paramString) {}
  
  @NotNull
  public SentryId getLastEventId() {
    return SentryId.EMPTY_ID;
  }
  
  @NotNull
  public ISentryLifecycleToken pushScope() {
    return NoOpScopesLifecycleToken.getInstance();
  }
  
  @NotNull
  public ISentryLifecycleToken pushIsolationScope() {
    return NoOpScopesLifecycleToken.getInstance();
  }
  
  @Deprecated
  public void popScope() {}
  
  public void withScope(@NotNull ScopeCallback paramScopeCallback) {
    paramScopeCallback.run(NoOpScope.getInstance());
  }
  
  public void withIsolationScope(@NotNull ScopeCallback paramScopeCallback) {
    paramScopeCallback.run(NoOpScope.getInstance());
  }
  
  public void configureScope(@Nullable ScopeType paramScopeType, @NotNull ScopeCallback paramScopeCallback) {}
  
  public void bindClient(@NotNull ISentryClient paramISentryClient) {}
  
  public boolean isHealthy() {
    return true;
  }
  
  public void flush(long paramLong) {}
  
  @Deprecated
  @NotNull
  public IHub clone() {
    return instance;
  }
  
  @NotNull
  public IScopes forkedScopes(@NotNull String paramString) {
    return NoOpScopes.getInstance();
  }
  
  @NotNull
  public IScopes forkedCurrentScope(@NotNull String paramString) {
    return NoOpScopes.getInstance();
  }
  
  @NotNull
  public ISentryLifecycleToken makeCurrent() {
    return NoOpScopesLifecycleToken.getInstance();
  }
  
  @Internal
  @NotNull
  public IScope getScope() {
    return NoOpScope.getInstance();
  }
  
  @Internal
  @NotNull
  public IScope getIsolationScope() {
    return NoOpScope.getInstance();
  }
  
  @Internal
  @NotNull
  public IScope getGlobalScope() {
    return NoOpScope.getInstance();
  }
  
  @Nullable
  public IScopes getParentScopes() {
    return null;
  }
  
  public boolean isAncestorOf(@Nullable IScopes paramIScopes) {
    return false;
  }
  
  @NotNull
  public IScopes forkedRootScopes(@NotNull String paramString) {
    return NoOpScopes.getInstance();
  }
  
  @NotNull
  public SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction, @Nullable TraceContext paramTraceContext, @Nullable Hint paramHint, @Nullable ProfilingTraceData paramProfilingTraceData) {
    return SentryId.EMPTY_ID;
  }
  
  @NotNull
  public ITransaction startTransaction(@NotNull TransactionContext paramTransactionContext, @NotNull TransactionOptions paramTransactionOptions) {
    return NoOpTransaction.getInstance();
  }
  
  @Deprecated
  @NotNull
  public SentryTraceHeader traceHeaders() {
    return new SentryTraceHeader(SentryId.EMPTY_ID, SpanId.EMPTY_ID, Boolean.valueOf(true));
  }
  
  public void setSpanContext(@NotNull Throwable paramThrowable, @NotNull ISpan paramISpan, @NotNull String paramString) {}
  
  @Nullable
  public ISpan getSpan() {
    return null;
  }
  
  public void setActiveSpan(@Nullable ISpan paramISpan) {}
  
  @Nullable
  public ITransaction getTransaction() {
    return null;
  }
  
  @NotNull
  public SentryOptions getOptions() {
    return this.emptyOptions;
  }
  
  @Nullable
  public Boolean isCrashedLastRun() {
    return null;
  }
  
  public void reportFullyDisplayed() {}
  
  @Nullable
  public TransactionContext continueTrace(@Nullable String paramString, @Nullable List<String> paramList) {
    return null;
  }
  
  @Nullable
  public SentryTraceHeader getTraceparent() {
    return null;
  }
  
  @Nullable
  public BaggageHeader getBaggage() {
    return null;
  }
  
  @Experimental
  @NotNull
  public SentryId captureCheckIn(@NotNull CheckIn paramCheckIn) {
    return SentryId.EMPTY_ID;
  }
  
  @Nullable
  public RateLimiter getRateLimiter() {
    return null;
  }
  
  @NotNull
  public MetricsApi metrics() {
    return this.metricsApi;
  }
  
  public boolean isNoOp() {
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpHub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
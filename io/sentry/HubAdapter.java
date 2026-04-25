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

@Deprecated
public final class HubAdapter implements IHub {
  private static final HubAdapter INSTANCE = new HubAdapter();
  
  public static HubAdapter getInstance() {
    return INSTANCE;
  }
  
  public boolean isEnabled() {
    return Sentry.isEnabled();
  }
  
  @NotNull
  public SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint) {
    return Sentry.captureEvent(paramSentryEvent, paramHint);
  }
  
  @NotNull
  public SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint, @NotNull ScopeCallback paramScopeCallback) {
    return Sentry.captureEvent(paramSentryEvent, paramHint, paramScopeCallback);
  }
  
  @NotNull
  public SentryId captureMessage(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel) {
    return Sentry.captureMessage(paramString, paramSentryLevel);
  }
  
  @NotNull
  public SentryId captureMessage(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel, @NotNull ScopeCallback paramScopeCallback) {
    return Sentry.captureMessage(paramString, paramSentryLevel, paramScopeCallback);
  }
  
  @Internal
  @NotNull
  public SentryId captureEnvelope(@NotNull SentryEnvelope paramSentryEnvelope, @Nullable Hint paramHint) {
    return Sentry.getCurrentScopes().captureEnvelope(paramSentryEnvelope, paramHint);
  }
  
  @NotNull
  public SentryId captureException(@NotNull Throwable paramThrowable, @Nullable Hint paramHint) {
    return Sentry.captureException(paramThrowable, paramHint);
  }
  
  @NotNull
  public SentryId captureException(@NotNull Throwable paramThrowable, @Nullable Hint paramHint, @NotNull ScopeCallback paramScopeCallback) {
    return Sentry.captureException(paramThrowable, paramHint, paramScopeCallback);
  }
  
  public void captureUserFeedback(@NotNull UserFeedback paramUserFeedback) {
    Sentry.captureUserFeedback(paramUserFeedback);
  }
  
  public void startSession() {
    Sentry.startSession();
  }
  
  public void endSession() {
    Sentry.endSession();
  }
  
  public void close(boolean paramBoolean) {
    Sentry.close();
  }
  
  public void close() {
    Sentry.close();
  }
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb, @Nullable Hint paramHint) {
    Sentry.addBreadcrumb(paramBreadcrumb, paramHint);
  }
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb) {
    addBreadcrumb(paramBreadcrumb, new Hint());
  }
  
  public void setLevel(@Nullable SentryLevel paramSentryLevel) {
    Sentry.setLevel(paramSentryLevel);
  }
  
  public void setTransaction(@Nullable String paramString) {
    Sentry.setTransaction(paramString);
  }
  
  public void setUser(@Nullable User paramUser) {
    Sentry.setUser(paramUser);
  }
  
  public void setFingerprint(@NotNull List<String> paramList) {
    Sentry.setFingerprint(paramList);
  }
  
  public void clearBreadcrumbs() {
    Sentry.clearBreadcrumbs();
  }
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {
    Sentry.setTag(paramString1, paramString2);
  }
  
  public void removeTag(@NotNull String paramString) {
    Sentry.removeTag(paramString);
  }
  
  public void setExtra(@NotNull String paramString1, @NotNull String paramString2) {
    Sentry.setExtra(paramString1, paramString2);
  }
  
  public void removeExtra(@NotNull String paramString) {
    Sentry.removeExtra(paramString);
  }
  
  @NotNull
  public SentryId getLastEventId() {
    return Sentry.getLastEventId();
  }
  
  @NotNull
  public ISentryLifecycleToken pushScope() {
    return Sentry.pushScope();
  }
  
  @NotNull
  public ISentryLifecycleToken pushIsolationScope() {
    return Sentry.pushIsolationScope();
  }
  
  @Deprecated
  public void popScope() {
    Sentry.popScope();
  }
  
  public void withScope(@NotNull ScopeCallback paramScopeCallback) {
    Sentry.withScope(paramScopeCallback);
  }
  
  public void withIsolationScope(@NotNull ScopeCallback paramScopeCallback) {
    Sentry.withIsolationScope(paramScopeCallback);
  }
  
  public void configureScope(@Nullable ScopeType paramScopeType, @NotNull ScopeCallback paramScopeCallback) {
    Sentry.configureScope(paramScopeType, paramScopeCallback);
  }
  
  public void bindClient(@NotNull ISentryClient paramISentryClient) {
    Sentry.bindClient(paramISentryClient);
  }
  
  public boolean isHealthy() {
    return Sentry.isHealthy();
  }
  
  public void flush(long paramLong) {
    Sentry.flush(paramLong);
  }
  
  @Deprecated
  @NotNull
  public IHub clone() {
    return Sentry.getCurrentScopes().clone();
  }
  
  @NotNull
  public IScopes forkedScopes(@NotNull String paramString) {
    return Sentry.forkedScopes(paramString);
  }
  
  @NotNull
  public IScopes forkedCurrentScope(@NotNull String paramString) {
    return Sentry.forkedCurrentScope(paramString);
  }
  
  @NotNull
  public IScopes forkedRootScopes(@NotNull String paramString) {
    return Sentry.forkedRootScopes(paramString);
  }
  
  @NotNull
  public ISentryLifecycleToken makeCurrent() {
    return NoOpScopesLifecycleToken.getInstance();
  }
  
  @Internal
  @NotNull
  public IScope getScope() {
    return Sentry.getCurrentScopes().getScope();
  }
  
  @Internal
  @NotNull
  public IScope getIsolationScope() {
    return Sentry.getCurrentScopes().getIsolationScope();
  }
  
  @Internal
  @NotNull
  public IScope getGlobalScope() {
    return Sentry.getGlobalScope();
  }
  
  @Nullable
  public IScopes getParentScopes() {
    return Sentry.getCurrentScopes().getParentScopes();
  }
  
  public boolean isAncestorOf(@Nullable IScopes paramIScopes) {
    return Sentry.getCurrentScopes().isAncestorOf(paramIScopes);
  }
  
  @NotNull
  public SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction, @Nullable TraceContext paramTraceContext, @Nullable Hint paramHint, @Nullable ProfilingTraceData paramProfilingTraceData) {
    return Sentry.getCurrentScopes().captureTransaction(paramSentryTransaction, paramTraceContext, paramHint, paramProfilingTraceData);
  }
  
  @NotNull
  public ITransaction startTransaction(@NotNull TransactionContext paramTransactionContext, @NotNull TransactionOptions paramTransactionOptions) {
    return Sentry.startTransaction(paramTransactionContext, paramTransactionOptions);
  }
  
  @Deprecated
  @Nullable
  public SentryTraceHeader traceHeaders() {
    return Sentry.traceHeaders();
  }
  
  public void setSpanContext(@NotNull Throwable paramThrowable, @NotNull ISpan paramISpan, @NotNull String paramString) {
    Sentry.getCurrentScopes().setSpanContext(paramThrowable, paramISpan, paramString);
  }
  
  @Nullable
  public ISpan getSpan() {
    return Sentry.getCurrentScopes().getSpan();
  }
  
  public void setActiveSpan(@Nullable ISpan paramISpan) {
    Sentry.getCurrentScopes().setActiveSpan(paramISpan);
  }
  
  @Internal
  @Nullable
  public ITransaction getTransaction() {
    return Sentry.getCurrentScopes().getTransaction();
  }
  
  @NotNull
  public SentryOptions getOptions() {
    return Sentry.getCurrentScopes().getOptions();
  }
  
  @Nullable
  public Boolean isCrashedLastRun() {
    return Sentry.isCrashedLastRun();
  }
  
  public void reportFullyDisplayed() {
    Sentry.reportFullyDisplayed();
  }
  
  @Nullable
  public TransactionContext continueTrace(@Nullable String paramString, @Nullable List<String> paramList) {
    return Sentry.continueTrace(paramString, paramList);
  }
  
  @Nullable
  public SentryTraceHeader getTraceparent() {
    return Sentry.getTraceparent();
  }
  
  @Nullable
  public BaggageHeader getBaggage() {
    return Sentry.getBaggage();
  }
  
  @Experimental
  @NotNull
  public SentryId captureCheckIn(@NotNull CheckIn paramCheckIn) {
    return Sentry.captureCheckIn(paramCheckIn);
  }
  
  @Internal
  @Nullable
  public RateLimiter getRateLimiter() {
    return Sentry.getCurrentScopes().getRateLimiter();
  }
  
  @NotNull
  public MetricsApi metrics() {
    return Sentry.getCurrentScopes().metrics();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\HubAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
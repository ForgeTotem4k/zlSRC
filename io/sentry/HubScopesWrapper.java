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
public final class HubScopesWrapper implements IHub {
  @NotNull
  private final IScopes scopes;
  
  public HubScopesWrapper(@NotNull IScopes paramIScopes) {
    this.scopes = paramIScopes;
  }
  
  public boolean isEnabled() {
    return this.scopes.isEnabled();
  }
  
  @NotNull
  public SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint) {
    return this.scopes.captureEvent(paramSentryEvent, paramHint);
  }
  
  @NotNull
  public SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint, @NotNull ScopeCallback paramScopeCallback) {
    return this.scopes.captureEvent(paramSentryEvent, paramHint, paramScopeCallback);
  }
  
  @NotNull
  public SentryId captureMessage(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel) {
    return this.scopes.captureMessage(paramString, paramSentryLevel);
  }
  
  @NotNull
  public SentryId captureMessage(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel, @NotNull ScopeCallback paramScopeCallback) {
    return this.scopes.captureMessage(paramString, paramSentryLevel, paramScopeCallback);
  }
  
  @NotNull
  public SentryId captureEnvelope(@NotNull SentryEnvelope paramSentryEnvelope, @Nullable Hint paramHint) {
    return this.scopes.captureEnvelope(paramSentryEnvelope, paramHint);
  }
  
  @NotNull
  public SentryId captureException(@NotNull Throwable paramThrowable, @Nullable Hint paramHint) {
    return this.scopes.captureException(paramThrowable, paramHint);
  }
  
  @NotNull
  public SentryId captureException(@NotNull Throwable paramThrowable, @Nullable Hint paramHint, @NotNull ScopeCallback paramScopeCallback) {
    return this.scopes.captureException(paramThrowable, paramHint, paramScopeCallback);
  }
  
  public void captureUserFeedback(@NotNull UserFeedback paramUserFeedback) {
    this.scopes.captureUserFeedback(paramUserFeedback);
  }
  
  public void startSession() {
    this.scopes.startSession();
  }
  
  public void endSession() {
    this.scopes.endSession();
  }
  
  public void close() {
    this.scopes.close();
  }
  
  public void close(boolean paramBoolean) {
    this.scopes.close(paramBoolean);
  }
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb, @Nullable Hint paramHint) {
    this.scopes.addBreadcrumb(paramBreadcrumb, paramHint);
  }
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb) {
    this.scopes.addBreadcrumb(paramBreadcrumb);
  }
  
  public void setLevel(@Nullable SentryLevel paramSentryLevel) {
    this.scopes.setLevel(paramSentryLevel);
  }
  
  public void setTransaction(@Nullable String paramString) {
    this.scopes.setTransaction(paramString);
  }
  
  public void setUser(@Nullable User paramUser) {
    this.scopes.setUser(paramUser);
  }
  
  public void setFingerprint(@NotNull List<String> paramList) {
    this.scopes.setFingerprint(paramList);
  }
  
  public void clearBreadcrumbs() {
    this.scopes.clearBreadcrumbs();
  }
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {
    this.scopes.setTag(paramString1, paramString2);
  }
  
  public void removeTag(@NotNull String paramString) {
    this.scopes.removeTag(paramString);
  }
  
  public void setExtra(@NotNull String paramString1, @NotNull String paramString2) {
    this.scopes.setExtra(paramString1, paramString2);
  }
  
  public void removeExtra(@NotNull String paramString) {
    this.scopes.removeExtra(paramString);
  }
  
  @NotNull
  public SentryId getLastEventId() {
    return this.scopes.getLastEventId();
  }
  
  @NotNull
  public ISentryLifecycleToken pushScope() {
    return this.scopes.pushScope();
  }
  
  @NotNull
  public ISentryLifecycleToken pushIsolationScope() {
    return this.scopes.pushIsolationScope();
  }
  
  @Deprecated
  public void popScope() {
    this.scopes.popScope();
  }
  
  public void withScope(@NotNull ScopeCallback paramScopeCallback) {
    this.scopes.withScope(paramScopeCallback);
  }
  
  public void withIsolationScope(@NotNull ScopeCallback paramScopeCallback) {
    this.scopes.withIsolationScope(paramScopeCallback);
  }
  
  public void configureScope(@Nullable ScopeType paramScopeType, @NotNull ScopeCallback paramScopeCallback) {
    this.scopes.configureScope(paramScopeType, paramScopeCallback);
  }
  
  public void bindClient(@NotNull ISentryClient paramISentryClient) {
    this.scopes.bindClient(paramISentryClient);
  }
  
  public boolean isHealthy() {
    return this.scopes.isHealthy();
  }
  
  public void flush(long paramLong) {
    this.scopes.flush(paramLong);
  }
  
  @Deprecated
  @NotNull
  public IHub clone() {
    return this.scopes.clone();
  }
  
  @NotNull
  public IScopes forkedScopes(@NotNull String paramString) {
    return this.scopes.forkedScopes(paramString);
  }
  
  @NotNull
  public IScopes forkedCurrentScope(@NotNull String paramString) {
    return this.scopes.forkedCurrentScope(paramString);
  }
  
  @NotNull
  public IScopes forkedRootScopes(@NotNull String paramString) {
    return Sentry.forkedRootScopes(paramString);
  }
  
  @NotNull
  public ISentryLifecycleToken makeCurrent() {
    return this.scopes.makeCurrent();
  }
  
  @Internal
  @NotNull
  public IScope getScope() {
    return this.scopes.getScope();
  }
  
  @Internal
  @NotNull
  public IScope getIsolationScope() {
    return this.scopes.getIsolationScope();
  }
  
  @Internal
  @NotNull
  public IScope getGlobalScope() {
    return Sentry.getGlobalScope();
  }
  
  @Nullable
  public IScopes getParentScopes() {
    return this.scopes.getParentScopes();
  }
  
  public boolean isAncestorOf(@Nullable IScopes paramIScopes) {
    return this.scopes.isAncestorOf(paramIScopes);
  }
  
  @Internal
  @NotNull
  public SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction, @Nullable TraceContext paramTraceContext, @Nullable Hint paramHint, @Nullable ProfilingTraceData paramProfilingTraceData) {
    return this.scopes.captureTransaction(paramSentryTransaction, paramTraceContext, paramHint, paramProfilingTraceData);
  }
  
  @NotNull
  public ITransaction startTransaction(@NotNull TransactionContext paramTransactionContext, @NotNull TransactionOptions paramTransactionOptions) {
    return this.scopes.startTransaction(paramTransactionContext, paramTransactionOptions);
  }
  
  @Nullable
  public SentryTraceHeader traceHeaders() {
    return this.scopes.traceHeaders();
  }
  
  @Internal
  public void setSpanContext(@NotNull Throwable paramThrowable, @NotNull ISpan paramISpan, @NotNull String paramString) {
    this.scopes.setSpanContext(paramThrowable, paramISpan, paramString);
  }
  
  @Nullable
  public ISpan getSpan() {
    return this.scopes.getSpan();
  }
  
  public void setActiveSpan(@Nullable ISpan paramISpan) {
    this.scopes.setActiveSpan(paramISpan);
  }
  
  @Internal
  @Nullable
  public ITransaction getTransaction() {
    return this.scopes.getTransaction();
  }
  
  @NotNull
  public SentryOptions getOptions() {
    return this.scopes.getOptions();
  }
  
  @Nullable
  public Boolean isCrashedLastRun() {
    return this.scopes.isCrashedLastRun();
  }
  
  public void reportFullyDisplayed() {
    this.scopes.reportFullyDisplayed();
  }
  
  @Nullable
  public TransactionContext continueTrace(@Nullable String paramString, @Nullable List<String> paramList) {
    return this.scopes.continueTrace(paramString, paramList);
  }
  
  @Nullable
  public SentryTraceHeader getTraceparent() {
    return this.scopes.getTraceparent();
  }
  
  @Nullable
  public BaggageHeader getBaggage() {
    return this.scopes.getBaggage();
  }
  
  @Experimental
  @NotNull
  public SentryId captureCheckIn(@NotNull CheckIn paramCheckIn) {
    return this.scopes.captureCheckIn(paramCheckIn);
  }
  
  @Internal
  @Nullable
  public RateLimiter getRateLimiter() {
    return this.scopes.getRateLimiter();
  }
  
  @Experimental
  @NotNull
  public MetricsApi metrics() {
    return this.scopes.metrics();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\HubScopesWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
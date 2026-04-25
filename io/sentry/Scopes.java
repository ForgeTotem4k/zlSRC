package io.sentry;

import io.sentry.clientreport.DiscardReason;
import io.sentry.hints.SessionEndHint;
import io.sentry.hints.SessionStartHint;
import io.sentry.metrics.LocalMetricsAggregator;
import io.sentry.metrics.MetricsApi;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.User;
import io.sentry.transport.RateLimiter;
import io.sentry.util.HintUtils;
import io.sentry.util.Objects;
import io.sentry.util.SpanUtils;
import io.sentry.util.TracingUtils;
import java.io.Closeable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Scopes implements IScopes, MetricsApi.IMetricsInterface {
  @NotNull
  private final IScope scope;
  
  @NotNull
  private final IScope isolationScope;
  
  @NotNull
  private final IScope globalScope;
  
  @Nullable
  private final Scopes parentScopes;
  
  @NotNull
  private final String creator;
  
  @NotNull
  private final TransactionPerformanceCollector transactionPerformanceCollector;
  
  @NotNull
  private final MetricsApi metricsApi;
  
  @NotNull
  private final CombinedScopeView combinedScope;
  
  public Scopes(@NotNull IScope paramIScope1, @NotNull IScope paramIScope2, @NotNull IScope paramIScope3, @NotNull String paramString) {
    this(paramIScope1, paramIScope2, paramIScope3, null, paramString);
  }
  
  private Scopes(@NotNull IScope paramIScope1, @NotNull IScope paramIScope2, @NotNull IScope paramIScope3, @Nullable Scopes paramScopes, @NotNull String paramString) {
    this.combinedScope = new CombinedScopeView(paramIScope3, paramIScope2, paramIScope1);
    this.scope = paramIScope1;
    this.isolationScope = paramIScope2;
    this.globalScope = paramIScope3;
    this.parentScopes = paramScopes;
    this.creator = paramString;
    SentryOptions sentryOptions = getOptions();
    validateOptions(sentryOptions);
    this.transactionPerformanceCollector = sentryOptions.getTransactionPerformanceCollector();
    this.metricsApi = new MetricsApi(this);
  }
  
  @NotNull
  public String getCreator() {
    return this.creator;
  }
  
  @Internal
  @NotNull
  public IScope getScope() {
    return this.scope;
  }
  
  @Internal
  @NotNull
  public IScope getIsolationScope() {
    return this.isolationScope;
  }
  
  @Internal
  @NotNull
  public IScope getGlobalScope() {
    return this.globalScope;
  }
  
  @Internal
  @Nullable
  public IScopes getParentScopes() {
    return this.parentScopes;
  }
  
  @Internal
  public boolean isAncestorOf(@Nullable IScopes paramIScopes) {
    return (paramIScopes == null) ? false : ((this == paramIScopes) ? true : ((paramIScopes.getParentScopes() != null) ? isAncestorOf(paramIScopes.getParentScopes()) : false));
  }
  
  @NotNull
  public IScopes forkedScopes(@NotNull String paramString) {
    return new Scopes(this.scope.clone(), this.isolationScope.clone(), this.globalScope, this, paramString);
  }
  
  @NotNull
  public IScopes forkedCurrentScope(@NotNull String paramString) {
    return new Scopes(this.scope.clone(), this.isolationScope, this.globalScope, this, paramString);
  }
  
  @NotNull
  public IScopes forkedRootScopes(@NotNull String paramString) {
    return Sentry.forkedRootScopes(paramString);
  }
  
  public boolean isEnabled() {
    return getClient().isEnabled();
  }
  
  @NotNull
  public SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint) {
    return captureEventInternal(paramSentryEvent, paramHint, null);
  }
  
  @NotNull
  public SentryId captureEvent(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint, @NotNull ScopeCallback paramScopeCallback) {
    return captureEventInternal(paramSentryEvent, paramHint, paramScopeCallback);
  }
  
  @NotNull
  private SentryId captureEventInternal(@NotNull SentryEvent paramSentryEvent, @Nullable Hint paramHint, @Nullable ScopeCallback paramScopeCallback) {
    SentryId sentryId = SentryId.EMPTY_ID;
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureEvent' call is a no-op.", new Object[0]);
    } else if (paramSentryEvent == null) {
      getOptions().getLogger().log(SentryLevel.WARNING, "captureEvent called with null parameter.", new Object[0]);
    } else {
      try {
        assignTraceContext(paramSentryEvent);
        IScope iScope = buildLocalScope(getCombinedScopeView(), paramScopeCallback);
        sentryId = getClient().captureEvent(paramSentryEvent, iScope, paramHint);
        updateLastEventId(sentryId);
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing event with id: " + paramSentryEvent.getEventId(), throwable);
      } 
    } 
    return sentryId;
  }
  
  @NotNull
  private ISentryClient getClient() {
    return getCombinedScopeView().getClient();
  }
  
  private void assignTraceContext(@NotNull SentryEvent paramSentryEvent) {
    getCombinedScopeView().assignTraceContext(paramSentryEvent);
  }
  
  private IScope buildLocalScope(@NotNull IScope paramIScope, @Nullable ScopeCallback paramScopeCallback) {
    if (paramScopeCallback != null)
      try {
        IScope iScope = paramIScope.clone();
        paramScopeCallback.run(iScope);
        return iScope;
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'ScopeCallback' callback.", throwable);
      }  
    return paramIScope;
  }
  
  @NotNull
  public SentryId captureMessage(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel) {
    return captureMessageInternal(paramString, paramSentryLevel, null);
  }
  
  @NotNull
  public SentryId captureMessage(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel, @NotNull ScopeCallback paramScopeCallback) {
    return captureMessageInternal(paramString, paramSentryLevel, paramScopeCallback);
  }
  
  @NotNull
  private SentryId captureMessageInternal(@NotNull String paramString, @NotNull SentryLevel paramSentryLevel, @Nullable ScopeCallback paramScopeCallback) {
    SentryId sentryId = SentryId.EMPTY_ID;
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureMessage' call is a no-op.", new Object[0]);
    } else if (paramString == null) {
      getOptions().getLogger().log(SentryLevel.WARNING, "captureMessage called with null parameter.", new Object[0]);
    } else {
      try {
        IScope iScope = buildLocalScope(getCombinedScopeView(), paramScopeCallback);
        sentryId = getClient().captureMessage(paramString, paramSentryLevel, iScope);
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing message: " + paramString, throwable);
      } 
    } 
    updateLastEventId(sentryId);
    return sentryId;
  }
  
  @Internal
  @NotNull
  public SentryId captureEnvelope(@NotNull SentryEnvelope paramSentryEnvelope, @Nullable Hint paramHint) {
    Objects.requireNonNull(paramSentryEnvelope, "SentryEnvelope is required.");
    SentryId sentryId = SentryId.EMPTY_ID;
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureEnvelope' call is a no-op.", new Object[0]);
    } else {
      try {
        SentryId sentryId1 = getClient().captureEnvelope(paramSentryEnvelope, paramHint);
        if (sentryId1 != null)
          sentryId = sentryId1; 
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing envelope.", throwable);
      } 
    } 
    return sentryId;
  }
  
  @NotNull
  public SentryId captureException(@NotNull Throwable paramThrowable, @Nullable Hint paramHint) {
    return captureExceptionInternal(paramThrowable, paramHint, null);
  }
  
  @NotNull
  public SentryId captureException(@NotNull Throwable paramThrowable, @Nullable Hint paramHint, @NotNull ScopeCallback paramScopeCallback) {
    return captureExceptionInternal(paramThrowable, paramHint, paramScopeCallback);
  }
  
  @NotNull
  private SentryId captureExceptionInternal(@NotNull Throwable paramThrowable, @Nullable Hint paramHint, @Nullable ScopeCallback paramScopeCallback) {
    SentryId sentryId = SentryId.EMPTY_ID;
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureException' call is a no-op.", new Object[0]);
    } else if (paramThrowable == null) {
      getOptions().getLogger().log(SentryLevel.WARNING, "captureException called with null parameter.", new Object[0]);
    } else {
      try {
        SentryEvent sentryEvent = new SentryEvent(paramThrowable);
        assignTraceContext(sentryEvent);
        IScope iScope = buildLocalScope(getCombinedScopeView(), paramScopeCallback);
        sentryId = getClient().captureEvent(sentryEvent, iScope, paramHint);
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing exception: " + paramThrowable.getMessage(), throwable);
      } 
    } 
    updateLastEventId(sentryId);
    return sentryId;
  }
  
  public void captureUserFeedback(@NotNull UserFeedback paramUserFeedback) {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureUserFeedback' call is a no-op.", new Object[0]);
    } else {
      try {
        getClient().captureUserFeedback(paramUserFeedback);
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing captureUserFeedback: " + paramUserFeedback.toString(), throwable);
      } 
    } 
  }
  
  public void startSession() {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'startSession' call is a no-op.", new Object[0]);
    } else {
      Scope.SessionPair sessionPair = getCombinedScopeView().startSession();
      if (sessionPair != null) {
        if (sessionPair.getPrevious() != null) {
          Hint hint1 = HintUtils.createWithTypeCheckHint(new SessionEndHint());
          getClient().captureSession(sessionPair.getPrevious(), hint1);
        } 
        Hint hint = HintUtils.createWithTypeCheckHint(new SessionStartHint());
        getClient().captureSession(sessionPair.getCurrent(), hint);
      } else {
        getOptions().getLogger().log(SentryLevel.WARNING, "Session could not be started.", new Object[0]);
      } 
    } 
  }
  
  public void endSession() {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'endSession' call is a no-op.", new Object[0]);
    } else {
      Session session = getCombinedScopeView().endSession();
      if (session != null) {
        Hint hint = HintUtils.createWithTypeCheckHint(new SessionEndHint());
        getClient().captureSession(session, hint);
      } 
    } 
  }
  
  private IScope getCombinedScopeView() {
    return this.combinedScope;
  }
  
  public void close() {
    close(false);
  }
  
  public void close(boolean paramBoolean) {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'close' call is a no-op.", new Object[0]);
    } else {
      try {
        for (Integration integration : getOptions().getIntegrations()) {
          if (integration instanceof Closeable)
            try {
              ((Closeable)integration).close();
            } catch (Throwable throwable) {
              getOptions().getLogger().log(SentryLevel.WARNING, "Failed to close the integration {}.", new Object[] { integration, throwable });
            }  
        } 
        configureScope(paramIScope -> paramIScope.clear());
        configureScope(ScopeType.ISOLATION, paramIScope -> paramIScope.clear());
        getOptions().getTransactionProfiler().close();
        getOptions().getTransactionPerformanceCollector().close();
        ISentryExecutorService iSentryExecutorService = getOptions().getExecutorService();
        if (paramBoolean) {
          iSentryExecutorService.submit(() -> paramISentryExecutorService.close(getOptions().getShutdownTimeoutMillis()));
        } else {
          iSentryExecutorService.close(getOptions().getShutdownTimeoutMillis());
        } 
        configureScope(ScopeType.CURRENT, paramIScope -> paramIScope.getClient().close(paramBoolean));
        configureScope(ScopeType.ISOLATION, paramIScope -> paramIScope.getClient().close(paramBoolean));
        configureScope(ScopeType.GLOBAL, paramIScope -> paramIScope.getClient().close(paramBoolean));
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error while closing the Scopes.", throwable);
      } 
    } 
  }
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb, @Nullable Hint paramHint) {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'addBreadcrumb' call is a no-op.", new Object[0]);
    } else if (paramBreadcrumb == null) {
      getOptions().getLogger().log(SentryLevel.WARNING, "addBreadcrumb called with null parameter.", new Object[0]);
    } else {
      getCombinedScopeView().addBreadcrumb(paramBreadcrumb, paramHint);
    } 
  }
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb) {
    addBreadcrumb(paramBreadcrumb, new Hint());
  }
  
  public void setLevel(@Nullable SentryLevel paramSentryLevel) {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setLevel' call is a no-op.", new Object[0]);
    } else {
      getCombinedScopeView().setLevel(paramSentryLevel);
    } 
  }
  
  public void setTransaction(@Nullable String paramString) {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setTransaction' call is a no-op.", new Object[0]);
    } else if (paramString != null) {
      getCombinedScopeView().setTransaction(paramString);
    } else {
      getOptions().getLogger().log(SentryLevel.WARNING, "Transaction cannot be null", new Object[0]);
    } 
  }
  
  public void setUser(@Nullable User paramUser) {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setUser' call is a no-op.", new Object[0]);
    } else {
      getCombinedScopeView().setUser(paramUser);
    } 
  }
  
  public void setFingerprint(@NotNull List<String> paramList) {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setFingerprint' call is a no-op.", new Object[0]);
    } else if (paramList == null) {
      getOptions().getLogger().log(SentryLevel.WARNING, "setFingerprint called with null parameter.", new Object[0]);
    } else {
      getCombinedScopeView().setFingerprint(paramList);
    } 
  }
  
  public void clearBreadcrumbs() {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'clearBreadcrumbs' call is a no-op.", new Object[0]);
    } else {
      getCombinedScopeView().clearBreadcrumbs();
    } 
  }
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setTag' call is a no-op.", new Object[0]);
    } else if (paramString1 == null || paramString2 == null) {
      getOptions().getLogger().log(SentryLevel.WARNING, "setTag called with null parameter.", new Object[0]);
    } else {
      getCombinedScopeView().setTag(paramString1, paramString2);
    } 
  }
  
  public void removeTag(@NotNull String paramString) {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'removeTag' call is a no-op.", new Object[0]);
    } else if (paramString == null) {
      getOptions().getLogger().log(SentryLevel.WARNING, "removeTag called with null parameter.", new Object[0]);
    } else {
      getCombinedScopeView().removeTag(paramString);
    } 
  }
  
  public void setExtra(@NotNull String paramString1, @NotNull String paramString2) {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setExtra' call is a no-op.", new Object[0]);
    } else if (paramString1 == null || paramString2 == null) {
      getOptions().getLogger().log(SentryLevel.WARNING, "setExtra called with null parameter.", new Object[0]);
    } else {
      getCombinedScopeView().setExtra(paramString1, paramString2);
    } 
  }
  
  public void removeExtra(@NotNull String paramString) {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'removeExtra' call is a no-op.", new Object[0]);
    } else if (paramString == null) {
      getOptions().getLogger().log(SentryLevel.WARNING, "removeExtra called with null parameter.", new Object[0]);
    } else {
      getCombinedScopeView().removeExtra(paramString);
    } 
  }
  
  private void updateLastEventId(@NotNull SentryId paramSentryId) {
    getCombinedScopeView().setLastEventId(paramSentryId);
  }
  
  @NotNull
  public SentryId getLastEventId() {
    return getCombinedScopeView().getLastEventId();
  }
  
  public ISentryLifecycleToken pushScope() {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'pushScope' call is a no-op.", new Object[0]);
      return NoOpScopesLifecycleToken.getInstance();
    } 
    IScopes iScopes = forkedCurrentScope("pushScope");
    return iScopes.makeCurrent();
  }
  
  public ISentryLifecycleToken pushIsolationScope() {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'pushIsolationScope' call is a no-op.", new Object[0]);
      return NoOpScopesLifecycleToken.getInstance();
    } 
    IScopes iScopes = forkedScopes("pushIsolationScope");
    return iScopes.makeCurrent();
  }
  
  @NotNull
  public ISentryLifecycleToken makeCurrent() {
    return Sentry.setCurrentScopes(this);
  }
  
  @Deprecated
  public void popScope() {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'popScope' call is a no-op.", new Object[0]);
    } else {
      Scopes scopes = this.parentScopes;
      if (scopes != null)
        scopes.makeCurrent(); 
    } 
  }
  
  public void withScope(@NotNull ScopeCallback paramScopeCallback) {
    if (!isEnabled()) {
      try {
        paramScopeCallback.run(NoOpScope.getInstance());
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'withScope' callback.", throwable);
      } 
    } else {
      IScopes iScopes = forkedCurrentScope("withScope");
      try {
        ISentryLifecycleToken iSentryLifecycleToken = iScopes.makeCurrent();
        try {
          paramScopeCallback.run(iScopes.getScope());
          if (iSentryLifecycleToken != null)
            iSentryLifecycleToken.close(); 
        } catch (Throwable throwable) {
          if (iSentryLifecycleToken != null)
            try {
              iSentryLifecycleToken.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'withScope' callback.", throwable);
      } 
    } 
  }
  
  public void withIsolationScope(@NotNull ScopeCallback paramScopeCallback) {
    if (!isEnabled()) {
      try {
        paramScopeCallback.run(NoOpScope.getInstance());
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'withIsolationScope' callback.", throwable);
      } 
    } else {
      IScopes iScopes = forkedScopes("withIsolationScope");
      try {
        ISentryLifecycleToken iSentryLifecycleToken = iScopes.makeCurrent();
        try {
          paramScopeCallback.run(iScopes.getIsolationScope());
          if (iSentryLifecycleToken != null)
            iSentryLifecycleToken.close(); 
        } catch (Throwable throwable) {
          if (iSentryLifecycleToken != null)
            try {
              iSentryLifecycleToken.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'withIsolationScope' callback.", throwable);
      } 
    } 
  }
  
  public void configureScope(@Nullable ScopeType paramScopeType, @NotNull ScopeCallback paramScopeCallback) {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'configureScope' call is a no-op.", new Object[0]);
    } else {
      try {
        paramScopeCallback.run(this.combinedScope.getSpecificScope(paramScopeType));
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'configureScope' callback.", throwable);
      } 
    } 
  }
  
  public void bindClient(@NotNull ISentryClient paramISentryClient) {
    if (paramISentryClient != null) {
      getOptions().getLogger().log(SentryLevel.DEBUG, "New client bound to scope.", new Object[0]);
      getCombinedScopeView().bindClient(paramISentryClient);
    } else {
      getOptions().getLogger().log(SentryLevel.DEBUG, "NoOp client bound to scope.", new Object[0]);
      getCombinedScopeView().bindClient(NoOpSentryClient.getInstance());
    } 
  }
  
  public boolean isHealthy() {
    return getClient().isHealthy();
  }
  
  public void flush(long paramLong) {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'flush' call is a no-op.", new Object[0]);
    } else {
      try {
        getClient().flush(paramLong);
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'client.flush'.", throwable);
      } 
    } 
  }
  
  @Deprecated
  @NotNull
  public IHub clone() {
    if (!isEnabled())
      getOptions().getLogger().log(SentryLevel.WARNING, "Disabled Scopes cloned.", new Object[0]); 
    return new HubScopesWrapper(forkedScopes("scopes clone"));
  }
  
  @Internal
  @NotNull
  public SentryId captureTransaction(@NotNull SentryTransaction paramSentryTransaction, @Nullable TraceContext paramTraceContext, @Nullable Hint paramHint, @Nullable ProfilingTraceData paramProfilingTraceData) {
    Objects.requireNonNull(paramSentryTransaction, "transaction is required");
    SentryId sentryId = SentryId.EMPTY_ID;
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureTransaction' call is a no-op.", new Object[0]);
    } else if (!paramSentryTransaction.isFinished()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Transaction: %s is not finished and this 'captureTransaction' call is a no-op.", new Object[] { paramSentryTransaction.getEventId() });
    } else if (!Boolean.TRUE.equals(Boolean.valueOf(paramSentryTransaction.isSampled()))) {
      getOptions().getLogger().log(SentryLevel.DEBUG, "Transaction %s was dropped due to sampling decision.", new Object[] { paramSentryTransaction.getEventId() });
      if (getOptions().getBackpressureMonitor().getDownsampleFactor() > 0) {
        getOptions().getClientReportRecorder().recordLostEvent(DiscardReason.BACKPRESSURE, DataCategory.Transaction);
        getOptions().getClientReportRecorder().recordLostEvent(DiscardReason.BACKPRESSURE, DataCategory.Span, (paramSentryTransaction.getSpans().size() + 1));
      } else {
        getOptions().getClientReportRecorder().recordLostEvent(DiscardReason.SAMPLE_RATE, DataCategory.Transaction);
        getOptions().getClientReportRecorder().recordLostEvent(DiscardReason.SAMPLE_RATE, DataCategory.Span, (paramSentryTransaction.getSpans().size() + 1));
      } 
    } else {
      try {
        sentryId = getClient().captureTransaction(paramSentryTransaction, paramTraceContext, getCombinedScopeView(), paramHint, paramProfilingTraceData);
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing transaction with id: " + paramSentryTransaction.getEventId(), throwable);
      } 
    } 
    return sentryId;
  }
  
  @NotNull
  public ITransaction startTransaction(@NotNull TransactionContext paramTransactionContext, @NotNull TransactionOptions paramTransactionOptions) {
    return createTransaction(paramTransactionContext, paramTransactionOptions);
  }
  
  @NotNull
  private ITransaction createTransaction(@NotNull TransactionContext paramTransactionContext, @NotNull TransactionOptions paramTransactionOptions) {
    ITransaction iTransaction;
    Objects.requireNonNull(paramTransactionContext, "transactionContext is required");
    paramTransactionContext.setOrigin(paramTransactionOptions.getOrigin());
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'startTransaction' returns a no-op.", new Object[0]);
      iTransaction = NoOpTransaction.getInstance();
    } else if (SpanUtils.isIgnored(getOptions().getIgnoredSpanOrigins(), paramTransactionContext.getOrigin())) {
      getOptions().getLogger().log(SentryLevel.DEBUG, "Returning no-op for span origin %s as the SDK has been configured to ignore it", new Object[] { paramTransactionContext.getOrigin() });
      iTransaction = NoOpTransaction.getInstance();
    } else if (!getOptions().isTracingEnabled()) {
      getOptions().getLogger().log(SentryLevel.INFO, "Tracing is disabled and this 'startTransaction' returns a no-op.", new Object[0]);
      iTransaction = NoOpTransaction.getInstance();
    } else {
      SamplingContext samplingContext = new SamplingContext(paramTransactionContext, paramTransactionOptions.getCustomSamplingContext());
      TracesSampler tracesSampler = getOptions().getInternalTracesSampler();
      TracesSamplingDecision tracesSamplingDecision = tracesSampler.sample(samplingContext);
      paramTransactionContext.setSamplingDecision(tracesSamplingDecision);
      ISpanFactory iSpanFactory1 = paramTransactionOptions.getSpanFactory();
      ISpanFactory iSpanFactory2 = (iSpanFactory1 == null) ? getOptions().getSpanFactory() : iSpanFactory1;
      iTransaction = iSpanFactory2.createTransaction(paramTransactionContext, this, paramTransactionOptions, this.transactionPerformanceCollector);
      if (tracesSamplingDecision.getSampled().booleanValue() && tracesSamplingDecision.getProfileSampled().booleanValue()) {
        ITransactionProfiler iTransactionProfiler = getOptions().getTransactionProfiler();
        if (!iTransactionProfiler.isRunning()) {
          iTransactionProfiler.start();
          iTransactionProfiler.bindTransaction(iTransaction);
        } else if (paramTransactionOptions.isAppStartTransaction()) {
          iTransactionProfiler.bindTransaction(iTransaction);
        } 
      } 
    } 
    if (paramTransactionOptions.isBindToScope())
      iTransaction.makeCurrent(); 
    return iTransaction;
  }
  
  @Deprecated
  @Nullable
  public SentryTraceHeader traceHeaders() {
    return getTraceparent();
  }
  
  @Internal
  public void setSpanContext(@NotNull Throwable paramThrowable, @NotNull ISpan paramISpan, @NotNull String paramString) {
    getCombinedScopeView().setSpanContext(paramThrowable, paramISpan, paramString);
  }
  
  @Nullable
  public ISpan getSpan() {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'getSpan' call is a no-op.", new Object[0]);
    } else {
      return getOptions().getSpanFactory().retrieveCurrentSpan(getCombinedScopeView());
    } 
    return null;
  }
  
  public void setActiveSpan(@Nullable ISpan paramISpan) {
    getCombinedScopeView().setActiveSpan(paramISpan);
  }
  
  @Internal
  @Nullable
  public ITransaction getTransaction() {
    ITransaction iTransaction = null;
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'getTransaction' call is a no-op.", new Object[0]);
    } else {
      iTransaction = getCombinedScopeView().getTransaction();
    } 
    return iTransaction;
  }
  
  @NotNull
  public SentryOptions getOptions() {
    return this.combinedScope.getOptions();
  }
  
  @Nullable
  public Boolean isCrashedLastRun() {
    return SentryCrashLastRunState.getInstance().isCrashedLastRun(getOptions().getCacheDirPath(), !getOptions().isEnableAutoSessionTracking());
  }
  
  public void reportFullyDisplayed() {
    if (getOptions().isEnableTimeToFullDisplayTracing())
      getOptions().getFullyDisplayedReporter().reportFullyDrawn(); 
  }
  
  @Nullable
  public TransactionContext continueTrace(@Nullable String paramString, @Nullable List<String> paramList) {
    PropagationContext propagationContext = PropagationContext.fromHeaders(getOptions().getLogger(), paramString, paramList);
    configureScope(paramIScope -> paramIScope.setPropagationContext(paramPropagationContext));
    return getOptions().isTracingEnabled() ? TransactionContext.fromPropagationContext(propagationContext) : null;
  }
  
  @Nullable
  public SentryTraceHeader getTraceparent() {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'getTraceparent' call is a no-op.", new Object[0]);
    } else {
      TracingUtils.TracingHeaders tracingHeaders = TracingUtils.trace(this, null, getSpan());
      if (tracingHeaders != null)
        return tracingHeaders.getSentryTraceHeader(); 
    } 
    return null;
  }
  
  @Nullable
  public BaggageHeader getBaggage() {
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'getBaggage' call is a no-op.", new Object[0]);
    } else {
      TracingUtils.TracingHeaders tracingHeaders = TracingUtils.trace(this, null, getSpan());
      if (tracingHeaders != null)
        return tracingHeaders.getBaggageHeader(); 
    } 
    return null;
  }
  
  @Experimental
  @NotNull
  public SentryId captureCheckIn(@NotNull CheckIn paramCheckIn) {
    SentryId sentryId = SentryId.EMPTY_ID;
    if (!isEnabled()) {
      getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureCheckIn' call is a no-op.", new Object[0]);
    } else {
      try {
        sentryId = getClient().captureCheckIn(paramCheckIn, getCombinedScopeView(), null);
      } catch (Throwable throwable) {
        getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing check-in for slug", throwable);
      } 
    } 
    updateLastEventId(sentryId);
    return sentryId;
  }
  
  @Internal
  @Nullable
  public RateLimiter getRateLimiter() {
    return getClient().getRateLimiter();
  }
  
  @NotNull
  public MetricsApi metrics() {
    return this.metricsApi;
  }
  
  @NotNull
  public IMetricsAggregator getMetricsAggregator() {
    return getClient().getMetricsAggregator();
  }
  
  @NotNull
  public Map<String, String> getDefaultTagsForMetrics() {
    if (!getOptions().isEnableDefaultTagsForMetrics())
      return Collections.emptyMap(); 
    HashMap<Object, Object> hashMap = new HashMap<>();
    String str1 = getOptions().getRelease();
    if (str1 != null)
      hashMap.put("release", str1); 
    String str2 = getOptions().getEnvironment();
    if (str2 != null)
      hashMap.put("environment", str2); 
    String str3 = getCombinedScopeView().getTransactionName();
    if (str3 != null)
      hashMap.put("transaction", str3); 
    return (Map)Collections.unmodifiableMap(hashMap);
  }
  
  @Nullable
  public ISpan startSpanForMetric(@NotNull String paramString1, @NotNull String paramString2) {
    ISpan iSpan = getSpan();
    return (iSpan != null) ? iSpan.startChild(paramString1, paramString2) : null;
  }
  
  @Nullable
  public LocalMetricsAggregator getLocalMetricsAggregator() {
    if (!getOptions().isEnableSpanLocalMetricAggregation())
      return null; 
    ISpan iSpan = getSpan();
    return (iSpan != null) ? iSpan.getLocalMetricsAggregator() : null;
  }
  
  private static void validateOptions(@NotNull SentryOptions paramSentryOptions) {
    Objects.requireNonNull(paramSentryOptions, "SentryOptions is required.");
    if (paramSentryOptions.getDsn() == null || paramSentryOptions.getDsn().isEmpty())
      throw new IllegalArgumentException("Scopes requires a DSN to be instantiated. Considering using the NoOpScopes if no DSN is available."); 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\Scopes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
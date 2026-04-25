package io.sentry;

import io.sentry.metrics.LocalMetricsAggregator;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.TransactionNameSource;
import io.sentry.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@Internal
public final class SentryTracer implements ITransaction {
  @NotNull
  private final SentryId eventId = new SentryId();
  
  @NotNull
  private final Span root;
  
  @NotNull
  private final List<Span> children = new CopyOnWriteArrayList<>();
  
  @NotNull
  private final IScopes scopes;
  
  @NotNull
  private String name;
  
  @NotNull
  private FinishStatus finishStatus = FinishStatus.NOT_FINISHED;
  
  @Nullable
  private volatile TimerTask idleTimeoutTask;
  
  @Nullable
  private volatile TimerTask deadlineTimeoutTask;
  
  @Nullable
  private volatile Timer timer = null;
  
  @NotNull
  private final Object timerLock = new Object();
  
  @NotNull
  private final AtomicBoolean isIdleFinishTimerRunning = new AtomicBoolean(false);
  
  @NotNull
  private final AtomicBoolean isDeadlineTimerRunning = new AtomicBoolean(false);
  
  @NotNull
  private final Baggage baggage;
  
  @NotNull
  private TransactionNameSource transactionNameSource;
  
  @NotNull
  private final Instrumenter instrumenter;
  
  @NotNull
  private final Contexts contexts = new Contexts();
  
  @Nullable
  private final TransactionPerformanceCollector transactionPerformanceCollector;
  
  @NotNull
  private final TransactionOptions transactionOptions;
  
  public SentryTracer(@NotNull TransactionContext paramTransactionContext, @NotNull IScopes paramIScopes) {
    this(paramTransactionContext, paramIScopes, new TransactionOptions(), null);
  }
  
  public SentryTracer(@NotNull TransactionContext paramTransactionContext, @NotNull IScopes paramIScopes, @NotNull TransactionOptions paramTransactionOptions) {
    this(paramTransactionContext, paramIScopes, paramTransactionOptions, null);
  }
  
  SentryTracer(@NotNull TransactionContext paramTransactionContext, @NotNull IScopes paramIScopes, @NotNull TransactionOptions paramTransactionOptions, @Nullable TransactionPerformanceCollector paramTransactionPerformanceCollector) {
    Objects.requireNonNull(paramTransactionContext, "context is required");
    Objects.requireNonNull(paramIScopes, "scopes are required");
    this.root = new Span(paramTransactionContext, this, paramIScopes, paramTransactionOptions);
    this.name = paramTransactionContext.getName();
    this.instrumenter = paramTransactionContext.getInstrumenter();
    this.scopes = paramIScopes;
    this.transactionPerformanceCollector = paramTransactionPerformanceCollector;
    this.transactionNameSource = paramTransactionContext.getTransactionNameSource();
    this.transactionOptions = paramTransactionOptions;
    if (paramTransactionContext.getBaggage() != null) {
      this.baggage = paramTransactionContext.getBaggage();
    } else {
      this.baggage = new Baggage(paramIScopes.getOptions().getLogger());
    } 
    if (paramTransactionPerformanceCollector != null)
      paramTransactionPerformanceCollector.start(this); 
    if (paramTransactionOptions.getIdleTimeout() != null || paramTransactionOptions.getDeadlineTimeout() != null) {
      this.timer = new Timer(true);
      scheduleDeadlineTimeout();
      scheduleFinish();
    } 
  }
  
  public void scheduleFinish() {
    synchronized (this.timerLock) {
      if (this.timer != null) {
        Long long_ = this.transactionOptions.getIdleTimeout();
        if (long_ != null) {
          cancelIdleTimer();
          this.isIdleFinishTimerRunning.set(true);
          this.idleTimeoutTask = new TimerTask() {
              public void run() {
                SentryTracer.this.onIdleTimeoutReached();
              }
            };
          try {
            this.timer.schedule(this.idleTimeoutTask, long_.longValue());
          } catch (Throwable throwable) {
            this.scopes.getOptions().getLogger().log(SentryLevel.WARNING, "Failed to schedule finish timer", throwable);
            onIdleTimeoutReached();
          } 
        } 
      } 
    } 
  }
  
  private void onIdleTimeoutReached() {
    SpanStatus spanStatus = getStatus();
    finish((spanStatus != null) ? spanStatus : SpanStatus.OK);
    this.isIdleFinishTimerRunning.set(false);
  }
  
  private void onDeadlineTimeoutReached() {
    SpanStatus spanStatus = getStatus();
    forceFinish((spanStatus != null) ? spanStatus : SpanStatus.DEADLINE_EXCEEDED, (this.transactionOptions.getIdleTimeout() != null), null);
    this.isDeadlineTimerRunning.set(false);
  }
  
  @NotNull
  public void forceFinish(@NotNull SpanStatus paramSpanStatus, boolean paramBoolean, @Nullable Hint paramHint) {
    if (isFinished())
      return; 
    SentryDate sentryDate = this.scopes.getOptions().getDateProvider().now();
    ListIterator<Span> listIterator = this.children.listIterator(this.children.size());
    while (listIterator.hasPrevious()) {
      Span span = listIterator.previous();
      span.setSpanFinishedCallback(null);
      span.finish(paramSpanStatus, sentryDate);
    } 
    finish(paramSpanStatus, sentryDate, paramBoolean, paramHint);
  }
  
  public void finish(@Nullable SpanStatus paramSpanStatus, @Nullable SentryDate paramSentryDate, boolean paramBoolean, @Nullable Hint paramHint) {
    SentryDate sentryDate = this.root.getFinishDate();
    if (paramSentryDate != null)
      sentryDate = paramSentryDate; 
    if (sentryDate == null)
      sentryDate = this.scopes.getOptions().getDateProvider().now(); 
    for (Span span : this.children) {
      if (span.getOptions().isIdle())
        span.finish((paramSpanStatus != null) ? paramSpanStatus : (getSpanContext()).status, sentryDate); 
    } 
    this.finishStatus = FinishStatus.finishing(paramSpanStatus);
    if (!this.root.isFinished() && (!this.transactionOptions.isWaitForChildren() || hasAllChildrenFinished())) {
      AtomicReference<List<PerformanceCollectionData>> atomicReference = new AtomicReference();
      SpanFinishedCallback spanFinishedCallback = this.root.getSpanFinishedCallback();
      this.root.setSpanFinishedCallback(paramSpan -> {
            if (paramSpanFinishedCallback != null)
              paramSpanFinishedCallback.execute(paramSpan); 
            TransactionFinishedCallback transactionFinishedCallback = this.transactionOptions.getTransactionFinishedCallback();
            if (transactionFinishedCallback != null)
              transactionFinishedCallback.execute(this); 
            if (this.transactionPerformanceCollector != null)
              paramAtomicReference.set(this.transactionPerformanceCollector.stop(this)); 
          });
      this.root.finish(this.finishStatus.spanStatus, sentryDate);
      ProfilingTraceData profilingTraceData = null;
      if (Boolean.TRUE.equals(isSampled()) && Boolean.TRUE.equals(isProfileSampled()))
        profilingTraceData = this.scopes.getOptions().getTransactionProfiler().onTransactionFinish(this, atomicReference.get(), this.scopes.getOptions()); 
      if (atomicReference.get() != null)
        ((List)atomicReference.get()).clear(); 
      this.scopes.configureScope(paramIScope -> paramIScope.withTransaction(()));
      SentryTransaction sentryTransaction = new SentryTransaction(this);
      if (this.timer != null)
        synchronized (this.timerLock) {
          if (this.timer != null) {
            cancelIdleTimer();
            cancelDeadlineTimer();
            this.timer.cancel();
            this.timer = null;
          } 
        }  
      if (paramBoolean && this.children.isEmpty() && this.transactionOptions.getIdleTimeout() != null) {
        this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "Dropping idle transaction %s because it has no child spans", new Object[] { this.name });
        return;
      } 
      sentryTransaction.getMeasurements().putAll(this.root.getMeasurements());
      this.scopes.captureTransaction(sentryTransaction, traceContext(), paramHint, profilingTraceData);
    } 
  }
  
  private void cancelIdleTimer() {
    synchronized (this.timerLock) {
      if (this.idleTimeoutTask != null) {
        this.idleTimeoutTask.cancel();
        this.isIdleFinishTimerRunning.set(false);
        this.idleTimeoutTask = null;
      } 
    } 
  }
  
  private void scheduleDeadlineTimeout() {
    Long long_ = this.transactionOptions.getDeadlineTimeout();
    if (long_ != null)
      synchronized (this.timerLock) {
        if (this.timer != null) {
          cancelDeadlineTimer();
          this.isDeadlineTimerRunning.set(true);
          this.deadlineTimeoutTask = new TimerTask() {
              public void run() {
                SentryTracer.this.onDeadlineTimeoutReached();
              }
            };
          try {
            this.timer.schedule(this.deadlineTimeoutTask, long_.longValue());
          } catch (Throwable throwable) {
            this.scopes.getOptions().getLogger().log(SentryLevel.WARNING, "Failed to schedule finish timer", throwable);
            onDeadlineTimeoutReached();
          } 
        } 
      }  
  }
  
  private void cancelDeadlineTimer() {
    synchronized (this.timerLock) {
      if (this.deadlineTimeoutTask != null) {
        this.deadlineTimeoutTask.cancel();
        this.isDeadlineTimerRunning.set(false);
        this.deadlineTimeoutTask = null;
      } 
    } 
  }
  
  @NotNull
  public List<Span> getChildren() {
    return this.children;
  }
  
  @NotNull
  public SentryDate getStartDate() {
    return this.root.getStartDate();
  }
  
  @Nullable
  public SentryDate getFinishDate() {
    return this.root.getFinishDate();
  }
  
  @NotNull
  ISpan startChild(@NotNull SpanId paramSpanId, @NotNull String paramString1, @Nullable String paramString2) {
    return startChild(paramSpanId, paramString1, paramString2, new SpanOptions());
  }
  
  @NotNull
  ISpan startChild(@NotNull SpanId paramSpanId, @NotNull String paramString1, @Nullable String paramString2, @NotNull SpanOptions paramSpanOptions) {
    return createChild(paramSpanId, paramString1, paramString2, paramSpanOptions);
  }
  
  @NotNull
  ISpan startChild(@NotNull SpanId paramSpanId, @NotNull String paramString1, @Nullable String paramString2, @Nullable SentryDate paramSentryDate, @NotNull Instrumenter paramInstrumenter) {
    SpanContext spanContext = getSpanContext().copyForChild(paramString1, paramSpanId, null);
    spanContext.setDescription(paramString2);
    spanContext.setInstrumenter(paramInstrumenter);
    SpanOptions spanOptions = new SpanOptions();
    spanOptions.setStartTimestamp(paramSentryDate);
    return createChild(spanContext, spanOptions);
  }
  
  @NotNull
  ISpan startChild(@NotNull SpanId paramSpanId, @NotNull String paramString1, @Nullable String paramString2, @Nullable SentryDate paramSentryDate, @NotNull Instrumenter paramInstrumenter, @NotNull SpanOptions paramSpanOptions) {
    SpanContext spanContext = getSpanContext().copyForChild(paramString1, paramSpanId, null);
    spanContext.setDescription(paramString2);
    spanContext.setInstrumenter(paramInstrumenter);
    paramSpanOptions.setStartTimestamp(paramSentryDate);
    return createChild(spanContext, paramSpanOptions);
  }
  
  @NotNull
  private ISpan createChild(@NotNull SpanId paramSpanId, @NotNull String paramString1, @Nullable String paramString2, @NotNull SpanOptions paramSpanOptions) {
    SpanContext spanContext = getSpanContext().copyForChild(paramString1, paramSpanId, null);
    spanContext.setDescription(paramString2);
    spanContext.setInstrumenter(Instrumenter.SENTRY);
    return createChild(spanContext, paramSpanOptions);
  }
  
  @NotNull
  private ISpan createChild(@NotNull SpanContext paramSpanContext, @NotNull SpanOptions paramSpanOptions) {
    if (this.root.isFinished())
      return NoOpSpan.getInstance(); 
    if (!this.instrumenter.equals(paramSpanContext.getInstrumenter()))
      return NoOpSpan.getInstance(); 
    SpanId spanId = paramSpanContext.getParentSpanId();
    String str1 = paramSpanContext.getOperation();
    String str2 = paramSpanContext.getDescription();
    if (this.children.size() < this.scopes.getOptions().getMaxSpans()) {
      Objects.requireNonNull(spanId, "parentSpanId is required");
      cancelIdleTimer();
      Span span = new Span(this, this.scopes, paramSpanContext, paramSpanOptions, paramSpan -> {
            if (this.transactionPerformanceCollector != null)
              this.transactionPerformanceCollector.onSpanFinished(paramSpan); 
            FinishStatus finishStatus = this.finishStatus;
            if (this.transactionOptions.getIdleTimeout() != null) {
              if (!this.transactionOptions.isWaitForChildren() || hasAllChildrenFinished())
                scheduleFinish(); 
            } else if (finishStatus.isFinishing) {
              finish(finishStatus.spanStatus);
            } 
          });
      span.setData("thread.id", String.valueOf(Thread.currentThread().getId()));
      span.setData("thread.name", this.scopes.getOptions().getMainThreadChecker().isMainThread() ? "main" : Thread.currentThread().getName());
      this.children.add(span);
      if (this.transactionPerformanceCollector != null)
        this.transactionPerformanceCollector.onSpanStarted(span); 
      return span;
    } 
    this.scopes.getOptions().getLogger().log(SentryLevel.WARNING, "Span operation: %s, description: %s dropped due to limit reached. Returning NoOpSpan.", new Object[] { str1, str2 });
    return NoOpSpan.getInstance();
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString) {
    return startChild(paramString, (String)null);
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString1, @Nullable String paramString2, @Nullable SentryDate paramSentryDate, @NotNull Instrumenter paramInstrumenter) {
    return startChild(paramString1, paramString2, paramSentryDate, paramInstrumenter, new SpanOptions());
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString1, @Nullable String paramString2, @Nullable SentryDate paramSentryDate, @NotNull Instrumenter paramInstrumenter, @NotNull SpanOptions paramSpanOptions) {
    return createChild(paramString1, paramString2, paramSentryDate, paramInstrumenter, paramSpanOptions);
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString1, @Nullable String paramString2, @Nullable SentryDate paramSentryDate) {
    return createChild(paramString1, paramString2, paramSentryDate, Instrumenter.SENTRY, new SpanOptions());
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString1, @Nullable String paramString2) {
    return startChild(paramString1, paramString2, (SentryDate)null, Instrumenter.SENTRY, new SpanOptions());
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString1, @Nullable String paramString2, @NotNull SpanOptions paramSpanOptions) {
    return createChild(paramString1, paramString2, null, Instrumenter.SENTRY, paramSpanOptions);
  }
  
  @NotNull
  public ISpan startChild(@NotNull SpanContext paramSpanContext, @NotNull SpanOptions paramSpanOptions) {
    return createChild(paramSpanContext, paramSpanOptions);
  }
  
  @NotNull
  private ISpan createChild(@NotNull String paramString1, @Nullable String paramString2, @Nullable SentryDate paramSentryDate, @NotNull Instrumenter paramInstrumenter, @NotNull SpanOptions paramSpanOptions) {
    if (this.root.isFinished())
      return NoOpSpan.getInstance(); 
    if (!this.instrumenter.equals(paramInstrumenter))
      return NoOpSpan.getInstance(); 
    if (this.children.size() < this.scopes.getOptions().getMaxSpans())
      return this.root.startChild(paramString1, paramString2, paramSentryDate, paramInstrumenter, paramSpanOptions); 
    this.scopes.getOptions().getLogger().log(SentryLevel.WARNING, "Span operation: %s, description: %s dropped due to limit reached. Returning NoOpSpan.", new Object[] { paramString1, paramString2 });
    return NoOpSpan.getInstance();
  }
  
  @NotNull
  public SentryTraceHeader toSentryTrace() {
    return this.root.toSentryTrace();
  }
  
  public void finish() {
    finish(getStatus());
  }
  
  public void finish(@Nullable SpanStatus paramSpanStatus) {
    finish(paramSpanStatus, null);
  }
  
  @Internal
  public void finish(@Nullable SpanStatus paramSpanStatus, @Nullable SentryDate paramSentryDate) {
    finish(paramSpanStatus, paramSentryDate, true, null);
  }
  
  @Nullable
  public TraceContext traceContext() {
    if (this.scopes.getOptions().isTraceSampling()) {
      updateBaggageValues();
      return this.baggage.toTraceContext();
    } 
    return null;
  }
  
  private void updateBaggageValues() {
    synchronized (this) {
      if (this.baggage.isMutable()) {
        this.baggage.setValuesFromTransaction(getSpanContext().getTraceId(), this.scopes.getOptions(), getSamplingDecision(), getName(), getTransactionNameSource());
        this.baggage.freeze();
      } 
    } 
  }
  
  @Nullable
  public BaggageHeader toBaggageHeader(@Nullable List<String> paramList) {
    if (this.scopes.getOptions().isTraceSampling()) {
      updateBaggageValues();
      return BaggageHeader.fromBaggageAndOutgoingHeader(this.baggage, paramList);
    } 
    return null;
  }
  
  private boolean hasAllChildrenFinished() {
    ArrayList<Span> arrayList = new ArrayList<>(this.children);
    if (!arrayList.isEmpty())
      for (Span span : arrayList) {
        if (!span.isFinished() && span.getFinishDate() == null)
          return false; 
      }  
    return true;
  }
  
  public void setOperation(@NotNull String paramString) {
    if (this.root.isFinished()) {
      this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Operation %s cannot be set", new Object[] { paramString });
      return;
    } 
    this.root.setOperation(paramString);
  }
  
  @NotNull
  public String getOperation() {
    return this.root.getOperation();
  }
  
  public void setDescription(@Nullable String paramString) {
    if (this.root.isFinished()) {
      this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Description %s cannot be set", new Object[] { paramString });
      return;
    } 
    this.root.setDescription(paramString);
  }
  
  @Nullable
  public String getDescription() {
    return this.root.getDescription();
  }
  
  public void setStatus(@Nullable SpanStatus paramSpanStatus) {
    if (this.root.isFinished()) {
      this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Status %s cannot be set", new Object[] { (paramSpanStatus == null) ? "null" : paramSpanStatus.name() });
      return;
    } 
    this.root.setStatus(paramSpanStatus);
  }
  
  @Nullable
  public SpanStatus getStatus() {
    return this.root.getStatus();
  }
  
  public void setThrowable(@Nullable Throwable paramThrowable) {
    if (this.root.isFinished()) {
      this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Throwable cannot be set", new Object[0]);
      return;
    } 
    this.root.setThrowable(paramThrowable);
  }
  
  @Nullable
  public Throwable getThrowable() {
    return this.root.getThrowable();
  }
  
  @NotNull
  public SpanContext getSpanContext() {
    return this.root.getSpanContext();
  }
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {
    if (this.root.isFinished()) {
      this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Tag %s cannot be set", new Object[] { paramString1 });
      return;
    } 
    this.root.setTag(paramString1, paramString2);
  }
  
  @Nullable
  public String getTag(@NotNull String paramString) {
    return this.root.getTag(paramString);
  }
  
  public boolean isFinished() {
    return this.root.isFinished();
  }
  
  public void setData(@NotNull String paramString, @NotNull Object paramObject) {
    if (this.root.isFinished()) {
      this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Data %s cannot be set", new Object[] { paramString });
      return;
    } 
    this.root.setData(paramString, paramObject);
  }
  
  @Nullable
  public Object getData(@NotNull String paramString) {
    return this.root.getData(paramString);
  }
  
  @Internal
  public void setMeasurementFromChild(@NotNull String paramString, @NotNull Number paramNumber) {
    if (!this.root.getMeasurements().containsKey(paramString))
      setMeasurement(paramString, paramNumber); 
  }
  
  @Internal
  public void setMeasurementFromChild(@NotNull String paramString, @NotNull Number paramNumber, @NotNull MeasurementUnit paramMeasurementUnit) {
    if (!this.root.getMeasurements().containsKey(paramString))
      setMeasurement(paramString, paramNumber, paramMeasurementUnit); 
  }
  
  public void setMeasurement(@NotNull String paramString, @NotNull Number paramNumber) {
    this.root.setMeasurement(paramString, paramNumber);
  }
  
  public void setMeasurement(@NotNull String paramString, @NotNull Number paramNumber, @NotNull MeasurementUnit paramMeasurementUnit) {
    this.root.setMeasurement(paramString, paramNumber, paramMeasurementUnit);
  }
  
  @Nullable
  public Map<String, Object> getData() {
    return this.root.getData();
  }
  
  @Nullable
  public Boolean isSampled() {
    return this.root.isSampled();
  }
  
  @Nullable
  public Boolean isProfileSampled() {
    return this.root.isProfileSampled();
  }
  
  @Nullable
  public TracesSamplingDecision getSamplingDecision() {
    return this.root.getSamplingDecision();
  }
  
  public void setName(@NotNull String paramString) {
    setName(paramString, TransactionNameSource.CUSTOM);
  }
  
  @Internal
  public void setName(@NotNull String paramString, @NotNull TransactionNameSource paramTransactionNameSource) {
    if (this.root.isFinished()) {
      this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The transaction is already finished. Name %s cannot be set", new Object[] { paramString });
      return;
    } 
    this.name = paramString;
    this.transactionNameSource = paramTransactionNameSource;
  }
  
  @NotNull
  public String getName() {
    return this.name;
  }
  
  @NotNull
  public TransactionNameSource getTransactionNameSource() {
    return this.transactionNameSource;
  }
  
  @NotNull
  public List<Span> getSpans() {
    return this.children;
  }
  
  @Nullable
  public ISpan getLatestActiveSpan() {
    ArrayList<Span> arrayList = new ArrayList<>(this.children);
    if (!arrayList.isEmpty())
      for (int i = arrayList.size() - 1; i >= 0; i--) {
        if (!((Span)arrayList.get(i)).isFinished())
          return arrayList.get(i); 
      }  
    return null;
  }
  
  @NotNull
  public SentryId getEventId() {
    return this.eventId;
  }
  
  @Internal
  @NotNull
  public ISentryLifecycleToken makeCurrent() {
    this.scopes.configureScope(paramIScope -> paramIScope.setTransaction(this));
    return NoOpScopesLifecycleToken.getInstance();
  }
  
  @NotNull
  Span getRoot() {
    return this.root;
  }
  
  @TestOnly
  @Nullable
  TimerTask getIdleTimeoutTask() {
    return this.idleTimeoutTask;
  }
  
  @TestOnly
  @Nullable
  TimerTask getDeadlineTimeoutTask() {
    return this.deadlineTimeoutTask;
  }
  
  @TestOnly
  @Nullable
  Timer getTimer() {
    return this.timer;
  }
  
  @TestOnly
  @NotNull
  AtomicBoolean isFinishTimerRunning() {
    return this.isIdleFinishTimerRunning;
  }
  
  @TestOnly
  @NotNull
  AtomicBoolean isDeadlineTimerRunning() {
    return this.isDeadlineTimerRunning;
  }
  
  @Internal
  public void setContext(@NotNull String paramString, @NotNull Object paramObject) {
    this.contexts.put(paramString, paramObject);
  }
  
  @Internal
  @NotNull
  public Contexts getContexts() {
    return this.contexts;
  }
  
  public boolean updateEndDate(@NotNull SentryDate paramSentryDate) {
    return this.root.updateEndDate(paramSentryDate);
  }
  
  public boolean isNoOp() {
    return false;
  }
  
  @Nullable
  public LocalMetricsAggregator getLocalMetricsAggregator() {
    return this.root.getLocalMetricsAggregator();
  }
  
  private static final class FinishStatus {
    static final FinishStatus NOT_FINISHED = notFinished();
    
    private final boolean isFinishing;
    
    @Nullable
    private final SpanStatus spanStatus;
    
    @NotNull
    static FinishStatus finishing(@Nullable SpanStatus param1SpanStatus) {
      return new FinishStatus(true, param1SpanStatus);
    }
    
    @NotNull
    private static FinishStatus notFinished() {
      return new FinishStatus(false, null);
    }
    
    private FinishStatus(boolean param1Boolean, @Nullable SpanStatus param1SpanStatus) {
      this.isFinishing = param1Boolean;
      this.spanStatus = param1SpanStatus;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryTracer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
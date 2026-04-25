package io.sentry;

import io.sentry.metrics.LocalMetricsAggregator;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.MeasurementValue;
import io.sentry.protocol.SentryId;
import io.sentry.util.LazyEvaluator;
import io.sentry.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class Span implements ISpan {
  @NotNull
  private SentryDate startTimestamp;
  
  @Nullable
  private SentryDate timestamp;
  
  @NotNull
  private final SpanContext context;
  
  @NotNull
  private final SentryTracer transaction;
  
  @Nullable
  private Throwable throwable;
  
  @NotNull
  private final IScopes scopes;
  
  private boolean finished = false;
  
  @NotNull
  private final AtomicBoolean isFinishing = new AtomicBoolean(false);
  
  @NotNull
  private final SpanOptions options;
  
  @Nullable
  private SpanFinishedCallback spanFinishedCallback;
  
  @NotNull
  private final Map<String, Object> data = new ConcurrentHashMap<>();
  
  @NotNull
  private final Map<String, MeasurementValue> measurements = new ConcurrentHashMap<>();
  
  @NotNull
  private final Contexts contexts = new Contexts();
  
  @NotNull
  private final LazyEvaluator<LocalMetricsAggregator> metricsAggregator = new LazyEvaluator(() -> new LocalMetricsAggregator());
  
  Span(@NotNull SentryTracer paramSentryTracer, @NotNull IScopes paramIScopes, @NotNull SpanContext paramSpanContext, @NotNull SpanOptions paramSpanOptions, @Nullable SpanFinishedCallback paramSpanFinishedCallback) {
    this.context = paramSpanContext;
    this.context.setOrigin(paramSpanOptions.getOrigin());
    this.transaction = (SentryTracer)Objects.requireNonNull(paramSentryTracer, "transaction is required");
    this.scopes = (IScopes)Objects.requireNonNull(paramIScopes, "Scopes are required");
    this.options = paramSpanOptions;
    this.spanFinishedCallback = paramSpanFinishedCallback;
    SentryDate sentryDate = paramSpanOptions.getStartTimestamp();
    if (sentryDate != null) {
      this.startTimestamp = sentryDate;
    } else {
      this.startTimestamp = paramIScopes.getOptions().getDateProvider().now();
    } 
  }
  
  public Span(@NotNull TransactionContext paramTransactionContext, @NotNull SentryTracer paramSentryTracer, @NotNull IScopes paramIScopes, @NotNull SpanOptions paramSpanOptions) {
    this.context = (SpanContext)Objects.requireNonNull(paramTransactionContext, "context is required");
    this.context.setOrigin(paramSpanOptions.getOrigin());
    this.transaction = (SentryTracer)Objects.requireNonNull(paramSentryTracer, "sentryTracer is required");
    this.scopes = (IScopes)Objects.requireNonNull(paramIScopes, "scopes are required");
    this.spanFinishedCallback = null;
    SentryDate sentryDate = paramSpanOptions.getStartTimestamp();
    if (sentryDate != null) {
      this.startTimestamp = sentryDate;
    } else {
      this.startTimestamp = paramIScopes.getOptions().getDateProvider().now();
    } 
    this.options = paramSpanOptions;
  }
  
  @NotNull
  public SentryDate getStartDate() {
    return this.startTimestamp;
  }
  
  @Nullable
  public SentryDate getFinishDate() {
    return this.timestamp;
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString) {
    return startChild(paramString, (String)null);
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString1, @Nullable String paramString2, @Nullable SentryDate paramSentryDate, @NotNull Instrumenter paramInstrumenter, @NotNull SpanOptions paramSpanOptions) {
    return this.finished ? NoOpSpan.getInstance() : this.transaction.startChild(this.context.getSpanId(), paramString1, paramString2, paramSentryDate, paramInstrumenter, paramSpanOptions);
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString1, @Nullable String paramString2) {
    return this.finished ? NoOpSpan.getInstance() : this.transaction.startChild(this.context.getSpanId(), paramString1, paramString2);
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString1, @Nullable String paramString2, @NotNull SpanOptions paramSpanOptions) {
    return this.finished ? NoOpSpan.getInstance() : this.transaction.startChild(this.context.getSpanId(), paramString1, paramString2, paramSpanOptions);
  }
  
  @NotNull
  public ISpan startChild(@NotNull SpanContext paramSpanContext, @NotNull SpanOptions paramSpanOptions) {
    return this.transaction.startChild(paramSpanContext, paramSpanOptions);
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString1, @Nullable String paramString2, @Nullable SentryDate paramSentryDate, @NotNull Instrumenter paramInstrumenter) {
    return startChild(paramString1, paramString2, paramSentryDate, paramInstrumenter, new SpanOptions());
  }
  
  @NotNull
  public SentryTraceHeader toSentryTrace() {
    return new SentryTraceHeader(this.context.getTraceId(), this.context.getSpanId(), this.context.getSampled());
  }
  
  @Nullable
  public TraceContext traceContext() {
    return this.transaction.traceContext();
  }
  
  @Nullable
  public BaggageHeader toBaggageHeader(@Nullable List<String> paramList) {
    return this.transaction.toBaggageHeader(paramList);
  }
  
  public void finish() {
    finish(this.context.getStatus());
  }
  
  public void finish(@Nullable SpanStatus paramSpanStatus) {
    finish(paramSpanStatus, this.scopes.getOptions().getDateProvider().now());
  }
  
  public void finish(@Nullable SpanStatus paramSpanStatus, @Nullable SentryDate paramSentryDate) {
    if (this.finished || !this.isFinishing.compareAndSet(false, true))
      return; 
    this.context.setStatus(paramSpanStatus);
    this.timestamp = (paramSentryDate == null) ? this.scopes.getOptions().getDateProvider().now() : paramSentryDate;
    if (this.options.isTrimStart() || this.options.isTrimEnd()) {
      SentryDate sentryDate1 = null;
      SentryDate sentryDate2 = null;
      List<Span> list = this.transaction.getRoot().getSpanId().equals(getSpanId()) ? this.transaction.getChildren() : getDirectChildren();
      for (Span span : list) {
        if (sentryDate1 == null || span.getStartDate().isBefore(sentryDate1))
          sentryDate1 = span.getStartDate(); 
        if (sentryDate2 == null || (span.getFinishDate() != null && span.getFinishDate().isAfter(sentryDate2)))
          sentryDate2 = span.getFinishDate(); 
      } 
      if (this.options.isTrimStart() && sentryDate1 != null && this.startTimestamp.isBefore(sentryDate1))
        updateStartDate(sentryDate1); 
      if (this.options.isTrimEnd() && sentryDate2 != null && (this.timestamp == null || this.timestamp.isAfter(sentryDate2)))
        updateEndDate(sentryDate2); 
    } 
    if (this.throwable != null)
      this.scopes.setSpanContext(this.throwable, this, this.transaction.getName()); 
    if (this.spanFinishedCallback != null)
      this.spanFinishedCallback.execute(this); 
    this.finished = true;
  }
  
  public void setOperation(@NotNull String paramString) {
    this.context.setOperation(paramString);
  }
  
  @NotNull
  public String getOperation() {
    return this.context.getOperation();
  }
  
  public void setDescription(@Nullable String paramString) {
    this.context.setDescription(paramString);
  }
  
  @Nullable
  public String getDescription() {
    return this.context.getDescription();
  }
  
  public void setStatus(@Nullable SpanStatus paramSpanStatus) {
    this.context.setStatus(paramSpanStatus);
  }
  
  @Nullable
  public SpanStatus getStatus() {
    return this.context.getStatus();
  }
  
  @NotNull
  public SpanContext getSpanContext() {
    return this.context;
  }
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {
    this.context.setTag(paramString1, paramString2);
  }
  
  @Nullable
  public String getTag(@NotNull String paramString) {
    return this.context.getTags().get(paramString);
  }
  
  public boolean isFinished() {
    return this.finished;
  }
  
  @NotNull
  public Map<String, Object> getData() {
    return this.data;
  }
  
  @Nullable
  public Boolean isSampled() {
    return this.context.getSampled();
  }
  
  @Nullable
  public Boolean isProfileSampled() {
    return this.context.getProfileSampled();
  }
  
  @Nullable
  public TracesSamplingDecision getSamplingDecision() {
    return this.context.getSamplingDecision();
  }
  
  public void setThrowable(@Nullable Throwable paramThrowable) {
    this.throwable = paramThrowable;
  }
  
  @Nullable
  public Throwable getThrowable() {
    return this.throwable;
  }
  
  @NotNull
  public SentryId getTraceId() {
    return this.context.getTraceId();
  }
  
  @NotNull
  public SpanId getSpanId() {
    return this.context.getSpanId();
  }
  
  @Nullable
  public SpanId getParentSpanId() {
    return this.context.getParentSpanId();
  }
  
  public Map<String, String> getTags() {
    return this.context.getTags();
  }
  
  public void setData(@NotNull String paramString, @NotNull Object paramObject) {
    this.data.put(paramString, paramObject);
  }
  
  @Nullable
  public Object getData(@NotNull String paramString) {
    return this.data.get(paramString);
  }
  
  public void setMeasurement(@NotNull String paramString, @NotNull Number paramNumber) {
    if (isFinished()) {
      this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The span is already finished. Measurement %s cannot be set", new Object[] { paramString });
      return;
    } 
    this.measurements.put(paramString, new MeasurementValue(paramNumber, null));
    if (this.transaction.getRoot() != this)
      this.transaction.setMeasurementFromChild(paramString, paramNumber); 
  }
  
  public void setMeasurement(@NotNull String paramString, @NotNull Number paramNumber, @NotNull MeasurementUnit paramMeasurementUnit) {
    if (isFinished()) {
      this.scopes.getOptions().getLogger().log(SentryLevel.DEBUG, "The span is already finished. Measurement %s cannot be set", new Object[] { paramString });
      return;
    } 
    this.measurements.put(paramString, new MeasurementValue(paramNumber, paramMeasurementUnit.apiName()));
    if (this.transaction.getRoot() != this)
      this.transaction.setMeasurementFromChild(paramString, paramNumber, paramMeasurementUnit); 
  }
  
  @NotNull
  public Map<String, MeasurementValue> getMeasurements() {
    return this.measurements;
  }
  
  public boolean updateEndDate(@NotNull SentryDate paramSentryDate) {
    if (this.timestamp != null) {
      this.timestamp = paramSentryDate;
      return true;
    } 
    return false;
  }
  
  public boolean isNoOp() {
    return false;
  }
  
  @NotNull
  public LocalMetricsAggregator getLocalMetricsAggregator() {
    return (LocalMetricsAggregator)this.metricsAggregator.getValue();
  }
  
  public void setContext(@NotNull String paramString, @NotNull Object paramObject) {
    this.contexts.put(paramString, paramObject);
  }
  
  @NotNull
  public Contexts getContexts() {
    return this.contexts;
  }
  
  void setSpanFinishedCallback(@Nullable SpanFinishedCallback paramSpanFinishedCallback) {
    this.spanFinishedCallback = paramSpanFinishedCallback;
  }
  
  @Nullable
  SpanFinishedCallback getSpanFinishedCallback() {
    return this.spanFinishedCallback;
  }
  
  private void updateStartDate(@NotNull SentryDate paramSentryDate) {
    this.startTimestamp = paramSentryDate;
  }
  
  @NotNull
  SpanOptions getOptions() {
    return this.options;
  }
  
  @NotNull
  private List<Span> getDirectChildren() {
    ArrayList<Span> arrayList = new ArrayList();
    for (Span span : this.transaction.getSpans()) {
      if (span.getParentSpanId() != null && span.getParentSpanId().equals(getSpanId()))
        arrayList.add(span); 
    } 
    return arrayList;
  }
  
  @NotNull
  public ISentryLifecycleToken makeCurrent() {
    return NoOpScopesLifecycleToken.getInstance();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\Span.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
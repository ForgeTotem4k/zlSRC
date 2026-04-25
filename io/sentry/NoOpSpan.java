package io.sentry;

import io.sentry.metrics.LocalMetricsAggregator;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.SentryId;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpSpan implements ISpan {
  private static final NoOpSpan instance = new NoOpSpan();
  
  public static NoOpSpan getInstance() {
    return instance;
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString) {
    return getInstance();
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString1, @Nullable String paramString2, @NotNull SpanOptions paramSpanOptions) {
    return getInstance();
  }
  
  @NotNull
  public ISpan startChild(@NotNull SpanContext paramSpanContext, @NotNull SpanOptions paramSpanOptions) {
    return getInstance();
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString1, @Nullable String paramString2, @Nullable SentryDate paramSentryDate, @NotNull Instrumenter paramInstrumenter) {
    return getInstance();
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString1, @Nullable String paramString2, @Nullable SentryDate paramSentryDate, @NotNull Instrumenter paramInstrumenter, @NotNull SpanOptions paramSpanOptions) {
    return getInstance();
  }
  
  @NotNull
  public ISpan startChild(@NotNull String paramString1, @Nullable String paramString2) {
    return getInstance();
  }
  
  @NotNull
  public SentryTraceHeader toSentryTrace() {
    return new SentryTraceHeader(SentryId.EMPTY_ID, SpanId.EMPTY_ID, Boolean.valueOf(false));
  }
  
  @NotNull
  public TraceContext traceContext() {
    return new TraceContext(SentryId.EMPTY_ID, "");
  }
  
  @Nullable
  public BaggageHeader toBaggageHeader(@Nullable List<String> paramList) {
    return null;
  }
  
  public void finish() {}
  
  public void finish(@Nullable SpanStatus paramSpanStatus) {}
  
  public void finish(@Nullable SpanStatus paramSpanStatus, @Nullable SentryDate paramSentryDate) {}
  
  public void setOperation(@NotNull String paramString) {}
  
  @NotNull
  public String getOperation() {
    return "";
  }
  
  public void setDescription(@Nullable String paramString) {}
  
  @Nullable
  public String getDescription() {
    return null;
  }
  
  public void setStatus(@Nullable SpanStatus paramSpanStatus) {}
  
  @Nullable
  public SpanStatus getStatus() {
    return null;
  }
  
  public void setThrowable(@Nullable Throwable paramThrowable) {}
  
  @Nullable
  public Throwable getThrowable() {
    return null;
  }
  
  @NotNull
  public SpanContext getSpanContext() {
    return new SpanContext(SentryId.EMPTY_ID, SpanId.EMPTY_ID, "op", null, null);
  }
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {}
  
  @Nullable
  public String getTag(@NotNull String paramString) {
    return null;
  }
  
  public boolean isFinished() {
    return false;
  }
  
  public void setData(@NotNull String paramString, @NotNull Object paramObject) {}
  
  @Nullable
  public Object getData(@NotNull String paramString) {
    return null;
  }
  
  public void setMeasurement(@NotNull String paramString, @NotNull Number paramNumber) {}
  
  public void setMeasurement(@NotNull String paramString, @NotNull Number paramNumber, @NotNull MeasurementUnit paramMeasurementUnit) {}
  
  public boolean updateEndDate(@NotNull SentryDate paramSentryDate) {
    return false;
  }
  
  @NotNull
  public SentryDate getStartDate() {
    return new SentryNanotimeDate();
  }
  
  @NotNull
  public SentryDate getFinishDate() {
    return new SentryNanotimeDate();
  }
  
  public boolean isNoOp() {
    return true;
  }
  
  @Nullable
  public LocalMetricsAggregator getLocalMetricsAggregator() {
    return null;
  }
  
  public void setContext(@NotNull String paramString, @NotNull Object paramObject) {}
  
  @NotNull
  public Contexts getContexts() {
    return new Contexts();
  }
  
  @Nullable
  public Boolean isSampled() {
    return null;
  }
  
  @Nullable
  public TracesSamplingDecision getSamplingDecision() {
    return null;
  }
  
  @NotNull
  public ISentryLifecycleToken makeCurrent() {
    return NoOpScopesLifecycleToken.getInstance();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpSpan.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
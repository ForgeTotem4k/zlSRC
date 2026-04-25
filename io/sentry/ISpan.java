package io.sentry;

import io.sentry.metrics.LocalMetricsAggregator;
import io.sentry.protocol.Contexts;
import java.util.List;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ISpan {
  @NotNull
  ISpan startChild(@NotNull String paramString);
  
  @Internal
  @NotNull
  ISpan startChild(@NotNull String paramString1, @Nullable String paramString2, @NotNull SpanOptions paramSpanOptions);
  
  @Internal
  @NotNull
  ISpan startChild(@NotNull SpanContext paramSpanContext, @NotNull SpanOptions paramSpanOptions);
  
  @Internal
  @NotNull
  ISpan startChild(@NotNull String paramString1, @Nullable String paramString2, @Nullable SentryDate paramSentryDate, @NotNull Instrumenter paramInstrumenter);
  
  @Internal
  @NotNull
  ISpan startChild(@NotNull String paramString1, @Nullable String paramString2, @Nullable SentryDate paramSentryDate, @NotNull Instrumenter paramInstrumenter, @NotNull SpanOptions paramSpanOptions);
  
  @NotNull
  ISpan startChild(@NotNull String paramString1, @Nullable String paramString2);
  
  @NotNull
  SentryTraceHeader toSentryTrace();
  
  @Nullable
  @Experimental
  TraceContext traceContext();
  
  @Nullable
  @Experimental
  BaggageHeader toBaggageHeader(@Nullable List<String> paramList);
  
  void finish();
  
  void finish(@Nullable SpanStatus paramSpanStatus);
  
  void finish(@Nullable SpanStatus paramSpanStatus, @Nullable SentryDate paramSentryDate);
  
  void setOperation(@NotNull String paramString);
  
  @NotNull
  String getOperation();
  
  void setDescription(@Nullable String paramString);
  
  @Nullable
  String getDescription();
  
  void setStatus(@Nullable SpanStatus paramSpanStatus);
  
  @Nullable
  SpanStatus getStatus();
  
  void setThrowable(@Nullable Throwable paramThrowable);
  
  @Nullable
  Throwable getThrowable();
  
  @NotNull
  SpanContext getSpanContext();
  
  void setTag(@NotNull String paramString1, @NotNull String paramString2);
  
  @Nullable
  String getTag(@NotNull String paramString);
  
  boolean isFinished();
  
  void setData(@NotNull String paramString, @NotNull Object paramObject);
  
  @Nullable
  Object getData(@NotNull String paramString);
  
  void setMeasurement(@NotNull String paramString, @NotNull Number paramNumber);
  
  void setMeasurement(@NotNull String paramString, @NotNull Number paramNumber, @NotNull MeasurementUnit paramMeasurementUnit);
  
  @Internal
  boolean updateEndDate(@NotNull SentryDate paramSentryDate);
  
  @Internal
  @NotNull
  SentryDate getStartDate();
  
  @Internal
  @Nullable
  SentryDate getFinishDate();
  
  @Internal
  boolean isNoOp();
  
  @Nullable
  LocalMetricsAggregator getLocalMetricsAggregator();
  
  void setContext(@NotNull String paramString, @NotNull Object paramObject);
  
  @NotNull
  Contexts getContexts();
  
  @Nullable
  Boolean isSampled();
  
  @Nullable
  TracesSamplingDecision getSamplingDecision();
  
  @Internal
  @NotNull
  ISentryLifecycleToken makeCurrent();
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\ISpan.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
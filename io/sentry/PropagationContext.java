package io.sentry;

import io.sentry.exception.InvalidSentryTraceHeaderException;
import io.sentry.protocol.SentryId;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class PropagationContext {
  @NotNull
  private SentryId traceId;
  
  @NotNull
  private SpanId spanId;
  
  @Nullable
  private SpanId parentSpanId;
  
  @Nullable
  private Boolean sampled;
  
  @Nullable
  private Baggage baggage;
  
  public static PropagationContext fromHeaders(@NotNull ILogger paramILogger, @Nullable String paramString1, @Nullable String paramString2) {
    return fromHeaders(paramILogger, paramString1, Arrays.asList(new String[] { paramString2 }));
  }
  
  @NotNull
  public static PropagationContext fromHeaders(@NotNull ILogger paramILogger, @Nullable String paramString, @Nullable List<String> paramList) {
    if (paramString == null)
      return new PropagationContext(); 
    try {
      SentryTraceHeader sentryTraceHeader = new SentryTraceHeader(paramString);
      Baggage baggage = Baggage.fromHeader(paramList, paramILogger);
      return fromHeaders(sentryTraceHeader, baggage, (SpanId)null);
    } catch (InvalidSentryTraceHeaderException invalidSentryTraceHeaderException) {
      paramILogger.log(SentryLevel.DEBUG, (Throwable)invalidSentryTraceHeaderException, "Failed to parse Sentry trace header: %s", new Object[] { invalidSentryTraceHeaderException.getMessage() });
      return new PropagationContext();
    } 
  }
  
  @NotNull
  public static PropagationContext fromHeaders(@NotNull SentryTraceHeader paramSentryTraceHeader, @Nullable Baggage paramBaggage, @Nullable SpanId paramSpanId) {
    SpanId spanId = (paramSpanId == null) ? new SpanId() : paramSpanId;
    return new PropagationContext(paramSentryTraceHeader.getTraceId(), spanId, paramSentryTraceHeader.getSpanId(), paramBaggage, paramSentryTraceHeader.isSampled());
  }
  
  public PropagationContext() {
    this(new SentryId(), new SpanId(), null, null, null);
  }
  
  public PropagationContext(@NotNull PropagationContext paramPropagationContext) {
    this(paramPropagationContext.getTraceId(), paramPropagationContext.getSpanId(), paramPropagationContext.getParentSpanId(), cloneBaggage(paramPropagationContext.getBaggage()), paramPropagationContext.isSampled());
  }
  
  @Nullable
  private static Baggage cloneBaggage(@Nullable Baggage paramBaggage) {
    return (paramBaggage != null) ? new Baggage(paramBaggage) : null;
  }
  
  public PropagationContext(@NotNull SentryId paramSentryId, @NotNull SpanId paramSpanId1, @Nullable SpanId paramSpanId2, @Nullable Baggage paramBaggage, @Nullable Boolean paramBoolean) {
    this.traceId = paramSentryId;
    this.spanId = paramSpanId1;
    this.parentSpanId = paramSpanId2;
    this.baggage = paramBaggage;
    this.sampled = paramBoolean;
  }
  
  @NotNull
  public SentryId getTraceId() {
    return this.traceId;
  }
  
  public void setTraceId(@NotNull SentryId paramSentryId) {
    this.traceId = paramSentryId;
  }
  
  @NotNull
  public SpanId getSpanId() {
    return this.spanId;
  }
  
  public void setSpanId(@NotNull SpanId paramSpanId) {
    this.spanId = paramSpanId;
  }
  
  @Nullable
  public SpanId getParentSpanId() {
    return this.parentSpanId;
  }
  
  public void setParentSpanId(@Nullable SpanId paramSpanId) {
    this.parentSpanId = paramSpanId;
  }
  
  @Nullable
  public Baggage getBaggage() {
    return this.baggage;
  }
  
  public void setBaggage(@Nullable Baggage paramBaggage) {
    this.baggage = paramBaggage;
  }
  
  @Nullable
  public Boolean isSampled() {
    return this.sampled;
  }
  
  public void setSampled(@Nullable Boolean paramBoolean) {
    this.sampled = paramBoolean;
  }
  
  @Nullable
  public TraceContext traceContext() {
    return (this.baggage != null) ? this.baggage.toTraceContext() : null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\PropagationContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry;

import io.sentry.exception.InvalidSentryTraceHeaderException;
import io.sentry.protocol.SentryId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryTraceHeader {
  public static final String SENTRY_TRACE_HEADER = "sentry-trace";
  
  @NotNull
  private final SentryId traceId;
  
  @NotNull
  private final SpanId spanId;
  
  @Nullable
  private final Boolean sampled;
  
  public SentryTraceHeader(@NotNull SentryId paramSentryId, @NotNull SpanId paramSpanId, @Nullable Boolean paramBoolean) {
    this.traceId = paramSentryId;
    this.spanId = paramSpanId;
    this.sampled = paramBoolean;
  }
  
  public SentryTraceHeader(@NotNull String paramString) throws InvalidSentryTraceHeaderException {
    String[] arrayOfString = paramString.split("-", -1);
    if (arrayOfString.length < 2)
      throw new InvalidSentryTraceHeaderException(paramString); 
    if (arrayOfString.length == 3) {
      this.sampled = Boolean.valueOf("1".equals(arrayOfString[2]));
    } else {
      this.sampled = null;
    } 
    try {
      this.traceId = new SentryId(arrayOfString[0]);
      this.spanId = new SpanId(arrayOfString[1]);
    } catch (Throwable throwable) {
      throw new InvalidSentryTraceHeaderException(paramString, throwable);
    } 
  }
  
  @NotNull
  public String getName() {
    return "sentry-trace";
  }
  
  @NotNull
  public String getValue() {
    return (this.sampled != null) ? String.format("%s-%s-%s", new Object[] { this.traceId, this.spanId, this.sampled.booleanValue() ? "1" : "0" }) : String.format("%s-%s", new Object[] { this.traceId, this.spanId });
  }
  
  @NotNull
  public SentryId getTraceId() {
    return this.traceId;
  }
  
  @NotNull
  public SpanId getSpanId() {
    return this.spanId;
  }
  
  @Nullable
  public Boolean isSampled() {
    return this.sampled;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryTraceHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
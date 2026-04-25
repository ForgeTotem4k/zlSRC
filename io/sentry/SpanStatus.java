package io.sentry;

import java.io.IOException;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum SpanStatus implements JsonSerializable {
  OK(200, 299),
  CANCELLED(499),
  INTERNAL_ERROR(500),
  UNKNOWN(500),
  UNKNOWN_ERROR(500),
  INVALID_ARGUMENT(400),
  DEADLINE_EXCEEDED(504),
  NOT_FOUND(404),
  ALREADY_EXISTS(409),
  PERMISSION_DENIED(403),
  RESOURCE_EXHAUSTED(429),
  FAILED_PRECONDITION(400),
  ABORTED(409),
  OUT_OF_RANGE(400),
  UNIMPLEMENTED(501),
  UNAVAILABLE(503),
  DATA_LOSS(500),
  UNAUTHENTICATED(401);
  
  private final int minHttpStatusCode;
  
  private final int maxHttpStatusCode;
  
  SpanStatus(int paramInt1) {
    this.minHttpStatusCode = paramInt1;
    this.maxHttpStatusCode = paramInt1;
  }
  
  SpanStatus(int paramInt1, int paramInt2) {
    this.minHttpStatusCode = paramInt1;
    this.maxHttpStatusCode = paramInt2;
  }
  
  @Nullable
  public static SpanStatus fromHttpStatusCode(int paramInt) {
    for (SpanStatus spanStatus : values()) {
      if (spanStatus.matches(paramInt))
        return spanStatus; 
    } 
    return null;
  }
  
  @NotNull
  public static SpanStatus fromHttpStatusCode(@Nullable Integer paramInteger, @NotNull SpanStatus paramSpanStatus) {
    SpanStatus spanStatus = (paramInteger != null) ? fromHttpStatusCode(paramInteger.intValue()) : paramSpanStatus;
    return (spanStatus != null) ? spanStatus : paramSpanStatus;
  }
  
  private boolean matches(int paramInt) {
    return (paramInt >= this.minHttpStatusCode && paramInt <= this.maxHttpStatusCode);
  }
  
  @NotNull
  public String apiName() {
    return name().toLowerCase(Locale.ROOT);
  }
  
  @Nullable
  public static SpanStatus fromApiNameSafely(@Nullable String paramString) {
    if (paramString == null)
      return null; 
    try {
      return valueOf(paramString.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException illegalArgumentException) {
      return null;
    } 
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.value(apiName());
  }
  
  public static final class Deserializer implements JsonDeserializer<SpanStatus> {
    @NotNull
    public SpanStatus deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      return SpanStatus.valueOf(param1JsonObjectReader.nextString().toUpperCase(Locale.ROOT));
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SpanStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
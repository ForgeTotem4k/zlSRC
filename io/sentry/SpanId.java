package io.sentry;

import io.sentry.util.Objects;
import io.sentry.util.StringUtils;
import java.io.IOException;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class SpanId implements JsonSerializable {
  public static final SpanId EMPTY_ID = new SpanId(new UUID(0L, 0L));
  
  @NotNull
  private final String value;
  
  public SpanId(@NotNull String paramString) {
    this.value = (String)Objects.requireNonNull(paramString, "value is required");
  }
  
  public SpanId() {
    this(UUID.randomUUID());
  }
  
  private SpanId(@NotNull UUID paramUUID) {
    this(StringUtils.normalizeUUID(paramUUID.toString()).replace("-", "").substring(0, 16));
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    SpanId spanId = (SpanId)paramObject;
    return this.value.equals(spanId.value);
  }
  
  public int hashCode() {
    return this.value.hashCode();
  }
  
  public String toString() {
    return this.value;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.value(this.value);
  }
  
  public static final class Deserializer implements JsonDeserializer<SpanId> {
    @NotNull
    public SpanId deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      return new SpanId(param1JsonObjectReader.nextString());
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SpanId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
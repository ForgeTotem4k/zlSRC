package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.ObjectWriter;
import io.sentry.util.StringUtils;
import java.io.IOException;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryId implements JsonSerializable {
  @NotNull
  private final UUID uuid;
  
  public static final SentryId EMPTY_ID = new SentryId(new UUID(0L, 0L));
  
  public SentryId() {
    this((UUID)null);
  }
  
  public SentryId(@Nullable UUID paramUUID) {
    if (paramUUID == null)
      paramUUID = UUID.randomUUID(); 
    this.uuid = paramUUID;
  }
  
  public SentryId(@NotNull String paramString) {
    this.uuid = fromStringSentryId(StringUtils.normalizeUUID(paramString));
  }
  
  public String toString() {
    return StringUtils.normalizeUUID(this.uuid.toString()).replace("-", "");
  }
  
  public boolean equals(@Nullable Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    SentryId sentryId = (SentryId)paramObject;
    return (this.uuid.compareTo(sentryId.uuid) == 0);
  }
  
  public int hashCode() {
    return this.uuid.hashCode();
  }
  
  @NotNull
  private UUID fromStringSentryId(@NotNull String paramString) {
    if (paramString.length() == 32)
      paramString = (new StringBuilder(paramString)).insert(8, "-").insert(13, "-").insert(18, "-").insert(23, "-").toString(); 
    if (paramString.length() != 36)
      throw new IllegalArgumentException("String representation of SentryId has either 32 (UUID no dashes) or 36 characters long (completed UUID). Received: " + paramString); 
    return UUID.fromString(paramString);
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.value(toString());
  }
  
  public static final class Deserializer implements JsonDeserializer<SentryId> {
    @NotNull
    public SentryId deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      return new SentryId(param1JsonObjectReader.nextString());
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\protocol\SentryId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry.clientreport;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonObjectReader;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class DiscardedEvent implements JsonUnknown, JsonSerializable {
  @NotNull
  private final String reason;
  
  @NotNull
  private final String category;
  
  @NotNull
  private final Long quantity;
  
  @Nullable
  private Map<String, Object> unknown;
  
  public DiscardedEvent(@NotNull String paramString1, @NotNull String paramString2, @NotNull Long paramLong) {
    this.reason = paramString1;
    this.category = paramString2;
    this.quantity = paramLong;
  }
  
  @NotNull
  public String getReason() {
    return this.reason;
  }
  
  @NotNull
  public String getCategory() {
    return this.category;
  }
  
  @NotNull
  public Long getQuantity() {
    return this.quantity;
  }
  
  public String toString() {
    return "DiscardedEvent{reason='" + this.reason + '\'' + ", category='" + this.category + '\'' + ", quantity=" + this.quantity + '}';
  }
  
  @Nullable
  public Map<String, Object> getUnknown() {
    return this.unknown;
  }
  
  public void setUnknown(@Nullable Map<String, Object> paramMap) {
    this.unknown = paramMap;
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger) throws IOException {
    paramObjectWriter.beginObject();
    paramObjectWriter.name("reason").value(this.reason);
    paramObjectWriter.name("category").value(this.category);
    paramObjectWriter.name("quantity").value(this.quantity);
    if (this.unknown != null)
      for (String str : this.unknown.keySet()) {
        Object object = this.unknown.get(str);
        paramObjectWriter.name(str).value(paramILogger, object);
      }  
    paramObjectWriter.endObject();
  }
  
  public static final class JsonKeys {
    public static final String REASON = "reason";
    
    public static final String CATEGORY = "category";
    
    public static final String QUANTITY = "quantity";
  }
  
  public static final class Deserializer implements JsonDeserializer<DiscardedEvent> {
    @NotNull
    public DiscardedEvent deserialize(@NotNull JsonObjectReader param1JsonObjectReader, @NotNull ILogger param1ILogger) throws Exception {
      String str1 = null;
      String str2 = null;
      Long long_ = null;
      HashMap<Object, Object> hashMap = null;
      param1JsonObjectReader.beginObject();
      while (param1JsonObjectReader.peek() == JsonToken.NAME) {
        String str = param1JsonObjectReader.nextName();
        switch (str) {
          case "reason":
            str1 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "category":
            str2 = param1JsonObjectReader.nextStringOrNull();
            continue;
          case "quantity":
            long_ = param1JsonObjectReader.nextLongOrNull();
            continue;
        } 
        if (hashMap == null)
          hashMap = new HashMap<>(); 
        param1JsonObjectReader.nextUnknown(param1ILogger, hashMap, str);
      } 
      param1JsonObjectReader.endObject();
      if (str1 == null)
        throw missingRequiredFieldException("reason", param1ILogger); 
      if (str2 == null)
        throw missingRequiredFieldException("category", param1ILogger); 
      if (long_ == null)
        throw missingRequiredFieldException("quantity", param1ILogger); 
      DiscardedEvent discardedEvent = new DiscardedEvent(str1, str2, long_);
      discardedEvent.setUnknown((Map)hashMap);
      return discardedEvent;
    }
    
    private Exception missingRequiredFieldException(String param1String, ILogger param1ILogger) {
      String str = "Missing required field \"" + param1String + "\"";
      IllegalStateException illegalStateException = new IllegalStateException(str);
      param1ILogger.log(SentryLevel.ERROR, str, illegalStateException);
      return illegalStateException;
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\clientreport\DiscardedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry;

import io.sentry.vendor.gson.stream.JsonReader;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class JsonObjectReader extends JsonReader {
  public JsonObjectReader(Reader paramReader) {
    super(paramReader);
  }
  
  @Nullable
  public String nextStringOrNull() throws IOException {
    if (peek() == JsonToken.NULL) {
      nextNull();
      return null;
    } 
    return nextString();
  }
  
  @Nullable
  public Double nextDoubleOrNull() throws IOException {
    if (peek() == JsonToken.NULL) {
      nextNull();
      return null;
    } 
    return Double.valueOf(nextDouble());
  }
  
  @Nullable
  public Float nextFloatOrNull() throws IOException {
    if (peek() == JsonToken.NULL) {
      nextNull();
      return null;
    } 
    return nextFloat();
  }
  
  @NotNull
  public Float nextFloat() throws IOException {
    return Float.valueOf((float)nextDouble());
  }
  
  @Nullable
  public Long nextLongOrNull() throws IOException {
    if (peek() == JsonToken.NULL) {
      nextNull();
      return null;
    } 
    return Long.valueOf(nextLong());
  }
  
  @Nullable
  public Integer nextIntegerOrNull() throws IOException {
    if (peek() == JsonToken.NULL) {
      nextNull();
      return null;
    } 
    return Integer.valueOf(nextInt());
  }
  
  @Nullable
  public Boolean nextBooleanOrNull() throws IOException {
    if (peek() == JsonToken.NULL) {
      nextNull();
      return null;
    } 
    return Boolean.valueOf(nextBoolean());
  }
  
  public void nextUnknown(ILogger paramILogger, Map<String, Object> paramMap, String paramString) {
    try {
      paramMap.put(paramString, nextObjectOrNull());
    } catch (Exception exception) {
      paramILogger.log(SentryLevel.ERROR, exception, "Error deserializing unknown key: %s", new Object[] { paramString });
    } 
  }
  
  @Nullable
  public <T> List<T> nextListOrNull(@NotNull ILogger paramILogger, @NotNull JsonDeserializer<T> paramJsonDeserializer) throws IOException {
    if (peek() == JsonToken.NULL) {
      nextNull();
      return null;
    } 
    beginArray();
    ArrayList<T> arrayList = new ArrayList();
    if (hasNext())
      do {
        try {
          arrayList.add(paramJsonDeserializer.deserialize(this, paramILogger));
        } catch (Exception exception) {
          paramILogger.log(SentryLevel.WARNING, "Failed to deserialize object in list.", exception);
        } 
      } while (peek() == JsonToken.BEGIN_OBJECT); 
    endArray();
    return arrayList;
  }
  
  @Nullable
  public <T> Map<String, T> nextMapOrNull(@NotNull ILogger paramILogger, @NotNull JsonDeserializer<T> paramJsonDeserializer) throws IOException {
    if (peek() == JsonToken.NULL) {
      nextNull();
      return null;
    } 
    beginObject();
    HashMap<Object, Object> hashMap = new HashMap<>();
    if (hasNext())
      do {
        try {
          String str = nextName();
          hashMap.put(str, paramJsonDeserializer.deserialize(this, paramILogger));
        } catch (Exception exception) {
          paramILogger.log(SentryLevel.WARNING, "Failed to deserialize object in map.", exception);
        } 
      } while (peek() == JsonToken.BEGIN_OBJECT || peek() == JsonToken.NAME); 
    endObject();
    return (Map)hashMap;
  }
  
  @Nullable
  public <T> Map<String, List<T>> nextMapOfListOrNull(@NotNull ILogger paramILogger, @NotNull JsonDeserializer<T> paramJsonDeserializer) throws IOException {
    if (peek() == JsonToken.NULL) {
      nextNull();
      return null;
    } 
    HashMap<Object, Object> hashMap = new HashMap<>();
    beginObject();
    if (hasNext())
      do {
        String str = nextName();
        List<T> list = nextListOrNull(paramILogger, paramJsonDeserializer);
        if (list == null)
          continue; 
        hashMap.put(str, list);
      } while (peek() == JsonToken.BEGIN_OBJECT || peek() == JsonToken.NAME); 
    endObject();
    return (Map)hashMap;
  }
  
  @Nullable
  public <T> T nextOrNull(@NotNull ILogger paramILogger, @NotNull JsonDeserializer<T> paramJsonDeserializer) throws Exception {
    if (peek() == JsonToken.NULL) {
      nextNull();
      return null;
    } 
    return paramJsonDeserializer.deserialize(this, paramILogger);
  }
  
  @Nullable
  public Date nextDateOrNull(ILogger paramILogger) throws IOException {
    if (peek() == JsonToken.NULL) {
      nextNull();
      return null;
    } 
    return dateOrNull(nextString(), paramILogger);
  }
  
  @Nullable
  public static Date dateOrNull(@Nullable String paramString, ILogger paramILogger) {
    if (paramString == null)
      return null; 
    try {
      return DateUtils.getDateTime(paramString);
    } catch (Exception exception) {
      try {
        return DateUtils.getDateTimeWithMillisPrecision(paramString);
      } catch (Exception exception1) {
        paramILogger.log(SentryLevel.ERROR, "Error when deserializing millis timestamp format.", exception1);
        return null;
      } 
    } 
  }
  
  @Nullable
  public TimeZone nextTimeZoneOrNull(ILogger paramILogger) throws IOException {
    if (peek() == JsonToken.NULL) {
      nextNull();
      return null;
    } 
    try {
      return TimeZone.getTimeZone(nextString());
    } catch (Exception exception) {
      paramILogger.log(SentryLevel.ERROR, "Error when deserializing TimeZone", exception);
      return null;
    } 
  }
  
  @Nullable
  public Object nextObjectOrNull() throws IOException {
    return (new JsonObjectDeserializer()).deserialize(this);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\JsonObjectReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
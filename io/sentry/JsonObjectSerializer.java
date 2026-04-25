package io.sentry;

import io.sentry.util.JsonSerializationUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class JsonObjectSerializer {
  public static final String OBJECT_PLACEHOLDER = "[OBJECT]";
  
  public final JsonReflectionObjectSerializer jsonReflectionObjectSerializer;
  
  public JsonObjectSerializer(int paramInt) {
    this.jsonReflectionObjectSerializer = new JsonReflectionObjectSerializer(paramInt);
  }
  
  public void serialize(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger, @Nullable Object paramObject) throws IOException {
    if (paramObject == null) {
      paramObjectWriter.nullValue();
    } else if (paramObject instanceof Character) {
      paramObjectWriter.value(Character.toString(((Character)paramObject).charValue()));
    } else if (paramObject instanceof String) {
      paramObjectWriter.value((String)paramObject);
    } else if (paramObject instanceof Boolean) {
      paramObjectWriter.value(((Boolean)paramObject).booleanValue());
    } else if (paramObject instanceof Number) {
      paramObjectWriter.value((Number)paramObject);
    } else if (paramObject instanceof Date) {
      serializeDate(paramObjectWriter, paramILogger, (Date)paramObject);
    } else if (paramObject instanceof TimeZone) {
      serializeTimeZone(paramObjectWriter, paramILogger, (TimeZone)paramObject);
    } else if (paramObject instanceof JsonSerializable) {
      ((JsonSerializable)paramObject).serialize(paramObjectWriter, paramILogger);
    } else if (paramObject instanceof Collection) {
      serializeCollection(paramObjectWriter, paramILogger, (Collection)paramObject);
    } else if (paramObject.getClass().isArray()) {
      serializeCollection(paramObjectWriter, paramILogger, Arrays.asList((Object[])paramObject));
    } else if (paramObject instanceof Map) {
      serializeMap(paramObjectWriter, paramILogger, (Map<?, ?>)paramObject);
    } else if (paramObject instanceof java.util.Locale) {
      paramObjectWriter.value(paramObject.toString());
    } else if (paramObject instanceof AtomicIntegerArray) {
      serializeCollection(paramObjectWriter, paramILogger, JsonSerializationUtils.atomicIntegerArrayToList((AtomicIntegerArray)paramObject));
    } else if (paramObject instanceof AtomicBoolean) {
      paramObjectWriter.value(((AtomicBoolean)paramObject).get());
    } else if (paramObject instanceof java.net.URI) {
      paramObjectWriter.value(paramObject.toString());
    } else if (paramObject instanceof java.net.InetAddress) {
      paramObjectWriter.value(paramObject.toString());
    } else if (paramObject instanceof java.util.UUID) {
      paramObjectWriter.value(paramObject.toString());
    } else if (paramObject instanceof java.util.Currency) {
      paramObjectWriter.value(paramObject.toString());
    } else if (paramObject instanceof Calendar) {
      serializeMap(paramObjectWriter, paramILogger, JsonSerializationUtils.calendarToMap((Calendar)paramObject));
    } else if (paramObject.getClass().isEnum()) {
      paramObjectWriter.value(paramObject.toString());
    } else {
      try {
        Object object = this.jsonReflectionObjectSerializer.serialize(paramObject, paramILogger);
        serialize(paramObjectWriter, paramILogger, object);
      } catch (Exception exception) {
        paramILogger.log(SentryLevel.ERROR, "Failed serializing unknown object.", exception);
        paramObjectWriter.value("[OBJECT]");
      } 
    } 
  }
  
  private void serializeDate(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger, @NotNull Date paramDate) throws IOException {
    try {
      paramObjectWriter.value(DateUtils.getTimestamp(paramDate));
    } catch (Exception exception) {
      paramILogger.log(SentryLevel.ERROR, "Error when serializing Date", exception);
      paramObjectWriter.nullValue();
    } 
  }
  
  private void serializeTimeZone(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger, @NotNull TimeZone paramTimeZone) throws IOException {
    try {
      paramObjectWriter.value(paramTimeZone.getID());
    } catch (Exception exception) {
      paramILogger.log(SentryLevel.ERROR, "Error when serializing TimeZone", exception);
      paramObjectWriter.nullValue();
    } 
  }
  
  private void serializeCollection(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger, @NotNull Collection<?> paramCollection) throws IOException {
    paramObjectWriter.beginArray();
    for (Object object : paramCollection)
      serialize(paramObjectWriter, paramILogger, object); 
    paramObjectWriter.endArray();
  }
  
  private void serializeMap(@NotNull ObjectWriter paramObjectWriter, @NotNull ILogger paramILogger, @NotNull Map<?, ?> paramMap) throws IOException {
    paramObjectWriter.beginObject();
    for (String str : paramMap.keySet()) {
      if (str instanceof String) {
        paramObjectWriter.name(str);
        serialize(paramObjectWriter, paramILogger, paramMap.get(str));
      } 
    } 
    paramObjectWriter.endObject();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\JsonObjectSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
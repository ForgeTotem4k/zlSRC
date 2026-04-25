package io.sentry.util;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.JsonSerializable;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class MapObjectWriter implements ObjectWriter {
  @NotNull
  final Map<String, Object> root;
  
  @NotNull
  final ArrayDeque<Object> stack;
  
  public MapObjectWriter(@NotNull Map<String, Object> paramMap) {
    this.root = paramMap;
    this.stack = new ArrayDeque();
    this.stack.addLast(paramMap);
  }
  
  public MapObjectWriter name(@NotNull String paramString) throws IOException {
    this.stack.add(paramString);
    return this;
  }
  
  public MapObjectWriter value(@NotNull ILogger paramILogger, @Nullable Object paramObject) throws IOException {
    if (paramObject == null) {
      nullValue();
    } else if (paramObject instanceof Character) {
      value(Character.toString(((Character)paramObject).charValue()));
    } else if (paramObject instanceof String) {
      value((String)paramObject);
    } else if (paramObject instanceof Boolean) {
      value(((Boolean)paramObject).booleanValue());
    } else if (paramObject instanceof Number) {
      value((Number)paramObject);
    } else if (paramObject instanceof Date) {
      serializeDate(paramILogger, (Date)paramObject);
    } else if (paramObject instanceof TimeZone) {
      serializeTimeZone(paramILogger, (TimeZone)paramObject);
    } else if (paramObject instanceof JsonSerializable) {
      ((JsonSerializable)paramObject).serialize(this, paramILogger);
    } else if (paramObject instanceof Collection) {
      serializeCollection(paramILogger, (Collection)paramObject);
    } else if (paramObject.getClass().isArray()) {
      serializeCollection(paramILogger, Arrays.asList((Object[])paramObject));
    } else if (paramObject instanceof Map) {
      serializeMap(paramILogger, (Map<?, ?>)paramObject);
    } else if (paramObject instanceof java.util.Locale) {
      value(paramObject.toString());
    } else if (paramObject instanceof AtomicIntegerArray) {
      serializeCollection(paramILogger, JsonSerializationUtils.atomicIntegerArrayToList((AtomicIntegerArray)paramObject));
    } else if (paramObject instanceof AtomicBoolean) {
      value(((AtomicBoolean)paramObject).get());
    } else if (paramObject instanceof java.net.URI) {
      value(paramObject.toString());
    } else if (paramObject instanceof java.net.InetAddress) {
      value(paramObject.toString());
    } else if (paramObject instanceof java.util.UUID) {
      value(paramObject.toString());
    } else if (paramObject instanceof java.util.Currency) {
      value(paramObject.toString());
    } else if (paramObject instanceof Calendar) {
      serializeMap(paramILogger, JsonSerializationUtils.calendarToMap((Calendar)paramObject));
    } else if (paramObject.getClass().isEnum()) {
      value(paramObject.toString());
    } else {
      paramILogger.log(SentryLevel.WARNING, "Failed serializing unknown object.", new Object[] { paramObject });
    } 
    return this;
  }
  
  public MapObjectWriter beginArray() throws IOException {
    this.stack.add(new ArrayList());
    return this;
  }
  
  public MapObjectWriter endArray() throws IOException {
    endObject();
    return this;
  }
  
  public MapObjectWriter beginObject() throws IOException {
    this.stack.addLast(new HashMap<>());
    return this;
  }
  
  public MapObjectWriter endObject() throws IOException {
    Object object = this.stack.removeLast();
    postValue(object);
    return this;
  }
  
  public MapObjectWriter value(@Nullable String paramString) throws IOException {
    postValue(paramString);
    return this;
  }
  
  public MapObjectWriter nullValue() throws IOException {
    postValue(null);
    return this;
  }
  
  public MapObjectWriter value(boolean paramBoolean) throws IOException {
    postValue(Boolean.valueOf(paramBoolean));
    return this;
  }
  
  public MapObjectWriter value(@Nullable Boolean paramBoolean) throws IOException {
    postValue(paramBoolean);
    return this;
  }
  
  public MapObjectWriter value(double paramDouble) throws IOException {
    postValue(Double.valueOf(paramDouble));
    return this;
  }
  
  public MapObjectWriter value(long paramLong) throws IOException {
    postValue(Long.valueOf(paramLong));
    return this;
  }
  
  public MapObjectWriter value(@Nullable Number paramNumber) throws IOException {
    postValue(paramNumber);
    return this;
  }
  
  private void serializeDate(@NotNull ILogger paramILogger, @NotNull Date paramDate) throws IOException {
    try {
      value(DateUtils.getTimestamp(paramDate));
    } catch (Exception exception) {
      paramILogger.log(SentryLevel.ERROR, "Error when serializing Date", exception);
      nullValue();
    } 
  }
  
  private void serializeTimeZone(@NotNull ILogger paramILogger, @NotNull TimeZone paramTimeZone) throws IOException {
    try {
      value(paramTimeZone.getID());
    } catch (Exception exception) {
      paramILogger.log(SentryLevel.ERROR, "Error when serializing TimeZone", exception);
      nullValue();
    } 
  }
  
  private void serializeCollection(@NotNull ILogger paramILogger, @NotNull Collection<?> paramCollection) throws IOException {
    beginArray();
    for (Object object : paramCollection)
      value(paramILogger, object); 
    endArray();
  }
  
  private void serializeMap(@NotNull ILogger paramILogger, @NotNull Map<?, ?> paramMap) throws IOException {
    beginObject();
    for (String str : paramMap.keySet()) {
      if (str instanceof String) {
        name(str);
        value(paramILogger, paramMap.get(str));
      } 
    } 
    endObject();
  }
  
  private void postValue(@Nullable Object paramObject) {
    Object object = this.stack.peekLast();
    if (object instanceof List) {
      ((List<Object>)object).add(paramObject);
    } else if (object instanceof String) {
      String str = (String)this.stack.removeLast();
      peekObject().put(str, paramObject);
    } else {
      throw new IllegalStateException("Invalid stack state, expected array or string on top");
    } 
  }
  
  @NotNull
  private Map<String, Object> peekObject() {
    Object object = this.stack.peekLast();
    if (object == null)
      throw new IllegalStateException("Stack is empty."); 
    if (object instanceof Map)
      return (Map<String, Object>)object; 
    throw new IllegalStateException("Stack element is not a Map.");
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\MapObjectWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry;

import io.sentry.util.JsonSerializationUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class JsonReflectionObjectSerializer {
  private final Set<Object> visiting = new HashSet();
  
  private final int maxDepth;
  
  JsonReflectionObjectSerializer(int paramInt) {
    this.maxDepth = paramInt;
  }
  
  @Nullable
  public Object serialize(@Nullable Object paramObject, @NotNull ILogger paramILogger) throws Exception {
    Map<String, Object> map;
    if (paramObject == null)
      return null; 
    if (paramObject instanceof Character)
      return paramObject.toString(); 
    if (paramObject instanceof Number)
      return paramObject; 
    if (paramObject instanceof Boolean)
      return paramObject; 
    if (paramObject instanceof String)
      return paramObject; 
    if (paramObject instanceof java.util.Locale)
      return paramObject.toString(); 
    if (paramObject instanceof AtomicIntegerArray)
      return JsonSerializationUtils.atomicIntegerArrayToList((AtomicIntegerArray)paramObject); 
    if (paramObject instanceof AtomicBoolean)
      return Boolean.valueOf(((AtomicBoolean)paramObject).get()); 
    if (paramObject instanceof java.net.URI)
      return paramObject.toString(); 
    if (paramObject instanceof java.net.InetAddress)
      return paramObject.toString(); 
    if (paramObject instanceof java.util.UUID)
      return paramObject.toString(); 
    if (paramObject instanceof java.util.Currency)
      return paramObject.toString(); 
    if (paramObject instanceof Calendar)
      return JsonSerializationUtils.calendarToMap((Calendar)paramObject); 
    if (paramObject.getClass().isEnum())
      return paramObject.toString(); 
    if (this.visiting.contains(paramObject)) {
      paramILogger.log(SentryLevel.INFO, "Cyclic reference detected. Calling toString() on object.", new Object[0]);
      return paramObject.toString();
    } 
    this.visiting.add(paramObject);
    if (this.visiting.size() > this.maxDepth) {
      this.visiting.remove(paramObject);
      paramILogger.log(SentryLevel.INFO, "Max depth exceeded. Calling toString() on object.", new Object[0]);
      return paramObject.toString();
    } 
    List<Object> list = null;
    try {
      if (paramObject.getClass().isArray()) {
        list = list((Object[])paramObject, paramILogger);
      } else if (paramObject instanceof Collection) {
        list = list((Collection)paramObject, paramILogger);
      } else if (paramObject instanceof Map) {
        map = map((Map<?, ?>)paramObject, paramILogger);
      } else {
        Map<String, Object> map1 = serializeObject(paramObject, paramILogger);
        if (map1.isEmpty()) {
          String str = paramObject.toString();
        } else {
          map = map1;
        } 
      } 
    } catch (Exception exception) {
      paramILogger.log(SentryLevel.INFO, "Not serializing object due to throwing sub-path.", exception);
    } finally {
      this.visiting.remove(paramObject);
    } 
    return map;
  }
  
  @NotNull
  public Map<String, Object> serializeObject(@NotNull Object paramObject, @NotNull ILogger paramILogger) throws Exception {
    Field[] arrayOfField = paramObject.getClass().getDeclaredFields();
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (Field field : arrayOfField) {
      if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
        String str = field.getName();
        try {
          field.setAccessible(true);
          Object object = field.get(paramObject);
          hashMap.put(str, serialize(object, paramILogger));
          field.setAccessible(false);
        } catch (Exception exception) {
          paramILogger.log(SentryLevel.INFO, "Cannot access field " + str + ".", new Object[0]);
        } 
      } 
    } 
    return (Map)hashMap;
  }
  
  @NotNull
  private List<Object> list(@NotNull Object[] paramArrayOfObject, @NotNull ILogger paramILogger) throws Exception {
    ArrayList<Object> arrayList = new ArrayList();
    for (Object object : paramArrayOfObject)
      arrayList.add(serialize(object, paramILogger)); 
    return arrayList;
  }
  
  @NotNull
  private List<Object> list(@NotNull Collection<?> paramCollection, @NotNull ILogger paramILogger) throws Exception {
    ArrayList<Object> arrayList = new ArrayList();
    for (Object object : paramCollection)
      arrayList.add(serialize(object, paramILogger)); 
    return arrayList;
  }
  
  @NotNull
  private Map<String, Object> map(@NotNull Map<?, ?> paramMap, @NotNull ILogger paramILogger) throws Exception {
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (Object object : paramMap.keySet()) {
      Object object1 = paramMap.get(object);
      if (object1 != null) {
        hashMap.put(object.toString(), serialize(object1, paramILogger));
        continue;
      } 
      hashMap.put(object.toString(), null);
    } 
    return (Map)hashMap;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\JsonReflectionObjectSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
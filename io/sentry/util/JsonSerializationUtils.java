package io.sentry.util;

import io.sentry.ILogger;
import io.sentry.ISerializer;
import io.sentry.JsonSerializable;
import io.sentry.SentryLevel;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class JsonSerializationUtils {
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  
  @NotNull
  public static Map<String, Object> calendarToMap(@NotNull Calendar paramCalendar) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    hashMap.put("year", Integer.valueOf(paramCalendar.get(1)));
    hashMap.put("month", Integer.valueOf(paramCalendar.get(2)));
    hashMap.put("dayOfMonth", Integer.valueOf(paramCalendar.get(5)));
    hashMap.put("hourOfDay", Integer.valueOf(paramCalendar.get(11)));
    hashMap.put("minute", Integer.valueOf(paramCalendar.get(12)));
    hashMap.put("second", Integer.valueOf(paramCalendar.get(13)));
    return (Map)hashMap;
  }
  
  @NotNull
  public static List<Object> atomicIntegerArrayToList(@NotNull AtomicIntegerArray paramAtomicIntegerArray) {
    int i = paramAtomicIntegerArray.length();
    ArrayList<Integer> arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++)
      arrayList.add(Integer.valueOf(paramAtomicIntegerArray.get(b))); 
    return (List)arrayList;
  }
  
  @Nullable
  public static byte[] bytesFrom(@NotNull ISerializer paramISerializer, @NotNull ILogger paramILogger, @NotNull JsonSerializable paramJsonSerializable) {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      try {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream, UTF_8));
        try {
          paramISerializer.serialize(paramJsonSerializable, bufferedWriter);
          byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
          bufferedWriter.close();
          byteArrayOutputStream.close();
          return arrayOfByte;
        } catch (Throwable throwable) {
          try {
            bufferedWriter.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } catch (Throwable throwable) {
        try {
          byteArrayOutputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      paramILogger.log(SentryLevel.ERROR, "Could not serialize serializable", throwable);
      return null;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\JsonSerializationUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
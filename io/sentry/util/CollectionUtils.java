package io.sentry.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class CollectionUtils {
  public static int size(@NotNull Iterable<?> paramIterable) {
    if (paramIterable instanceof Collection)
      return ((Collection)paramIterable).size(); 
    byte b = 0;
    for (Object object : paramIterable)
      b++; 
    return b;
  }
  
  @Nullable
  public static <K, V> Map<K, V> newConcurrentHashMap(@Nullable Map<K, V> paramMap) {
    if (paramMap != null) {
      ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();
      for (Map.Entry<K, V> entry : paramMap.entrySet()) {
        if (entry.getKey() != null && entry.getValue() != null)
          concurrentHashMap.put(entry.getKey(), entry.getValue()); 
      } 
      return (Map)concurrentHashMap;
    } 
    return null;
  }
  
  @Nullable
  public static <K, V> Map<K, V> newHashMap(@Nullable Map<K, V> paramMap) {
    return (paramMap != null) ? new HashMap<>(paramMap) : null;
  }
  
  @Nullable
  public static <T> List<T> newArrayList(@Nullable List<T> paramList) {
    return (paramList != null) ? new ArrayList<>(paramList) : null;
  }
  
  @NotNull
  public static <K, V> Map<K, V> filterMapEntries(@NotNull Map<K, V> paramMap, @NotNull Predicate<Map.Entry<K, V>> paramPredicate) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (Map.Entry<K, V> entry : paramMap.entrySet()) {
      if (paramPredicate.test(entry))
        hashMap.put(entry.getKey(), entry.getValue()); 
    } 
    return (Map)hashMap;
  }
  
  @NotNull
  public static <T, R> List<R> map(@NotNull List<T> paramList, @NotNull Mapper<T, R> paramMapper) {
    // Byte code:
    //   0: new java/util/ArrayList
    //   3: dup
    //   4: aload_0
    //   5: invokeinterface size : ()I
    //   10: invokespecial <init> : (I)V
    //   13: astore_2
    //   14: aload_0
    //   15: invokeinterface iterator : ()Ljava/util/Iterator;
    //   20: astore_3
    //   21: aload_3
    //   22: invokeinterface hasNext : ()Z
    //   27: ifeq -> 56
    //   30: aload_3
    //   31: invokeinterface next : ()Ljava/lang/Object;
    //   36: astore #4
    //   38: aload_2
    //   39: aload_1
    //   40: aload #4
    //   42: invokeinterface map : (Ljava/lang/Object;)Ljava/lang/Object;
    //   47: invokeinterface add : (Ljava/lang/Object;)Z
    //   52: pop
    //   53: goto -> 21
    //   56: aload_2
    //   57: areturn
  }
  
  @NotNull
  public static <T> List<T> filterListEntries(@NotNull List<T> paramList, @NotNull Predicate<T> paramPredicate) {
    // Byte code:
    //   0: new java/util/ArrayList
    //   3: dup
    //   4: aload_0
    //   5: invokeinterface size : ()I
    //   10: invokespecial <init> : (I)V
    //   13: astore_2
    //   14: aload_0
    //   15: invokeinterface iterator : ()Ljava/util/Iterator;
    //   20: astore_3
    //   21: aload_3
    //   22: invokeinterface hasNext : ()Z
    //   27: ifeq -> 61
    //   30: aload_3
    //   31: invokeinterface next : ()Ljava/lang/Object;
    //   36: astore #4
    //   38: aload_1
    //   39: aload #4
    //   41: invokeinterface test : (Ljava/lang/Object;)Z
    //   46: ifeq -> 58
    //   49: aload_2
    //   50: aload #4
    //   52: invokeinterface add : (Ljava/lang/Object;)Z
    //   57: pop
    //   58: goto -> 21
    //   61: aload_2
    //   62: areturn
  }
  
  public static <T> boolean contains(@NotNull T[] paramArrayOfT, @NotNull T paramT) {
    for (T t : paramArrayOfT) {
      if (paramT.equals(t))
        return true; 
    } 
    return false;
  }
  
  static {
  
  }
  
  public static interface Predicate<T> {
    boolean test(T param1T);
    
    static {
    
    }
  }
  
  public static interface Mapper<T, R> {
    R map(T param1T);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\CollectionUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry.util;

import io.sentry.Hint;
import io.sentry.ILogger;
import io.sentry.hints.ApplyScopeData;
import io.sentry.hints.Backfillable;
import io.sentry.hints.Cached;
import io.sentry.hints.EventDropReason;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class HintUtils {
  public static void setIsFromHybridSdk(@NotNull Hint paramHint, @NotNull String paramString) {
    if (paramString.startsWith("sentry.javascript") || paramString.startsWith("sentry.dart") || paramString.startsWith("sentry.dotnet"))
      paramHint.set("sentry:isFromHybridSdk", Boolean.valueOf(true)); 
  }
  
  public static boolean isFromHybridSdk(@NotNull Hint paramHint) {
    return Boolean.TRUE.equals(paramHint.getAs("sentry:isFromHybridSdk", Boolean.class));
  }
  
  public static void setEventDropReason(@NotNull Hint paramHint, @NotNull EventDropReason paramEventDropReason) {
    paramHint.set("sentry:eventDropReason", paramEventDropReason);
  }
  
  @Nullable
  public static EventDropReason getEventDropReason(@NotNull Hint paramHint) {
    return (EventDropReason)paramHint.getAs("sentry:eventDropReason", EventDropReason.class);
  }
  
  public static Hint createWithTypeCheckHint(Object paramObject) {
    Hint hint = new Hint();
    setTypeCheckHint(hint, paramObject);
    return hint;
  }
  
  public static void setTypeCheckHint(@NotNull Hint paramHint, Object paramObject) {
    paramHint.set("sentry:typeCheckHint", paramObject);
  }
  
  @Nullable
  public static Object getSentrySdkHint(@NotNull Hint paramHint) {
    return paramHint.get("sentry:typeCheckHint");
  }
  
  public static boolean hasType(@NotNull Hint paramHint, @NotNull Class<?> paramClass) {
    Object object = getSentrySdkHint(paramHint);
    return paramClass.isInstance(object);
  }
  
  public static <T> void runIfDoesNotHaveType(@NotNull Hint paramHint, @NotNull Class<T> paramClass, SentryNullableConsumer<Object> paramSentryNullableConsumer) {
    runIfHasType(paramHint, paramClass, paramObject -> {
        
        }(paramObject, paramClass) -> paramSentryNullableConsumer.accept(paramObject));
  }
  
  public static <T> void runIfHasType(@NotNull Hint paramHint, @NotNull Class<T> paramClass, SentryConsumer<T> paramSentryConsumer) {
    runIfHasType(paramHint, paramClass, paramSentryConsumer, (paramObject, paramClass) -> {
        
        });
  }
  
  public static <T> void runIfHasTypeLogIfNot(@NotNull Hint paramHint, @NotNull Class<T> paramClass, ILogger paramILogger, SentryConsumer<T> paramSentryConsumer) {
    runIfHasType(paramHint, paramClass, paramSentryConsumer, (paramObject, paramClass) -> LogUtils.logNotInstanceOf(paramClass, paramObject, paramILogger));
  }
  
  public static <T> void runIfHasType(@NotNull Hint paramHint, @NotNull Class<T> paramClass, SentryConsumer<T> paramSentryConsumer, SentryHintFallback paramSentryHintFallback) {
    Object object = getSentrySdkHint(paramHint);
    if (hasType(paramHint, paramClass) && object != null) {
      paramSentryConsumer.accept((T)object);
    } else {
      paramSentryHintFallback.accept(object, paramClass);
    } 
  }
  
  public static boolean shouldApplyScopeData(@NotNull Hint paramHint) {
    return ((!hasType(paramHint, Cached.class) && !hasType(paramHint, Backfillable.class)) || hasType(paramHint, ApplyScopeData.class));
  }
  
  static {
  
  }
  
  @FunctionalInterface
  public static interface SentryConsumer<T> {
    void accept(@NotNull T param1T);
    
    static {
    
    }
  }
  
  @FunctionalInterface
  public static interface SentryNullableConsumer<T> {
    void accept(@Nullable T param1T);
    
    static {
    
    }
  }
  
  @FunctionalInterface
  public static interface SentryHintFallback {
    void accept(@Nullable Object param1Object, @NotNull Class<?> param1Class);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\HintUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry;

import io.sentry.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DuplicateEventDetectionEventProcessor implements EventProcessor {
  @NotNull
  private final Map<Throwable, Object> capturedObjects = Collections.synchronizedMap(new WeakHashMap<>());
  
  @NotNull
  private final SentryOptions options;
  
  public DuplicateEventDetectionEventProcessor(@NotNull SentryOptions paramSentryOptions) {
    this.options = (SentryOptions)Objects.requireNonNull(paramSentryOptions, "options are required");
  }
  
  @Nullable
  public SentryEvent process(@NotNull SentryEvent paramSentryEvent, @NotNull Hint paramHint) {
    if (this.options.isEnableDeduplication()) {
      Throwable throwable = paramSentryEvent.getThrowable();
      if (throwable != null) {
        if (this.capturedObjects.containsKey(throwable) || containsAnyKey(this.capturedObjects, allCauses(throwable))) {
          this.options.getLogger().log(SentryLevel.DEBUG, "Duplicate Exception detected. Event %s will be discarded.", new Object[] { paramSentryEvent.getEventId() });
          return null;
        } 
        this.capturedObjects.put(throwable, null);
      } 
    } else {
      this.options.getLogger().log(SentryLevel.DEBUG, "Event deduplication is disabled.", new Object[0]);
    } 
    return paramSentryEvent;
  }
  
  private static <T> boolean containsAnyKey(@NotNull Map<T, Object> paramMap, @NotNull List<T> paramList) {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface iterator : ()Ljava/util/Iterator;
    //   6: astore_2
    //   7: aload_2
    //   8: invokeinterface hasNext : ()Z
    //   13: ifeq -> 38
    //   16: aload_2
    //   17: invokeinterface next : ()Ljava/lang/Object;
    //   22: astore_3
    //   23: aload_0
    //   24: aload_3
    //   25: invokeinterface containsKey : (Ljava/lang/Object;)Z
    //   30: ifeq -> 35
    //   33: iconst_1
    //   34: ireturn
    //   35: goto -> 7
    //   38: iconst_0
    //   39: ireturn
  }
  
  @NotNull
  private static List<Throwable> allCauses(@NotNull Throwable paramThrowable) {
    ArrayList<Throwable> arrayList = new ArrayList();
    for (Throwable throwable = paramThrowable; throwable.getCause() != null; throwable = throwable.getCause())
      arrayList.add(throwable.getCause()); 
    return arrayList;
  }
  
  @Nullable
  public Long getOrder() {
    return Long.valueOf(1000L);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\DuplicateEventDetectionEventProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
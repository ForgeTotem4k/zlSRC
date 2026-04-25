package io.sentry;

import io.sentry.hints.EventDropReason;
import io.sentry.protocol.SentryException;
import io.sentry.util.HintUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DeduplicateMultithreadedEventProcessor implements EventProcessor {
  @NotNull
  private final Map<String, Long> processedEvents = Collections.synchronizedMap(new HashMap<>());
  
  @NotNull
  private final SentryOptions options;
  
  public DeduplicateMultithreadedEventProcessor(@NotNull SentryOptions paramSentryOptions) {
    this.options = paramSentryOptions;
  }
  
  @Nullable
  public SentryEvent process(@NotNull SentryEvent paramSentryEvent, @NotNull Hint paramHint) {
    if (!HintUtils.hasType(paramHint, UncaughtExceptionHandlerIntegration.UncaughtExceptionHint.class))
      return paramSentryEvent; 
    SentryException sentryException = paramSentryEvent.getUnhandledException();
    if (sentryException == null)
      return paramSentryEvent; 
    String str = sentryException.getType();
    if (str == null)
      return paramSentryEvent; 
    Long long_1 = sentryException.getThreadId();
    if (long_1 == null)
      return paramSentryEvent; 
    Long long_2 = this.processedEvents.get(str);
    if (long_2 != null && !long_2.equals(long_1)) {
      this.options.getLogger().log(SentryLevel.INFO, "Event %s has been dropped due to multi-threaded deduplication", new Object[] { paramSentryEvent.getEventId() });
      HintUtils.setEventDropReason(paramHint, EventDropReason.MULTITHREADED_DEDUPLICATION);
      return null;
    } 
    this.processedEvents.put(str, long_1);
    return paramSentryEvent;
  }
  
  @Nullable
  public Long getOrder() {
    return Long.valueOf(7000L);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\DeduplicateMultithreadedEventProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
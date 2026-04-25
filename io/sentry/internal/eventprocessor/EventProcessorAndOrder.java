package io.sentry.internal.eventprocessor;

import io.sentry.EventProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EventProcessorAndOrder implements Comparable<EventProcessorAndOrder> {
  @NotNull
  private final EventProcessor eventProcessor;
  
  @NotNull
  private final Long order;
  
  public EventProcessorAndOrder(@NotNull EventProcessor paramEventProcessor, @Nullable Long paramLong) {
    this.eventProcessor = paramEventProcessor;
    if (paramLong == null) {
      this.order = Long.valueOf(System.nanoTime());
    } else {
      this.order = paramLong;
    } 
  }
  
  @NotNull
  public EventProcessor getEventProcessor() {
    return this.eventProcessor;
  }
  
  @NotNull
  public Long getOrder() {
    return this.order;
  }
  
  public int compareTo(@NotNull EventProcessorAndOrder paramEventProcessorAndOrder) {
    return this.order.compareTo(paramEventProcessorAndOrder.order);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\internal\eventprocessor\EventProcessorAndOrder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
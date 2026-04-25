package io.sentry.util;

import io.sentry.EventProcessor;
import io.sentry.internal.eventprocessor.EventProcessorAndOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.Nullable;

public final class EventProcessorUtils {
  public static List<EventProcessor> unwrap(@Nullable List<EventProcessorAndOrder> paramList) {
    ArrayList<EventProcessor> arrayList = new ArrayList();
    if (paramList != null)
      for (EventProcessorAndOrder eventProcessorAndOrder : paramList)
        arrayList.add(eventProcessorAndOrder.getEventProcessor());  
    return new CopyOnWriteArrayList<>(arrayList);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\EventProcessorUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
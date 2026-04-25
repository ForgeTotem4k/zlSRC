package io.sentry.clientreport;

import io.sentry.DataCategory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
final class AtomicClientReportStorage implements IClientReportStorage {
  @NotNull
  private final Map<ClientReportKey, AtomicLong> lostEventCounts;
  
  public AtomicClientReportStorage() {
    ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();
    for (DiscardReason discardReason : DiscardReason.values()) {
      for (DataCategory dataCategory : DataCategory.values())
        concurrentHashMap.put(new ClientReportKey(discardReason.getReason(), dataCategory.getCategory()), new AtomicLong(0L)); 
    } 
    this.lostEventCounts = Collections.unmodifiableMap(concurrentHashMap);
  }
  
  public void addCount(ClientReportKey paramClientReportKey, Long paramLong) {
    AtomicLong atomicLong = this.lostEventCounts.get(paramClientReportKey);
    if (atomicLong != null)
      atomicLong.addAndGet(paramLong.longValue()); 
  }
  
  public List<DiscardedEvent> resetCountsAndGet() {
    ArrayList<DiscardedEvent> arrayList = new ArrayList();
    for (Map.Entry<ClientReportKey, AtomicLong> entry : this.lostEventCounts.entrySet()) {
      Long long_ = Long.valueOf(((AtomicLong)entry.getValue()).getAndSet(0L));
      if (long_.longValue() > 0L)
        arrayList.add(new DiscardedEvent(((ClientReportKey)entry.getKey()).getReason(), ((ClientReportKey)entry.getKey()).getCategory(), long_)); 
    } 
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\clientreport\AtomicClientReportStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
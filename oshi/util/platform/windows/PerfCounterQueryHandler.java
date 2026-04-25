package oshi.util.platform.windows;

import com.sun.jna.platform.win32.WinNT;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.NotThreadSafe;
import oshi.jna.ByRef;
import oshi.util.FormatUtil;

@NotThreadSafe
public final class PerfCounterQueryHandler implements AutoCloseable {
  private static final Logger LOG = LoggerFactory.getLogger(PerfCounterQueryHandler.class);
  
  private Map<PerfDataUtil.PerfCounter, ByRef.CloseableHANDLEByReference> counterHandleMap = new HashMap<>();
  
  private ByRef.CloseableHANDLEByReference queryHandle = null;
  
  public boolean addCounterToQuery(PerfDataUtil.PerfCounter paramPerfCounter) {
    if (this.queryHandle == null) {
      this.queryHandle = new ByRef.CloseableHANDLEByReference();
      if (!PerfDataUtil.openQuery((WinNT.HANDLEByReference)this.queryHandle)) {
        LOG.warn("Failed to open a query for PDH counter: {}", paramPerfCounter.getCounterPath());
        this.queryHandle.close();
        this.queryHandle = null;
        return false;
      } 
    } 
    ByRef.CloseableHANDLEByReference closeableHANDLEByReference = new ByRef.CloseableHANDLEByReference();
    if (!PerfDataUtil.addCounter((WinNT.HANDLEByReference)this.queryHandle, paramPerfCounter.getCounterPath(), (WinNT.HANDLEByReference)closeableHANDLEByReference)) {
      LOG.warn("Failed to add counter for PDH counter: {}", paramPerfCounter.getCounterPath());
      closeableHANDLEByReference.close();
      return false;
    } 
    this.counterHandleMap.put(paramPerfCounter, closeableHANDLEByReference);
    return true;
  }
  
  public boolean removeCounterFromQuery(PerfDataUtil.PerfCounter paramPerfCounter) {
    boolean bool = false;
    ByRef.CloseableHANDLEByReference closeableHANDLEByReference = this.counterHandleMap.remove(paramPerfCounter);
    try {
      if (closeableHANDLEByReference != null)
        bool = PerfDataUtil.removeCounter((WinNT.HANDLEByReference)closeableHANDLEByReference); 
      if (closeableHANDLEByReference != null)
        closeableHANDLEByReference.close(); 
    } catch (Throwable throwable) {
      if (closeableHANDLEByReference != null)
        try {
          closeableHANDLEByReference.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    if (this.counterHandleMap.isEmpty()) {
      PerfDataUtil.closeQuery((WinNT.HANDLEByReference)this.queryHandle);
      this.queryHandle.close();
      this.queryHandle = null;
    } 
    return bool;
  }
  
  public void removeAllCounters() {
    for (ByRef.CloseableHANDLEByReference closeableHANDLEByReference : this.counterHandleMap.values()) {
      PerfDataUtil.removeCounter((WinNT.HANDLEByReference)closeableHANDLEByReference);
      closeableHANDLEByReference.close();
    } 
    this.counterHandleMap.clear();
    if (this.queryHandle != null) {
      PerfDataUtil.closeQuery((WinNT.HANDLEByReference)this.queryHandle);
      this.queryHandle.close();
      this.queryHandle = null;
    } 
  }
  
  public long updateQuery() {
    if (this.queryHandle == null) {
      LOG.warn("Query does not exist to update.");
      return 0L;
    } 
    return PerfDataUtil.updateQueryTimestamp((WinNT.HANDLEByReference)this.queryHandle);
  }
  
  public long queryCounter(PerfDataUtil.PerfCounter paramPerfCounter) {
    if (!this.counterHandleMap.containsKey(paramPerfCounter)) {
      if (LOG.isWarnEnabled())
        LOG.warn("Counter {} does not exist to query.", paramPerfCounter.getCounterPath()); 
      return 0L;
    } 
    long l = paramPerfCounter.isBaseCounter() ? PerfDataUtil.querySecondCounter((WinNT.HANDLEByReference)this.counterHandleMap.get(paramPerfCounter)) : PerfDataUtil.queryCounter((WinNT.HANDLEByReference)this.counterHandleMap.get(paramPerfCounter));
    if (l < 0L) {
      if (LOG.isWarnEnabled())
        LOG.warn("Error querying counter {}: {}", paramPerfCounter.getCounterPath(), String.format(Locale.ROOT, FormatUtil.formatError((int)l), new Object[0])); 
      return 0L;
    } 
    return l;
  }
  
  public void close() {
    removeAllCounters();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\platform\windows\PerfCounterQueryHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
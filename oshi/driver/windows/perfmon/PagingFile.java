package oshi.driver.windows.perfmon;

import java.util.Collections;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterQuery;

@ThreadSafe
public final class PagingFile {
  public static Map<PagingPercentProperty, Long> querySwapUsed() {
    return PerfmonDisabled.PERF_OS_DISABLED ? Collections.emptyMap() : PerfCounterQuery.queryValues(PagingPercentProperty.class, "Paging File", "Win32_PerfRawData_PerfOS_PagingFile");
  }
  
  public enum PagingPercentProperty implements PerfCounterQuery.PdhCounterProperty {
    PERCENTUSAGE("_Total", "% Usage");
    
    private final String instance;
    
    private final String counter;
    
    PagingPercentProperty(String param1String1, String param1String2) {
      this.instance = param1String1;
      this.counter = param1String2;
    }
    
    public String getInstance() {
      return this.instance;
    }
    
    public String getCounter() {
      return this.counter;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\perfmon\PagingFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
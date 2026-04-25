package oshi.driver.windows.perfmon;

import java.util.Collections;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterQuery;

@ThreadSafe
public final class SystemInformation {
  public static Map<ContextSwitchProperty, Long> queryContextSwitchCounters() {
    return PerfmonDisabled.PERF_OS_DISABLED ? Collections.emptyMap() : PerfCounterQuery.queryValues(ContextSwitchProperty.class, "System", "Win32_PerfRawData_PerfOS_System");
  }
  
  public static Map<ProcessorQueueLengthProperty, Long> queryProcessorQueueLength() {
    return PerfmonDisabled.PERF_OS_DISABLED ? Collections.emptyMap() : PerfCounterQuery.queryValues(ProcessorQueueLengthProperty.class, "System", "Win32_PerfRawData_PerfOS_System");
  }
  
  public enum ContextSwitchProperty implements PerfCounterQuery.PdhCounterProperty {
    CONTEXTSWITCHESPERSEC(null, "Context Switches/sec");
    
    private final String instance;
    
    private final String counter;
    
    ContextSwitchProperty(String param1String1, String param1String2) {
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
  
  public enum ProcessorQueueLengthProperty implements PerfCounterQuery.PdhCounterProperty {
    PROCESSORQUEUELENGTH(null, "Processor Queue Length");
    
    private final String instance;
    
    private final String counter;
    
    ProcessorQueueLengthProperty(String param1String1, String param1String2) {
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


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\perfmon\SystemInformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
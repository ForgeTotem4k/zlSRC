package oshi.driver.windows.perfmon;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterWildcardQuery;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class ProcessInformation {
  public static Pair<List<String>, Map<ProcessPerformanceProperty, List<Long>>> queryProcessCounters() {
    return PerfmonDisabled.PERF_PROC_DISABLED ? new Pair(Collections.emptyList(), Collections.emptyMap()) : PerfCounterWildcardQuery.queryInstancesAndValues(ProcessPerformanceProperty.class, "Process", "Win32_PerfRawData_PerfProc_Process WHERE NOT Name LIKE \"%_Total\"");
  }
  
  public static Pair<List<String>, Map<HandleCountProperty, List<Long>>> queryHandles() {
    return PerfmonDisabled.PERF_PROC_DISABLED ? new Pair(Collections.emptyList(), Collections.emptyMap()) : PerfCounterWildcardQuery.queryInstancesAndValues(HandleCountProperty.class, "Process", "Win32_PerfRawData_PerfProc_Process");
  }
  
  public static Pair<List<String>, Map<IdleProcessorTimeProperty, List<Long>>> queryIdleProcessCounters() {
    return PerfmonDisabled.PERF_OS_DISABLED ? new Pair(Collections.emptyList(), Collections.emptyMap()) : PerfCounterWildcardQuery.queryInstancesAndValues(IdleProcessorTimeProperty.class, "Process", "Win32_PerfRawData_PerfProc_Process WHERE IDProcess=0");
  }
  
  public enum ProcessPerformanceProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
    NAME("^*_Total"),
    PRIORITYBASE("Priority Base"),
    ELAPSEDTIME("Elapsed Time"),
    IDPROCESS("ID Process"),
    CREATINGPROCESSID("Creating Process ID"),
    IOREADBYTESPERSEC("IO Read Bytes/sec"),
    IOWRITEBYTESPERSEC("IO Write Bytes/sec"),
    PRIVATEBYTES("Working Set - Private"),
    PAGEFAULTSPERSEC("Page Faults/sec");
    
    private final String counter;
    
    ProcessPerformanceProperty(String param1String1) {
      this.counter = param1String1;
    }
    
    public String getCounter() {
      return this.counter;
    }
  }
  
  public enum HandleCountProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
    NAME("_Total"),
    HANDLECOUNT("Handle Count");
    
    private final String counter;
    
    HandleCountProperty(String param1String1) {
      this.counter = param1String1;
    }
    
    public String getCounter() {
      return this.counter;
    }
  }
  
  public enum IdleProcessorTimeProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
    NAME("_Total|Idle"),
    PERCENTPROCESSORTIME("% Processor Time"),
    ELAPSEDTIME("Elapsed Time");
    
    private final String counter;
    
    IdleProcessorTimeProperty(String param1String1) {
      this.counter = param1String1;
    }
    
    public String getCounter() {
      return this.counter;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\perfmon\ProcessInformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
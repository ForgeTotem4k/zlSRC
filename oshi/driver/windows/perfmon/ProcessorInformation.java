package oshi.driver.windows.perfmon;

import com.sun.jna.platform.win32.VersionHelpers;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterQuery;
import oshi.util.platform.windows.PerfCounterWildcardQuery;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class ProcessorInformation {
  private static final boolean IS_WIN7_OR_GREATER = VersionHelpers.IsWindows7OrGreater();
  
  public static Pair<List<String>, Map<ProcessorTickCountProperty, List<Long>>> queryProcessorCounters() {
    return PerfmonDisabled.PERF_OS_DISABLED ? new Pair(Collections.emptyList(), Collections.emptyMap()) : (IS_WIN7_OR_GREATER ? PerfCounterWildcardQuery.queryInstancesAndValues(ProcessorTickCountProperty.class, "Processor Information", "Win32_PerfRawData_Counters_ProcessorInformation WHERE NOT Name LIKE \"%_Total\"") : PerfCounterWildcardQuery.queryInstancesAndValues(ProcessorTickCountProperty.class, "Processor", "Win32_PerfRawData_PerfOS_Processor WHERE Name!=\"_Total\""));
  }
  
  public static Map<SystemTickCountProperty, Long> querySystemCounters() {
    return PerfCounterQuery.queryValues(SystemTickCountProperty.class, "Processor", "Win32_PerfRawData_PerfOS_Processor WHERE Name=\"_Total\"");
  }
  
  public static Pair<List<String>, Map<ProcessorUtilityTickCountProperty, List<Long>>> queryProcessorCapacityCounters() {
    return PerfmonDisabled.PERF_OS_DISABLED ? new Pair(Collections.emptyList(), Collections.emptyMap()) : PerfCounterWildcardQuery.queryInstancesAndValues(ProcessorUtilityTickCountProperty.class, "Processor Information", "Win32_PerfRawData_Counters_ProcessorInformation WHERE NOT Name LIKE \"%_Total\"");
  }
  
  public static Map<InterruptsProperty, Long> queryInterruptCounters() {
    return PerfmonDisabled.PERF_OS_DISABLED ? Collections.emptyMap() : PerfCounterQuery.queryValues(InterruptsProperty.class, "Processor", "Win32_PerfRawData_PerfOS_Processor WHERE Name=\"_Total\"");
  }
  
  public static Pair<List<String>, Map<ProcessorFrequencyProperty, List<Long>>> queryFrequencyCounters() {
    return PerfmonDisabled.PERF_OS_DISABLED ? new Pair(Collections.emptyList(), Collections.emptyMap()) : PerfCounterWildcardQuery.queryInstancesAndValues(ProcessorFrequencyProperty.class, "Processor Information", "Win32_PerfRawData_Counters_ProcessorInformation WHERE NOT Name LIKE \"%_Total\"");
  }
  
  public enum ProcessorTickCountProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
    NAME("^*_Total"),
    PERCENTDPCTIME("% DPC Time"),
    PERCENTINTERRUPTTIME("% Interrupt Time"),
    PERCENTPRIVILEGEDTIME("% Privileged Time"),
    PERCENTPROCESSORTIME("% Processor Time"),
    PERCENTUSERTIME("% User Time");
    
    private final String counter;
    
    ProcessorTickCountProperty(String param1String1) {
      this.counter = param1String1;
    }
    
    public String getCounter() {
      return this.counter;
    }
  }
  
  public enum SystemTickCountProperty implements PerfCounterQuery.PdhCounterProperty {
    PERCENTDPCTIME("_Total", "% DPC Time"),
    PERCENTINTERRUPTTIME("_Total", "% Interrupt Time");
    
    private final String instance;
    
    private final String counter;
    
    SystemTickCountProperty(String param1String1, String param1String2) {
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
  
  public enum ProcessorUtilityTickCountProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
    NAME("^*_Total"),
    PERCENTDPCTIME("% DPC Time"),
    PERCENTINTERRUPTTIME("% Interrupt Time"),
    PERCENTPRIVILEGEDTIME("% Privileged Time"),
    PERCENTPROCESSORTIME("% Processor Time"),
    TIMESTAMP_SYS100NS("% Processor Time_Base"),
    PERCENTPRIVILEGEDUTILITY("% Privileged Utility"),
    PERCENTPROCESSORUTILITY("% Processor Utility"),
    PERCENTPROCESSORUTILITY_BASE("% Processor Utility_Base"),
    PERCENTUSERTIME("% User Time");
    
    private final String counter;
    
    ProcessorUtilityTickCountProperty(String param1String1) {
      this.counter = param1String1;
    }
    
    public String getCounter() {
      return this.counter;
    }
  }
  
  public enum InterruptsProperty implements PerfCounterQuery.PdhCounterProperty {
    INTERRUPTSPERSEC("_Total", "Interrupts/sec");
    
    private final String instance;
    
    private final String counter;
    
    InterruptsProperty(String param1String1, String param1String2) {
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
  
  public enum ProcessorFrequencyProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
    NAME("^*_Total"),
    PERCENTOFMAXIMUMFREQUENCY("% of Maximum Frequency");
    
    private final String counter;
    
    ProcessorFrequencyProperty(String param1String1) {
      this.counter = param1String1;
    }
    
    public String getCounter() {
      return this.counter;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\perfmon\ProcessorInformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
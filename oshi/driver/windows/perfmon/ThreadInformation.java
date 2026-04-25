package oshi.driver.windows.perfmon;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.PerfCounterWildcardQuery;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class ThreadInformation {
  public static Pair<List<String>, Map<ThreadPerformanceProperty, List<Long>>> queryThreadCounters() {
    return PerfCounterWildcardQuery.queryInstancesAndValues(ThreadPerformanceProperty.class, "Thread", "Win32_PerfRawData_PerfProc_Thread WHERE NOT Name LIKE \"%_Total\"");
  }
  
  public static Pair<List<String>, Map<ThreadPerformanceProperty, List<Long>>> queryThreadCounters(String paramString, int paramInt) {
    String str = paramString.toLowerCase(Locale.ROOT);
    return (paramInt >= 0) ? PerfCounterWildcardQuery.queryInstancesAndValues(ThreadPerformanceProperty.class, "Thread", "Win32_PerfRawData_PerfProc_Thread WHERE Name LIKE \\\"" + str + "\\\" AND IDThread=" + paramInt, str + "/" + paramInt) : PerfCounterWildcardQuery.queryInstancesAndValues(ThreadPerformanceProperty.class, "Thread", "Win32_PerfRawData_PerfProc_Thread WHERE Name LIKE \\\"" + str + "\\\"", str + "/*");
  }
  
  public enum ThreadPerformanceProperty implements PerfCounterWildcardQuery.PdhCounterWildcardProperty {
    NAME("^*_Total"),
    PERCENTUSERTIME("% User Time"),
    PERCENTPRIVILEGEDTIME("% Privileged Time"),
    ELAPSEDTIME("Elapsed Time"),
    PRIORITYCURRENT("Priority Current"),
    STARTADDRESS("Start Address"),
    THREADSTATE("Thread State"),
    THREADWAITREASON("Thread Wait Reason"),
    IDPROCESS("ID Process"),
    IDTHREAD("ID Thread"),
    CONTEXTSWITCHESPERSEC("Context Switches/sec");
    
    private final String counter;
    
    ThreadPerformanceProperty(String param1String1) {
      this.counter = param1String1;
    }
    
    public String getCounter() {
      return this.counter;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\perfmon\ThreadInformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
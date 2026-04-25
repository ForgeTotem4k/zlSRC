package oshi.driver.windows.registry;

import com.sun.jna.platform.win32.WinBase;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.ProcessInformation;
import oshi.util.GlobalConfig;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class ProcessPerformanceData {
  private static final String PROCESS = "Process";
  
  private static final boolean PERFDATA = GlobalConfig.get("oshi.os.windows.hkeyperfdata", true);
  
  public static Map<Integer, PerfCounterBlock> buildProcessMapFromRegistry(Collection<Integer> paramCollection) {
    Triplet<List<Map<ProcessInformation.ProcessPerformanceProperty, Object>>, Long, Long> triplet = null;
    if (PERFDATA)
      triplet = HkeyPerformanceDataUtil.readPerfDataFromRegistry("Process", ProcessInformation.ProcessPerformanceProperty.class); 
    if (triplet == null)
      return null; 
    List list = (List)triplet.getA();
    long l = ((Long)triplet.getC()).longValue();
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (Map map : list) {
      int i = ((Integer)map.get(ProcessInformation.ProcessPerformanceProperty.IDPROCESS)).intValue();
      String str = (String)map.get(ProcessInformation.ProcessPerformanceProperty.NAME);
      if ((paramCollection == null || paramCollection.contains(Integer.valueOf(i))) && !"_Total".equals(str)) {
        long l1 = ((Long)map.get(ProcessInformation.ProcessPerformanceProperty.ELAPSEDTIME)).longValue();
        if (l1 > l)
          l1 = WinBase.FILETIME.filetimeToDate((int)(l1 >> 32L), (int)(l1 & 0xFFFFFFFFL)).getTime(); 
        long l2 = l - l1;
        if (l2 < 1L)
          l2 = 1L; 
        hashMap.put(Integer.valueOf(i), new PerfCounterBlock(str, ((Integer)map.get(ProcessInformation.ProcessPerformanceProperty.CREATINGPROCESSID)).intValue(), ((Integer)map.get(ProcessInformation.ProcessPerformanceProperty.PRIORITYBASE)).intValue(), ((Long)map.get(ProcessInformation.ProcessPerformanceProperty.PRIVATEBYTES)).longValue(), l1, l2, ((Long)map.get(ProcessInformation.ProcessPerformanceProperty.IOREADBYTESPERSEC)).longValue(), ((Long)map.get(ProcessInformation.ProcessPerformanceProperty.IOWRITEBYTESPERSEC)).longValue(), ((Integer)map.get(ProcessInformation.ProcessPerformanceProperty.PAGEFAULTSPERSEC)).intValue()));
      } 
    } 
    return (Map)hashMap;
  }
  
  public static Map<Integer, PerfCounterBlock> buildProcessMapFromPerfCounters(Collection<Integer> paramCollection) {
    return buildProcessMapFromPerfCounters(paramCollection, null);
  }
  
  public static Map<Integer, PerfCounterBlock> buildProcessMapFromPerfCounters(Collection<Integer> paramCollection, String paramString) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    Pair pair = ProcessInformation.queryProcessCounters();
    long l = System.currentTimeMillis();
    List<String> list = (List)pair.getA();
    Map map = (Map)pair.getB();
    List<Long> list1 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.IDPROCESS);
    List<Long> list2 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.CREATINGPROCESSID);
    List<Long> list3 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.PRIORITYBASE);
    List<Long> list4 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.IOREADBYTESPERSEC);
    List<Long> list5 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.IOWRITEBYTESPERSEC);
    List<Long> list6 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.PRIVATEBYTES);
    List<Long> list7 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.ELAPSEDTIME);
    List<Long> list8 = (List)map.get(ProcessInformation.ProcessPerformanceProperty.PAGEFAULTSPERSEC);
    for (byte b = 0; b < list.size(); b++) {
      int i = ((Long)list1.get(b)).intValue();
      if (paramCollection == null || paramCollection.contains(Integer.valueOf(i))) {
        long l1 = ((Long)list7.get(b)).longValue();
        if (l1 > l)
          l1 = WinBase.FILETIME.filetimeToDate((int)(l1 >> 32L), (int)(l1 & 0xFFFFFFFFL)).getTime(); 
        long l2 = l - l1;
        if (l2 < 1L)
          l2 = 1L; 
        hashMap.put(Integer.valueOf(i), new PerfCounterBlock(list.get(b), ((Long)list2.get(b)).intValue(), ((Long)list3.get(b)).intValue(), ((Long)list6.get(b)).longValue(), l1, l2, ((Long)list4.get(b)).longValue(), ((Long)list5.get(b)).longValue(), ((Long)list8.get(b)).intValue()));
      } 
    } 
    return (Map)hashMap;
  }
  
  @Immutable
  public static class PerfCounterBlock {
    private final String name;
    
    private final int parentProcessID;
    
    private final int priority;
    
    private final long residentSetSize;
    
    private final long startTime;
    
    private final long upTime;
    
    private final long bytesRead;
    
    private final long bytesWritten;
    
    private final int pageFaults;
    
    public PerfCounterBlock(String param1String, int param1Int1, int param1Int2, long param1Long1, long param1Long2, long param1Long3, long param1Long4, long param1Long5, int param1Int3) {
      this.name = param1String;
      this.parentProcessID = param1Int1;
      this.priority = param1Int2;
      this.residentSetSize = param1Long1;
      this.startTime = param1Long2;
      this.upTime = param1Long3;
      this.bytesRead = param1Long4;
      this.bytesWritten = param1Long5;
      this.pageFaults = param1Int3;
    }
    
    public String getName() {
      return this.name;
    }
    
    public int getParentProcessID() {
      return this.parentProcessID;
    }
    
    public int getPriority() {
      return this.priority;
    }
    
    public long getResidentSetSize() {
      return this.residentSetSize;
    }
    
    public long getStartTime() {
      return this.startTime;
    }
    
    public long getUpTime() {
      return this.upTime;
    }
    
    public long getBytesRead() {
      return this.bytesRead;
    }
    
    public long getBytesWritten() {
      return this.bytesWritten;
    }
    
    public long getPageFaults() {
      return this.pageFaults;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\registry\ProcessPerformanceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
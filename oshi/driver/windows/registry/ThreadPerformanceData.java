package oshi.driver.windows.registry;

import com.sun.jna.platform.win32.WinBase;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.perfmon.PerfmonDisabled;
import oshi.driver.windows.perfmon.ThreadInformation;
import oshi.util.Util;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class ThreadPerformanceData {
  private static final String THREAD = "Thread";
  
  public static Map<Integer, PerfCounterBlock> buildThreadMapFromRegistry(Collection<Integer> paramCollection) {
    Triplet<List<Map<ThreadInformation.ThreadPerformanceProperty, Object>>, Long, Long> triplet = HkeyPerformanceDataUtil.readPerfDataFromRegistry("Thread", ThreadInformation.ThreadPerformanceProperty.class);
    if (triplet == null)
      return null; 
    List list = (List)triplet.getA();
    long l1 = ((Long)triplet.getB()).longValue();
    long l2 = ((Long)triplet.getC()).longValue();
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (Map map : list) {
      int i = ((Integer)map.get(ThreadInformation.ThreadPerformanceProperty.IDPROCESS)).intValue();
      if ((paramCollection == null || paramCollection.contains(Integer.valueOf(i))) && i > 0) {
        int j = ((Integer)map.get(ThreadInformation.ThreadPerformanceProperty.IDTHREAD)).intValue();
        String str = (String)map.get(ThreadInformation.ThreadPerformanceProperty.NAME);
        long l3 = (l1 - ((Long)map.get(ThreadInformation.ThreadPerformanceProperty.ELAPSEDTIME)).longValue()) / 10000L;
        if (l3 < 1L)
          l3 = 1L; 
        long l4 = ((Long)map.get(ThreadInformation.ThreadPerformanceProperty.PERCENTUSERTIME)).longValue() / 10000L;
        long l5 = ((Long)map.get(ThreadInformation.ThreadPerformanceProperty.PERCENTPRIVILEGEDTIME)).longValue() / 10000L;
        int k = ((Integer)map.get(ThreadInformation.ThreadPerformanceProperty.PRIORITYCURRENT)).intValue();
        int m = ((Integer)map.get(ThreadInformation.ThreadPerformanceProperty.THREADSTATE)).intValue();
        int n = ((Integer)map.get(ThreadInformation.ThreadPerformanceProperty.THREADWAITREASON)).intValue();
        Object object = map.get(ThreadInformation.ThreadPerformanceProperty.STARTADDRESS);
        long l6 = object.getClass().equals(Long.class) ? ((Long)object).longValue() : Integer.toUnsignedLong(((Integer)object).intValue());
        int i1 = ((Integer)map.get(ThreadInformation.ThreadPerformanceProperty.CONTEXTSWITCHESPERSEC)).intValue();
        hashMap.put(Integer.valueOf(j), new PerfCounterBlock(str, j, i, l2 - l3, l4, l5, k, m, n, l6, i1));
      } 
    } 
    return (Map)hashMap;
  }
  
  public static Map<Integer, PerfCounterBlock> buildThreadMapFromPerfCounters(Collection<Integer> paramCollection) {
    return buildThreadMapFromPerfCounters(paramCollection, null, -1);
  }
  
  public static Map<Integer, PerfCounterBlock> buildThreadMapFromPerfCounters(Collection<Integer> paramCollection, String paramString, int paramInt) {
    if (PerfmonDisabled.PERF_PROC_DISABLED)
      return Collections.emptyMap(); 
    HashMap<Object, Object> hashMap = new HashMap<>();
    Pair pair = Util.isBlank(paramString) ? ThreadInformation.queryThreadCounters() : ThreadInformation.queryThreadCounters(paramString, paramInt);
    long l = System.currentTimeMillis();
    List list = (List)pair.getA();
    Map map = (Map)pair.getB();
    List<Long> list1 = (List)map.get(ThreadInformation.ThreadPerformanceProperty.IDTHREAD);
    List<Long> list2 = (List)map.get(ThreadInformation.ThreadPerformanceProperty.IDPROCESS);
    List<Long> list3 = (List)map.get(ThreadInformation.ThreadPerformanceProperty.PERCENTUSERTIME);
    List<Long> list4 = (List)map.get(ThreadInformation.ThreadPerformanceProperty.PERCENTPRIVILEGEDTIME);
    List<Long> list5 = (List)map.get(ThreadInformation.ThreadPerformanceProperty.ELAPSEDTIME);
    List<Long> list6 = (List)map.get(ThreadInformation.ThreadPerformanceProperty.PRIORITYCURRENT);
    List<Long> list7 = (List)map.get(ThreadInformation.ThreadPerformanceProperty.THREADSTATE);
    List<Long> list8 = (List)map.get(ThreadInformation.ThreadPerformanceProperty.THREADWAITREASON);
    List<Long> list9 = (List)map.get(ThreadInformation.ThreadPerformanceProperty.STARTADDRESS);
    List<Long> list10 = (List)map.get(ThreadInformation.ThreadPerformanceProperty.CONTEXTSWITCHESPERSEC);
    byte b1 = 0;
    for (byte b2 = 0; b2 < list.size(); b2++) {
      int i = ((Long)list2.get(b2)).intValue();
      if (paramCollection == null || paramCollection.contains(Integer.valueOf(i))) {
        int j = ((Long)list1.get(b2)).intValue();
        String str = Integer.toString(b1++);
        long l1 = ((Long)list5.get(b2)).longValue();
        int k = (int)(l1 >> 32L);
        int m = (int)(l1 & 0xFFFFFFFFL);
        l1 = WinBase.FILETIME.filetimeToDate(k, m).getTime();
        if (l1 > l)
          l1 = l - 1L; 
        long l2 = ((Long)list3.get(b2)).longValue() / 10000L;
        long l3 = ((Long)list4.get(b2)).longValue() / 10000L;
        int n = ((Long)list6.get(b2)).intValue();
        int i1 = ((Long)list7.get(b2)).intValue();
        int i2 = ((Long)list8.get(b2)).intValue();
        long l4 = ((Long)list9.get(b2)).longValue();
        int i3 = ((Long)list10.get(b2)).intValue();
        hashMap.put(Integer.valueOf(j), new PerfCounterBlock(str, j, i, l1, l2, l3, n, i1, i2, l4, i3));
      } 
    } 
    return (Map)hashMap;
  }
  
  @Immutable
  public static class PerfCounterBlock {
    private final String name;
    
    private final int threadID;
    
    private final int owningProcessID;
    
    private final long startTime;
    
    private final long userTime;
    
    private final long kernelTime;
    
    private final int priority;
    
    private final int threadState;
    
    private final int threadWaitReason;
    
    private final long startAddress;
    
    private final int contextSwitches;
    
    public PerfCounterBlock(String param1String, int param1Int1, int param1Int2, long param1Long1, long param1Long2, long param1Long3, int param1Int3, int param1Int4, int param1Int5, long param1Long4, int param1Int6) {
      this.name = param1String;
      this.threadID = param1Int1;
      this.owningProcessID = param1Int2;
      this.startTime = param1Long1;
      this.userTime = param1Long2;
      this.kernelTime = param1Long3;
      this.priority = param1Int3;
      this.threadState = param1Int4;
      this.threadWaitReason = param1Int5;
      this.startAddress = param1Long4;
      this.contextSwitches = param1Int6;
    }
    
    public String getName() {
      return this.name;
    }
    
    public int getThreadID() {
      return this.threadID;
    }
    
    public int getOwningProcessID() {
      return this.owningProcessID;
    }
    
    public long getStartTime() {
      return this.startTime;
    }
    
    public long getUserTime() {
      return this.userTime;
    }
    
    public long getKernelTime() {
      return this.kernelTime;
    }
    
    public int getPriority() {
      return this.priority;
    }
    
    public int getThreadState() {
      return this.threadState;
    }
    
    public int getThreadWaitReason() {
      return this.threadWaitReason;
    }
    
    public long getStartAddress() {
      return this.startAddress;
    }
    
    public int getContextSwitches() {
      return this.contextSwitches;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\registry\ThreadPerformanceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
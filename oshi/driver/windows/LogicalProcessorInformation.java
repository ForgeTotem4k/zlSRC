package oshi.driver.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.WinNT;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.wmi.Win32Processor;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class LogicalProcessorInformation {
  private static final boolean IS_WIN10_OR_GREATER = VersionHelpers.IsWindows10OrGreater();
  
  public static Triplet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>> getLogicalProcessorInformationEx() {
    WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX[] arrayOfSYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX = Kernel32Util.getLogicalProcessorInformationEx(65535);
    ArrayList<WinNT.GROUP_AFFINITY[]> arrayList = new ArrayList();
    HashSet<CentralProcessor.ProcessorCache> hashSet = new HashSet();
    ArrayList<WinNT.GROUP_AFFINITY> arrayList1 = new ArrayList();
    ArrayList<WinNT.NUMA_NODE_RELATIONSHIP> arrayList2 = new ArrayList();
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    for (WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX sYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX : arrayOfSYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX) {
      WinNT.CACHE_RELATIONSHIP cACHE_RELATIONSHIP;
      WinNT.PROCESSOR_RELATIONSHIP pROCESSOR_RELATIONSHIP;
      switch (sYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX.relationship) {
        case 3:
          arrayList.add(((WinNT.PROCESSOR_RELATIONSHIP)sYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX).groupMask);
          break;
        case 2:
          cACHE_RELATIONSHIP = (WinNT.CACHE_RELATIONSHIP)sYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX;
          hashSet.add(new CentralProcessor.ProcessorCache(cACHE_RELATIONSHIP.level, cACHE_RELATIONSHIP.associativity, cACHE_RELATIONSHIP.lineSize, cACHE_RELATIONSHIP.size, CentralProcessor.ProcessorCache.Type.values()[cACHE_RELATIONSHIP.type]));
          break;
        case 0:
          pROCESSOR_RELATIONSHIP = (WinNT.PROCESSOR_RELATIONSHIP)sYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX;
          arrayList1.add(pROCESSOR_RELATIONSHIP.groupMask[0]);
          if (IS_WIN10_OR_GREATER)
            hashMap1.put(pROCESSOR_RELATIONSHIP.groupMask[0], Integer.valueOf(pROCESSOR_RELATIONSHIP.efficiencyClass)); 
          break;
        case 1:
          arrayList2.add((WinNT.NUMA_NODE_RELATIONSHIP)sYSTEM_LOGICAL_PROCESSOR_INFORMATION_EX);
          break;
      } 
    } 
    arrayList1.sort(Comparator.comparing(paramGROUP_AFFINITY -> Long.valueOf(paramGROUP_AFFINITY.group * 64L + Long.numberOfTrailingZeros(paramGROUP_AFFINITY.mask.longValue()))));
    arrayList.sort(Comparator.comparing(paramArrayOfGROUP_AFFINITY -> Long.valueOf((paramArrayOfGROUP_AFFINITY[0]).group * 64L + Long.numberOfTrailingZeros((paramArrayOfGROUP_AFFINITY[0]).mask.longValue()))));
    arrayList2.sort(Comparator.comparing(paramNUMA_NODE_RELATIONSHIP -> Integer.valueOf(paramNUMA_NODE_RELATIONSHIP.nodeNumber)));
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    WbemcliUtil.WmiResult wmiResult = Win32Processor.queryProcessorId();
    for (byte b = 0; b < wmiResult.getResultCount(); b++)
      hashMap2.put(Integer.valueOf(b), WmiUtil.getString(wmiResult, (Enum)Win32Processor.ProcessorIdProperty.PROCESSORID, b)); 
    ArrayList<CentralProcessor.LogicalProcessor> arrayList3 = new ArrayList();
    HashMap<Object, Object> hashMap3 = new HashMap<>();
    HashMap<Object, Object> hashMap4 = new HashMap<>();
    for (WinNT.NUMA_NODE_RELATIONSHIP nUMA_NODE_RELATIONSHIP : arrayList2) {
      int i = nUMA_NODE_RELATIONSHIP.nodeNumber;
      short s = nUMA_NODE_RELATIONSHIP.groupMask.group;
      long l = nUMA_NODE_RELATIONSHIP.groupMask.mask.longValue();
      int j = Long.numberOfTrailingZeros(l);
      int k = 63 - Long.numberOfLeadingZeros(l);
      for (int m = j; m <= k; m++) {
        if ((l & 1L << m) != 0L) {
          int n = getMatchingCore(arrayList1, s, m);
          int i1 = getMatchingPackage((List<WinNT.GROUP_AFFINITY[]>)arrayList, s, m);
          hashMap3.put(Integer.valueOf(n), Integer.valueOf(i1));
          hashMap4.put(Integer.valueOf(n), hashMap2.getOrDefault(Integer.valueOf(i1), ""));
          CentralProcessor.LogicalProcessor logicalProcessor = new CentralProcessor.LogicalProcessor(m, n, i1, i, s);
          arrayList3.add(logicalProcessor);
        } 
      } 
    } 
    List<CentralProcessor.PhysicalProcessor> list = getPhysProcs(arrayList1, (Map)hashMap1, (Map)hashMap3, (Map)hashMap4);
    return new Triplet(arrayList3, list, AbstractCentralProcessor.orderedProcCaches(hashSet));
  }
  
  private static List<CentralProcessor.PhysicalProcessor> getPhysProcs(List<WinNT.GROUP_AFFINITY> paramList, Map<WinNT.GROUP_AFFINITY, Integer> paramMap, Map<Integer, Integer> paramMap1, Map<Integer, String> paramMap2) {
    ArrayList<CentralProcessor.PhysicalProcessor> arrayList = new ArrayList();
    for (byte b = 0; b < paramList.size(); b++) {
      int i = ((Integer)paramMap.getOrDefault(paramList.get(b), Integer.valueOf(0))).intValue();
      String str = paramMap2.getOrDefault(Integer.valueOf(b), "");
      int j = ((Integer)paramMap1.getOrDefault(Integer.valueOf(b), Integer.valueOf(0))).intValue();
      arrayList.add(new CentralProcessor.PhysicalProcessor(j, b, i, str));
    } 
    return arrayList;
  }
  
  private static int getMatchingPackage(List<WinNT.GROUP_AFFINITY[]> paramList, int paramInt1, int paramInt2) {
    for (byte b = 0; b < paramList.size(); b++) {
      for (byte b1 = 0; b1 < ((WinNT.GROUP_AFFINITY[])paramList.get(b)).length; b1++) {
        if (((((WinNT.GROUP_AFFINITY[])paramList.get(b))[b1]).mask.longValue() & 1L << paramInt2) != 0L && (((WinNT.GROUP_AFFINITY[])paramList.get(b))[b1]).group == paramInt1)
          return b; 
      } 
    } 
    return 0;
  }
  
  private static int getMatchingCore(List<WinNT.GROUP_AFFINITY> paramList, int paramInt1, int paramInt2) {
    for (byte b = 0; b < paramList.size(); b++) {
      if ((((WinNT.GROUP_AFFINITY)paramList.get(b)).mask.longValue() & 1L << paramInt2) != 0L && ((WinNT.GROUP_AFFINITY)paramList.get(b)).group == paramInt1)
        return b; 
    } 
    return 0;
  }
  
  public static Triplet<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>, List<CentralProcessor.ProcessorCache>> getLogicalProcessorInformation() {
    ArrayList<Long> arrayList1 = new ArrayList();
    ArrayList<Long> arrayList2 = new ArrayList();
    WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION[] arrayOfSYSTEM_LOGICAL_PROCESSOR_INFORMATION = Kernel32Util.getLogicalProcessorInformation();
    for (WinNT.SYSTEM_LOGICAL_PROCESSOR_INFORMATION sYSTEM_LOGICAL_PROCESSOR_INFORMATION : arrayOfSYSTEM_LOGICAL_PROCESSOR_INFORMATION) {
      if (sYSTEM_LOGICAL_PROCESSOR_INFORMATION.relationship == 3) {
        arrayList1.add(Long.valueOf(sYSTEM_LOGICAL_PROCESSOR_INFORMATION.processorMask.longValue()));
      } else if (sYSTEM_LOGICAL_PROCESSOR_INFORMATION.relationship == 0) {
        arrayList2.add(Long.valueOf(sYSTEM_LOGICAL_PROCESSOR_INFORMATION.processorMask.longValue()));
      } 
    } 
    arrayList2.sort(null);
    arrayList1.sort(null);
    ArrayList<CentralProcessor.LogicalProcessor> arrayList = new ArrayList();
    for (byte b = 0; b < arrayList2.size(); b++) {
      long l = ((Long)arrayList2.get(b)).longValue();
      int i = Long.numberOfTrailingZeros(l);
      int j = 63 - Long.numberOfLeadingZeros(l);
      for (int k = i; k <= j; k++) {
        if ((l & 1L << k) != 0L) {
          CentralProcessor.LogicalProcessor logicalProcessor = new CentralProcessor.LogicalProcessor(k, b, getBitMatchingPackageNumber(arrayList1, k));
          arrayList.add(logicalProcessor);
        } 
      } 
    } 
    return new Triplet(arrayList, null, null);
  }
  
  private static int getBitMatchingPackageNumber(List<Long> paramList, int paramInt) {
    for (byte b = 0; b < paramList.size(); b++) {
      if ((((Long)paramList.get(b)).longValue() & 1L << paramInt) != 0L)
        return b; 
    } 
    return 0;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\LogicalProcessorInformation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
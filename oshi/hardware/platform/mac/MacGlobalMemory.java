package oshi.hardware.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.jna.ByRef;
import oshi.jna.Struct;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.mac.SysctlUtil;

@ThreadSafe
final class MacGlobalMemory extends AbstractGlobalMemory {
  private static final Logger LOG = LoggerFactory.getLogger(MacGlobalMemory.class);
  
  private final Supplier<Long> available = Memoizer.memoize(this::queryVmStats, Memoizer.defaultExpiration());
  
  private final Supplier<Long> total = Memoizer.memoize(MacGlobalMemory::queryPhysMem);
  
  private final Supplier<Long> pageSize = Memoizer.memoize(MacGlobalMemory::queryPageSize);
  
  private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);
  
  public long getAvailable() {
    return ((Long)this.available.get()).longValue();
  }
  
  public long getTotal() {
    return ((Long)this.total.get()).longValue();
  }
  
  public long getPageSize() {
    return ((Long)this.pageSize.get()).longValue();
  }
  
  public VirtualMemory getVirtualMemory() {
    return this.vm.get();
  }
  
  public List<PhysicalMemory> getPhysicalMemory() {
    ArrayList<PhysicalMemory> arrayList = new ArrayList();
    List list = ExecutingCommand.runNative("system_profiler SPMemoryDataType");
    byte b = 0;
    String str1 = "unknown";
    long l1 = 0L;
    long l2 = 0L;
    String str2 = "unknown";
    String str3 = "unknown";
    String str4 = "unknown";
    String str5 = "unknown";
    for (String str : list) {
      if (str.trim().startsWith("BANK")) {
        if (b++ > 0)
          arrayList.add(new PhysicalMemory(str1, l1, l2, str2, str3, "unknown", str5)); 
        str1 = str.trim();
        int i = str1.lastIndexOf(':');
        if (i > 0)
          str1 = str1.substring(0, i - 1); 
        continue;
      } 
      if (b > 0) {
        String[] arrayOfString = str.trim().split(":");
        if (arrayOfString.length == 2)
          switch (arrayOfString[0]) {
            case "Size":
              l1 = ParseUtil.parseDecimalMemorySizeToBinary(arrayOfString[1].trim());
            case "Type":
              str3 = arrayOfString[1].trim();
            case "Speed":
              l2 = ParseUtil.parseHertz(arrayOfString[1]);
            case "Manufacturer":
              str2 = arrayOfString[1].trim();
            case "Part Number":
              str4 = arrayOfString[1].trim();
            case "Serial Number":
              str5 = arrayOfString[1].trim();
          }  
      } 
    } 
    arrayList.add(new PhysicalMemory(str1, l1, l2, str2, str3, str4, str5));
    return arrayList;
  }
  
  private long queryVmStats() {
    Struct.CloseableVMStatistics closeableVMStatistics = new Struct.CloseableVMStatistics();
    try {
      ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference(closeableVMStatistics.size() / SystemB.INT_SIZE);
      try {
        if (0 != SystemB.INSTANCE.host_statistics(SystemB.INSTANCE.mach_host_self(), 2, (Structure)closeableVMStatistics, (IntByReference)closeableIntByReference)) {
          LOG.error("Failed to get host VM info. Error code: {}", Integer.valueOf(Native.getLastError()));
          long l1 = 0L;
          closeableIntByReference.close();
          closeableVMStatistics.close();
          return l1;
        } 
        long l = (closeableVMStatistics.free_count + closeableVMStatistics.inactive_count) * getPageSize();
        closeableIntByReference.close();
        closeableVMStatistics.close();
        return l;
      } catch (Throwable throwable) {
        try {
          closeableIntByReference.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      try {
        closeableVMStatistics.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  private static long queryPhysMem() {
    return SysctlUtil.sysctl("hw.memsize", 0L);
  }
  
  private static long queryPageSize() {
    ByRef.CloseableLongByReference closeableLongByReference = new ByRef.CloseableLongByReference();
    try {
      if (0 == SystemB.INSTANCE.host_page_size(SystemB.INSTANCE.mach_host_self(), (LongByReference)closeableLongByReference)) {
        long l = closeableLongByReference.getValue();
        closeableLongByReference.close();
        return l;
      } 
      closeableLongByReference.close();
    } catch (Throwable throwable) {
      try {
        closeableLongByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    LOG.error("Failed to get host page size. Error code: {}", Integer.valueOf(Native.getLastError()));
    return 4098L;
  }
  
  private VirtualMemory createVirtualMemory() {
    return (VirtualMemory)new MacVirtualMemory(this);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacGlobalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package oshi.hardware.platform.linux;

import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.software.os.linux.LinuxOperatingSystem;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class LinuxGlobalMemory extends AbstractGlobalMemory {
  private static final long PAGE_SIZE = LinuxOperatingSystem.getPageSize();
  
  private final Supplier<Pair<Long, Long>> availTotal = Memoizer.memoize(LinuxGlobalMemory::readMemInfo, Memoizer.defaultExpiration());
  
  private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);
  
  public long getAvailable() {
    return ((Long)((Pair)this.availTotal.get()).getA()).longValue();
  }
  
  public long getTotal() {
    return ((Long)((Pair)this.availTotal.get()).getB()).longValue();
  }
  
  public long getPageSize() {
    return PAGE_SIZE;
  }
  
  public VirtualMemory getVirtualMemory() {
    return this.vm.get();
  }
  
  private static Pair<Long, Long> readMemInfo() {
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    long l4 = 0L;
    long l5 = 0L;
    List list = FileUtil.readFile(ProcPath.MEMINFO);
    for (String str : list) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str, 2);
      if (arrayOfString.length > 1) {
        long l;
        switch (arrayOfString[0]) {
          case "MemTotal:":
            l5 = ParseUtil.parseDecimalMemorySizeToBinary(arrayOfString[1]);
          case "MemAvailable:":
            l = ParseUtil.parseDecimalMemorySizeToBinary(arrayOfString[1]);
            return new Pair(Long.valueOf(l), Long.valueOf(l5));
          case "MemFree:":
            l1 = ParseUtil.parseDecimalMemorySizeToBinary(arrayOfString[1]);
          case "Active(file):":
            l2 = ParseUtil.parseDecimalMemorySizeToBinary(arrayOfString[1]);
          case "Inactive(file):":
            l3 = ParseUtil.parseDecimalMemorySizeToBinary(arrayOfString[1]);
          case "SReclaimable:":
            l4 = ParseUtil.parseDecimalMemorySizeToBinary(arrayOfString[1]);
        } 
      } 
    } 
    return new Pair(Long.valueOf(l1 + l2 + l3 + l4), Long.valueOf(l5));
  }
  
  private VirtualMemory createVirtualMemory() {
    return (VirtualMemory)new LinuxVirtualMemory(this);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxGlobalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package oshi.hardware.platform.linux;

import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
final class LinuxVirtualMemory extends AbstractVirtualMemory {
  private final LinuxGlobalMemory global;
  
  private final Supplier<Triplet<Long, Long, Long>> usedTotalCommitLim = Memoizer.memoize(LinuxVirtualMemory::queryMemInfo, Memoizer.defaultExpiration());
  
  private final Supplier<Pair<Long, Long>> inOut = Memoizer.memoize(LinuxVirtualMemory::queryVmStat, Memoizer.defaultExpiration());
  
  LinuxVirtualMemory(LinuxGlobalMemory paramLinuxGlobalMemory) {
    this.global = paramLinuxGlobalMemory;
  }
  
  public long getSwapUsed() {
    return ((Long)((Triplet)this.usedTotalCommitLim.get()).getA()).longValue();
  }
  
  public long getSwapTotal() {
    return ((Long)((Triplet)this.usedTotalCommitLim.get()).getB()).longValue();
  }
  
  public long getVirtualMax() {
    return ((Long)((Triplet)this.usedTotalCommitLim.get()).getC()).longValue();
  }
  
  public long getVirtualInUse() {
    return this.global.getTotal() - this.global.getAvailable() + getSwapUsed();
  }
  
  public long getSwapPagesIn() {
    return ((Long)((Pair)this.inOut.get()).getA()).longValue();
  }
  
  public long getSwapPagesOut() {
    return ((Long)((Pair)this.inOut.get()).getB()).longValue();
  }
  
  private static Triplet<Long, Long, Long> queryMemInfo() {
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    List list = FileUtil.readFile(ProcPath.MEMINFO);
    for (String str : list) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str);
      if (arrayOfString.length > 1)
        switch (arrayOfString[0]) {
          case "SwapTotal:":
            l2 = parseMeminfo(arrayOfString);
          case "SwapFree:":
            l1 = parseMeminfo(arrayOfString);
          case "CommitLimit:":
            l3 = parseMeminfo(arrayOfString);
        }  
    } 
    return new Triplet(Long.valueOf(l2 - l1), Long.valueOf(l2), Long.valueOf(l3));
  }
  
  private static Pair<Long, Long> queryVmStat() {
    long l1 = 0L;
    long l2 = 0L;
    List list = FileUtil.readFile(ProcPath.VMSTAT);
    for (String str : list) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str);
      if (arrayOfString.length > 1)
        switch (arrayOfString[0]) {
          case "pswpin":
            l1 = ParseUtil.parseLongOrDefault(arrayOfString[1], 0L);
          case "pswpout":
            l2 = ParseUtil.parseLongOrDefault(arrayOfString[1], 0L);
        }  
    } 
    return new Pair(Long.valueOf(l1), Long.valueOf(l2));
  }
  
  private static long parseMeminfo(String[] paramArrayOfString) {
    if (paramArrayOfString.length < 2)
      return 0L; 
    long l = ParseUtil.parseLongOrDefault(paramArrayOfString[1], 0L);
    if (paramArrayOfString.length > 2 && "kB".equals(paramArrayOfString[2]))
      l *= 1024L; 
    return l;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\linux\LinuxVirtualMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
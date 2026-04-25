package oshi.hardware.platform.unix.openbsd;

import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.tuples.Triplet;

@ThreadSafe
final class OpenBsdVirtualMemory extends AbstractVirtualMemory {
  private final OpenBsdGlobalMemory global;
  
  private final Supplier<Triplet<Integer, Integer, Integer>> usedTotalPgin = Memoizer.memoize(OpenBsdVirtualMemory::queryVmstat, Memoizer.defaultExpiration());
  
  private final Supplier<Integer> pgout = Memoizer.memoize(OpenBsdVirtualMemory::queryUvm, Memoizer.defaultExpiration());
  
  OpenBsdVirtualMemory(OpenBsdGlobalMemory paramOpenBsdGlobalMemory) {
    this.global = paramOpenBsdGlobalMemory;
  }
  
  public long getSwapUsed() {
    return ((Integer)((Triplet)this.usedTotalPgin.get()).getA()).intValue() * this.global.getPageSize();
  }
  
  public long getSwapTotal() {
    return ((Integer)((Triplet)this.usedTotalPgin.get()).getB()).intValue() * this.global.getPageSize();
  }
  
  public long getVirtualMax() {
    return this.global.getTotal() + getSwapTotal();
  }
  
  public long getVirtualInUse() {
    return this.global.getTotal() - this.global.getAvailable() + getSwapUsed();
  }
  
  public long getSwapPagesIn() {
    return ((Integer)((Triplet)this.usedTotalPgin.get()).getC()).intValue() * this.global.getPageSize();
  }
  
  public long getSwapPagesOut() {
    return ((Integer)this.pgout.get()).intValue() * this.global.getPageSize();
  }
  
  private static Triplet<Integer, Integer, Integer> queryVmstat() {
    int i = 0;
    int j = 0;
    int k = 0;
    for (String str : ExecutingCommand.runNative("vmstat -s")) {
      if (str.contains("swap pages in use")) {
        i = ParseUtil.getFirstIntValue(str);
        continue;
      } 
      if (str.contains("swap pages")) {
        j = ParseUtil.getFirstIntValue(str);
        continue;
      } 
      if (str.contains("pagein operations"))
        k = ParseUtil.getFirstIntValue(str); 
    } 
    return new Triplet(Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k));
  }
  
  private static int queryUvm() {
    for (String str : ExecutingCommand.runNative("systat -ab uvm")) {
      if (str.contains("pdpageouts"))
        return ParseUtil.getFirstIntValue(str); 
    } 
    return 0;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\openbsd\OpenBsdVirtualMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
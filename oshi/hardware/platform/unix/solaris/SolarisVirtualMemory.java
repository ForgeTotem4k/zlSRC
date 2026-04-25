package oshi.hardware.platform.unix.solaris;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.solaris.kstat.SystemPages;
import oshi.hardware.common.AbstractVirtualMemory;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
final class SolarisVirtualMemory extends AbstractVirtualMemory {
  private static final Pattern SWAP_INFO = Pattern.compile(".+\\s(\\d+)K\\s+(\\d+)K$");
  
  private final SolarisGlobalMemory global;
  
  private final Supplier<Pair<Long, Long>> availTotal = Memoizer.memoize(SystemPages::queryAvailableTotal, Memoizer.defaultExpiration());
  
  private final Supplier<Pair<Long, Long>> usedTotal = Memoizer.memoize(SolarisVirtualMemory::querySwapInfo, Memoizer.defaultExpiration());
  
  private final Supplier<Long> pagesIn = Memoizer.memoize(SolarisVirtualMemory::queryPagesIn, Memoizer.defaultExpiration());
  
  private final Supplier<Long> pagesOut = Memoizer.memoize(SolarisVirtualMemory::queryPagesOut, Memoizer.defaultExpiration());
  
  SolarisVirtualMemory(SolarisGlobalMemory paramSolarisGlobalMemory) {
    this.global = paramSolarisGlobalMemory;
  }
  
  public long getSwapUsed() {
    return ((Long)((Pair)this.usedTotal.get()).getA()).longValue();
  }
  
  public long getSwapTotal() {
    return ((Long)((Pair)this.usedTotal.get()).getB()).longValue();
  }
  
  public long getVirtualMax() {
    return this.global.getPageSize() * ((Long)((Pair)this.availTotal.get()).getB()).longValue() + getSwapTotal();
  }
  
  public long getVirtualInUse() {
    return this.global.getPageSize() * (((Long)((Pair)this.availTotal.get()).getB()).longValue() - ((Long)((Pair)this.availTotal.get()).getA()).longValue()) + getSwapUsed();
  }
  
  public long getSwapPagesIn() {
    return ((Long)this.pagesIn.get()).longValue();
  }
  
  public long getSwapPagesOut() {
    return ((Long)this.pagesOut.get()).longValue();
  }
  
  private static long queryPagesIn() {
    long l = 0L;
    for (String str : ExecutingCommand.runNative("kstat -p cpu_stat:::pgswapin"))
      l += ParseUtil.parseLastLong(str, 0L); 
    return l;
  }
  
  private static long queryPagesOut() {
    long l = 0L;
    for (String str : ExecutingCommand.runNative("kstat -p cpu_stat:::pgswapout"))
      l += ParseUtil.parseLastLong(str, 0L); 
    return l;
  }
  
  private static Pair<Long, Long> querySwapInfo() {
    long l1 = 0L;
    long l2 = 0L;
    String str = ExecutingCommand.getAnswerAt("swap -lk", 1);
    Matcher matcher = SWAP_INFO.matcher(str);
    if (matcher.matches()) {
      l1 = ParseUtil.parseLongOrDefault(matcher.group(1), 0L) << 10L;
      l2 = l1 - (ParseUtil.parseLongOrDefault(matcher.group(2), 0L) << 10L);
    } 
    return new Pair(Long.valueOf(l2), Long.valueOf(l1));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\solaris\SolarisVirtualMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
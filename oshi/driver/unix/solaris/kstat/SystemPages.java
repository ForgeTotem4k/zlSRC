package oshi.driver.unix.solaris.kstat;

import com.sun.jna.platform.unix.solaris.LibKstat;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.unix.solaris.SolarisOperatingSystem;
import oshi.util.platform.unix.solaris.KstatUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class SystemPages {
  public static Pair<Long, Long> queryAvailableTotal() {
    if (SolarisOperatingSystem.HAS_KSTAT2)
      return queryAvailableTotal2(); 
    long l1 = 0L;
    long l2 = 0L;
    KstatUtil.KstatChain kstatChain = KstatUtil.openChain();
    try {
      LibKstat.Kstat kstat = kstatChain.lookup(null, -1, "system_pages");
      if (kstat != null && kstatChain.read(kstat)) {
        l1 = KstatUtil.dataLookupLong(kstat, "availrmem");
        l2 = KstatUtil.dataLookupLong(kstat, "physmem");
      } 
      if (kstatChain != null)
        kstatChain.close(); 
    } catch (Throwable throwable) {
      if (kstatChain != null)
        try {
          kstatChain.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return new Pair(Long.valueOf(l1), Long.valueOf(l2));
  }
  
  private static Pair<Long, Long> queryAvailableTotal2() {
    Object[] arrayOfObject = KstatUtil.queryKstat2("kstat:/pages/unix/system_pages", new String[] { "availrmem", "physmem" });
    long l1 = (arrayOfObject[0] == null) ? 0L : ((Long)arrayOfObject[0]).longValue();
    long l2 = (arrayOfObject[1] == null) ? 0L : ((Long)arrayOfObject[1]).longValue();
    return new Pair(Long.valueOf(l1), Long.valueOf(l2));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\solaris\kstat\SystemPages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
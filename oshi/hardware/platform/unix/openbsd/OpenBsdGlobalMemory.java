package oshi.hardware.platform.unix.openbsd;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.jna.platform.unix.OpenBsdLibc;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.openbsd.OpenBsdSysctlUtil;

@ThreadSafe
final class OpenBsdGlobalMemory extends AbstractGlobalMemory {
  private final Supplier<Long> available = Memoizer.memoize(OpenBsdGlobalMemory::queryAvailable, Memoizer.defaultExpiration());
  
  private final Supplier<Long> total = Memoizer.memoize(OpenBsdGlobalMemory::queryPhysMem);
  
  private final Supplier<Long> pageSize = Memoizer.memoize(OpenBsdGlobalMemory::queryPageSize);
  
  private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);
  
  public long getAvailable() {
    return ((Long)this.available.get()).longValue() * getPageSize();
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
  
  private static long queryAvailable() {
    long l1 = 0L;
    long l2 = 0L;
    for (String str : ExecutingCommand.runNative("vmstat -s")) {
      if (str.endsWith("pages free")) {
        l1 = ParseUtil.getFirstIntValue(str);
        continue;
      } 
      if (str.endsWith("pages inactive"))
        l2 = ParseUtil.getFirstIntValue(str); 
    } 
    int[] arrayOfInt = new int[3];
    arrayOfInt[0] = 10;
    arrayOfInt[1] = 0;
    arrayOfInt[2] = 3;
    Memory memory = OpenBsdSysctlUtil.sysctl(arrayOfInt);
    try {
      OpenBsdLibc.Bcachestats bcachestats = new OpenBsdLibc.Bcachestats((Pointer)memory);
      long l = bcachestats.numbufpages + l1 + l2;
      if (memory != null)
        memory.close(); 
      return l;
    } catch (Throwable throwable) {
      if (memory != null)
        try {
          memory.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  private static long queryPhysMem() {
    return OpenBsdSysctlUtil.sysctl("hw.physmem", 0L);
  }
  
  private static long queryPageSize() {
    return OpenBsdSysctlUtil.sysctl("hw.pagesize", 4096L);
  }
  
  private VirtualMemory createVirtualMemory() {
    return (VirtualMemory)new OpenBsdVirtualMemory(this);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\openbsd\OpenBsdGlobalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
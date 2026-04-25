package oshi.hardware.platform.unix.aix;

import com.sun.jna.platform.unix.aix.Perfstat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.perfstat.PerfstatMemory;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;

@ThreadSafe
final class AixGlobalMemory extends AbstractGlobalMemory {
  private final Supplier<Perfstat.perfstat_memory_total_t> perfstatMem = Memoizer.memoize(AixGlobalMemory::queryPerfstat, Memoizer.defaultExpiration());
  
  private final Supplier<List<String>> lscfg;
  
  private static final long PAGESIZE = 4096L;
  
  private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);
  
  AixGlobalMemory(Supplier<List<String>> paramSupplier) {
    this.lscfg = paramSupplier;
  }
  
  public long getAvailable() {
    return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).real_avail * 4096L;
  }
  
  public long getTotal() {
    return ((Perfstat.perfstat_memory_total_t)this.perfstatMem.get()).real_total * 4096L;
  }
  
  public long getPageSize() {
    return 4096L;
  }
  
  public VirtualMemory getVirtualMemory() {
    return this.vm.get();
  }
  
  public List<PhysicalMemory> getPhysicalMemory() {
    ArrayList<PhysicalMemory> arrayList = new ArrayList();
    boolean bool1 = false;
    boolean bool2 = false;
    String str1 = "unknown";
    String str2 = "";
    String str3 = "unknown";
    long l = 0L;
    for (String str4 : this.lscfg.get()) {
      String str5 = str4.trim();
      if (str5.endsWith("memory-module")) {
        bool1 = true;
        continue;
      } 
      if (str5.startsWith("Memory DIMM")) {
        bool2 = true;
        continue;
      } 
      if (bool1) {
        if (str5.startsWith("Node:")) {
          str1 = str5.substring(5).trim();
          if (str1.startsWith("IBM,"))
            str1 = str1.substring(4); 
          continue;
        } 
        if (str5.startsWith("Physical Location:")) {
          str2 = "/" + str5.substring(18).trim();
          continue;
        } 
        if (str5.startsWith("Size")) {
          l = ParseUtil.parseLongOrDefault(ParseUtil.removeLeadingDots(str5.substring(4).trim()), 0L) << 20L;
          continue;
        } 
        if (str5.startsWith("Hardware Location Code")) {
          if (l > 0L)
            arrayList.add(new PhysicalMemory(str1 + str2, l, 0L, "IBM", "unknown", "unknown", "unknown")); 
          str1 = "unknown";
          str2 = "";
          l = 0L;
          bool1 = false;
        } 
        continue;
      } 
      if (bool2) {
        if (str5.startsWith("Hardware Location Code")) {
          str2 = ParseUtil.removeLeadingDots(str5.substring(23).trim());
          continue;
        } 
        if (str5.startsWith("Size")) {
          l = ParseUtil.parseLongOrDefault(ParseUtil.removeLeadingDots(str5.substring(4).trim()), 0L) << 20L;
          continue;
        } 
        if (str5.startsWith("Part Number") || str5.startsWith("FRU Number")) {
          str3 = ParseUtil.removeLeadingDots(str5.substring(11).trim());
          continue;
        } 
        if (str5.startsWith("Physical Location:")) {
          if (l > 0L)
            arrayList.add(new PhysicalMemory(str2, l, 0L, "IBM", "unknown", str3, "unknown")); 
          str3 = "unknown";
          str2 = "";
          l = 0L;
          bool2 = false;
        } 
      } 
    } 
    return arrayList;
  }
  
  private static Perfstat.perfstat_memory_total_t queryPerfstat() {
    return PerfstatMemory.queryMemoryTotal();
  }
  
  private VirtualMemory createVirtualMemory() {
    return (VirtualMemory)new AixVirtualMemory(this.perfstatMem);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platfor\\unix\aix\AixGlobalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.VersionHelpers;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.wmi.Win32PhysicalMemory;
import oshi.hardware.PhysicalMemory;
import oshi.hardware.VirtualMemory;
import oshi.hardware.common.AbstractGlobalMemory;
import oshi.jna.Struct;
import oshi.util.Memoizer;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Triplet;

@ThreadSafe
final class WindowsGlobalMemory extends AbstractGlobalMemory {
  private static final Logger LOG = LoggerFactory.getLogger(WindowsGlobalMemory.class);
  
  private static final boolean IS_WINDOWS10_OR_GREATER = VersionHelpers.IsWindows10OrGreater();
  
  private final Supplier<Triplet<Long, Long, Long>> availTotalSize = Memoizer.memoize(WindowsGlobalMemory::readPerfInfo, Memoizer.defaultExpiration());
  
  private final Supplier<VirtualMemory> vm = Memoizer.memoize(this::createVirtualMemory);
  
  public long getAvailable() {
    return ((Long)((Triplet)this.availTotalSize.get()).getA()).longValue();
  }
  
  public long getTotal() {
    return ((Long)((Triplet)this.availTotalSize.get()).getB()).longValue();
  }
  
  public long getPageSize() {
    return ((Long)((Triplet)this.availTotalSize.get()).getC()).longValue();
  }
  
  public VirtualMemory getVirtualMemory() {
    return this.vm.get();
  }
  
  private VirtualMemory createVirtualMemory() {
    return (VirtualMemory)new WindowsVirtualMemory(this);
  }
  
  public List<PhysicalMemory> getPhysicalMemory() {
    ArrayList<PhysicalMemory> arrayList = new ArrayList();
    if (IS_WINDOWS10_OR_GREATER) {
      WbemcliUtil.WmiResult wmiResult = Win32PhysicalMemory.queryphysicalMemory();
      for (byte b = 0; b < wmiResult.getResultCount(); b++) {
        String str1 = WmiUtil.getString(wmiResult, (Enum)Win32PhysicalMemory.PhysicalMemoryProperty.BANKLABEL, b);
        long l1 = WmiUtil.getUint64(wmiResult, (Enum)Win32PhysicalMemory.PhysicalMemoryProperty.CAPACITY, b);
        long l2 = WmiUtil.getUint32(wmiResult, (Enum)Win32PhysicalMemory.PhysicalMemoryProperty.SPEED, b) * 1000000L;
        String str2 = WmiUtil.getString(wmiResult, (Enum)Win32PhysicalMemory.PhysicalMemoryProperty.MANUFACTURER, b);
        String str3 = smBiosMemoryType(WmiUtil.getUint32(wmiResult, (Enum)Win32PhysicalMemory.PhysicalMemoryProperty.SMBIOSMEMORYTYPE, b));
        String str4 = WmiUtil.getString(wmiResult, (Enum)Win32PhysicalMemory.PhysicalMemoryProperty.PARTNUMBER, b);
        String str5 = WmiUtil.getString(wmiResult, (Enum)Win32PhysicalMemory.PhysicalMemoryProperty.SERIALNUMBER, b);
        arrayList.add(new PhysicalMemory(str1, l1, l2, str2, str3, str4, str5));
      } 
    } else {
      WbemcliUtil.WmiResult wmiResult = Win32PhysicalMemory.queryphysicalMemoryWin8();
      for (byte b = 0; b < wmiResult.getResultCount(); b++) {
        String str1 = WmiUtil.getString(wmiResult, (Enum)Win32PhysicalMemory.PhysicalMemoryPropertyWin8.BANKLABEL, b);
        long l1 = WmiUtil.getUint64(wmiResult, (Enum)Win32PhysicalMemory.PhysicalMemoryPropertyWin8.CAPACITY, b);
        long l2 = WmiUtil.getUint32(wmiResult, (Enum)Win32PhysicalMemory.PhysicalMemoryPropertyWin8.SPEED, b) * 1000000L;
        String str2 = WmiUtil.getString(wmiResult, (Enum)Win32PhysicalMemory.PhysicalMemoryPropertyWin8.MANUFACTURER, b);
        String str3 = memoryType(WmiUtil.getUint16(wmiResult, (Enum)Win32PhysicalMemory.PhysicalMemoryPropertyWin8.MEMORYTYPE, b));
        String str4 = WmiUtil.getString(wmiResult, (Enum)Win32PhysicalMemory.PhysicalMemoryPropertyWin8.PARTNUMBER, b);
        String str5 = WmiUtil.getString(wmiResult, (Enum)Win32PhysicalMemory.PhysicalMemoryPropertyWin8.SERIALNUMBER, b);
        arrayList.add(new PhysicalMemory(str1, l1, l2, str2, str3, str4, str5));
      } 
    } 
    return arrayList;
  }
  
  private static String memoryType(int paramInt) {
    switch (paramInt) {
      case 0:
        return "Unknown";
      case 1:
        return "Other";
      case 2:
        return "DRAM";
      case 3:
        return "Synchronous DRAM";
      case 4:
        return "Cache DRAM";
      case 5:
        return "EDO";
      case 6:
        return "EDRAM";
      case 7:
        return "VRAM";
      case 8:
        return "SRAM";
      case 9:
        return "RAM";
      case 10:
        return "ROM";
      case 11:
        return "Flash";
      case 12:
        return "EEPROM";
      case 13:
        return "FEPROM";
      case 14:
        return "EPROM";
      case 15:
        return "CDRAM";
      case 16:
        return "3DRAM";
      case 17:
        return "SDRAM";
      case 18:
        return "SGRAM";
      case 19:
        return "RDRAM";
      case 20:
        return "DDR";
      case 21:
        return "DDR2";
      case 22:
        return "BRAM";
      case 23:
        return "DDR FB-DIMM";
    } 
    return smBiosMemoryType(paramInt);
  }
  
  private static String smBiosMemoryType(int paramInt) {
    switch (paramInt) {
      case 1:
        return "Other";
      case 3:
        return "DRAM";
      case 4:
        return "EDRAM";
      case 5:
        return "VRAM";
      case 6:
        return "SRAM";
      case 7:
        return "RAM";
      case 8:
        return "ROM";
      case 9:
        return "FLASH";
      case 10:
        return "EEPROM";
      case 11:
        return "FEPROM";
      case 12:
        return "EPROM";
      case 13:
        return "CDRAM";
      case 14:
        return "3DRAM";
      case 15:
        return "SDRAM";
      case 16:
        return "SGRAM";
      case 17:
        return "RDRAM";
      case 18:
        return "DDR";
      case 19:
        return "DDR2";
      case 20:
        return "DDR2 FB-DIMM";
      case 24:
        return "DDR3";
      case 25:
        return "FBD2";
      case 26:
        return "DDR4";
      case 27:
        return "LPDDR";
      case 28:
        return "LPDDR2";
      case 29:
        return "LPDDR3";
      case 30:
        return "LPDDR4";
      case 31:
        return "Logical non-volatile device";
      case 32:
        return "HBM";
      case 33:
        return "HBM2";
      case 34:
        return "DDR5";
      case 35:
        return "LPDDR5";
      case 36:
        return "HBM3";
    } 
    return "Unknown";
  }
  
  private static Triplet<Long, Long, Long> readPerfInfo() {
    Struct.CloseablePerformanceInformation closeablePerformanceInformation = new Struct.CloseablePerformanceInformation();
    try {
      if (!Psapi.INSTANCE.GetPerformanceInfo((Psapi.PERFORMANCE_INFORMATION)closeablePerformanceInformation, closeablePerformanceInformation.size())) {
        LOG.error("Failed to get Performance Info. Error code: {}", Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
        Triplet<Long, Long, Long> triplet1 = new Triplet(Long.valueOf(0L), Long.valueOf(0L), Long.valueOf(4098L));
        closeablePerformanceInformation.close();
        return triplet1;
      } 
      long l1 = closeablePerformanceInformation.PageSize.longValue();
      long l2 = l1 * closeablePerformanceInformation.PhysicalAvailable.longValue();
      long l3 = l1 * closeablePerformanceInformation.PhysicalTotal.longValue();
      Triplet<Long, Long, Long> triplet = new Triplet(Long.valueOf(l2), Long.valueOf(l3), Long.valueOf(l1));
      closeablePerformanceInformation.close();
      return triplet;
    } catch (Throwable throwable) {
      try {
        closeablePerformanceInformation.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\windows\WindowsGlobalMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package oshi.driver.windows.perfmon;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.GlobalConfig;
import oshi.util.Util;

@ThreadSafe
public final class PerfmonDisabled {
  private static final Logger LOG = LoggerFactory.getLogger(PerfmonDisabled.class);
  
  public static final boolean PERF_OS_DISABLED = isDisabled("oshi.os.windows.perfos.disabled", "PerfOS");
  
  public static final boolean PERF_PROC_DISABLED = isDisabled("oshi.os.windows.perfproc.disabled", "PerfProc");
  
  public static final boolean PERF_DISK_DISABLED = isDisabled("oshi.os.windows.perfdisk.disabled", "PerfDisk");
  
  private PerfmonDisabled() {
    throw new AssertionError();
  }
  
  private static boolean isDisabled(String paramString1, String paramString2) {
    String str = GlobalConfig.get(paramString1);
    if (Util.isBlank(str)) {
      String str1 = String.format(Locale.ROOT, "SYSTEM\\CurrentControlSet\\Services\\%s\\Performance", new Object[] { paramString2 });
      String str2 = "Disable Performance Counters";
      if (Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, str1, str2)) {
        Object object = Advapi32Util.registryGetValue(WinReg.HKEY_LOCAL_MACHINE, str1, str2);
        if (object instanceof Integer) {
          if (((Integer)object).intValue() > 0) {
            LOG.warn("{} counters are disabled and won't return data: {}\\\\{}\\\\{} > 0.", new Object[] { paramString2, "HKEY_LOCAL_MACHINE", str1, str2 });
            return true;
          } 
        } else {
          LOG.warn("Invalid registry value type detected for {} counters. Should be REG_DWORD. Ignoring: {}\\\\{}\\\\{}.", new Object[] { paramString2, "HKEY_LOCAL_MACHINE", str1, str2 });
        } 
      } 
      return false;
    } 
    return Boolean.parseBoolean(str);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\perfmon\PerfmonDisabled.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
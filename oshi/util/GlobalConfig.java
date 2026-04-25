package oshi.util;

import java.util.Properties;
import oshi.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class GlobalConfig {
  private static final String OSHI_PROPERTIES = "oshi.properties";
  
  private static final Properties CONFIG = FileUtil.readPropertiesFromFilename("oshi.properties");
  
  public static final String OSHI_UTIL_MEMOIZER_EXPIRATION = "oshi.util.memoizer.expiration";
  
  public static final String OSHI_UTIL_WMI_TIMEOUT = "oshi.util.wmi.timeout";
  
  public static final String OSHI_UTIL_PROC_PATH = "oshi.util.proc.path";
  
  public static final String OSHI_UTIL_SYS_PATH = "oshi.util.sys.path";
  
  public static final String OSHI_UTIL_DEV_PATH = "oshi.util.dev.path";
  
  public static final String OSHI_PSEUDO_FILESYSTEM_TYPES = "oshi.pseudo.filesystem.types";
  
  public static final String OSHI_NETWORK_FILESYSTEM_TYPES = "oshi.network.filesystem.types";
  
  public static final String OSHI_OS_LINUX_ALLOWUDEV = "oshi.os.linux.allowudev";
  
  public static final String OSHI_OS_LINUX_PROCFS_LOGWARNING = "oshi.os.linux.procfs.logwarning";
  
  public static final String OSHI_OS_MAC_SYSCTL_LOGWARNING = "oshi.os.mac.sysctl.logwarning";
  
  public static final String OSHI_OS_WINDOWS_EVENTLOG = "oshi.os.windows.eventlog";
  
  public static final String OSHI_OS_WINDOWS_PROCSTATE_SUSPENDED = "oshi.os.windows.procstate.suspended";
  
  public static final String OSHI_OS_WINDOWS_COMMANDLINE_BATCH = "oshi.os.windows.commandline.batch";
  
  public static final String OSHI_OS_WINDOWS_HKEYPERFDATA = "oshi.os.windows.hkeyperfdata";
  
  public static final String OSHI_OS_WINDOWS_LEGACY_SYSTEM_COUNTERS = "oshi.os.windows.legacy.system.counters";
  
  public static final String OSHI_OS_WINDOWS_LOADAVERAGE = "oshi.os.windows.loadaverage";
  
  public static final String OSHI_OS_WINDOWS_CPU_UTILITY = "oshi.os.windows.cpu.utility";
  
  public static final String OSHI_OS_WINDOWS_PERFDISK_DIABLED = "oshi.os.windows.perfdisk.disabled";
  
  public static final String OSHI_OS_WINDOWS_PERFOS_DIABLED = "oshi.os.windows.perfos.disabled";
  
  public static final String OSHI_OS_WINDOWS_PERFPROC_DIABLED = "oshi.os.windows.perfproc.disabled";
  
  public static final String OSHI_OS_WINDOWS_PERF_DISABLE_ALL_ON_FAILURE = "oshi.os.windows.perf.disable.all.on.failure";
  
  public static final String OSHI_OS_UNIX_WHOCOMMAND = "oshi.os.unix.whoCommand";
  
  public static final String OSHI_OS_SOLARIS_ALLOWKSTAT2 = "oshi.os.solaris.allowKstat2";
  
  public static String get(String paramString) {
    return CONFIG.getProperty(paramString);
  }
  
  public static String get(String paramString1, String paramString2) {
    return CONFIG.getProperty(paramString1, paramString2);
  }
  
  public static int get(String paramString, int paramInt) {
    String str = CONFIG.getProperty(paramString);
    return (str == null) ? paramInt : ParseUtil.parseIntOrDefault(str, paramInt);
  }
  
  public static double get(String paramString, double paramDouble) {
    String str = CONFIG.getProperty(paramString);
    return (str == null) ? paramDouble : ParseUtil.parseDoubleOrDefault(str, paramDouble);
  }
  
  public static boolean get(String paramString, boolean paramBoolean) {
    String str = CONFIG.getProperty(paramString);
    return (str == null) ? paramBoolean : Boolean.parseBoolean(str);
  }
  
  public static void set(String paramString, Object paramObject) {
    if (paramObject == null) {
      CONFIG.remove(paramString);
    } else {
      CONFIG.setProperty(paramString, paramObject.toString());
    } 
  }
  
  public static void remove(String paramString) {
    CONFIG.remove(paramString);
  }
  
  public static void clear() {
    CONFIG.clear();
  }
  
  public static void load(Properties paramProperties) {
    CONFIG.putAll(paramProperties);
  }
  
  static {
    System.getProperties().forEach((paramObject1, paramObject2) -> {
          String str = paramObject1.toString();
          if (str.startsWith("oshi."))
            set(str, paramObject2); 
        });
  }
  
  public static class PropertyException extends RuntimeException {
    private static final long serialVersionUID = -7482581936621748005L;
    
    public PropertyException(String param1String) {
      super("Invalid property: \"" + param1String + "\" = " + GlobalConfig.get(param1String, (String)null));
    }
    
    public PropertyException(String param1String1, String param1String2) {
      super("Invalid property \"" + param1String1 + "\": " + param1String2);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\GlobalConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
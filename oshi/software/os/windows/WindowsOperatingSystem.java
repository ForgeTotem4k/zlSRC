package oshi.software.os.windows;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.W32ServiceManager;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Winsvc;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.EnumWindows;
import oshi.driver.windows.registry.HkeyUserData;
import oshi.driver.windows.registry.NetSessionData;
import oshi.driver.windows.registry.ProcessPerformanceData;
import oshi.driver.windows.registry.ProcessWtsData;
import oshi.driver.windows.registry.SessionWtsData;
import oshi.driver.windows.registry.ThreadPerformanceData;
import oshi.driver.windows.wmi.Win32OperatingSystem;
import oshi.driver.windows.wmi.Win32Processor;
import oshi.jna.ByRef;
import oshi.jna.Struct;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.ApplicationInfo;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSDesktopWindow;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSSession;
import oshi.software.os.OSThread;
import oshi.software.os.OperatingSystem;
import oshi.util.GlobalConfig;
import oshi.util.Memoizer;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public class WindowsOperatingSystem extends AbstractOperatingSystem {
  private static final Logger LOG = LoggerFactory.getLogger(WindowsOperatingSystem.class);
  
  private static final boolean USE_PROCSTATE_SUSPENDED = GlobalConfig.get("oshi.os.windows.procstate.suspended", false);
  
  private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
  
  private static Supplier<String> systemLog = Memoizer.memoize(WindowsOperatingSystem::querySystemLog, TimeUnit.HOURS.toNanos(1L));
  
  private static final long BOOTTIME = querySystemBootTime();
  
  private static final boolean X86 = isCurrentX86();
  
  private static final boolean WOW = isCurrentWow();
  
  private final Supplier<List<ApplicationInfo>> installedAppsSupplier = Memoizer.memoize(WindowsInstalledApps::queryInstalledApps, Memoizer.installedAppsExpiration());
  
  private Supplier<Map<Integer, ProcessPerformanceData.PerfCounterBlock>> processMapFromRegistry = Memoizer.memoize(WindowsOperatingSystem::queryProcessMapFromRegistry, Memoizer.defaultExpiration());
  
  private Supplier<Map<Integer, ProcessPerformanceData.PerfCounterBlock>> processMapFromPerfCounters = Memoizer.memoize(WindowsOperatingSystem::queryProcessMapFromPerfCounters, Memoizer.defaultExpiration());
  
  private Supplier<Map<Integer, ThreadPerformanceData.PerfCounterBlock>> threadMapFromRegistry = Memoizer.memoize(WindowsOperatingSystem::queryThreadMapFromRegistry, Memoizer.defaultExpiration());
  
  private Supplier<Map<Integer, ThreadPerformanceData.PerfCounterBlock>> threadMapFromPerfCounters = Memoizer.memoize(WindowsOperatingSystem::queryThreadMapFromPerfCounters, Memoizer.defaultExpiration());
  
  public String queryManufacturer() {
    return "Microsoft";
  }
  
  public Pair<String, OperatingSystem.OSVersionInfo> queryFamilyVersionInfo() {
    String str1 = System.getProperty("os.name");
    if (str1.startsWith("Windows "))
      str1 = str1.substring(8); 
    String str2 = null;
    int i = 0;
    String str3 = "";
    WbemcliUtil.WmiResult wmiResult = Win32OperatingSystem.queryOsVersion();
    if (wmiResult.getResultCount() > 0) {
      str2 = WmiUtil.getString(wmiResult, (Enum)Win32OperatingSystem.OSVersionProperty.CSDVERSION, 0);
      if (!str2.isEmpty() && !"unknown".equals(str2))
        str1 = str1 + " " + str2.replace("Service Pack ", "SP"); 
      i = WmiUtil.getUint32(wmiResult, (Enum)Win32OperatingSystem.OSVersionProperty.SUITEMASK, 0);
      str3 = WmiUtil.getString(wmiResult, (Enum)Win32OperatingSystem.OSVersionProperty.BUILDNUMBER, 0);
    } 
    String str4 = parseCodeName(i);
    if ("10".equals(str1) && str3.compareTo("22000") >= 0)
      str1 = "11"; 
    if ("Server 2016".equals(str1) && str3.compareTo("17762") > 0)
      str1 = "Server 2019"; 
    if ("Server 2019".equals(str1) && str3.compareTo("20347") > 0)
      str1 = "Server 2022"; 
    if ("Server 2022".equals(str1) && str3.compareTo("26039") > 0)
      str1 = "Server 2025"; 
    return new Pair("Windows", new OperatingSystem.OSVersionInfo(str1, str4, str3));
  }
  
  private static String parseCodeName(int paramInt) {
    ArrayList<String> arrayList = new ArrayList();
    if ((paramInt & 0x2) != 0)
      arrayList.add("Enterprise"); 
    if ((paramInt & 0x4) != 0)
      arrayList.add("BackOffice"); 
    if ((paramInt & 0x8) != 0)
      arrayList.add("Communications Server"); 
    if ((paramInt & 0x80) != 0)
      arrayList.add("Datacenter"); 
    if ((paramInt & 0x200) != 0)
      arrayList.add("Home"); 
    if ((paramInt & 0x400) != 0)
      arrayList.add("Web Server"); 
    if ((paramInt & 0x2000) != 0)
      arrayList.add("Storage Server"); 
    if ((paramInt & 0x4000) != 0)
      arrayList.add("Compute Cluster"); 
    if ((paramInt & 0x8000) != 0)
      arrayList.add("Home Server"); 
    return String.join(",", (Iterable)arrayList);
  }
  
  protected int queryBitness(int paramInt) {
    if (paramInt < 64 && System.getenv("ProgramFiles(x86)") != null && IS_VISTA_OR_GREATER) {
      WbemcliUtil.WmiResult wmiResult = Win32Processor.queryBitness();
      if (wmiResult.getResultCount() > 0)
        return WmiUtil.getUint16(wmiResult, (Enum)Win32Processor.BitnessProperty.ADDRESSWIDTH, 0); 
    } 
    return paramInt;
  }
  
  public boolean isElevated() {
    return Advapi32Util.isCurrentProcessElevated();
  }
  
  public FileSystem getFileSystem() {
    return (FileSystem)new WindowsFileSystem();
  }
  
  public InternetProtocolStats getInternetProtocolStats() {
    return (InternetProtocolStats)new WindowsInternetProtocolStats();
  }
  
  public List<OSSession> getSessions() {
    List<OSSession> list = HkeyUserData.queryUserSessions();
    list.addAll(SessionWtsData.queryUserSessions());
    list.addAll(NetSessionData.queryUserSessions());
    return list;
  }
  
  public List<OSProcess> getProcesses(Collection<Integer> paramCollection) {
    return processMapToList(paramCollection);
  }
  
  public List<OSProcess> queryAllProcesses() {
    return processMapToList((Collection<Integer>)null);
  }
  
  public List<OSProcess> queryChildProcesses(int paramInt) {
    Set<Integer> set = getChildrenOrDescendants(getParentPidsFromSnapshot(), paramInt, false);
    return processMapToList(set);
  }
  
  public List<OSProcess> queryDescendantProcesses(int paramInt) {
    Set<Integer> set = getChildrenOrDescendants(getParentPidsFromSnapshot(), paramInt, true);
    return processMapToList(set);
  }
  
  private static Map<Integer, Integer> getParentPidsFromSnapshot() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    ByRef.CloseablePROCESSENTRY32ByReference closeablePROCESSENTRY32ByReference = new ByRef.CloseablePROCESSENTRY32ByReference();
    try {
      WinNT.HANDLE hANDLE = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0L));
      try {
        while (Kernel32.INSTANCE.Process32Next(hANDLE, (Tlhelp32.PROCESSENTRY32)closeablePROCESSENTRY32ByReference))
          hashMap.put(Integer.valueOf(closeablePROCESSENTRY32ByReference.th32ProcessID.intValue()), Integer.valueOf(closeablePROCESSENTRY32ByReference.th32ParentProcessID.intValue())); 
      } finally {
        Kernel32.INSTANCE.CloseHandle(hANDLE);
      } 
      closeablePROCESSENTRY32ByReference.close();
    } catch (Throwable throwable) {
      try {
        closeablePROCESSENTRY32ByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return (Map)hashMap;
  }
  
  public OSProcess getProcess(int paramInt) {
    List<OSProcess> list = processMapToList(Arrays.asList(new Integer[] { Integer.valueOf(paramInt) }));
    return list.isEmpty() ? null : list.get(0);
  }
  
  private List<OSProcess> processMapToList(Collection<Integer> paramCollection) {
    Map map1 = this.processMapFromRegistry.get();
    if (map1 == null || map1.isEmpty())
      map1 = (paramCollection == null) ? this.processMapFromPerfCounters.get() : ProcessPerformanceData.buildProcessMapFromPerfCounters(paramCollection); 
    Map map2 = null;
    if (USE_PROCSTATE_SUSPENDED) {
      map2 = this.threadMapFromRegistry.get();
      if (map2 == null || map2.isEmpty())
        map2 = (paramCollection == null) ? this.threadMapFromPerfCounters.get() : ThreadPerformanceData.buildThreadMapFromPerfCounters(paramCollection); 
    } 
    Map map3 = ProcessWtsData.queryProcessWtsMap(paramCollection);
    HashSet hashSet = new HashSet(map3.keySet());
    hashSet.retainAll(map1.keySet());
    Map map4 = map1;
    Map map5 = map2;
    return (List<OSProcess>)hashSet.stream().parallel().map(paramInteger -> new WindowsOSProcess(paramInteger.intValue(), this, paramMap1, paramMap2, paramMap3)).filter(OperatingSystem.ProcessFiltering.VALID_PROCESS).collect(Collectors.toList());
  }
  
  private static Map<Integer, ProcessPerformanceData.PerfCounterBlock> queryProcessMapFromRegistry() {
    return ProcessPerformanceData.buildProcessMapFromRegistry(null);
  }
  
  private static Map<Integer, ProcessPerformanceData.PerfCounterBlock> queryProcessMapFromPerfCounters() {
    return ProcessPerformanceData.buildProcessMapFromPerfCounters(null);
  }
  
  private static Map<Integer, ThreadPerformanceData.PerfCounterBlock> queryThreadMapFromRegistry() {
    return ThreadPerformanceData.buildThreadMapFromRegistry(null);
  }
  
  private static Map<Integer, ThreadPerformanceData.PerfCounterBlock> queryThreadMapFromPerfCounters() {
    return ThreadPerformanceData.buildThreadMapFromPerfCounters(null);
  }
  
  public int getProcessId() {
    return Kernel32.INSTANCE.GetCurrentProcessId();
  }
  
  public int getProcessCount() {
    Struct.CloseablePerformanceInformation closeablePerformanceInformation = new Struct.CloseablePerformanceInformation();
    try {
      if (!Psapi.INSTANCE.GetPerformanceInfo((Psapi.PERFORMANCE_INFORMATION)closeablePerformanceInformation, closeablePerformanceInformation.size())) {
        LOG.error("Failed to get Performance Info. Error code: {}", Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
        boolean bool = false;
        closeablePerformanceInformation.close();
        return bool;
      } 
      int i = closeablePerformanceInformation.ProcessCount.intValue();
      closeablePerformanceInformation.close();
      return i;
    } catch (Throwable throwable) {
      try {
        closeablePerformanceInformation.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public int getThreadId() {
    return Kernel32.INSTANCE.GetCurrentThreadId();
  }
  
  public OSThread getCurrentThread() {
    OSProcess oSProcess = getCurrentProcess();
    int i = getThreadId();
    return (OSThread)oSProcess.getThreadDetails().stream().filter(paramOSThread -> (paramOSThread.getThreadId() == paramInt)).findFirst().orElse(new WindowsOSThread(oSProcess.getProcessID(), i, null, null));
  }
  
  public int getThreadCount() {
    Struct.CloseablePerformanceInformation closeablePerformanceInformation = new Struct.CloseablePerformanceInformation();
    try {
      if (!Psapi.INSTANCE.GetPerformanceInfo((Psapi.PERFORMANCE_INFORMATION)closeablePerformanceInformation, closeablePerformanceInformation.size())) {
        LOG.error("Failed to get Performance Info. Error code: {}", Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
        boolean bool = false;
        closeablePerformanceInformation.close();
        return bool;
      } 
      int i = closeablePerformanceInformation.ThreadCount.intValue();
      closeablePerformanceInformation.close();
      return i;
    } catch (Throwable throwable) {
      try {
        closeablePerformanceInformation.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public long getSystemUptime() {
    return querySystemUptime();
  }
  
  private static long querySystemUptime() {
    return IS_VISTA_OR_GREATER ? (Kernel32.INSTANCE.GetTickCount64() / 1000L) : (Kernel32.INSTANCE.GetTickCount() / 1000L);
  }
  
  public long getSystemBootTime() {
    return BOOTTIME;
  }
  
  private static long querySystemBootTime() {
    String str = systemLog.get();
    if (str != null)
      try {
        Advapi32Util.EventLogIterator eventLogIterator = new Advapi32Util.EventLogIterator(null, str, 8);
        long l = 0L;
        while (eventLogIterator.hasNext()) {
          Advapi32Util.EventLogRecord eventLogRecord = eventLogIterator.next();
          if (eventLogRecord.getStatusCode() == 12)
            return (eventLogRecord.getRecord()).TimeGenerated.longValue(); 
          if (eventLogRecord.getStatusCode() == 6005) {
            if (l > 0L)
              return l; 
            l = (eventLogRecord.getRecord()).TimeGenerated.longValue();
          } 
        } 
        if (l > 0L)
          return l; 
      } catch (Win32Exception win32Exception) {
        LOG.warn("Can't open event log \"{}\".", str);
      }  
    return System.currentTimeMillis() / 1000L - querySystemUptime();
  }
  
  public NetworkParams getNetworkParams() {
    return (NetworkParams)new WindowsNetworkParams();
  }
  
  private static boolean enableDebugPrivilege() {
    ByRef.CloseableHANDLEByReference closeableHANDLEByReference = new ByRef.CloseableHANDLEByReference();
    try {
      boolean bool = Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), 40, (WinNT.HANDLEByReference)closeableHANDLEByReference);
      if (!bool) {
        LOG.error("OpenProcessToken failed. Error: {}", Integer.valueOf(Native.getLastError()));
        boolean bool1 = false;
        closeableHANDLEByReference.close();
        return bool1;
      } 
      try {
        WinNT.LUID lUID = new WinNT.LUID();
        bool = Advapi32.INSTANCE.LookupPrivilegeValue(null, "SeDebugPrivilege", lUID);
        if (!bool) {
          LOG.error("LookupPrivilegeValue failed. Error: {}", Integer.valueOf(Native.getLastError()));
          boolean bool1 = false;
          Kernel32.INSTANCE.CloseHandle(closeableHANDLEByReference.getValue());
          return bool1;
        } 
        WinNT.TOKEN_PRIVILEGES tOKEN_PRIVILEGES = new WinNT.TOKEN_PRIVILEGES(1);
        tOKEN_PRIVILEGES.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(lUID, new WinDef.DWORD(2L));
        bool = Advapi32.INSTANCE.AdjustTokenPrivileges(closeableHANDLEByReference.getValue(), false, tOKEN_PRIVILEGES, 0, null, null);
        int i = Native.getLastError();
        if (!bool) {
          LOG.error("AdjustTokenPrivileges failed. Error: {}", Integer.valueOf(i));
          boolean bool1 = false;
          Kernel32.INSTANCE.CloseHandle(closeableHANDLEByReference.getValue());
          return bool1;
        } 
        if (i == 1300) {
          LOG.debug("Debug privileges not enabled.");
          boolean bool1 = false;
          Kernel32.INSTANCE.CloseHandle(closeableHANDLEByReference.getValue());
          return bool1;
        } 
      } finally {
        Kernel32.INSTANCE.CloseHandle(closeableHANDLEByReference.getValue());
      } 
      closeableHANDLEByReference.close();
    } catch (Throwable throwable) {
      try {
        closeableHANDLEByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return true;
  }
  
  public List<OSService> getServices() {
    try {
      W32ServiceManager w32ServiceManager = new W32ServiceManager();
      try {
        w32ServiceManager.open(4);
        Winsvc.ENUM_SERVICE_STATUS_PROCESS[] arrayOfENUM_SERVICE_STATUS_PROCESS = w32ServiceManager.enumServicesStatusExProcess(48, 3, null);
        ArrayList<OSService> arrayList1 = new ArrayList();
        for (Winsvc.ENUM_SERVICE_STATUS_PROCESS eNUM_SERVICE_STATUS_PROCESS : arrayOfENUM_SERVICE_STATUS_PROCESS) {
          OSService.State state;
          switch (eNUM_SERVICE_STATUS_PROCESS.ServiceStatusProcess.dwCurrentState) {
            case 1:
              state = OSService.State.STOPPED;
              break;
            case 4:
              state = OSService.State.RUNNING;
              break;
            default:
              state = OSService.State.OTHER;
              break;
          } 
          arrayList1.add(new OSService(eNUM_SERVICE_STATUS_PROCESS.lpDisplayName, eNUM_SERVICE_STATUS_PROCESS.ServiceStatusProcess.dwProcessId, state));
        } 
        ArrayList<OSService> arrayList2 = arrayList1;
        w32ServiceManager.close();
        return arrayList2;
      } catch (Throwable throwable) {
        try {
          w32ServiceManager.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Win32Exception win32Exception) {
      LOG.error("Win32Exception: {}", win32Exception.getMessage());
      return Collections.emptyList();
    } 
  }
  
  private static String querySystemLog() {
    String str = GlobalConfig.get("oshi.os.windows.eventlog", "System");
    if (str.isEmpty())
      return null; 
    WinNT.HANDLE hANDLE = Advapi32.INSTANCE.OpenEventLog(null, str);
    if (hANDLE == null) {
      LOG.warn("Unable to open configured system Event log \"{}\". Calculating boot time from uptime.", str);
      return null;
    } 
    return str;
  }
  
  public List<OSDesktopWindow> getDesktopWindows(boolean paramBoolean) {
    return EnumWindows.queryDesktopWindows(paramBoolean);
  }
  
  public List<ApplicationInfo> getInstalledApplications() {
    return this.installedAppsSupplier.get();
  }
  
  static boolean isX86() {
    return X86;
  }
  
  private static boolean isCurrentX86() {
    Struct.CloseableSystemInfo closeableSystemInfo = new Struct.CloseableSystemInfo();
    try {
      Kernel32.INSTANCE.GetNativeSystemInfo((WinBase.SYSTEM_INFO)closeableSystemInfo);
      boolean bool = (0 == closeableSystemInfo.processorArchitecture.pi.wProcessorArchitecture.intValue()) ? true : false;
      closeableSystemInfo.close();
      return bool;
    } catch (Throwable throwable) {
      try {
        closeableSystemInfo.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  static boolean isWow() {
    return WOW;
  }
  
  static boolean isWow(WinNT.HANDLE paramHANDLE) {
    if (X86)
      return true; 
    ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
    try {
      Kernel32.INSTANCE.IsWow64Process(paramHANDLE, (IntByReference)closeableIntByReference);
      boolean bool = (closeableIntByReference.getValue() != 0) ? true : false;
      closeableIntByReference.close();
      return bool;
    } catch (Throwable throwable) {
      try {
        closeableIntByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  private static boolean isCurrentWow() {
    if (X86)
      return true; 
    WinNT.HANDLE hANDLE = Kernel32.INSTANCE.GetCurrentProcess();
    return (hANDLE == null) ? false : isWow(hANDLE);
  }
  
  static {
    enableDebugPrivilege();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\windows\WindowsOperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
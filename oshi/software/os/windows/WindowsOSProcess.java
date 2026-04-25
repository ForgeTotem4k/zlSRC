package oshi.software.os.windows;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Shell32Util;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.registry.ProcessPerformanceData;
import oshi.driver.windows.registry.ProcessWtsData;
import oshi.driver.windows.registry.ThreadPerformanceData;
import oshi.driver.windows.wmi.Win32Process;
import oshi.driver.windows.wmi.Win32ProcessCached;
import oshi.jna.ByRef;
import oshi.jna.platform.windows.NtDll;
import oshi.software.common.AbstractOSProcess;
import oshi.software.os.OSProcess;
import oshi.software.os.OSThread;
import oshi.util.GlobalConfig;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
public class WindowsOSProcess extends AbstractOSProcess {
  private static final Logger LOG = LoggerFactory.getLogger(WindowsOSProcess.class);
  
  private static final boolean USE_BATCH_COMMANDLINE = GlobalConfig.get("oshi.os.windows.commandline.batch", false);
  
  private static final boolean USE_PROCSTATE_SUSPENDED = GlobalConfig.get("oshi.os.windows.procstate.suspended", false);
  
  private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
  
  private static final boolean IS_WINDOWS7_OR_GREATER = VersionHelpers.IsWindows7OrGreater();
  
  private final WindowsOperatingSystem os;
  
  private Supplier<Pair<String, String>> userInfo = Memoizer.memoize(this::queryUserInfo);
  
  private Supplier<Pair<String, String>> groupInfo = Memoizer.memoize(this::queryGroupInfo);
  
  private Supplier<String> currentWorkingDirectory = Memoizer.memoize(this::queryCwd);
  
  private Supplier<String> commandLine = Memoizer.memoize(this::queryCommandLine);
  
  private Supplier<List<String>> args = Memoizer.memoize(this::queryArguments);
  
  private Supplier<Triplet<String, String, Map<String, String>>> cwdCmdEnv = Memoizer.memoize(this::queryCwdCommandlineEnvironment);
  
  private Map<Integer, ThreadPerformanceData.PerfCounterBlock> tcb;
  
  private String name;
  
  private String path;
  
  private OSProcess.State state = OSProcess.State.INVALID;
  
  private int parentProcessID;
  
  private int threadCount;
  
  private int priority;
  
  private long virtualSize;
  
  private long residentSetSize;
  
  private long kernelTime;
  
  private long userTime;
  
  private long startTime;
  
  private long upTime;
  
  private long bytesRead;
  
  private long bytesWritten;
  
  private long openFiles;
  
  private int bitness;
  
  private long pageFaults;
  
  public WindowsOSProcess(int paramInt, WindowsOperatingSystem paramWindowsOperatingSystem, Map<Integer, ProcessPerformanceData.PerfCounterBlock> paramMap, Map<Integer, ProcessWtsData.WtsInfo> paramMap1, Map<Integer, ThreadPerformanceData.PerfCounterBlock> paramMap2) {
    super(paramInt);
    this.os = paramWindowsOperatingSystem;
    this.bitness = paramWindowsOperatingSystem.getBitness();
    this.tcb = paramMap2;
    updateAttributes(paramMap.get(Integer.valueOf(paramInt)), paramMap1.get(Integer.valueOf(paramInt)));
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getPath() {
    return this.path;
  }
  
  public String getCommandLine() {
    return this.commandLine.get();
  }
  
  public List<String> getArguments() {
    return this.args.get();
  }
  
  public Map<String, String> getEnvironmentVariables() {
    return (Map<String, String>)((Triplet)this.cwdCmdEnv.get()).getC();
  }
  
  public String getCurrentWorkingDirectory() {
    return this.currentWorkingDirectory.get();
  }
  
  public String getUser() {
    return (String)((Pair)this.userInfo.get()).getA();
  }
  
  public String getUserID() {
    return (String)((Pair)this.userInfo.get()).getB();
  }
  
  public String getGroup() {
    return (String)((Pair)this.groupInfo.get()).getA();
  }
  
  public String getGroupID() {
    return (String)((Pair)this.groupInfo.get()).getB();
  }
  
  public OSProcess.State getState() {
    return this.state;
  }
  
  public int getParentProcessID() {
    return this.parentProcessID;
  }
  
  public int getThreadCount() {
    return this.threadCount;
  }
  
  public int getPriority() {
    return this.priority;
  }
  
  public long getVirtualSize() {
    return this.virtualSize;
  }
  
  public long getResidentSetSize() {
    return this.residentSetSize;
  }
  
  public long getKernelTime() {
    return this.kernelTime;
  }
  
  public long getUserTime() {
    return this.userTime;
  }
  
  public long getUpTime() {
    return this.upTime;
  }
  
  public long getStartTime() {
    return this.startTime;
  }
  
  public long getBytesRead() {
    return this.bytesRead;
  }
  
  public long getBytesWritten() {
    return this.bytesWritten;
  }
  
  public long getOpenFiles() {
    return this.openFiles;
  }
  
  public long getSoftOpenFileLimit() {
    return WindowsFileSystem.MAX_WINDOWS_HANDLES;
  }
  
  public long getHardOpenFileLimit() {
    return WindowsFileSystem.MAX_WINDOWS_HANDLES;
  }
  
  public int getBitness() {
    return this.bitness;
  }
  
  public long getAffinityMask() {
    WinNT.HANDLE hANDLE = Kernel32.INSTANCE.OpenProcess(1024, false, getProcessID());
    if (hANDLE != null)
      try {
        ByRef.CloseableULONGptrByReference closeableULONGptrByReference = new ByRef.CloseableULONGptrByReference();
        try {
          ByRef.CloseableULONGptrByReference closeableULONGptrByReference1 = new ByRef.CloseableULONGptrByReference();
          try {
            if (Kernel32.INSTANCE.GetProcessAffinityMask(hANDLE, (BaseTSD.ULONG_PTRByReference)closeableULONGptrByReference, (BaseTSD.ULONG_PTRByReference)closeableULONGptrByReference1)) {
              long l = Pointer.nativeValue(closeableULONGptrByReference.getValue().toPointer());
              closeableULONGptrByReference1.close();
              closeableULONGptrByReference.close();
              return l;
            } 
            closeableULONGptrByReference1.close();
          } catch (Throwable throwable) {
            try {
              closeableULONGptrByReference1.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
          closeableULONGptrByReference.close();
        } catch (Throwable throwable) {
          try {
            closeableULONGptrByReference.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } finally {
        Kernel32.INSTANCE.CloseHandle(hANDLE);
      }  
    return 0L;
  }
  
  public long getMinorFaults() {
    return this.pageFaults;
  }
  
  public List<OSThread> getThreadDetails() {
    Map map = (this.tcb == null) ? ThreadPerformanceData.buildThreadMapFromPerfCounters(Collections.singleton(Integer.valueOf(getProcessID())), getName(), -1) : this.tcb;
    return (List<OSThread>)map.entrySet().stream().parallel().map(paramEntry -> new WindowsOSThread(getProcessID(), ((Integer)paramEntry.getKey()).intValue(), this.name, (ThreadPerformanceData.PerfCounterBlock)paramEntry.getValue())).collect(Collectors.toList());
  }
  
  public boolean updateAttributes() {
    Set<Integer> set = Collections.singleton(Integer.valueOf(getProcessID()));
    Map map1 = ProcessPerformanceData.buildProcessMapFromRegistry(null);
    if (map1 == null)
      map1 = ProcessPerformanceData.buildProcessMapFromPerfCounters(set); 
    if (USE_PROCSTATE_SUSPENDED) {
      this.tcb = ThreadPerformanceData.buildThreadMapFromRegistry(null);
      if (this.tcb == null)
        this.tcb = ThreadPerformanceData.buildThreadMapFromPerfCounters(null); 
    } 
    Map map2 = ProcessWtsData.queryProcessWtsMap(set);
    return updateAttributes((ProcessPerformanceData.PerfCounterBlock)map1.get(Integer.valueOf(getProcessID())), (ProcessWtsData.WtsInfo)map2.get(Integer.valueOf(getProcessID())));
  }
  
  private boolean updateAttributes(ProcessPerformanceData.PerfCounterBlock paramPerfCounterBlock, ProcessWtsData.WtsInfo paramWtsInfo) {
    this.name = paramPerfCounterBlock.getName();
    this.path = paramWtsInfo.getPath();
    this.parentProcessID = paramPerfCounterBlock.getParentProcessID();
    this.threadCount = paramWtsInfo.getThreadCount();
    this.priority = paramPerfCounterBlock.getPriority();
    this.virtualSize = paramWtsInfo.getVirtualSize();
    this.residentSetSize = paramPerfCounterBlock.getResidentSetSize();
    this.kernelTime = paramWtsInfo.getKernelTime();
    this.userTime = paramWtsInfo.getUserTime();
    this.startTime = paramPerfCounterBlock.getStartTime();
    this.upTime = paramPerfCounterBlock.getUpTime();
    this.bytesRead = paramPerfCounterBlock.getBytesRead();
    this.bytesWritten = paramPerfCounterBlock.getBytesWritten();
    this.openFiles = paramWtsInfo.getOpenFiles();
    this.pageFaults = paramPerfCounterBlock.getPageFaults();
    this.state = OSProcess.State.RUNNING;
    if (this.tcb != null) {
      int i = getProcessID();
      for (ThreadPerformanceData.PerfCounterBlock perfCounterBlock : this.tcb.values()) {
        if (perfCounterBlock.getOwningProcessID() == i) {
          if (perfCounterBlock.getThreadWaitReason() == 5) {
            this.state = OSProcess.State.SUSPENDED;
            continue;
          } 
          this.state = OSProcess.State.RUNNING;
          break;
        } 
      } 
    } 
    WinNT.HANDLE hANDLE = Kernel32.INSTANCE.OpenProcess(1024, false, getProcessID());
    if (hANDLE != null)
      try {
        if (IS_VISTA_OR_GREATER && this.bitness == 64) {
          ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
          try {
            if (Kernel32.INSTANCE.IsWow64Process(hANDLE, (IntByReference)closeableIntByReference) && closeableIntByReference.getValue() > 0)
              this.bitness = 32; 
            closeableIntByReference.close();
          } catch (Throwable throwable) {
            try {
              closeableIntByReference.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        } 
        try {
          if (IS_WINDOWS7_OR_GREATER)
            this.path = Kernel32Util.QueryFullProcessImageName(hANDLE, 0); 
        } catch (Win32Exception win32Exception) {
          this.state = OSProcess.State.INVALID;
        } 
      } finally {
        Kernel32.INSTANCE.CloseHandle(hANDLE);
      }  
    return !this.state.equals(OSProcess.State.INVALID);
  }
  
  private String queryCommandLine() {
    if (!((String)((Triplet)this.cwdCmdEnv.get()).getB()).isEmpty())
      return (String)((Triplet)this.cwdCmdEnv.get()).getB(); 
    if (USE_BATCH_COMMANDLINE)
      return Win32ProcessCached.getInstance().getCommandLine(getProcessID(), getStartTime()); 
    WbemcliUtil.WmiResult wmiResult = Win32Process.queryCommandLines(Collections.singleton(Integer.valueOf(getProcessID())));
    return (wmiResult.getResultCount() > 0) ? WmiUtil.getString(wmiResult, (Enum)Win32Process.CommandLineProperty.COMMANDLINE, 0) : "";
  }
  
  private List<String> queryArguments() {
    String str = getCommandLine();
    return !str.isEmpty() ? Arrays.asList(Shell32Util.CommandLineToArgv(str)) : Collections.emptyList();
  }
  
  private String queryCwd() {
    if (!((String)((Triplet)this.cwdCmdEnv.get()).getA()).isEmpty())
      return (String)((Triplet)this.cwdCmdEnv.get()).getA(); 
    if (getProcessID() == this.os.getProcessId()) {
      String str = (new File(".")).getAbsolutePath();
      if (!str.isEmpty())
        return str.substring(0, str.length() - 1); 
    } 
    return "";
  }
  
  private Pair<String, String> queryUserInfo() {
    Pair<String, String> pair = null;
    WinNT.HANDLE hANDLE = Kernel32.INSTANCE.OpenProcess(1024, false, getProcessID());
    if (hANDLE != null) {
      ByRef.CloseableHANDLEByReference closeableHANDLEByReference = new ByRef.CloseableHANDLEByReference();
      try {
        try {
          if (Advapi32.INSTANCE.OpenProcessToken(hANDLE, 10, (WinNT.HANDLEByReference)closeableHANDLEByReference)) {
            Advapi32Util.Account account = Advapi32Util.getTokenAccount(closeableHANDLEByReference.getValue());
            pair = new Pair(account.name, account.sidString);
          } else {
            int i = Kernel32.INSTANCE.GetLastError();
            if (i != 5)
              LOG.error("Failed to get process token for process {}: {}", Integer.valueOf(getProcessID()), Integer.valueOf(Kernel32.INSTANCE.GetLastError())); 
          } 
        } catch (Win32Exception win32Exception) {
          LOG.warn("Failed to query user info for process {} ({}): {}", new Object[] { Integer.valueOf(getProcessID()), getName(), win32Exception.getMessage() });
        } finally {
          WinNT.HANDLE hANDLE1 = closeableHANDLEByReference.getValue();
          if (hANDLE1 != null)
            Kernel32.INSTANCE.CloseHandle(hANDLE1); 
          Kernel32.INSTANCE.CloseHandle(hANDLE);
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
    } 
    return (pair == null) ? new Pair("unknown", "unknown") : pair;
  }
  
  private Pair<String, String> queryGroupInfo() {
    Pair<String, String> pair = null;
    WinNT.HANDLE hANDLE = Kernel32.INSTANCE.OpenProcess(1024, false, getProcessID());
    if (hANDLE != null) {
      ByRef.CloseableHANDLEByReference closeableHANDLEByReference = new ByRef.CloseableHANDLEByReference();
      try {
        if (Advapi32.INSTANCE.OpenProcessToken(hANDLE, 10, (WinNT.HANDLEByReference)closeableHANDLEByReference)) {
          Advapi32Util.Account account = Advapi32Util.getTokenPrimaryGroup(closeableHANDLEByReference.getValue());
          pair = new Pair(account.name, account.sidString);
        } else {
          int i = Kernel32.INSTANCE.GetLastError();
          if (i != 5)
            LOG.error("Failed to get process token for process {}: {}", Integer.valueOf(getProcessID()), Integer.valueOf(Kernel32.INSTANCE.GetLastError())); 
        } 
        WinNT.HANDLE hANDLE1 = closeableHANDLEByReference.getValue();
        if (hANDLE1 != null)
          Kernel32.INSTANCE.CloseHandle(hANDLE1); 
        Kernel32.INSTANCE.CloseHandle(hANDLE);
        closeableHANDLEByReference.close();
      } catch (Throwable throwable) {
        try {
          closeableHANDLEByReference.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } 
    return (pair == null) ? new Pair("unknown", "unknown") : pair;
  }
  
  private Triplet<String, String, Map<String, String>> queryCwdCommandlineEnvironment() {
    WinNT.HANDLE hANDLE = Kernel32.INSTANCE.OpenProcess(1040, false, getProcessID());
    if (hANDLE != null)
      try {
        if (WindowsOperatingSystem.isX86() == WindowsOperatingSystem.isWow(hANDLE)) {
          ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
          try {
            NtDll.PROCESS_BASIC_INFORMATION pROCESS_BASIC_INFORMATION = new NtDll.PROCESS_BASIC_INFORMATION();
            int i = NtDll.INSTANCE.NtQueryInformationProcess(hANDLE, 0, pROCESS_BASIC_INFORMATION.getPointer(), pROCESS_BASIC_INFORMATION.size(), (IntByReference)closeableIntByReference);
            if (i != 0) {
              Triplet<String, String, Map<String, String>> triplet1 = defaultCwdCommandlineEnvironment();
              closeableIntByReference.close();
              return triplet1;
            } 
            pROCESS_BASIC_INFORMATION.read();
            NtDll.PEB pEB = new NtDll.PEB();
            Kernel32.INSTANCE.ReadProcessMemory(hANDLE, pROCESS_BASIC_INFORMATION.PebBaseAddress, pEB.getPointer(), pEB.size(), (IntByReference)closeableIntByReference);
            if (closeableIntByReference.getValue() == 0) {
              Triplet<String, String, Map<String, String>> triplet1 = defaultCwdCommandlineEnvironment();
              closeableIntByReference.close();
              return triplet1;
            } 
            pEB.read();
            NtDll.RTL_USER_PROCESS_PARAMETERS rTL_USER_PROCESS_PARAMETERS = new NtDll.RTL_USER_PROCESS_PARAMETERS();
            Kernel32.INSTANCE.ReadProcessMemory(hANDLE, pEB.ProcessParameters, rTL_USER_PROCESS_PARAMETERS.getPointer(), rTL_USER_PROCESS_PARAMETERS.size(), (IntByReference)closeableIntByReference);
            if (closeableIntByReference.getValue() == 0) {
              Triplet<String, String, Map<String, String>> triplet1 = defaultCwdCommandlineEnvironment();
              closeableIntByReference.close();
              return triplet1;
            } 
            rTL_USER_PROCESS_PARAMETERS.read();
            String str1 = readUnicodeString(hANDLE, rTL_USER_PROCESS_PARAMETERS.CurrentDirectory.DosPath);
            String str2 = readUnicodeString(hANDLE, rTL_USER_PROCESS_PARAMETERS.CommandLine);
            int j = rTL_USER_PROCESS_PARAMETERS.EnvironmentSize.intValue();
            if (j > 0) {
              Memory memory = new Memory(j);
              try {
                Kernel32.INSTANCE.ReadProcessMemory(hANDLE, rTL_USER_PROCESS_PARAMETERS.Environment, (Pointer)memory, j, (IntByReference)closeableIntByReference);
                if (closeableIntByReference.getValue() > 0) {
                  char[] arrayOfChar = memory.getCharArray(0L, j / 2);
                  Map<?, ?> map = ParseUtil.parseCharArrayToStringMap(arrayOfChar);
                  map.remove("");
                  Triplet<String, String, Map<String, String>> triplet1 = new Triplet(str1, str2, Collections.unmodifiableMap(map));
                  memory.close();
                  closeableIntByReference.close();
                  return triplet1;
                } 
                memory.close();
              } catch (Throwable throwable) {
                try {
                  memory.close();
                } catch (Throwable throwable1) {
                  throwable.addSuppressed(throwable1);
                } 
                throw throwable;
              } 
            } 
            Triplet<String, String, Map<String, String>> triplet = new Triplet(str1, str2, Collections.emptyMap());
            closeableIntByReference.close();
            return triplet;
          } catch (Throwable throwable) {
            try {
              closeableIntByReference.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        } 
      } finally {
        Kernel32.INSTANCE.CloseHandle(hANDLE);
      }  
    return defaultCwdCommandlineEnvironment();
  }
  
  private static Triplet<String, String, Map<String, String>> defaultCwdCommandlineEnvironment() {
    return new Triplet("", "", Collections.emptyMap());
  }
  
  private static String readUnicodeString(WinNT.HANDLE paramHANDLE, NtDll.UNICODE_STRING paramUNICODE_STRING) {
    if (paramUNICODE_STRING.Length > 0) {
      Memory memory = new Memory(paramUNICODE_STRING.Length + 2L);
      try {
        ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
        try {
          memory.clear();
          Kernel32.INSTANCE.ReadProcessMemory(paramHANDLE, paramUNICODE_STRING.Buffer, (Pointer)memory, paramUNICODE_STRING.Length, (IntByReference)closeableIntByReference);
          if (closeableIntByReference.getValue() > 0) {
            String str = memory.getWideString(0L);
            closeableIntByReference.close();
            memory.close();
            return str;
          } 
          closeableIntByReference.close();
        } catch (Throwable throwable) {
          try {
            closeableIntByReference.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
        memory.close();
      } catch (Throwable throwable) {
        try {
          memory.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } 
    return "";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\windows\WindowsOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
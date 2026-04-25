package oshi.software.os.mac;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.platform.unix.LibCAPI;
import com.sun.jna.platform.unix.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.mac.ThreadInfo;
import oshi.jna.Struct;
import oshi.software.common.AbstractOSProcess;
import oshi.software.os.OSProcess;
import oshi.software.os.OSThread;
import oshi.util.GlobalConfig;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.mac.SysctlUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public class MacOSProcess extends AbstractOSProcess {
  private static final Logger LOG = LoggerFactory.getLogger(MacOSProcess.class);
  
  private static final int ARGMAX = SysctlUtil.sysctl("kern.argmax", 0);
  
  private static final long TICKS_PER_MS;
  
  private static final boolean LOG_MAC_SYSCTL_WARNING = GlobalConfig.get("oshi.os.mac.sysctl.logwarning", false);
  
  private static final int MAC_RLIMIT_NOFILE = 8;
  
  private static final int P_LP64 = 4;
  
  private static final int SSLEEP = 1;
  
  private static final int SWAIT = 2;
  
  private static final int SRUN = 3;
  
  private static final int SIDL = 4;
  
  private static final int SZOMB = 5;
  
  private static final int SSTOP = 6;
  
  private int majorVersion;
  
  private int minorVersion;
  
  private final MacOperatingSystem os;
  
  private Supplier<String> commandLine = Memoizer.memoize(this::queryCommandLine);
  
  private Supplier<Pair<List<String>, Map<String, String>>> argsEnviron = Memoizer.memoize(this::queryArgsAndEnvironment);
  
  private String name = "";
  
  private String path = "";
  
  private String currentWorkingDirectory;
  
  private String user;
  
  private String userID;
  
  private String group;
  
  private String groupID;
  
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
  
  private long minorFaults;
  
  private long majorFaults;
  
  private long contextSwitches;
  
  public MacOSProcess(int paramInt1, int paramInt2, int paramInt3, MacOperatingSystem paramMacOperatingSystem) {
    super(paramInt1);
    this.majorVersion = paramInt2;
    this.minorVersion = paramInt3;
    this.os = paramMacOperatingSystem;
    updateAttributes();
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
  
  private String queryCommandLine() {
    return String.join(" ", (Iterable)getArguments());
  }
  
  public List<String> getArguments() {
    return (List<String>)((Pair)this.argsEnviron.get()).getA();
  }
  
  public Map<String, String> getEnvironmentVariables() {
    return (Map<String, String>)((Pair)this.argsEnviron.get()).getB();
  }
  
  private Pair<List<String>, Map<String, String>> queryArgsAndEnvironment() {
    int i = getProcessID();
    ArrayList<String> arrayList = new ArrayList();
    LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
    int[] arrayOfInt = new int[3];
    arrayOfInt[0] = 1;
    arrayOfInt[1] = 49;
    arrayOfInt[2] = i;
    Memory memory = new Memory(ARGMAX);
    try {
      memory.clear();
      LibCAPI.size_t.ByReference byReference = new LibCAPI.size_t.ByReference(ARGMAX);
      if (0 == SystemB.INSTANCE.sysctl(arrayOfInt, arrayOfInt.length, (Pointer)memory, byReference, null, LibCAPI.size_t.ZERO)) {
        int j = memory.getInt(0L);
        if (j > 0 && j <= 1024) {
          long l = SystemB.INT_SIZE;
          for (l += memory.getString(l).length(); l < byReference.longValue(); l += str.length()) {
            do {
            
            } while (memory.getByte(l) == 0 && ++l < byReference.longValue());
            String str = memory.getString(l);
            if (j-- > 0) {
              arrayList.add(str);
            } else {
              int k = str.indexOf('=');
              if (k > 0)
                linkedHashMap.put(str.substring(0, k), str.substring(k + 1)); 
            } 
          } 
        } 
      } else if (i > 0 && LOG_MAC_SYSCTL_WARNING) {
        LOG.warn("Failed sysctl call for process arguments (kern.procargs2), process {} may not exist. Error code: {}", Integer.valueOf(i), Integer.valueOf(Native.getLastError()));
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
    return new Pair(Collections.unmodifiableList(arrayList), Collections.unmodifiableMap(linkedHashMap));
  }
  
  public String getCurrentWorkingDirectory() {
    return this.currentWorkingDirectory;
  }
  
  public String getUser() {
    return this.user;
  }
  
  public String getUserID() {
    return this.userID;
  }
  
  public String getGroup() {
    return this.group;
  }
  
  public String getGroupID() {
    return this.groupID;
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
  
  public List<OSThread> getThreadDetails() {
    long l = System.currentTimeMillis();
    return (List<OSThread>)ThreadInfo.queryTaskThreads(getProcessID()).stream().parallel().map(paramThreadStats -> {
          long l = Math.max(paramLong - paramThreadStats.getUpTime(), getStartTime());
          return new MacOSThread(getProcessID(), paramThreadStats.getThreadId(), paramThreadStats.getState(), paramThreadStats.getSystemTime(), paramThreadStats.getUserTime(), l, paramLong - l, paramThreadStats.getPriority());
        }).collect(Collectors.toList());
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
    if (getProcessID() == this.os.getProcessId()) {
      Resource.Rlimit rlimit = new Resource.Rlimit();
      SystemB.INSTANCE.getrlimit(8, rlimit);
      return rlimit.rlim_cur;
    } 
    return -1L;
  }
  
  public long getHardOpenFileLimit() {
    if (getProcessID() == this.os.getProcessId()) {
      Resource.Rlimit rlimit = new Resource.Rlimit();
      SystemB.INSTANCE.getrlimit(8, rlimit);
      return rlimit.rlim_max;
    } 
    return -1L;
  }
  
  public int getBitness() {
    return this.bitness;
  }
  
  public long getAffinityMask() {
    int i = SysctlUtil.sysctl("hw.logicalcpu", 1);
    return (i < 64) ? ((1L << i) - 1L) : -1L;
  }
  
  public long getMinorFaults() {
    return this.minorFaults;
  }
  
  public long getMajorFaults() {
    return this.majorFaults;
  }
  
  public long getContextSwitches() {
    return this.contextSwitches;
  }
  
  public boolean updateAttributes() {
    long l = System.currentTimeMillis();
    Struct.CloseableProcTaskAllInfo closeableProcTaskAllInfo = new Struct.CloseableProcTaskAllInfo();
    try {
      if (0 > SystemB.INSTANCE.proc_pidinfo(getProcessID(), 2, 0L, (Structure)closeableProcTaskAllInfo, closeableProcTaskAllInfo.size()) || closeableProcTaskAllInfo.ptinfo.pti_threadnum < 1) {
        this.state = OSProcess.State.INVALID;
        boolean bool = false;
        closeableProcTaskAllInfo.close();
        return bool;
      } 
      Memory memory = new Memory(4096L);
      try {
        if (0 < SystemB.INSTANCE.proc_pidpath(getProcessID(), (Pointer)memory, 4096)) {
          this.path = memory.getString(0L).trim();
          String[] arrayOfString = this.path.split("/");
          if (arrayOfString.length > 0)
            this.name = arrayOfString[arrayOfString.length - 1]; 
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
      if (this.name.isEmpty())
        this.name = Native.toString(closeableProcTaskAllInfo.pbsd.pbi_comm, StandardCharsets.UTF_8); 
      switch (closeableProcTaskAllInfo.pbsd.pbi_status) {
        case 1:
          this.state = OSProcess.State.SLEEPING;
          break;
        case 2:
          this.state = OSProcess.State.WAITING;
          break;
        case 3:
          this.state = OSProcess.State.RUNNING;
          break;
        case 4:
          this.state = OSProcess.State.NEW;
          break;
        case 5:
          this.state = OSProcess.State.ZOMBIE;
          break;
        case 6:
          this.state = OSProcess.State.STOPPED;
          break;
        default:
          this.state = OSProcess.State.OTHER;
          break;
      } 
      this.parentProcessID = closeableProcTaskAllInfo.pbsd.pbi_ppid;
      this.userID = Integer.toString(closeableProcTaskAllInfo.pbsd.pbi_uid);
      SystemB.Passwd passwd = SystemB.INSTANCE.getpwuid(closeableProcTaskAllInfo.pbsd.pbi_uid);
      this.user = (passwd == null) ? Integer.toString(closeableProcTaskAllInfo.pbsd.pbi_uid) : passwd.pw_name;
      this.groupID = Integer.toString(closeableProcTaskAllInfo.pbsd.pbi_gid);
      SystemB.Group group = SystemB.INSTANCE.getgrgid(closeableProcTaskAllInfo.pbsd.pbi_gid);
      this.group = (group == null) ? Integer.toString(closeableProcTaskAllInfo.pbsd.pbi_gid) : group.gr_name;
      this.threadCount = closeableProcTaskAllInfo.ptinfo.pti_threadnum;
      this.priority = closeableProcTaskAllInfo.ptinfo.pti_priority;
      this.virtualSize = closeableProcTaskAllInfo.ptinfo.pti_virtual_size;
      this.residentSetSize = closeableProcTaskAllInfo.ptinfo.pti_resident_size;
      this.kernelTime = closeableProcTaskAllInfo.ptinfo.pti_total_system / TICKS_PER_MS;
      this.userTime = closeableProcTaskAllInfo.ptinfo.pti_total_user / TICKS_PER_MS;
      this.startTime = closeableProcTaskAllInfo.pbsd.pbi_start_tvsec * 1000L + closeableProcTaskAllInfo.pbsd.pbi_start_tvusec / 1000L;
      this.upTime = l - this.startTime;
      this.openFiles = closeableProcTaskAllInfo.pbsd.pbi_nfiles;
      this.bitness = ((closeableProcTaskAllInfo.pbsd.pbi_flags & 0x4) == 0) ? 32 : 64;
      this.majorFaults = closeableProcTaskAllInfo.ptinfo.pti_pageins;
      this.minorFaults = (closeableProcTaskAllInfo.ptinfo.pti_faults - closeableProcTaskAllInfo.ptinfo.pti_pageins);
      this.contextSwitches = closeableProcTaskAllInfo.ptinfo.pti_csw;
      closeableProcTaskAllInfo.close();
    } catch (Throwable throwable) {
      try {
        closeableProcTaskAllInfo.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    if (this.majorVersion > 10 || this.minorVersion >= 9) {
      Struct.CloseableRUsageInfoV2 closeableRUsageInfoV2 = new Struct.CloseableRUsageInfoV2();
      try {
        if (0 == SystemB.INSTANCE.proc_pid_rusage(getProcessID(), 2, (SystemB.RUsageInfoV2)closeableRUsageInfoV2)) {
          this.bytesRead = closeableRUsageInfoV2.ri_diskio_bytesread;
          this.bytesWritten = closeableRUsageInfoV2.ri_diskio_byteswritten;
        } 
        closeableRUsageInfoV2.close();
      } catch (Throwable throwable) {
        try {
          closeableRUsageInfoV2.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } 
    Struct.CloseableVnodePathInfo closeableVnodePathInfo = new Struct.CloseableVnodePathInfo();
    try {
      if (0 < SystemB.INSTANCE.proc_pidinfo(getProcessID(), 9, 0L, (Structure)closeableVnodePathInfo, closeableVnodePathInfo.size()))
        this.currentWorkingDirectory = Native.toString(closeableVnodePathInfo.pvi_cdir.vip_path, StandardCharsets.US_ASCII); 
      closeableVnodePathInfo.close();
    } catch (Throwable throwable) {
      try {
        closeableVnodePathInfo.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return true;
  }
  
  static {
    long l = 1000000000L;
    IOKit.IOIterator iOIterator = IOKitUtil.getMatchingServices("IOPlatformDevice");
    if (iOIterator != null) {
      for (IOKit.IORegistryEntry iORegistryEntry = iOIterator.next(); iORegistryEntry != null; iORegistryEntry = iOIterator.next()) {
        try {
          String str = iORegistryEntry.getName().toLowerCase(Locale.ROOT);
          if (str.startsWith("cpu") && str.length() > 3) {
            byte[] arrayOfByte = iORegistryEntry.getByteArrayProperty("timebase-frequency");
            if (arrayOfByte != null) {
              l = ParseUtil.byteArrayToLong(arrayOfByte, 4, false);
              iORegistryEntry.release();
              break;
            } 
          } 
        } finally {
          iORegistryEntry.release();
        } 
      } 
      iOIterator.release();
    } 
    TICKS_PER_MS = l / 1000L;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\mac\MacOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
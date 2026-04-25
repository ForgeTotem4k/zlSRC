package oshi.software.os.linux;

import com.sun.jna.platform.unix.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.proc.ProcessStat;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.software.common.AbstractOSProcess;
import oshi.software.os.OSProcess;
import oshi.software.os.OSThread;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.GlobalConfig;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.UserGroupInfo;
import oshi.util.Util;
import oshi.util.platform.linux.ProcPath;

@ThreadSafe
public class LinuxOSProcess extends AbstractOSProcess {
  private static final Logger LOG = LoggerFactory.getLogger(LinuxOSProcess.class);
  
  private static final boolean LOG_PROCFS_WARNING = GlobalConfig.get("oshi.os.linux.procfs.logwarning", false);
  
  private static final int[] PROC_PID_STAT_ORDERS = new int[(ProcPidStat.values()).length];
  
  private final LinuxOperatingSystem os;
  
  private Supplier<Integer> bitness = Memoizer.memoize(this::queryBitness);
  
  private Supplier<String> commandLine = Memoizer.memoize(this::queryCommandLine);
  
  private Supplier<List<String>> arguments = Memoizer.memoize(this::queryArguments);
  
  private Supplier<Map<String, String>> environmentVariables = Memoizer.memoize(this::queryEnvironmentVariables);
  
  private Supplier<String> user = Memoizer.memoize(this::queryUser);
  
  private Supplier<String> group = Memoizer.memoize(this::queryGroup);
  
  private String name;
  
  private String path = "";
  
  private String userID;
  
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
  
  private long minorFaults;
  
  private long majorFaults;
  
  private long contextSwitches;
  
  public LinuxOSProcess(int paramInt, LinuxOperatingSystem paramLinuxOperatingSystem) {
    super(paramInt);
    this.os = paramLinuxOperatingSystem;
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
    return Arrays.<CharSequence>stream((CharSequence[])FileUtil.getStringFromFile(String.format(Locale.ROOT, ProcPath.PID_CMDLINE, new Object[] { Integer.valueOf(getProcessID()) })).split("\000")).collect(Collectors.joining(" "));
  }
  
  public List<String> getArguments() {
    return this.arguments.get();
  }
  
  private List<String> queryArguments() {
    return Collections.unmodifiableList(ParseUtil.parseByteArrayToStrings(FileUtil.readAllBytes(String.format(Locale.ROOT, ProcPath.PID_CMDLINE, new Object[] { Integer.valueOf(getProcessID()) }), LOG_PROCFS_WARNING)));
  }
  
  public Map<String, String> getEnvironmentVariables() {
    return this.environmentVariables.get();
  }
  
  private Map<String, String> queryEnvironmentVariables() {
    return Collections.unmodifiableMap(ParseUtil.parseByteArrayToStringMap(FileUtil.readAllBytes(String.format(Locale.ROOT, ProcPath.PID_ENVIRON, new Object[] { Integer.valueOf(getProcessID()) }), LOG_PROCFS_WARNING)));
  }
  
  public String getCurrentWorkingDirectory() {
    try {
      String str1 = String.format(Locale.ROOT, ProcPath.PID_CWD, new Object[] { Integer.valueOf(getProcessID()) });
      String str2 = (new File(str1)).getCanonicalPath();
      if (!str2.equals(str1))
        return str2; 
    } catch (IOException iOException) {
      LOG.trace("Couldn't find cwd for pid {}: {}", Integer.valueOf(getProcessID()), iOException.getMessage());
    } 
    return "";
  }
  
  public String getUser() {
    return this.user.get();
  }
  
  private String queryUser() {
    return UserGroupInfo.getUser(this.userID);
  }
  
  public String getUserID() {
    return this.userID;
  }
  
  public String getGroup() {
    return this.group.get();
  }
  
  private String queryGroup() {
    return UserGroupInfo.getGroupName(this.groupID);
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
  
  public List<OSThread> getThreadDetails() {
    return (List<OSThread>)ProcessStat.getThreadIds(getProcessID()).stream().parallel().map(paramInteger -> new LinuxOSThread(getProcessID(), paramInteger.intValue())).filter(OSThread.ThreadFiltering.VALID_THREAD).collect(Collectors.toList());
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
  
  public long getOpenFiles() {
    return (ProcessStat.getFileDescriptorFiles(getProcessID())).length;
  }
  
  public long getSoftOpenFileLimit() {
    if (getProcessID() == this.os.getProcessId()) {
      Resource.Rlimit rlimit = new Resource.Rlimit();
      LinuxLibc.INSTANCE.getrlimit(7, rlimit);
      return rlimit.rlim_cur;
    } 
    return getProcessOpenFileLimit(getProcessID(), 1);
  }
  
  public long getHardOpenFileLimit() {
    if (getProcessID() == this.os.getProcessId()) {
      Resource.Rlimit rlimit = new Resource.Rlimit();
      LinuxLibc.INSTANCE.getrlimit(7, rlimit);
      return rlimit.rlim_max;
    } 
    return getProcessOpenFileLimit(getProcessID(), 2);
  }
  
  public int getBitness() {
    return ((Integer)this.bitness.get()).intValue();
  }
  
  private int queryBitness() {
    byte[] arrayOfByte = new byte[5];
    if (!this.path.isEmpty())
      try {
        FileInputStream fileInputStream = new FileInputStream(this.path);
        try {
          if (fileInputStream.read(arrayOfByte) == arrayOfByte.length) {
            byte b = (arrayOfByte[4] == 1) ? 32 : 64;
            fileInputStream.close();
            return b;
          } 
          fileInputStream.close();
        } catch (Throwable throwable) {
          try {
            fileInputStream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } catch (IOException iOException) {
        LOG.warn("Failed to read process file: {}", this.path);
      }  
    return 0;
  }
  
  public long getAffinityMask() {
    String str = ExecutingCommand.getFirstAnswer("taskset -p " + getProcessID());
    String[] arrayOfString = ParseUtil.whitespaces.split(str);
    try {
      return (new BigInteger(arrayOfString[arrayOfString.length - 1], 16)).longValue();
    } catch (NumberFormatException numberFormatException) {
      return 0L;
    } 
  }
  
  public boolean updateAttributes() {
    String str1 = String.format(Locale.ROOT, ProcPath.PID_EXE, new Object[] { Integer.valueOf(getProcessID()) });
    try {
      Path path = Paths.get(str1, new String[0]);
      this.path = Files.readSymbolicLink(path).toString();
      int i = this.path.indexOf(" (deleted)");
      if (i != -1)
        this.path = this.path.substring(0, i); 
    } catch (InvalidPathException|IOException|UnsupportedOperationException|SecurityException invalidPathException) {
      LOG.debug("Unable to open symbolic link {}", str1);
    } 
    Map map = FileUtil.getKeyValueMapFromFile(String.format(Locale.ROOT, ProcPath.PID_IO, new Object[] { Integer.valueOf(getProcessID()) }), ":");
    Map<String, String> map1 = FileUtil.getKeyValueMapFromFile(String.format(Locale.ROOT, ProcPath.PID_STATUS, new Object[] { Integer.valueOf(getProcessID()) }), ":");
    String str2 = FileUtil.getStringFromFile(String.format(Locale.ROOT, ProcPath.PID_STAT, new Object[] { Integer.valueOf(getProcessID()) }));
    if (str2.isEmpty()) {
      this.state = OSProcess.State.INVALID;
      return false;
    } 
    getMissingDetails(map1, str2);
    long l1 = System.currentTimeMillis();
    long[] arrayOfLong = ParseUtil.parseStringToLongArray(str2, PROC_PID_STAT_ORDERS, ProcessStat.PROC_PID_STAT_LENGTH, ' ');
    this.startTime = (LinuxOperatingSystem.BOOTTIME * LinuxOperatingSystem.getHz() + arrayOfLong[ProcPidStat.START_TIME.ordinal()]) * 1000L / LinuxOperatingSystem.getHz();
    if (this.startTime >= l1)
      this.startTime = l1 - 1L; 
    this.parentProcessID = (int)arrayOfLong[ProcPidStat.PPID.ordinal()];
    this.threadCount = (int)arrayOfLong[ProcPidStat.THREAD_COUNT.ordinal()];
    this.priority = (int)arrayOfLong[ProcPidStat.PRIORITY.ordinal()];
    this.virtualSize = arrayOfLong[ProcPidStat.VSZ.ordinal()];
    this.residentSetSize = arrayOfLong[ProcPidStat.RSS.ordinal()] * LinuxOperatingSystem.getPageSize();
    this.kernelTime = arrayOfLong[ProcPidStat.KERNEL_TIME.ordinal()] * 1000L / LinuxOperatingSystem.getHz();
    this.userTime = arrayOfLong[ProcPidStat.USER_TIME.ordinal()] * 1000L / LinuxOperatingSystem.getHz();
    this.minorFaults = arrayOfLong[ProcPidStat.MINOR_FAULTS.ordinal()];
    this.majorFaults = arrayOfLong[ProcPidStat.MAJOR_FAULTS.ordinal()];
    long l2 = ParseUtil.parseLongOrDefault(map1.get("nonvoluntary_ctxt_switches"), 0L);
    long l3 = ParseUtil.parseLongOrDefault(map1.get("voluntary_ctxt_switches"), 0L);
    this.contextSwitches = l3 + l2;
    this.upTime = l1 - this.startTime;
    this.bytesRead = ParseUtil.parseLongOrDefault((String)map.getOrDefault("read_bytes", ""), 0L);
    this.bytesWritten = ParseUtil.parseLongOrDefault((String)map.getOrDefault("write_bytes", ""), 0L);
    this.userID = ParseUtil.whitespaces.split((CharSequence)map1.getOrDefault("Uid", (V)""))[0];
    this.groupID = ParseUtil.whitespaces.split((CharSequence)map1.getOrDefault("Gid", (V)""))[0];
    this.name = map1.getOrDefault("Name", "");
    this.state = ProcessStat.getState(((String)map1.getOrDefault("State", "U")).charAt(0));
    return true;
  }
  
  private static void getMissingDetails(Map<String, String> paramMap, String paramString) {
    if (paramMap == null || paramString == null)
      return; 
    int i = paramString.indexOf('(');
    int j = paramString.indexOf(')');
    if (Util.isBlank(paramMap.get("Name")) && i > 0 && i < j) {
      String str = paramString.substring(i + 1, j);
      paramMap.put("Name", str);
    } 
    if (Util.isBlank(paramMap.get("State")) && j > 0 && paramString.length() > j + 2) {
      String str = String.valueOf(paramString.charAt(j + 2));
      paramMap.put("State", str);
    } 
  }
  
  private long getProcessOpenFileLimit(long paramLong, int paramInt) {
    String str = String.format(Locale.ROOT, "/proc/%d/limits", new Object[] { Long.valueOf(paramLong) });
    if (!Files.exists(Paths.get(str, new String[0]), new java.nio.file.LinkOption[0]))
      return -1L; 
    List list = FileUtil.readFile(str);
    Optional<String> optional = list.stream().filter(paramString -> paramString.startsWith("Max open files")).findFirst();
    if (!optional.isPresent())
      return -1L; 
    String[] arrayOfString = ((String)optional.get()).split("\\D+");
    return ParseUtil.parseLongOrDefault(arrayOfString[paramInt], -1L);
  }
  
  static {
    for (ProcPidStat procPidStat : ProcPidStat.values())
      PROC_PID_STAT_ORDERS[procPidStat.ordinal()] = procPidStat.getOrder() - 1; 
  }
  
  private enum ProcPidStat {
    PPID(4),
    MINOR_FAULTS(10),
    MAJOR_FAULTS(12),
    USER_TIME(14),
    KERNEL_TIME(15),
    PRIORITY(18),
    THREAD_COUNT(20),
    START_TIME(22),
    VSZ(23),
    RSS(24);
    
    private final int order;
    
    public int getOrder() {
      return this.order;
    }
    
    ProcPidStat(int param1Int1) {
      this.order = param1Int1;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\linux\LinuxOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
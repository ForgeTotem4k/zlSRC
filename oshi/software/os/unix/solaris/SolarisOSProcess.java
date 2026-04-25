package oshi.software.os.unix.solaris;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.solaris.PsInfo;
import oshi.jna.platform.unix.SolarisLibc;
import oshi.software.common.AbstractOSProcess;
import oshi.software.os.OSProcess;
import oshi.software.os.OSThread;
import oshi.util.Constants;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.UserGroupInfo;
import oshi.util.tuples.Pair;

@ThreadSafe
public class SolarisOSProcess extends AbstractOSProcess {
  private static final Logger LOG = LoggerFactory.getLogger(SolarisOSProcess.class);
  
  private final SolarisOperatingSystem os;
  
  private Supplier<Integer> bitness = Memoizer.memoize(this::queryBitness);
  
  private Supplier<SolarisLibc.SolarisPsInfo> psinfo = Memoizer.memoize(this::queryPsInfo, Memoizer.defaultExpiration());
  
  private Supplier<String> commandLine = Memoizer.memoize(this::queryCommandLine);
  
  private Supplier<Pair<List<String>, Map<String, String>>> cmdEnv = Memoizer.memoize(this::queryCommandlineEnvironment);
  
  private Supplier<SolarisLibc.SolarisPrUsage> prusage = Memoizer.memoize(this::queryPrUsage, Memoizer.defaultExpiration());
  
  private String name;
  
  private String path = "";
  
  private String commandLineBackup;
  
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
  
  private long minorFaults;
  
  private long majorFaults;
  
  private long contextSwitches = 0L;
  
  public SolarisOSProcess(int paramInt, SolarisOperatingSystem paramSolarisOperatingSystem) {
    super(paramInt);
    this.os = paramSolarisOperatingSystem;
    updateAttributes();
  }
  
  private SolarisLibc.SolarisPsInfo queryPsInfo() {
    return PsInfo.queryPsInfo(getProcessID());
  }
  
  private SolarisLibc.SolarisPrUsage queryPrUsage() {
    return PsInfo.queryPrUsage(getProcessID());
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
    String str = String.join(" ", (Iterable)getArguments());
    return str.isEmpty() ? this.commandLineBackup : str;
  }
  
  public List<String> getArguments() {
    return (List<String>)((Pair)this.cmdEnv.get()).getA();
  }
  
  public Map<String, String> getEnvironmentVariables() {
    return (Map<String, String>)((Pair)this.cmdEnv.get()).getB();
  }
  
  private Pair<List<String>, Map<String, String>> queryCommandlineEnvironment() {
    return PsInfo.queryArgsEnv(getProcessID(), this.psinfo.get());
  }
  
  public String getCurrentWorkingDirectory() {
    try {
      String str1 = "/proc" + getProcessID() + "/cwd";
      String str2 = (new File(str1)).getCanonicalPath();
      if (!str2.equals(str1))
        return str2; 
    } catch (IOException iOException) {
      LOG.trace("Couldn't find cwd for pid {}: {}", Integer.valueOf(getProcessID()), iOException.getMessage());
    } 
    return "";
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
    try {
      Stream<Path> stream = Files.list(Paths.get("/proc/" + getProcessID() + "/fd", new String[0]));
      try {
        long l = stream.count();
        if (stream != null)
          stream.close(); 
        return l;
      } catch (Throwable throwable) {
        if (stream != null)
          try {
            stream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (IOException iOException) {
      return 0L;
    } 
  }
  
  public long getSoftOpenFileLimit() {
    if (getProcessID() == this.os.getProcessId()) {
      Resource.Rlimit rlimit = new Resource.Rlimit();
      SolarisLibc.INSTANCE.getrlimit(7, rlimit);
      return rlimit.rlim_cur;
    } 
    return getProcessOpenFileLimit(getProcessID(), 1);
  }
  
  public long getHardOpenFileLimit() {
    if (getProcessID() == this.os.getProcessId()) {
      Resource.Rlimit rlimit = new Resource.Rlimit();
      SolarisLibc.INSTANCE.getrlimit(7, rlimit);
      return rlimit.rlim_max;
    } 
    return getProcessOpenFileLimit(getProcessID(), 2);
  }
  
  public int getBitness() {
    return ((Integer)this.bitness.get()).intValue();
  }
  
  private int queryBitness() {
    List list = ExecutingCommand.runNative("pflags " + getProcessID());
    for (String str : list) {
      if (str.contains("data model")) {
        if (str.contains("LP32"))
          return 32; 
        if (str.contains("LP64"))
          return 64; 
      } 
    } 
    return 0;
  }
  
  public long getAffinityMask() {
    long l = 0L;
    String str = ExecutingCommand.getFirstAnswer("pbind -q " + getProcessID());
    if (str.isEmpty()) {
      List list = ExecutingCommand.runNative("psrinfo");
      for (String str1 : list) {
        String[] arrayOfString = ParseUtil.whitespaces.split(str1);
        int i = ParseUtil.parseIntOrDefault(arrayOfString[0], -1);
        if (i >= 0)
          l |= 1L << i; 
      } 
      return l;
    } 
    if (str.endsWith(".") && str.contains("strongly bound to processor(s)")) {
      String str1 = str.substring(0, str.length() - 1);
      String[] arrayOfString = ParseUtil.whitespaces.split(str1);
      int i = arrayOfString.length - 1;
      while (i >= 0) {
        int j = ParseUtil.parseIntOrDefault(arrayOfString[i], -1);
        if (j >= 0) {
          l |= 1L << j;
          i--;
        } 
      } 
    } 
    return l;
  }
  
  public List<OSThread> getThreadDetails() {
    File file = new File(String.format(Locale.ROOT, "/proc/%d/lwp", new Object[] { Integer.valueOf(getProcessID()) }));
    File[] arrayOfFile = file.listFiles(paramFile -> Constants.DIGITS.matcher(paramFile.getName()).matches());
    return (arrayOfFile == null) ? Collections.emptyList() : (List<OSThread>)Arrays.<File>stream(arrayOfFile).parallel().map(paramFile -> new SolarisOSThread(getProcessID(), ParseUtil.parseIntOrDefault(paramFile.getName(), 0))).filter(OSThread.ThreadFiltering.VALID_THREAD).collect(Collectors.toList());
  }
  
  public boolean updateAttributes() {
    SolarisLibc.SolarisPsInfo solarisPsInfo = this.psinfo.get();
    if (solarisPsInfo == null) {
      this.state = OSProcess.State.INVALID;
      return false;
    } 
    SolarisLibc.SolarisPrUsage solarisPrUsage = this.prusage.get();
    long l1 = System.currentTimeMillis();
    this.state = getStateFromOutput((char)solarisPsInfo.pr_lwp.pr_sname);
    this.parentProcessID = solarisPsInfo.pr_ppid;
    this.userID = Integer.toString(solarisPsInfo.pr_euid);
    this.user = UserGroupInfo.getUser(this.userID);
    this.groupID = Integer.toString(solarisPsInfo.pr_egid);
    this.group = UserGroupInfo.getGroupName(this.groupID);
    this.threadCount = solarisPsInfo.pr_nlwp;
    this.priority = solarisPsInfo.pr_lwp.pr_pri;
    this.virtualSize = solarisPsInfo.pr_size.longValue() * 1024L;
    this.residentSetSize = solarisPsInfo.pr_rssize.longValue() * 1024L;
    this.startTime = solarisPsInfo.pr_start.tv_sec.longValue() * 1000L + solarisPsInfo.pr_start.tv_nsec.longValue() / 1000000L;
    long l2 = l1 - this.startTime;
    this.upTime = (l2 < 1L) ? 1L : l2;
    this.kernelTime = 0L;
    this.userTime = solarisPsInfo.pr_time.tv_sec.longValue() * 1000L + solarisPsInfo.pr_time.tv_nsec.longValue() / 1000000L;
    this.commandLineBackup = Native.toString(solarisPsInfo.pr_psargs);
    this.path = ParseUtil.whitespaces.split(this.commandLineBackup)[0];
    this.name = this.path.substring(this.path.lastIndexOf('/') + 1);
    if (solarisPrUsage != null) {
      this.userTime = solarisPrUsage.pr_utime.tv_sec.longValue() * 1000L + solarisPrUsage.pr_utime.tv_nsec.longValue() / 1000000L;
      this.kernelTime = solarisPrUsage.pr_stime.tv_sec.longValue() * 1000L + solarisPrUsage.pr_stime.tv_nsec.longValue() / 1000000L;
      this.bytesRead = solarisPrUsage.pr_ioch.longValue();
      this.majorFaults = solarisPrUsage.pr_majf.longValue();
      this.minorFaults = solarisPrUsage.pr_minf.longValue();
      this.contextSwitches = solarisPrUsage.pr_ictx.longValue() + solarisPrUsage.pr_vctx.longValue();
    } 
    return true;
  }
  
  static OSProcess.State getStateFromOutput(char paramChar) {
    switch (paramChar) {
      case 'O':
        return OSProcess.State.RUNNING;
      case 'S':
        return OSProcess.State.SLEEPING;
      case 'R':
      case 'W':
        return OSProcess.State.WAITING;
      case 'Z':
        return OSProcess.State.ZOMBIE;
      case 'T':
        return OSProcess.State.STOPPED;
    } 
    return OSProcess.State.OTHER;
  }
  
  private long getProcessOpenFileLimit(long paramLong, int paramInt) {
    List list = ExecutingCommand.runNative("plimit " + paramLong);
    if (list.isEmpty())
      return -1L; 
    Optional<String> optional = list.stream().filter(paramString -> paramString.trim().startsWith("nofiles")).findFirst();
    if (!optional.isPresent())
      return -1L; 
    String[] arrayOfString = ((String)optional.get()).split("\\D+");
    return ParseUtil.parseLongOrDefault(arrayOfString[paramInt], -1L);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\solaris\SolarisOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
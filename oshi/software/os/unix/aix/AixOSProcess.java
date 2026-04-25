package oshi.software.os.unix.aix;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.Resource;
import com.sun.jna.platform.unix.aix.Perfstat;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.PsInfo;
import oshi.driver.unix.aix.perfstat.PerfstatCpu;
import oshi.jna.platform.unix.AixLibc;
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
public class AixOSProcess extends AbstractOSProcess {
  private static final Logger LOG = LoggerFactory.getLogger(AixOSProcess.class);
  
  private Supplier<Integer> bitness = Memoizer.memoize(this::queryBitness);
  
  private Supplier<AixLibc.AixPsInfo> psinfo = Memoizer.memoize(this::queryPsInfo, Memoizer.defaultExpiration());
  
  private Supplier<String> commandLine = Memoizer.memoize(this::queryCommandLine);
  
  private Supplier<Pair<List<String>, Map<String, String>>> cmdEnv = Memoizer.memoize(this::queryCommandlineEnvironment);
  
  private final Supplier<Long> affinityMask = Memoizer.memoize(PerfstatCpu::queryCpuAffinityMask, Memoizer.defaultExpiration());
  
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
  
  private Supplier<Perfstat.perfstat_process_t[]> procCpu;
  
  private final AixOperatingSystem os;
  
  public AixOSProcess(int paramInt, Pair<Long, Long> paramPair, Supplier<Perfstat.perfstat_process_t[]> paramSupplier, AixOperatingSystem paramAixOperatingSystem) {
    super(paramInt);
    this.procCpu = paramSupplier;
    this.os = paramAixOperatingSystem;
    updateAttributes(paramPair);
  }
  
  private AixLibc.AixPsInfo queryPsInfo() {
    return PsInfo.queryPsInfo(getProcessID());
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
      AixLibc.INSTANCE.getrlimit(7, rlimit);
      return rlimit.rlim_cur;
    } 
    return -1L;
  }
  
  public long getHardOpenFileLimit() {
    if (getProcessID() == this.os.getProcessId()) {
      Resource.Rlimit rlimit = new Resource.Rlimit();
      AixLibc.INSTANCE.getrlimit(7, rlimit);
      return rlimit.rlim_max;
    } 
    return -1L;
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
    File file = new File(String.format(Locale.ROOT, "/proc/%d/lwp", new Object[] { Integer.valueOf(getProcessID()) }));
    File[] arrayOfFile = file.listFiles(paramFile -> Constants.DIGITS.matcher(paramFile.getName()).matches());
    if (arrayOfFile == null)
      return l; 
    for (File file1 : arrayOfFile) {
      int i = ParseUtil.parseIntOrDefault(file1.getName(), 0);
      AixLibc.AixLwpsInfo aixLwpsInfo = PsInfo.queryLwpsInfo(getProcessID(), i);
      if (aixLwpsInfo != null)
        l |= aixLwpsInfo.pr_bindpro; 
    } 
    l &= ((Long)this.affinityMask.get()).longValue();
    return l;
  }
  
  public List<OSThread> getThreadDetails() {
    File file = new File(String.format(Locale.ROOT, "/proc/%d/lwp", new Object[] { Integer.valueOf(getProcessID()) }));
    File[] arrayOfFile = file.listFiles(paramFile -> Constants.DIGITS.matcher(paramFile.getName()).matches());
    return (arrayOfFile == null) ? Collections.emptyList() : (List<OSThread>)Arrays.<File>stream(arrayOfFile).parallel().map(paramFile -> new AixOSThread(getProcessID(), ParseUtil.parseIntOrDefault(paramFile.getName(), 0))).filter(OSThread.ThreadFiltering.VALID_THREAD).collect(Collectors.toList());
  }
  
  public boolean updateAttributes() {
    Perfstat.perfstat_process_t[] arrayOfPerfstat_process_t = this.procCpu.get();
    for (Perfstat.perfstat_process_t perfstat_process_t : arrayOfPerfstat_process_t) {
      int i = (int)perfstat_process_t.pid;
      if (i == getProcessID())
        return updateAttributes(new Pair(Long.valueOf((long)perfstat_process_t.ucpu_time), Long.valueOf((long)perfstat_process_t.scpu_time))); 
    } 
    this.state = OSProcess.State.INVALID;
    return false;
  }
  
  private boolean updateAttributes(Pair<Long, Long> paramPair) {
    AixLibc.AixPsInfo aixPsInfo = this.psinfo.get();
    if (aixPsInfo == null) {
      this.state = OSProcess.State.INVALID;
      return false;
    } 
    long l1 = System.currentTimeMillis();
    this.state = getStateFromOutput((char)aixPsInfo.pr_lwp.pr_sname);
    this.parentProcessID = (int)aixPsInfo.pr_ppid;
    this.userID = Long.toString(aixPsInfo.pr_euid);
    this.user = UserGroupInfo.getUser(this.userID);
    this.groupID = Long.toString(aixPsInfo.pr_egid);
    this.group = UserGroupInfo.getGroupName(this.groupID);
    this.threadCount = aixPsInfo.pr_nlwp;
    this.priority = aixPsInfo.pr_lwp.pr_pri;
    this.virtualSize = aixPsInfo.pr_size * 1024L;
    this.residentSetSize = aixPsInfo.pr_rssize * 1024L;
    this.startTime = aixPsInfo.pr_start.tv_sec * 1000L + aixPsInfo.pr_start.tv_nsec / 1000000L;
    long l2 = l1 - this.startTime;
    this.upTime = (l2 < 1L) ? 1L : l2;
    this.userTime = ((Long)paramPair.getA()).longValue();
    this.kernelTime = ((Long)paramPair.getB()).longValue();
    this.commandLineBackup = Native.toString(aixPsInfo.pr_psargs);
    this.path = ParseUtil.whitespaces.split(this.commandLineBackup)[0];
    this.name = this.path.substring(this.path.lastIndexOf('/') + 1);
    if (this.name.isEmpty())
      this.name = Native.toString(aixPsInfo.pr_fname); 
    return true;
  }
  
  static OSProcess.State getStateFromOutput(char paramChar) {
    switch (paramChar) {
      case 'O':
        return OSProcess.State.INVALID;
      case 'A':
      case 'R':
        return OSProcess.State.RUNNING;
      case 'I':
        return OSProcess.State.WAITING;
      case 'S':
      case 'W':
        return OSProcess.State.SLEEPING;
      case 'Z':
        return OSProcess.State.ZOMBIE;
      case 'T':
        return OSProcess.State.STOPPED;
    } 
    return OSProcess.State.OTHER;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\aix\AixOSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
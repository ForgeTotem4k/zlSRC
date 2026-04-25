package oshi.software.os;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.Who;
import oshi.driver.unix.Xwininfo;
import oshi.util.UserGroupInfo;
import oshi.util.Util;

@ThreadSafe
public interface OperatingSystem {
  String getFamily();
  
  String getManufacturer();
  
  OSVersionInfo getVersionInfo();
  
  FileSystem getFileSystem();
  
  InternetProtocolStats getInternetProtocolStats();
  
  default List<OSProcess> getProcesses() {
    return getProcesses(null, null, 0);
  }
  
  List<OSProcess> getProcesses(Predicate<OSProcess> paramPredicate, Comparator<OSProcess> paramComparator, int paramInt);
  
  default List<OSProcess> getProcesses(Collection<Integer> paramCollection) {
    return (List<OSProcess>)paramCollection.stream().map(this::getProcess).filter(Objects::nonNull).filter(ProcessFiltering.VALID_PROCESS).collect(Collectors.toList());
  }
  
  OSProcess getProcess(int paramInt);
  
  List<OSProcess> getChildProcesses(int paramInt1, Predicate<OSProcess> paramPredicate, Comparator<OSProcess> paramComparator, int paramInt2);
  
  List<OSProcess> getDescendantProcesses(int paramInt1, Predicate<OSProcess> paramPredicate, Comparator<OSProcess> paramComparator, int paramInt2);
  
  int getProcessId();
  
  default OSProcess getCurrentProcess() {
    return getProcess(getProcessId());
  }
  
  int getProcessCount();
  
  int getThreadId();
  
  OSThread getCurrentThread();
  
  int getThreadCount();
  
  int getBitness();
  
  long getSystemUptime();
  
  long getSystemBootTime();
  
  default boolean isElevated() {
    return UserGroupInfo.isElevated();
  }
  
  NetworkParams getNetworkParams();
  
  default List<OSService> getServices() {
    return new ArrayList<>();
  }
  
  default List<OSSession> getSessions() {
    return Who.queryWho();
  }
  
  default List<OSDesktopWindow> getDesktopWindows(boolean paramBoolean) {
    return Xwininfo.queryXWindows(paramBoolean);
  }
  
  default List<ApplicationInfo> getInstalledApplications() {
    return Collections.emptyList();
  }
  
  public static final class ProcessFiltering {
    public static final Predicate<OSProcess> ALL_PROCESSES = param1OSProcess -> true;
    
    public static final Predicate<OSProcess> VALID_PROCESS;
    
    public static final Predicate<OSProcess> NO_PARENT;
    
    public static final Predicate<OSProcess> BITNESS_64;
    
    public static final Predicate<OSProcess> BITNESS_32;
    
    static {
      VALID_PROCESS = (param1OSProcess -> !param1OSProcess.getState().equals(OSProcess.State.INVALID));
      NO_PARENT = (param1OSProcess -> (param1OSProcess.getParentProcessID() == param1OSProcess.getProcessID()));
      BITNESS_64 = (param1OSProcess -> (param1OSProcess.getBitness() == 64));
      BITNESS_32 = (param1OSProcess -> (param1OSProcess.getBitness() == 32));
    }
  }
  
  @Immutable
  public static class OSVersionInfo {
    private final String version;
    
    private final String codeName;
    
    private final String buildNumber;
    
    private final String versionStr;
    
    public OSVersionInfo(String param1String1, String param1String2, String param1String3) {
      this.version = param1String1;
      this.codeName = param1String2;
      this.buildNumber = param1String3;
      StringBuilder stringBuilder = new StringBuilder((getVersion() != null) ? getVersion() : "unknown");
      if (!Util.isBlank(getCodeName()))
        stringBuilder.append(" (").append(getCodeName()).append(')'); 
      if (!Util.isBlank(getBuildNumber()))
        stringBuilder.append(" build ").append(getBuildNumber()); 
      this.versionStr = stringBuilder.toString();
    }
    
    public String getVersion() {
      return this.version;
    }
    
    public String getCodeName() {
      return this.codeName;
    }
    
    public String getBuildNumber() {
      return this.buildNumber;
    }
    
    public String toString() {
      return this.versionStr;
    }
  }
  
  public static final class ProcessSorting {
    public static final Comparator<OSProcess> NO_SORTING = (param1OSProcess1, param1OSProcess2) -> 0;
    
    public static final Comparator<OSProcess> CPU_DESC = Comparator.<OSProcess>comparingDouble(OSProcess::getProcessCpuLoadCumulative).reversed();
    
    public static final Comparator<OSProcess> RSS_DESC = Comparator.<OSProcess>comparingLong(OSProcess::getResidentSetSize).reversed();
    
    public static final Comparator<OSProcess> UPTIME_ASC = Comparator.comparingLong(OSProcess::getUpTime);
    
    public static final Comparator<OSProcess> UPTIME_DESC = UPTIME_ASC.reversed();
    
    public static final Comparator<OSProcess> PID_ASC = Comparator.comparingInt(OSProcess::getProcessID);
    
    public static final Comparator<OSProcess> PARENTPID_ASC = Comparator.comparingInt(OSProcess::getParentProcessID);
    
    public static final Comparator<OSProcess> NAME_ASC = Comparator.comparing(OSProcess::getName, String.CASE_INSENSITIVE_ORDER);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\OperatingSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
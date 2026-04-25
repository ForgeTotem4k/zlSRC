package oshi.software.os;

import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface OSProcess {
  String getName();
  
  String getPath();
  
  String getCommandLine();
  
  List<String> getArguments();
  
  Map<String, String> getEnvironmentVariables();
  
  String getCurrentWorkingDirectory();
  
  String getUser();
  
  String getUserID();
  
  String getGroup();
  
  String getGroupID();
  
  State getState();
  
  int getProcessID();
  
  int getParentProcessID();
  
  int getThreadCount();
  
  int getPriority();
  
  long getVirtualSize();
  
  long getResidentSetSize();
  
  long getKernelTime();
  
  long getUserTime();
  
  long getUpTime();
  
  long getStartTime();
  
  long getBytesRead();
  
  long getBytesWritten();
  
  long getOpenFiles();
  
  long getSoftOpenFileLimit();
  
  long getHardOpenFileLimit();
  
  double getProcessCpuLoadCumulative();
  
  double getProcessCpuLoadBetweenTicks(OSProcess paramOSProcess);
  
  int getBitness();
  
  long getAffinityMask();
  
  boolean updateAttributes();
  
  List<OSThread> getThreadDetails();
  
  default long getMinorFaults() {
    return 0L;
  }
  
  default long getMajorFaults() {
    return 0L;
  }
  
  default long getContextSwitches() {
    return 0L;
  }
  
  public enum State {
    NEW, RUNNING, SLEEPING, WAITING, ZOMBIE, STOPPED, OTHER, INVALID, SUSPENDED;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\OSProcess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
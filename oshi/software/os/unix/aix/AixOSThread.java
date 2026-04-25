package oshi.software.os.unix.aix;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.PsInfo;
import oshi.jna.platform.unix.AixLibc;
import oshi.software.common.AbstractOSThread;
import oshi.software.os.OSProcess;

@ThreadSafe
public class AixOSThread extends AbstractOSThread {
  private int threadId;
  
  private OSProcess.State state = OSProcess.State.INVALID;
  
  private long startMemoryAddress;
  
  private long contextSwitches;
  
  private long kernelTime;
  
  private long userTime;
  
  private long startTime;
  
  private long upTime;
  
  private int priority;
  
  public AixOSThread(int paramInt1, int paramInt2) {
    super(paramInt1);
    this.threadId = paramInt2;
    updateAttributes();
  }
  
  public int getThreadId() {
    return this.threadId;
  }
  
  public OSProcess.State getState() {
    return this.state;
  }
  
  public long getStartMemoryAddress() {
    return this.startMemoryAddress;
  }
  
  public long getContextSwitches() {
    return this.contextSwitches;
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
  
  public int getPriority() {
    return this.priority;
  }
  
  public boolean updateAttributes() {
    AixLibc.AixLwpsInfo aixLwpsInfo = PsInfo.queryLwpsInfo(getOwningProcessId(), getThreadId());
    if (aixLwpsInfo == null) {
      this.state = OSProcess.State.INVALID;
      return false;
    } 
    this.threadId = (int)aixLwpsInfo.pr_lwpid;
    this.startMemoryAddress = aixLwpsInfo.pr_addr;
    this.state = AixOSProcess.getStateFromOutput((char)aixLwpsInfo.pr_sname);
    this.priority = aixLwpsInfo.pr_pri;
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\aix\AixOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
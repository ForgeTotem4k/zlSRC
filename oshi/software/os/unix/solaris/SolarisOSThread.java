package oshi.software.os.unix.solaris;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.solaris.PsInfo;
import oshi.jna.platform.unix.SolarisLibc;
import oshi.software.common.AbstractOSThread;
import oshi.software.os.OSProcess;
import oshi.util.Memoizer;
import oshi.util.Util;

@ThreadSafe
public class SolarisOSThread extends AbstractOSThread {
  private Supplier<SolarisLibc.SolarisLwpsInfo> lwpsinfo = Memoizer.memoize(this::queryLwpsInfo, Memoizer.defaultExpiration());
  
  private Supplier<SolarisLibc.SolarisPrUsage> prusage = Memoizer.memoize(this::queryPrUsage, Memoizer.defaultExpiration());
  
  private String name;
  
  private int threadId;
  
  private OSProcess.State state = OSProcess.State.INVALID;
  
  private long startMemoryAddress;
  
  private long contextSwitches;
  
  private long kernelTime;
  
  private long userTime;
  
  private long startTime;
  
  private long upTime;
  
  private int priority;
  
  public SolarisOSThread(int paramInt1, int paramInt2) {
    super(paramInt1);
    this.threadId = paramInt2;
    updateAttributes();
  }
  
  private SolarisLibc.SolarisLwpsInfo queryLwpsInfo() {
    return PsInfo.queryLwpsInfo(getOwningProcessId(), getThreadId());
  }
  
  private SolarisLibc.SolarisPrUsage queryPrUsage() {
    return PsInfo.queryPrUsage(getOwningProcessId(), getThreadId());
  }
  
  public String getName() {
    return (this.name != null) ? this.name : "";
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
    SolarisLibc.SolarisLwpsInfo solarisLwpsInfo = this.lwpsinfo.get();
    if (solarisLwpsInfo == null) {
      this.state = OSProcess.State.INVALID;
      return false;
    } 
    SolarisLibc.SolarisPrUsage solarisPrUsage = this.prusage.get();
    long l1 = System.currentTimeMillis();
    this.state = SolarisOSProcess.getStateFromOutput((char)solarisLwpsInfo.pr_sname);
    this.startTime = solarisLwpsInfo.pr_start.tv_sec.longValue() * 1000L + solarisLwpsInfo.pr_start.tv_nsec.longValue() / 1000000L;
    long l2 = l1 - this.startTime;
    this.upTime = (l2 < 1L) ? 1L : l2;
    this.kernelTime = 0L;
    this.userTime = solarisLwpsInfo.pr_time.tv_sec.longValue() * 1000L + solarisLwpsInfo.pr_time.tv_nsec.longValue() / 1000000L;
    this.startMemoryAddress = Pointer.nativeValue(solarisLwpsInfo.pr_addr);
    this.priority = solarisLwpsInfo.pr_pri;
    if (solarisPrUsage != null) {
      this.userTime = solarisPrUsage.pr_utime.tv_sec.longValue() * 1000L + solarisPrUsage.pr_utime.tv_nsec.longValue() / 1000000L;
      this.kernelTime = solarisPrUsage.pr_stime.tv_sec.longValue() * 1000L + solarisPrUsage.pr_stime.tv_nsec.longValue() / 1000000L;
      this.contextSwitches = solarisPrUsage.pr_ictx.longValue() + solarisPrUsage.pr_vctx.longValue();
    } 
    this.name = Native.toString(solarisLwpsInfo.pr_name);
    if (Util.isBlank(this.name))
      this.name = Native.toString(solarisLwpsInfo.pr_oldname); 
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\solaris\SolarisOSThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
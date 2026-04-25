package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import oshi.annotation.concurrent.GuardedBy;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.Memoizer;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public final class Win32ProcessCached {
  private static final Supplier<Win32ProcessCached> INSTANCE = Memoizer.memoize(Win32ProcessCached::createInstance);
  
  @GuardedBy("commandLineCacheLock")
  private final Map<Integer, Pair<Long, String>> commandLineCache = new HashMap<>();
  
  private final ReentrantLock commandLineCacheLock = new ReentrantLock();
  
  public static Win32ProcessCached getInstance() {
    return INSTANCE.get();
  }
  
  private static Win32ProcessCached createInstance() {
    return new Win32ProcessCached();
  }
  
  public String getCommandLine(int paramInt, long paramLong) {
    this.commandLineCacheLock.lock();
    try {
      Pair pair = this.commandLineCache.get(Integer.valueOf(paramInt));
      if (pair != null && paramLong < ((Long)pair.getA()).longValue())
        return (String)pair.getB(); 
      long l = System.currentTimeMillis();
      WbemcliUtil.WmiResult<Win32Process.CommandLineProperty> wmiResult = Win32Process.queryCommandLines(null);
      if (this.commandLineCache.size() > wmiResult.getResultCount() * 2)
        this.commandLineCache.clear(); 
      String str = "";
      for (byte b = 0; b < wmiResult.getResultCount(); b++) {
        int i = WmiUtil.getUint32(wmiResult, Win32Process.CommandLineProperty.PROCESSID, b);
        String str1 = WmiUtil.getString(wmiResult, Win32Process.CommandLineProperty.COMMANDLINE, b);
        this.commandLineCache.put(Integer.valueOf(i), new Pair(Long.valueOf(l), str1));
        if (i == paramInt)
          str = str1; 
      } 
      return str;
    } finally {
      this.commandLineCacheLock.unlock();
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\Win32ProcessCached.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
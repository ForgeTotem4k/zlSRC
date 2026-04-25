package oshi.driver.windows.registry;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.Wtsapi32;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.wmi.Win32Process;
import oshi.jna.ByRef;
import oshi.util.platform.windows.WmiUtil;

@ThreadSafe
public final class ProcessWtsData {
  private static final Logger LOG = LoggerFactory.getLogger(ProcessWtsData.class);
  
  private static final boolean IS_WINDOWS7_OR_GREATER = VersionHelpers.IsWindows7OrGreater();
  
  public static Map<Integer, WtsInfo> queryProcessWtsMap(Collection<Integer> paramCollection) {
    return IS_WINDOWS7_OR_GREATER ? queryProcessWtsMapFromWTS(paramCollection) : queryProcessWtsMapFromPerfMon(paramCollection);
  }
  
  private static Map<Integer, WtsInfo> queryProcessWtsMapFromWTS(Collection<Integer> paramCollection) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference(0);
    try {
      ByRef.CloseablePointerByReference closeablePointerByReference = new ByRef.CloseablePointerByReference();
      try {
        ByRef.CloseableIntByReference closeableIntByReference1 = new ByRef.CloseableIntByReference(1);
        try {
          if (!Wtsapi32.INSTANCE.WTSEnumerateProcessesEx(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, (IntByReference)closeableIntByReference1, -2, (PointerByReference)closeablePointerByReference, (IntByReference)closeableIntByReference)) {
            LOG.error("Failed to enumerate Processes. Error code: {}", Integer.valueOf(Kernel32.INSTANCE.GetLastError()));
            HashMap<Object, Object> hashMap1 = hashMap;
            closeableIntByReference1.close();
            closeablePointerByReference.close();
            closeableIntByReference.close();
            return (Map)hashMap1;
          } 
          Pointer pointer = closeablePointerByReference.getValue();
          Wtsapi32.WTS_PROCESS_INFO_EX wTS_PROCESS_INFO_EX = new Wtsapi32.WTS_PROCESS_INFO_EX(pointer);
          Wtsapi32.WTS_PROCESS_INFO_EX[] arrayOfWTS_PROCESS_INFO_EX = (Wtsapi32.WTS_PROCESS_INFO_EX[])wTS_PROCESS_INFO_EX.toArray(closeableIntByReference.getValue());
          for (Wtsapi32.WTS_PROCESS_INFO_EX wTS_PROCESS_INFO_EX1 : arrayOfWTS_PROCESS_INFO_EX) {
            if (paramCollection == null || paramCollection.contains(Integer.valueOf(wTS_PROCESS_INFO_EX1.ProcessId)))
              hashMap.put(Integer.valueOf(wTS_PROCESS_INFO_EX1.ProcessId), new WtsInfo(wTS_PROCESS_INFO_EX1.pProcessName, "", wTS_PROCESS_INFO_EX1.NumberOfThreads, wTS_PROCESS_INFO_EX1.PagefileUsage & 0xFFFFFFFFL, wTS_PROCESS_INFO_EX1.KernelTime.getValue() / 10000L, wTS_PROCESS_INFO_EX1.UserTime.getValue() / 10000L, wTS_PROCESS_INFO_EX1.HandleCount)); 
          } 
          if (!Wtsapi32.INSTANCE.WTSFreeMemoryEx(1, pointer, closeableIntByReference.getValue()))
            LOG.warn("Failed to Free Memory for Processes. Error code: {}", Integer.valueOf(Kernel32.INSTANCE.GetLastError())); 
          closeableIntByReference1.close();
        } catch (Throwable throwable) {
          try {
            closeableIntByReference1.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
        closeablePointerByReference.close();
      } catch (Throwable throwable) {
        try {
          closeablePointerByReference.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
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
    return (Map)hashMap;
  }
  
  private static Map<Integer, WtsInfo> queryProcessWtsMapFromPerfMon(Collection<Integer> paramCollection) {
    HashMap<Object, Object> hashMap = new HashMap<>();
    WbemcliUtil.WmiResult wmiResult = Win32Process.queryProcesses(paramCollection);
    for (byte b = 0; b < wmiResult.getResultCount(); b++)
      hashMap.put(Integer.valueOf(WmiUtil.getUint32(wmiResult, (Enum)Win32Process.ProcessXPProperty.PROCESSID, b)), new WtsInfo(WmiUtil.getString(wmiResult, (Enum)Win32Process.ProcessXPProperty.NAME, b), WmiUtil.getString(wmiResult, (Enum)Win32Process.ProcessXPProperty.EXECUTABLEPATH, b), WmiUtil.getUint32(wmiResult, (Enum)Win32Process.ProcessXPProperty.THREADCOUNT, b), 1024L * (WmiUtil.getUint32(wmiResult, (Enum)Win32Process.ProcessXPProperty.PAGEFILEUSAGE, b) & 0xFFFFFFFFL), WmiUtil.getUint64(wmiResult, (Enum)Win32Process.ProcessXPProperty.KERNELMODETIME, b) / 10000L, WmiUtil.getUint64(wmiResult, (Enum)Win32Process.ProcessXPProperty.USERMODETIME, b) / 10000L, WmiUtil.getUint32(wmiResult, (Enum)Win32Process.ProcessXPProperty.HANDLECOUNT, b))); 
    return (Map)hashMap;
  }
  
  @Immutable
  public static class WtsInfo {
    private final String name;
    
    private final String path;
    
    private final int threadCount;
    
    private final long virtualSize;
    
    private final long kernelTime;
    
    private final long userTime;
    
    private final long openFiles;
    
    public WtsInfo(String param1String1, String param1String2, int param1Int, long param1Long1, long param1Long2, long param1Long3, long param1Long4) {
      this.name = param1String1;
      this.path = param1String2;
      this.threadCount = param1Int;
      this.virtualSize = param1Long1;
      this.kernelTime = param1Long2;
      this.userTime = param1Long3;
      this.openFiles = param1Long4;
    }
    
    public String getName() {
      return this.name;
    }
    
    public String getPath() {
      return this.path;
    }
    
    public int getThreadCount() {
      return this.threadCount;
    }
    
    public long getVirtualSize() {
      return this.virtualSize;
    }
    
    public long getKernelTime() {
      return this.kernelTime;
    }
    
    public long getUserTime() {
      return this.userTime;
    }
    
    public long getOpenFiles() {
      return this.openFiles;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\registry\ProcessWtsData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
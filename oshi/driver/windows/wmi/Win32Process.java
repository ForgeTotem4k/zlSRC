package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class Win32Process {
  private static final String WIN32_PROCESS = "Win32_Process";
  
  public static WbemcliUtil.WmiResult<CommandLineProperty> queryCommandLines(Set<Integer> paramSet) {
    String str = "Win32_Process";
    if (paramSet != null)
      str = str + " WHERE ProcessID=" + (String)paramSet.stream().map(String::valueOf).collect(Collectors.joining(" OR PROCESSID=")); 
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery(str, CommandLineProperty.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public static WbemcliUtil.WmiResult<ProcessXPProperty> queryProcesses(Collection<Integer> paramCollection) {
    String str = "Win32_Process";
    if (paramCollection != null)
      str = str + " WHERE ProcessID=" + (String)paramCollection.stream().map(String::valueOf).collect(Collectors.joining(" OR PROCESSID=")); 
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery(str, ProcessXPProperty.class);
    return ((WmiQueryHandler)Objects.<WmiQueryHandler>requireNonNull(WmiQueryHandler.createInstance())).queryWMI(wmiQuery);
  }
  
  public enum CommandLineProperty {
    PROCESSID, COMMANDLINE;
  }
  
  public enum ProcessXPProperty {
    PROCESSID, NAME, KERNELMODETIME, USERMODETIME, THREADCOUNT, PAGEFILEUSAGE, HANDLECOUNT, EXECUTABLEPATH;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\Win32Process.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
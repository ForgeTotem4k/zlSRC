package oshi.util.platform.unix.openbsd;

import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class FstatUtil {
  public static String getCwd(int paramInt) {
    List<String> list = ExecutingCommand.runNative("ps -axwwo cwd -p " + paramInt);
    return (list.size() > 1) ? list.get(1) : "";
  }
  
  public static long getOpenFiles(int paramInt) {
    long l = 0L;
    List list = ExecutingCommand.runNative("fstat -sp " + paramInt);
    for (String str : list) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str.trim(), 11);
      if (arrayOfString.length == 11 && !"pipe".contains(arrayOfString[4]) && !"unix".contains(arrayOfString[4]))
        l++; 
    } 
    return l - 1L;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\platfor\\unix\openbsd\FstatUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
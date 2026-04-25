package oshi.util.platform.unix.freebsd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class ProcstatUtil {
  public static Map<Integer, String> getCwdMap(int paramInt) {
    List list = ExecutingCommand.runNative("procstat -f " + ((paramInt < 0) ? "-a" : (String)Integer.valueOf(paramInt)));
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (String str : list) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str.trim(), 10);
      if (arrayOfString.length == 10 && arrayOfString[2].equals("cwd"))
        hashMap.put(Integer.valueOf(ParseUtil.parseIntOrDefault(arrayOfString[0], -1)), arrayOfString[9]); 
    } 
    return (Map)hashMap;
  }
  
  public static String getCwd(int paramInt) {
    List list = ExecutingCommand.runNative("procstat -f " + paramInt);
    for (String str : list) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str.trim(), 10);
      if (arrayOfString.length == 10 && arrayOfString[2].equals("cwd"))
        return arrayOfString[9]; 
    } 
    return "";
  }
  
  public static long getOpenFiles(int paramInt) {
    long l = 0L;
    List list = ExecutingCommand.runNative("procstat -f " + paramInt);
    for (String str : list) {
      String[] arrayOfString = ParseUtil.whitespaces.split(str.trim(), 10);
      if (arrayOfString.length == 10 && !"Vd-".contains(arrayOfString[4]))
        l++; 
    } 
    return l;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\platfor\\unix\freebsd\ProcstatUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
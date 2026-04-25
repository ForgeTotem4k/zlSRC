package oshi.driver.unix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class Xrandr {
  private static final String[] XRANDR_VERBOSE = new String[] { "xrandr", "--verbose" };
  
  public static List<byte[]> getEdidArrays() {
    List list = ExecutingCommand.runNative(XRANDR_VERBOSE, null);
    if (list.isEmpty())
      return (List)Collections.emptyList(); 
    ArrayList<byte[]> arrayList = new ArrayList();
    StringBuilder stringBuilder = null;
    for (String str : list) {
      if (str.contains("EDID")) {
        stringBuilder = new StringBuilder();
        continue;
      } 
      if (stringBuilder != null) {
        stringBuilder.append(str.trim());
        if (stringBuilder.length() < 256)
          continue; 
        String str1 = stringBuilder.toString();
        byte[] arrayOfByte = ParseUtil.hexStringToByteArray(str1);
        if (arrayOfByte.length >= 128)
          arrayList.add(arrayOfByte); 
        stringBuilder = null;
      } 
    } 
    return (List<byte[]>)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\Xrandr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
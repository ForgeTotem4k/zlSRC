package oshi.driver.mac;

import com.sun.jna.Native;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.mac.SystemB;
import oshi.software.os.OSSession;
import oshi.util.Util;

@ThreadSafe
public final class Who {
  private static final SystemB SYS = SystemB.INSTANCE;
  
  public static synchronized List<OSSession> queryUtxent() {
    ArrayList<OSSession> arrayList = new ArrayList();
    SYS.setutxent();
    try {
      SystemB.MacUtmpx macUtmpx;
      while ((macUtmpx = SYS.getutxent()) != null) {
        if (macUtmpx.ut_type == 7 || macUtmpx.ut_type == 6) {
          String str1 = Native.toString(macUtmpx.ut_user, StandardCharsets.US_ASCII);
          String str2 = Native.toString(macUtmpx.ut_line, StandardCharsets.US_ASCII);
          String str3 = Native.toString(macUtmpx.ut_host, StandardCharsets.US_ASCII);
          long l = macUtmpx.ut_tv.tv_sec.longValue() * 1000L + macUtmpx.ut_tv.tv_usec / 1000L;
          if (!Util.isSessionValid(str1, str2, Long.valueOf(l)))
            return oshi.driver.unix.Who.queryWho(); 
          arrayList.add(new OSSession(str1, str2, l, str3));
        } 
      } 
    } finally {
      SYS.endutxent();
    } 
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\mac\Who.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
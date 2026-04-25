package oshi.driver.linux;

import com.sun.jna.Native;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.software.os.OSSession;
import oshi.util.ParseUtil;
import oshi.util.Util;

@ThreadSafe
public final class Who {
  private static final LinuxLibc LIBC = LinuxLibc.INSTANCE;
  
  public static synchronized List<OSSession> queryUtxent() {
    ArrayList<OSSession> arrayList = new ArrayList();
    LIBC.setutxent();
    try {
      LinuxLibc.LinuxUtmpx linuxUtmpx;
      while ((linuxUtmpx = LIBC.getutxent()) != null) {
        if (linuxUtmpx.ut_type == 7 || linuxUtmpx.ut_type == 6) {
          String str1 = Native.toString(linuxUtmpx.ut_user, Charset.defaultCharset());
          String str2 = Native.toString(linuxUtmpx.ut_line, Charset.defaultCharset());
          String str3 = ParseUtil.parseUtAddrV6toIP(linuxUtmpx.ut_addr_v6);
          long l = linuxUtmpx.ut_tv.tv_sec * 1000L + linuxUtmpx.ut_tv.tv_usec / 1000L;
          if (!Util.isSessionValid(str1, str2, Long.valueOf(l)))
            return oshi.driver.unix.Who.queryWho(); 
          arrayList.add(new OSSession(str1, str2, l, str3));
        } 
      } 
    } finally {
      LIBC.endutxent();
    } 
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\linux\Who.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
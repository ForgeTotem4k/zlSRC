package oshi.driver.unix.solaris;

import com.sun.jna.Native;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.SolarisLibc;
import oshi.software.os.OSSession;
import oshi.util.Util;

@ThreadSafe
public final class Who {
  private static final SolarisLibc LIBC = SolarisLibc.INSTANCE;
  
  public static synchronized List<OSSession> queryUtxent() {
    ArrayList<OSSession> arrayList = new ArrayList();
    LIBC.setutxent();
    try {
      SolarisLibc.SolarisUtmpx solarisUtmpx;
      while ((solarisUtmpx = LIBC.getutxent()) != null) {
        if (solarisUtmpx.ut_type == 7 || solarisUtmpx.ut_type == 6) {
          String str1 = Native.toString(solarisUtmpx.ut_user, StandardCharsets.US_ASCII);
          String str2 = Native.toString(solarisUtmpx.ut_line, StandardCharsets.US_ASCII);
          String str3 = Native.toString(solarisUtmpx.ut_host, StandardCharsets.US_ASCII);
          long l = solarisUtmpx.ut_tv.tv_sec.longValue() * 1000L + solarisUtmpx.ut_tv.tv_usec.longValue() / 1000L;
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


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\solaris\Who.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
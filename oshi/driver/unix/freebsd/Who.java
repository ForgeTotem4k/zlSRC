package oshi.driver.unix.freebsd;

import com.sun.jna.Native;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.unix.FreeBsdLibc;
import oshi.software.os.OSSession;
import oshi.util.Util;

@ThreadSafe
public final class Who {
  private static final FreeBsdLibc LIBC = FreeBsdLibc.INSTANCE;
  
  public static synchronized List<OSSession> queryUtxent() {
    ArrayList<OSSession> arrayList = new ArrayList();
    LIBC.setutxent();
    try {
      FreeBsdLibc.FreeBsdUtmpx freeBsdUtmpx;
      while ((freeBsdUtmpx = LIBC.getutxent()) != null) {
        if (freeBsdUtmpx.ut_type == 7 || freeBsdUtmpx.ut_type == 6) {
          String str1 = Native.toString(freeBsdUtmpx.ut_user, StandardCharsets.US_ASCII);
          String str2 = Native.toString(freeBsdUtmpx.ut_line, StandardCharsets.US_ASCII);
          String str3 = Native.toString(freeBsdUtmpx.ut_host, StandardCharsets.US_ASCII);
          long l = freeBsdUtmpx.ut_tv.tv_sec * 1000L + freeBsdUtmpx.ut_tv.tv_usec / 1000L;
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


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\drive\\unix\freebsd\Who.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
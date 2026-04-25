package oshi.driver.windows.registry;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.OSSession;

@ThreadSafe
public final class HkeyUserData {
  private static final String PATH_DELIMITER = "\\";
  
  private static final String DEFAULT_DEVICE = "Console";
  
  private static final String VOLATILE_ENV_SUBKEY = "Volatile Environment";
  
  private static final String CLIENTNAME = "CLIENTNAME";
  
  private static final String SESSIONNAME = "SESSIONNAME";
  
  private static final Logger LOG = LoggerFactory.getLogger(HkeyUserData.class);
  
  public static List<OSSession> queryUserSessions() {
    ArrayList<OSSession> arrayList = new ArrayList();
    for (String str : Advapi32Util.registryGetKeys(WinReg.HKEY_USERS)) {
      if (!str.startsWith(".") && !str.endsWith("_Classes"))
        try {
          Advapi32Util.Account account = Advapi32Util.getAccountBySid(str);
          String str1 = account.name;
          String str2 = "Console";
          String str3 = account.domain;
          long l = 0L;
          String str4 = str + "\\" + "Volatile Environment";
          if (Advapi32Util.registryKeyExists(WinReg.HKEY_USERS, str4)) {
            WinReg.HKEY hKEY = Advapi32Util.registryGetKey(WinReg.HKEY_USERS, str4, 131097).getValue();
            Advapi32Util.InfoKey infoKey = Advapi32Util.registryQueryInfoKey(hKEY, 0);
            l = infoKey.lpftLastWriteTime.toTime();
            for (String str5 : Advapi32Util.registryGetKeys(hKEY)) {
              String str6 = str4 + "\\" + str5;
              if (Advapi32Util.registryValueExists(WinReg.HKEY_USERS, str6, "SESSIONNAME")) {
                String str7 = Advapi32Util.registryGetStringValue(WinReg.HKEY_USERS, str6, "SESSIONNAME");
                if (!str7.isEmpty())
                  str2 = str7; 
              } 
              if (Advapi32Util.registryValueExists(WinReg.HKEY_USERS, str6, "CLIENTNAME")) {
                String str7 = Advapi32Util.registryGetStringValue(WinReg.HKEY_USERS, str6, "CLIENTNAME");
                if (!str7.isEmpty() && !"Console".equals(str7))
                  str3 = str7; 
              } 
            } 
            Advapi32Util.registryCloseKey(hKEY);
          } 
          arrayList.add(new OSSession(str1, str2, l, str3));
        } catch (Win32Exception win32Exception) {
          LOG.warn("Error querying SID {} from registry: {}", str, win32Exception.getMessage());
        }  
    } 
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\registry\HkeyUserData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package oshi.software.os;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import oshi.annotation.concurrent.Immutable;

@Immutable
public class OSSession {
  private static final DateTimeFormatter LOGIN_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ROOT);
  
  private final String userName;
  
  private final String terminalDevice;
  
  private final long loginTime;
  
  private final String host;
  
  public OSSession(String paramString1, String paramString2, long paramLong, String paramString3) {
    this.userName = paramString1;
    this.terminalDevice = paramString2;
    this.loginTime = paramLong;
    this.host = paramString3;
  }
  
  public String getUserName() {
    return this.userName;
  }
  
  public String getTerminalDevice() {
    return this.terminalDevice;
  }
  
  public long getLoginTime() {
    return this.loginTime;
  }
  
  public String getHost() {
    return this.host;
  }
  
  public String toString() {
    String str1 = (this.loginTime == 0L) ? "No login" : LocalDateTime.ofInstant(Instant.ofEpochMilli(this.loginTime), ZoneId.systemDefault()).format(LOGIN_FORMAT);
    String str2 = "";
    if (!this.host.isEmpty() && !this.host.equals("::") && !this.host.equals("0.0.0.0"))
      str2 = ", (" + this.host + ")"; 
    return String.format(Locale.ROOT, "%s, %s, %s%s", new Object[] { this.userName, this.terminalDevice, str1, str2 });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\os\OSSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
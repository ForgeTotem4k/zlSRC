package pro.gravit.launcher.base.events.request;

import pro.gravit.launcher.base.events.ExtendedTokenRequestEvent;
import pro.gravit.launcher.base.events.RequestEvent;

public class HardwareReportRequestEvent extends RequestEvent implements ExtendedTokenRequestEvent {
  public String extendedToken;
  
  public long expire;
  
  public HardwareReportRequestEvent() {}
  
  public HardwareReportRequestEvent(String paramString, long paramLong) {
    this.extendedToken = paramString;
    this.expire = paramLong;
  }
  
  public String getType() {
    return "hardwareReport";
  }
  
  public String getExtendedTokenName() {
    return "hardware";
  }
  
  public String getExtendedToken() {
    return this.extendedToken;
  }
  
  public long getExtendedTokenExpire() {
    return this.expire;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\HardwareReportRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
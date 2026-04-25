package pro.gravit.launcher.base.events.request;

import pro.gravit.launcher.base.events.ExtendedTokenRequestEvent;
import pro.gravit.launcher.base.events.RequestEvent;

public class VerifySecureLevelKeyRequestEvent extends RequestEvent implements ExtendedTokenRequestEvent {
  public boolean needHardwareInfo;
  
  public boolean onlyStatisticInfo;
  
  public String extendedToken;
  
  public long expire;
  
  public VerifySecureLevelKeyRequestEvent() {}
  
  public VerifySecureLevelKeyRequestEvent(boolean paramBoolean) {
    this.needHardwareInfo = paramBoolean;
  }
  
  public VerifySecureLevelKeyRequestEvent(boolean paramBoolean1, boolean paramBoolean2, String paramString, long paramLong) {
    this.needHardwareInfo = paramBoolean1;
    this.onlyStatisticInfo = paramBoolean2;
    this.extendedToken = paramString;
    this.expire = paramLong;
  }
  
  public String getType() {
    return "verifySecureLevelKey";
  }
  
  public String getExtendedTokenName() {
    return "publicKey";
  }
  
  public String getExtendedToken() {
    return this.extendedToken;
  }
  
  public long getExtendedTokenExpire() {
    return this.expire;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\VerifySecureLevelKeyRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
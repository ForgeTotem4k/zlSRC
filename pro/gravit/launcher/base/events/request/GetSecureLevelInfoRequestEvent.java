package pro.gravit.launcher.base.events.request;

import pro.gravit.launcher.base.events.RequestEvent;

public class GetSecureLevelInfoRequestEvent extends RequestEvent {
  public final byte[] verifySecureKey;
  
  public boolean enabled;
  
  public GetSecureLevelInfoRequestEvent(byte[] paramArrayOfbyte) {
    this.verifySecureKey = paramArrayOfbyte;
  }
  
  public GetSecureLevelInfoRequestEvent(byte[] paramArrayOfbyte, boolean paramBoolean) {
    this.verifySecureKey = paramArrayOfbyte;
    this.enabled = paramBoolean;
  }
  
  public String getType() {
    return "getSecureLevelInfo";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\GetSecureLevelInfoRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
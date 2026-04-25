package pro.gravit.launcher.base.request.auth;

import pro.gravit.launcher.base.events.request.RefreshTokenRequestEvent;
import pro.gravit.launcher.base.request.Request;

public class RefreshTokenRequest extends Request<RefreshTokenRequestEvent> {
  public String authId;
  
  public String refreshToken;
  
  public RefreshTokenRequest(String paramString1, String paramString2) {
    this.authId = paramString1;
    this.refreshToken = paramString2;
  }
  
  public String getType() {
    return "refreshToken";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\RefreshTokenRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
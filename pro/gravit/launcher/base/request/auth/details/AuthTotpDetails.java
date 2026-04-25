package pro.gravit.launcher.base.request.auth.details;

import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;

public class AuthTotpDetails implements GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails {
  public final String alg;
  
  public final int maxKeyLength;
  
  public AuthTotpDetails(String paramString, int paramInt) {
    this.alg = paramString;
    this.maxKeyLength = paramInt;
  }
  
  public AuthTotpDetails(String paramString) {
    this.alg = paramString;
    this.maxKeyLength = 6;
  }
  
  public String getType() {
    return "totp";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\details\AuthTotpDetails.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
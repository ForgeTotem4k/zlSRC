package pro.gravit.launcher.base.request.auth.details;

import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;

public class AuthWebViewDetails implements GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails {
  public final String url;
  
  public final String redirectUrl;
  
  public final boolean canBrowser;
  
  public final boolean onlyBrowser;
  
  public AuthWebViewDetails(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2) {
    this.url = paramString1;
    this.redirectUrl = paramString2;
    this.canBrowser = paramBoolean1;
    this.onlyBrowser = paramBoolean2;
  }
  
  public AuthWebViewDetails(String paramString1, String paramString2) {
    this.url = paramString1;
    this.redirectUrl = paramString2;
    this.canBrowser = true;
    this.onlyBrowser = false;
  }
  
  public String getType() {
    return "webview";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\details\AuthWebViewDetails.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
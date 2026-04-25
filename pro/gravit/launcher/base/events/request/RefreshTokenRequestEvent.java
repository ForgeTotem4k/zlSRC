package pro.gravit.launcher.base.events.request;

import pro.gravit.launcher.base.events.RequestEvent;

public class RefreshTokenRequestEvent extends RequestEvent {
  public AuthRequestEvent.OAuthRequestEvent oauth;
  
  public RefreshTokenRequestEvent(AuthRequestEvent.OAuthRequestEvent paramOAuthRequestEvent) {
    this.oauth = paramOAuthRequestEvent;
  }
  
  public String getType() {
    return "refreshToken";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\RefreshTokenRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
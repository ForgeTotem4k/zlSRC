package pro.gravit.launcher.base.events.request;

public class OAuthRequestEvent {
  public final String accessToken;
  
  public final String refreshToken;
  
  public final long expire;
  
  public OAuthRequestEvent(String paramString1, String paramString2, long paramLong) {
    this.accessToken = paramString1;
    this.refreshToken = paramString2;
    this.expire = paramLong;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\AuthRequestEvent$OAuthRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.base.request.auth.password;

import pro.gravit.launcher.base.request.auth.AuthRequest;

public class AuthOAuthPassword implements AuthRequest.AuthPasswordInterface {
  public final String accessToken;
  
  public final String refreshToken;
  
  public final int expire;
  
  public AuthOAuthPassword(String paramString1, String paramString2, int paramInt) {
    this.accessToken = paramString1;
    this.refreshToken = paramString2;
    this.expire = paramInt;
  }
  
  public AuthOAuthPassword(String paramString, int paramInt) {
    this.accessToken = paramString;
    this.refreshToken = null;
    this.expire = paramInt;
  }
  
  public AuthOAuthPassword(String paramString) {
    this.accessToken = paramString;
    this.refreshToken = null;
    this.expire = 0;
  }
  
  public boolean check() {
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\password\AuthOAuthPassword.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
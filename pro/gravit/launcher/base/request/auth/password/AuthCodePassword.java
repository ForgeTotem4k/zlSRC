package pro.gravit.launcher.base.request.auth.password;

import pro.gravit.launcher.base.request.auth.AuthRequest;

public class AuthCodePassword implements AuthRequest.AuthPasswordInterface {
  public final String uri;
  
  public AuthCodePassword(String paramString) {
    this.uri = paramString;
  }
  
  public boolean check() {
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\password\AuthCodePassword.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
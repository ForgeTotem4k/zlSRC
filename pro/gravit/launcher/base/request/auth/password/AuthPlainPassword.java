package pro.gravit.launcher.base.request.auth.password;

import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class AuthPlainPassword implements AuthRequest.AuthPasswordInterface {
  @LauncherNetworkAPI
  public final String password;
  
  public AuthPlainPassword(String paramString) {
    this.password = paramString;
  }
  
  public boolean check() {
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\password\AuthPlainPassword.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
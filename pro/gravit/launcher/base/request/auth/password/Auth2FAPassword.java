package pro.gravit.launcher.base.request.auth.password;

import pro.gravit.launcher.base.request.auth.AuthRequest;

public class Auth2FAPassword implements AuthRequest.AuthPasswordInterface {
  public AuthRequest.AuthPasswordInterface firstPassword;
  
  public AuthRequest.AuthPasswordInterface secondPassword;
  
  public boolean check() {
    return (this.firstPassword != null && this.firstPassword.check() && this.secondPassword != null && this.secondPassword.check());
  }
  
  public boolean isAllowSave() {
    return (this.firstPassword.isAllowSave() && this.secondPassword.isAllowSave());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\password\Auth2FAPassword.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
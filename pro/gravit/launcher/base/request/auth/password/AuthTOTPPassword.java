package pro.gravit.launcher.base.request.auth.password;

import pro.gravit.launcher.base.request.auth.AuthRequest;

public class AuthTOTPPassword implements AuthRequest.AuthPasswordInterface {
  public String totp;
  
  public boolean check() {
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\password\AuthTOTPPassword.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.base.request.auth.password;

import pro.gravit.launcher.base.request.auth.AuthRequest;

public class AuthRSAPassword implements AuthRequest.AuthPasswordInterface {
  public final byte[] password;
  
  public AuthRSAPassword(byte[] paramArrayOfbyte) {
    this.password = paramArrayOfbyte;
  }
  
  public boolean check() {
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\password\AuthRSAPassword.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
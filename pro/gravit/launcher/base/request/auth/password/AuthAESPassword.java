package pro.gravit.launcher.base.request.auth.password;

import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class AuthAESPassword implements AuthRequest.AuthPasswordInterface {
  @LauncherNetworkAPI
  public final byte[] password;
  
  public AuthAESPassword(byte[] paramArrayOfbyte) {
    this.password = paramArrayOfbyte;
  }
  
  public boolean check() {
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\password\AuthAESPassword.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
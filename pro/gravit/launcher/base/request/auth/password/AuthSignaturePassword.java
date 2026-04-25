package pro.gravit.launcher.base.request.auth.password;

import pro.gravit.launcher.base.request.auth.AuthRequest;

public class AuthSignaturePassword implements AuthRequest.AuthPasswordInterface {
  public byte[] signature;
  
  public byte[] publicKey;
  
  public byte[] salt;
  
  public AuthSignaturePassword(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3) {
    this.signature = paramArrayOfbyte1;
    this.publicKey = paramArrayOfbyte2;
    this.salt = paramArrayOfbyte3;
  }
  
  public boolean check() {
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\password\AuthSignaturePassword.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
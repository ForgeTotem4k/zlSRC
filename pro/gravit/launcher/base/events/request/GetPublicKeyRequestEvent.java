package pro.gravit.launcher.base.events.request;

import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import pro.gravit.launcher.base.events.RequestEvent;

public class GetPublicKeyRequestEvent extends RequestEvent {
  public byte[] rsaPublicKey;
  
  public byte[] ecdsaPublicKey;
  
  public GetPublicKeyRequestEvent(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.rsaPublicKey = paramArrayOfbyte1;
    this.ecdsaPublicKey = paramArrayOfbyte2;
  }
  
  public GetPublicKeyRequestEvent(RSAPublicKey paramRSAPublicKey, ECPublicKey paramECPublicKey) {
    this.rsaPublicKey = paramRSAPublicKey.getEncoded();
    this.ecdsaPublicKey = paramECPublicKey.getEncoded();
  }
  
  public String getType() {
    return "getPublicKey";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\GetPublicKeyRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
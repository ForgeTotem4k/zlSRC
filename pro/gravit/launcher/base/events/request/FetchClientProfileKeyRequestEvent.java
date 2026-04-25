package pro.gravit.launcher.base.events.request;

import java.security.PrivateKey;
import java.security.PublicKey;
import pro.gravit.launcher.base.events.RequestEvent;

public class FetchClientProfileKeyRequestEvent extends RequestEvent {
  public byte[] publicKey;
  
  public byte[] privateKey;
  
  public byte[] signature;
  
  public long expiresAt;
  
  public long refreshedAfter;
  
  public FetchClientProfileKeyRequestEvent(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, long paramLong1, long paramLong2) {
    this.publicKey = paramArrayOfbyte1;
    this.privateKey = paramArrayOfbyte2;
    this.signature = paramArrayOfbyte3;
    this.expiresAt = paramLong1;
    this.refreshedAfter = paramLong2;
  }
  
  public FetchClientProfileKeyRequestEvent(PublicKey paramPublicKey, PrivateKey paramPrivateKey, byte[] paramArrayOfbyte, long paramLong1, long paramLong2) {
    this.publicKey = paramPublicKey.getEncoded();
    this.privateKey = paramPrivateKey.getEncoded();
    this.signature = paramArrayOfbyte;
    this.expiresAt = paramLong1;
    this.refreshedAfter = paramLong2;
  }
  
  public String getType() {
    return "clientProfileKey";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\FetchClientProfileKeyRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
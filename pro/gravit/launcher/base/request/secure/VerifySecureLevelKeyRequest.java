package pro.gravit.launcher.base.request.secure;

import pro.gravit.launcher.base.events.request.VerifySecureLevelKeyRequestEvent;
import pro.gravit.launcher.base.request.Request;

public class VerifySecureLevelKeyRequest extends Request<VerifySecureLevelKeyRequestEvent> {
  public final byte[] publicKey;
  
  public final byte[] signature;
  
  public VerifySecureLevelKeyRequest(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.publicKey = paramArrayOfbyte1;
    this.signature = paramArrayOfbyte2;
  }
  
  public String getType() {
    return "verifySecureLevelKey";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\secure\VerifySecureLevelKeyRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
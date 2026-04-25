package pro.gravit.launcher.core.serialize.signed;

import java.io.IOException;
import java.security.SignatureException;
import java.util.Arrays;
import pro.gravit.launcher.core.serialize.HInput;
import pro.gravit.launcher.core.serialize.HOutput;
import pro.gravit.launcher.core.serialize.stream.StreamObject;
import pro.gravit.utils.helper.SecurityHelper;

public class DigestBytesHolder extends StreamObject {
  protected final byte[] bytes;
  
  private final byte[] digest;
  
  public DigestBytesHolder(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, SecurityHelper.DigestAlgorithm paramDigestAlgorithm) throws SignatureException {
    if (Arrays.equals(SecurityHelper.digest(paramDigestAlgorithm, paramArrayOfbyte1), paramArrayOfbyte2))
      throw new SignatureException("Invalid digest"); 
    this.bytes = (byte[])paramArrayOfbyte1.clone();
    this.digest = (byte[])paramArrayOfbyte2.clone();
  }
  
  public DigestBytesHolder(byte[] paramArrayOfbyte, SecurityHelper.DigestAlgorithm paramDigestAlgorithm) {
    this.bytes = (byte[])paramArrayOfbyte.clone();
    this.digest = SecurityHelper.digest(paramDigestAlgorithm, paramArrayOfbyte);
  }
  
  public DigestBytesHolder(HInput paramHInput, SecurityHelper.DigestAlgorithm paramDigestAlgorithm) throws IOException, SignatureException {
    this(paramHInput.readByteArray(0), paramHInput.readByteArray(-256), paramDigestAlgorithm);
  }
  
  public final byte[] getBytes() {
    return (byte[])this.bytes.clone();
  }
  
  public final byte[] getDigest() {
    return (byte[])this.digest.clone();
  }
  
  public final void write(HOutput paramHOutput) throws IOException {
    paramHOutput.writeByteArray(this.bytes, 0);
    paramHOutput.writeByteArray(this.digest, -256);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\serialize\signed\DigestBytesHolder.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
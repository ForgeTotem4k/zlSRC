package pro.gravit.launcher.core.hasher;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.launcher.core.serialize.HInput;
import pro.gravit.launcher.core.serialize.HOutput;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.SecurityHelper;
import pro.gravit.utils.helper.VerifyHelper;

public final class HashedFile extends HashedEntry {
  public static final SecurityHelper.DigestAlgorithm DIGEST_ALGO = SecurityHelper.DigestAlgorithm.SHA1;
  
  @LauncherNetworkAPI
  public final long size;
  
  @LauncherNetworkAPI
  private final byte[] digest;
  
  public HashedFile(HInput paramHInput) throws IOException {
    this(paramHInput.readVarLong(), paramHInput.readBoolean() ? paramHInput.readByteArray(-DIGEST_ALGO.bytes) : null);
  }
  
  public HashedFile(long paramLong, byte[] paramArrayOfbyte) {
    this.size = VerifyHelper.verifyLong(paramLong, VerifyHelper.L_NOT_NEGATIVE, "Illegal size: " + paramLong);
    this.digest = (paramArrayOfbyte == null) ? null : (byte[])DIGEST_ALGO.verify(paramArrayOfbyte).clone();
  }
  
  public HashedFile(Path paramPath, long paramLong, boolean paramBoolean) throws IOException {
    this(paramLong, paramBoolean ? SecurityHelper.digest(DIGEST_ALGO, paramPath) : null);
  }
  
  public HashedEntry.Type getType() {
    return HashedEntry.Type.FILE;
  }
  
  public boolean isSame(HashedFile paramHashedFile) {
    return (this.size == paramHashedFile.size && (this.digest == null || paramHashedFile.digest == null || Arrays.equals(this.digest, paramHashedFile.digest)));
  }
  
  public boolean isSame(Path paramPath, boolean paramBoolean) throws IOException {
    if (this.size != IOHelper.readAttributes(paramPath).size())
      return false; 
    if (!paramBoolean || this.digest == null)
      return true; 
    byte[] arrayOfByte = SecurityHelper.digest(DIGEST_ALGO, paramPath);
    return Arrays.equals(this.digest, arrayOfByte);
  }
  
  public boolean isSameDigest(byte[] paramArrayOfbyte) {
    return (this.digest == null || paramArrayOfbyte == null || Arrays.equals(this.digest, paramArrayOfbyte));
  }
  
  public long size() {
    return this.size;
  }
  
  public byte[] getDigest() {
    return this.digest;
  }
  
  public void write(HOutput paramHOutput) throws IOException {
    paramHOutput.writeVarLong(this.size);
    paramHOutput.writeBoolean((this.digest != null));
    if (this.digest != null)
      paramHOutput.writeByteArray(this.digest, -DIGEST_ALGO.bytes); 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\hasher\HashedFile.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
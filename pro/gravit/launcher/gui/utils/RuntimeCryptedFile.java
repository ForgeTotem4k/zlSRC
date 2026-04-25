package pro.gravit.launcher.gui.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.function.Supplier;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import pro.gravit.utils.enfs.dir.FileEntry;
import pro.gravit.utils.helper.SecurityHelper;

public class RuntimeCryptedFile extends FileEntry {
  private final Supplier<InputStream> inputStream;
  
  private final String alg;
  
  private final SecretKeySpec sKeySpec;
  
  private final IvParameterSpec iKeySpec;
  
  public RuntimeCryptedFile(Supplier<InputStream> paramSupplier, byte[] paramArrayOfbyte) {
    this.inputStream = paramSupplier;
    this.alg = "AES/CBC/PKCS5Padding";
    try {
      byte[] arrayOfByte = SecurityHelper.getAESKey(paramArrayOfbyte);
      this.sKeySpec = new SecretKeySpec(arrayOfByte, "AES");
      this.iKeySpec = new IvParameterSpec("8u3d90ikr7o67lsq".getBytes());
    } catch (Exception exception) {
      throw new SecurityException(exception);
    } 
  }
  
  public InputStream getInputStream() throws IOException {
    Cipher cipher;
    try {
      cipher = Cipher.getInstance(this.alg);
      cipher.init(2, this.sKeySpec, this.iKeySpec);
    } catch (NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException|java.security.InvalidKeyException|java.security.InvalidAlgorithmParameterException noSuchAlgorithmException) {
      throw new IOException(noSuchAlgorithmException);
    } 
    return new BufferedInputStream(new CipherInputStream(this.inputStream.get(), cipher));
  }
  
  public long getContentLength() {
    return Long.MAX_VALUE;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gu\\utils\RuntimeCryptedFile.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
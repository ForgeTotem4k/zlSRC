package pro.gravit.launcher.base.profiles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import pro.gravit.launcher.core.serialize.HOutput;
import pro.gravit.launcher.core.serialize.stream.StreamObject;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.SecurityHelper;

public final class Texture extends StreamObject {
  private static final SecurityHelper.DigestAlgorithm DIGEST_ALGO = SecurityHelper.DigestAlgorithm.SHA256;
  
  public final String url;
  
  public final byte[] digest;
  
  public final Map<String, String> metadata;
  
  public Texture(String paramString, boolean paramBoolean, Map<String, String> paramMap) throws IOException {
    byte[] arrayOfByte;
    this.url = IOHelper.verifyURL(paramString);
    try {
      InputStream inputStream = IOHelper.newInput((new URI(paramString)).toURL());
      try {
        arrayOfByte = IOHelper.read(inputStream);
        if (inputStream != null)
          inputStream.close(); 
      } catch (Throwable throwable) {
        if (inputStream != null)
          try {
            inputStream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (URISyntaxException uRISyntaxException) {
      throw new IOException(uRISyntaxException);
    } 
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
    try {
      IOHelper.readTexture(byteArrayInputStream, paramBoolean);
      byteArrayInputStream.close();
    } catch (Throwable throwable) {
      try {
        byteArrayInputStream.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    this.digest = SecurityHelper.digest(DIGEST_ALGO, arrayOfByte);
    this.metadata = paramMap;
  }
  
  public Texture(String paramString, Path paramPath, boolean paramBoolean, Map<String, String> paramMap) throws IOException {
    byte[] arrayOfByte;
    this.url = IOHelper.verifyURL(paramString);
    InputStream inputStream = IOHelper.newInput(paramPath);
    try {
      arrayOfByte = IOHelper.read(inputStream);
      if (inputStream != null)
        inputStream.close(); 
    } catch (Throwable throwable) {
      if (inputStream != null)
        try {
          inputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    inputStream = new ByteArrayInputStream(arrayOfByte);
    try {
      IOHelper.readTexture(inputStream, paramBoolean);
      inputStream.close();
    } catch (Throwable throwable) {
      try {
        inputStream.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    this.digest = SecurityHelper.digest(DIGEST_ALGO, arrayOfByte);
    this.metadata = paramMap;
  }
  
  @Deprecated
  public Texture(String paramString, byte[] paramArrayOfbyte) {
    this.url = IOHelper.verifyURL(paramString);
    this.digest = (paramArrayOfbyte == null) ? new byte[0] : paramArrayOfbyte;
    this.metadata = null;
  }
  
  public Texture(String paramString, byte[] paramArrayOfbyte, Map<String, String> paramMap) {
    this.url = paramString;
    this.digest = (paramArrayOfbyte == null) ? new byte[0] : paramArrayOfbyte;
    this.metadata = paramMap;
  }
  
  public void write(HOutput paramHOutput) throws IOException {
    paramHOutput.writeASCII(this.url, 2048);
    paramHOutput.writeByteArray(this.digest, -DIGEST_ALGO.bytes);
  }
  
  public String toString() {
    return "Texture{url='" + this.url + "', digest=" + Arrays.toString(this.digest) + ", metadata=" + String.valueOf(this.metadata) + "}";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\Texture.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.utils.helper;

import java.util.HashMap;
import java.util.Map;

public enum DigestAlgorithm {
  PLAIN("plain", -1),
  MD5("MD5", 128),
  SHA1("SHA-1", 160),
  SHA224("SHA-224", 224),
  SHA256("SHA-256", 256),
  SHA512("SHA-512", 512);
  
  private static final Map<String, DigestAlgorithm> ALGORITHMS;
  
  public final String name;
  
  public final int bits;
  
  public final int bytes;
  
  DigestAlgorithm(String paramString1, int paramInt1) {
    this.name = paramString1;
    this.bits = paramInt1;
    this.bytes = paramInt1 / 8;
    assert paramInt1 % 8 == 0;
  }
  
  public static DigestAlgorithm byName(String paramString) {
    return VerifyHelper.<String, DigestAlgorithm>getMapValue(ALGORITHMS, paramString, String.format("Unknown digest algorithm: '%s'", new Object[] { paramString }));
  }
  
  public String toString() {
    return this.name;
  }
  
  public byte[] verify(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length != this.bytes)
      throw new IllegalArgumentException("Invalid digest length: " + paramArrayOfbyte.length); 
    return paramArrayOfbyte;
  }
  
  static {
    DigestAlgorithm[] arrayOfDigestAlgorithm = values();
    ALGORITHMS = new HashMap<>(arrayOfDigestAlgorithm.length);
    for (DigestAlgorithm digestAlgorithm : arrayOfDigestAlgorithm)
      ALGORITHMS.put(digestAlgorithm.name, digestAlgorithm); 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\SecurityHelper$DigestAlgorithm.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
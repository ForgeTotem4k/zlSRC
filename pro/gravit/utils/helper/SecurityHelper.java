package pro.gravit.utils.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public final class SecurityHelper {
  public static final String EC_ALGO = "EC";
  
  public static final String EC_CURVE = "secp256r1";
  
  public static final String EC_SIGN_ALGO = "SHA256withECDSA";
  
  public static final int TOKEN_LENGTH = 16;
  
  public static final String RSA_ALGO = "RSA";
  
  public static final String RSA_SIGN_ALGO = "SHA256withRSA";
  
  public static final String RSA_CIPHER_ALGO = "RSA/ECB/PKCS1Padding";
  
  public static final String AES_CIPHER_ALGO = "AES/ECB/PKCS5Padding";
  
  public static final int AES_KEY_LENGTH = 16;
  
  public static final int TOKEN_STRING_LENGTH = 32;
  
  public static final int RSA_KEY_LENGTH_BITS = 2048;
  
  public static final int RSA_KEY_LENGTH = 256;
  
  public static final int CRYPTO_MAX_LENGTH = 2048;
  
  public static final String HEX = "0123456789abcdef";
  
  public static byte[] digest(DigestAlgorithm paramDigestAlgorithm, byte[] paramArrayOfbyte) {
    return newDigest(paramDigestAlgorithm).digest(paramArrayOfbyte);
  }
  
  public static byte[] digest(DigestAlgorithm paramDigestAlgorithm, InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte = IOHelper.newBuffer();
    MessageDigest messageDigest = newDigest(paramDigestAlgorithm);
    int i;
    for (i = paramInputStream.read(arrayOfByte); i != -1; i = paramInputStream.read(arrayOfByte))
      messageDigest.update(arrayOfByte, 0, i); 
    return messageDigest.digest();
  }
  
  public static byte[] digest(DigestAlgorithm paramDigestAlgorithm, Path paramPath) throws IOException {
    InputStream inputStream = IOHelper.newInput(paramPath);
    try {
      byte[] arrayOfByte = digest(paramDigestAlgorithm, inputStream);
      if (inputStream != null)
        inputStream.close(); 
      return arrayOfByte;
    } catch (Throwable throwable) {
      if (inputStream != null)
        try {
          inputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static byte[] digest(DigestAlgorithm paramDigestAlgorithm, String paramString) {
    return digest(paramDigestAlgorithm, IOHelper.encode(paramString));
  }
  
  public static byte[] digest(DigestAlgorithm paramDigestAlgorithm, URL paramURL) throws IOException {
    InputStream inputStream = IOHelper.newInput(paramURL);
    try {
      byte[] arrayOfByte = digest(paramDigestAlgorithm, inputStream);
      if (inputStream != null)
        inputStream.close(); 
      return arrayOfByte;
    } catch (Throwable throwable) {
      if (inputStream != null)
        try {
          inputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static KeyPair genECDSAKeyPair(SecureRandom paramSecureRandom) {
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
      keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"), paramSecureRandom);
      return keyPairGenerator.genKeyPair();
    } catch (NoSuchAlgorithmException|java.security.InvalidAlgorithmParameterException noSuchAlgorithmException) {
      throw new InternalError(noSuchAlgorithmException);
    } 
  }
  
  public static KeyPair genRSAKeyPair(SecureRandom paramSecureRandom) {
    try {
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
      keyPairGenerator.initialize(2048, paramSecureRandom);
      return keyPairGenerator.genKeyPair();
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new InternalError(noSuchAlgorithmException);
    } 
  }
  
  public static boolean isValidSign(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, ECPublicKey paramECPublicKey) throws SignatureException {
    Signature signature = newECVerifySignature(paramECPublicKey);
    try {
      signature.update(paramArrayOfbyte1);
    } catch (SignatureException signatureException) {
      throw new InternalError(signatureException);
    } 
    return signature.verify(paramArrayOfbyte2);
  }
  
  public static boolean isValidSign(InputStream paramInputStream, byte[] paramArrayOfbyte, ECPublicKey paramECPublicKey) throws IOException, SignatureException {
    Signature signature = newECVerifySignature(paramECPublicKey);
    updateSignature(paramInputStream, signature);
    return signature.verify(paramArrayOfbyte);
  }
  
  public static boolean isValidSign(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, RSAPublicKey paramRSAPublicKey) throws SignatureException {
    Signature signature = newRSAVerifySignature(paramRSAPublicKey);
    try {
      signature.update(paramArrayOfbyte1);
    } catch (SignatureException signatureException) {
      throw new InternalError(signatureException);
    } 
    return signature.verify(paramArrayOfbyte2);
  }
  
  public static boolean isValidSign(InputStream paramInputStream, byte[] paramArrayOfbyte, RSAPublicKey paramRSAPublicKey) throws IOException, SignatureException {
    Signature signature = newRSAVerifySignature(paramRSAPublicKey);
    updateSignature(paramInputStream, signature);
    return signature.verify(paramArrayOfbyte);
  }
  
  public static boolean isValidToken(CharSequence paramCharSequence) {
    return (paramCharSequence.length() == 32 && paramCharSequence.chars().allMatch(paramInt -> ("0123456789abcdef".indexOf(paramInt) >= 0)));
  }
  
  public static Cipher newCipher(String paramString) {
    try {
      return Cipher.getInstance(paramString);
    } catch (NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException noSuchAlgorithmException) {
      throw new InternalError(noSuchAlgorithmException);
    } 
  }
  
  private static Cipher newBCCipher(String paramString) {
    try {
      return Cipher.getInstance(paramString, "BC");
    } catch (NoSuchAlgorithmException|javax.crypto.NoSuchPaddingException|java.security.NoSuchProviderException noSuchAlgorithmException) {
      throw new InternalError(noSuchAlgorithmException);
    } 
  }
  
  public static MessageDigest newDigest(DigestAlgorithm paramDigestAlgorithm) {
    VerifyHelper.verify(paramDigestAlgorithm, paramDigestAlgorithm -> (paramDigestAlgorithm != DigestAlgorithm.PLAIN), "PLAIN digest");
    try {
      return MessageDigest.getInstance(paramDigestAlgorithm.name);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new InternalError(noSuchAlgorithmException);
    } 
  }
  
  public static SecureRandom newRandom() {
    return new SecureRandom();
  }
  
  private static Cipher newRSACipher(int paramInt, RSAKey paramRSAKey) {
    Cipher cipher = newCipher("RSA/ECB/PKCS1Padding");
    try {
      cipher.init(paramInt, (Key)paramRSAKey);
    } catch (InvalidKeyException invalidKeyException) {
      throw new InternalError(invalidKeyException);
    } 
    return cipher;
  }
  
  private static Cipher newAESCipher(int paramInt, byte[] paramArrayOfbyte) {
    Cipher cipher = newCipher("AES/ECB/PKCS5Padding");
    try {
      cipher.init(paramInt, new SecretKeySpec(paramArrayOfbyte, "AES"));
    } catch (InvalidKeyException invalidKeyException) {
      throw new InternalError(invalidKeyException);
    } 
    return cipher;
  }
  
  private static KeyFactory newECDSAKeyFactory() {
    try {
      return KeyFactory.getInstance("EC");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new InternalError(noSuchAlgorithmException);
    } 
  }
  
  private static KeyFactory newRSAKeyFactory() {
    try {
      return KeyFactory.getInstance("RSA");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new InternalError(noSuchAlgorithmException);
    } 
  }
  
  private static Signature newECSignature() {
    try {
      return Signature.getInstance("SHA256withECDSA");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new InternalError(noSuchAlgorithmException);
    } 
  }
  
  private static Signature newRSASignature() {
    try {
      return Signature.getInstance("SHA256withRSA");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new InternalError(noSuchAlgorithmException);
    } 
  }
  
  public static Signature newECSignSignature(ECPrivateKey paramECPrivateKey) {
    Signature signature = newECSignature();
    try {
      signature.initSign(paramECPrivateKey);
    } catch (InvalidKeyException invalidKeyException) {
      throw new InternalError(invalidKeyException);
    } 
    return signature;
  }
  
  public static Signature newRSASignSignature(RSAPrivateKey paramRSAPrivateKey) {
    Signature signature = newRSASignature();
    try {
      signature.initSign(paramRSAPrivateKey);
    } catch (InvalidKeyException invalidKeyException) {
      throw new InternalError(invalidKeyException);
    } 
    return signature;
  }
  
  public static Signature newECVerifySignature(ECPublicKey paramECPublicKey) {
    Signature signature = newECSignature();
    try {
      signature.initVerify(paramECPublicKey);
    } catch (InvalidKeyException invalidKeyException) {
      throw new InternalError(invalidKeyException);
    } 
    return signature;
  }
  
  public static Signature newRSAVerifySignature(RSAPublicKey paramRSAPublicKey) {
    Signature signature = newRSASignature();
    try {
      signature.initVerify(paramRSAPublicKey);
    } catch (InvalidKeyException invalidKeyException) {
      throw new InternalError(invalidKeyException);
    } 
    return signature;
  }
  
  public static byte[] randomBytes(int paramInt) {
    return randomBytes(newRandom(), paramInt);
  }
  
  public static byte[] randomBytes(Random paramRandom, int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    paramRandom.nextBytes(arrayOfByte);
    return arrayOfByte;
  }
  
  public static String randomStringToken() {
    return randomStringToken(newRandom());
  }
  
  public static String randomStringToken(Random paramRandom) {
    return toHex(randomToken(paramRandom));
  }
  
  public static byte[] randomToken() {
    return randomToken(newRandom());
  }
  
  public static byte[] randomToken(Random paramRandom) {
    return randomBytes(paramRandom, 16);
  }
  
  public static String randomStringAESKey() {
    return toHex(randomAESKey(newRandom()));
  }
  
  public static String randomStringAESKey(Random paramRandom) {
    return toHex(randomAESKey(paramRandom));
  }
  
  public static byte[] randomAESKey() {
    return randomAESKey(newRandom());
  }
  
  public static byte[] randomAESKey(Random paramRandom) {
    return randomBytes(paramRandom, 16);
  }
  
  public static byte[] sign(byte[] paramArrayOfbyte, ECPrivateKey paramECPrivateKey) {
    Signature signature = newECSignSignature(paramECPrivateKey);
    try {
      signature.update(paramArrayOfbyte);
      return signature.sign();
    } catch (SignatureException signatureException) {
      throw new InternalError(signatureException);
    } 
  }
  
  public static byte[] sign(byte[] paramArrayOfbyte, RSAPrivateKey paramRSAPrivateKey) {
    Signature signature = newRSASignSignature(paramRSAPrivateKey);
    try {
      signature.update(paramArrayOfbyte);
      return signature.sign();
    } catch (SignatureException signatureException) {
      throw new InternalError(signatureException);
    } 
  }
  
  public static String toHex(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      return null; 
    byte b = 0;
    char[] arrayOfChar = new char[paramArrayOfbyte.length << 1];
    for (byte b1 : paramArrayOfbyte) {
      int i = Byte.toUnsignedInt(b1);
      arrayOfChar[b] = "0123456789abcdef".charAt(i >>> 4);
      arrayOfChar[++b] = "0123456789abcdef".charAt(i & 0xF);
      b++;
    } 
    return new String(arrayOfChar);
  }
  
  public static ECPublicKey toPublicECDSAKey(byte[] paramArrayOfbyte) throws InvalidKeySpecException {
    return (ECPublicKey)newECDSAKeyFactory().generatePublic(new X509EncodedKeySpec(paramArrayOfbyte));
  }
  
  public static ECPrivateKey toPrivateECDSAKey(byte[] paramArrayOfbyte) throws InvalidKeySpecException {
    return (ECPrivateKey)newECDSAKeyFactory().generatePrivate(new PKCS8EncodedKeySpec(paramArrayOfbyte));
  }
  
  public static RSAPublicKey toPublicRSAKey(byte[] paramArrayOfbyte) throws InvalidKeySpecException {
    return (RSAPublicKey)newRSAKeyFactory().generatePublic(new X509EncodedKeySpec(paramArrayOfbyte));
  }
  
  public static RSAPrivateKey toPrivateRSAKey(byte[] paramArrayOfbyte) throws InvalidKeySpecException {
    return (RSAPrivateKey)newRSAKeyFactory().generatePrivate(new PKCS8EncodedKeySpec(paramArrayOfbyte));
  }
  
  private static void updateSignature(InputStream paramInputStream, Signature paramSignature) throws IOException {
    byte[] arrayOfByte = IOHelper.newBuffer();
    for (int i = paramInputStream.read(arrayOfByte); i >= 0; i = paramInputStream.read(arrayOfByte)) {
      try {
        paramSignature.update(arrayOfByte, 0, i);
      } catch (SignatureException signatureException) {
        throw new InternalError(signatureException);
      } 
    } 
  }
  
  public static void verifySign(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, ECPublicKey paramECPublicKey) throws SignatureException {
    if (!isValidSign(paramArrayOfbyte1, paramArrayOfbyte2, paramECPublicKey))
      throw new SignatureException("Invalid sign"); 
  }
  
  public static void verifySign(InputStream paramInputStream, byte[] paramArrayOfbyte, ECPublicKey paramECPublicKey) throws SignatureException, IOException {
    if (!isValidSign(paramInputStream, paramArrayOfbyte, paramECPublicKey))
      throw new SignatureException("Invalid stream sign"); 
  }
  
  public static void verifySign(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, RSAPublicKey paramRSAPublicKey) throws SignatureException {
    if (!isValidSign(paramArrayOfbyte1, paramArrayOfbyte2, paramRSAPublicKey))
      throw new SignatureException("Invalid sign"); 
  }
  
  public static void verifySign(InputStream paramInputStream, byte[] paramArrayOfbyte, RSAPublicKey paramRSAPublicKey) throws SignatureException, IOException {
    if (!isValidSign(paramInputStream, paramArrayOfbyte, paramRSAPublicKey))
      throw new SignatureException("Invalid stream sign"); 
  }
  
  public static String verifyToken(String paramString) {
    return VerifyHelper.<String>verify(paramString, SecurityHelper::isValidToken, String.format("Invalid token: '%s'", new Object[] { paramString }));
  }
  
  public static Cipher newRSADecryptCipher(RSAPrivateKey paramRSAPrivateKey) {
    try {
      return newRSACipher(2, paramRSAPrivateKey);
    } catch (SecurityException securityException) {
      throw new InternalError(securityException);
    } 
  }
  
  public static Cipher newRSAEncryptCipher(RSAPublicKey paramRSAPublicKey) {
    try {
      return newRSACipher(1, paramRSAPublicKey);
    } catch (SecurityException securityException) {
      throw new InternalError(securityException);
    } 
  }
  
  public static Cipher newAESDecryptCipher(byte[] paramArrayOfbyte) {
    try {
      return newAESCipher(2, paramArrayOfbyte);
    } catch (SecurityException securityException) {
      throw new InternalError(securityException);
    } 
  }
  
  public static Cipher newAESEncryptCipher(byte[] paramArrayOfbyte) {
    try {
      return newAESCipher(1, paramArrayOfbyte);
    } catch (SecurityException securityException) {
      throw new InternalError(securityException);
    } 
  }
  
  public static byte[] encrypt(String paramString, byte[] paramArrayOfbyte) throws Exception {
    byte[] arrayOfByte = getAESKey(IOHelper.encode(paramString));
    return encrypt(arrayOfByte, paramArrayOfbyte);
  }
  
  public static byte[] encrypt(String paramString1, String paramString2) throws Exception {
    return encrypt(paramString1, IOHelper.encode(paramString2));
  }
  
  public static byte[] getAESKey(byte[] paramArrayOfbyte) throws Exception {
    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
    SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
    secureRandom.setSeed(paramArrayOfbyte);
    keyGenerator.init(128, secureRandom);
    SecretKey secretKey = keyGenerator.generateKey();
    return secretKey.getEncoded();
  }
  
  public static byte[] encrypt(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws Exception {
    SecretKeySpec secretKeySpec = new SecretKeySpec(paramArrayOfbyte1, "AES");
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(1, secretKeySpec);
    return cipher.doFinal(paramArrayOfbyte2);
  }
  
  public static byte[] decrypt(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws Exception {
    SecretKeySpec secretKeySpec = new SecretKeySpec(paramArrayOfbyte1, "AES");
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(2, secretKeySpec);
    return cipher.doFinal(paramArrayOfbyte2);
  }
  
  public static byte[] decrypt(String paramString, byte[] paramArrayOfbyte) throws Exception {
    return decrypt(getAESKey(IOHelper.encode(paramString)), paramArrayOfbyte);
  }
  
  public static byte[] fromHex(String paramString) {
    int i = paramString.length() / 2;
    byte[] arrayOfByte = new byte[i];
    for (byte b = 0; b < i; b++)
      arrayOfByte[b] = Integer.valueOf(paramString.substring(2 * b, 2 * b + 2), 16).byteValue(); 
    return arrayOfByte;
  }
  
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
    
    DigestAlgorithm(String param1String1, int param1Int1) {
      this.name = param1String1;
      this.bits = param1Int1;
      this.bytes = param1Int1 / 8;
      assert param1Int1 % 8 == 0;
    }
    
    public static DigestAlgorithm byName(String param1String) {
      return VerifyHelper.<String, DigestAlgorithm>getMapValue(ALGORITHMS, param1String, String.format("Unknown digest algorithm: '%s'", new Object[] { param1String }));
    }
    
    public String toString() {
      return this.name;
    }
    
    public byte[] verify(byte[] param1ArrayOfbyte) {
      if (param1ArrayOfbyte.length != this.bytes)
        throw new IllegalArgumentException("Invalid digest length: " + param1ArrayOfbyte.length); 
      return param1ArrayOfbyte;
    }
    
    static {
      DigestAlgorithm[] arrayOfDigestAlgorithm = values();
      ALGORITHMS = new HashMap<>(arrayOfDigestAlgorithm.length);
      for (DigestAlgorithm digestAlgorithm : arrayOfDigestAlgorithm)
        ALGORITHMS.put(digestAlgorithm.name, digestAlgorithm); 
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\SecurityHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
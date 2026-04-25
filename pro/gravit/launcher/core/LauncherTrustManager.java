package pro.gravit.launcher.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import pro.gravit.utils.helper.LogHelper;

public class LauncherTrustManager {
  private final X509Certificate[] trustSigners;
  
  private final List<X509Certificate> trustCache = new ArrayList<>();
  
  private static boolean isCertificatePinning = false;
  
  public LauncherTrustManager(X509Certificate[] paramArrayOfX509Certificate) {
    this.trustSigners = paramArrayOfX509Certificate;
    if (requireCustomTrustStore())
      injectCertificates(); 
  }
  
  public LauncherTrustManager(List<byte[]> paramList) throws CertificateException {
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    this.trustSigners = (X509Certificate[])paramList.stream().map(paramArrayOfbyte -> {
          try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfbyte);
            try {
              X509Certificate x509Certificate = (X509Certificate)paramCertificateFactory.generateCertificate(byteArrayInputStream);
              byteArrayInputStream.close();
              return x509Certificate;
            } catch (Throwable throwable) {
              try {
                byteArrayInputStream.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              } 
              throw throwable;
            } 
          } catch (IOException|CertificateException iOException) {
            LogHelper.error(iOException);
            return null;
          } 
        }).toArray(paramInt -> new X509Certificate[paramInt]);
    if (requireCustomTrustStore())
      injectCertificates(); 
  }
  
  private boolean requireCustomTrustStore() {
    return (this.trustSigners != null && this.trustSigners.length != 0 && isCertificatePinning);
  }
  
  private void injectCertificates() {
    try {
      Map<String, Certificate> map = getDefaultKeyStore();
      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      keyStore.load(null, new char[0]);
      Arrays.<X509Certificate>stream(this.trustSigners).forEach(paramX509Certificate -> setCertificateEntry(paramKeyStore, "injected-certificate" + String.valueOf(UUID.randomUUID()), paramX509Certificate));
      map.keySet().forEach(paramString -> setCertificateEntry(paramKeyStore, paramString, (Certificate)paramMap.get(paramString)));
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(keyStore);
      SSLContext sSLContext = SSLContext.getInstance("TLS");
      sSLContext.init(null, trustManagerFactory.getTrustManagers(), null);
      HttpsURLConnection.setDefaultSSLSocketFactory(sSLContext.getSocketFactory());
      LogHelper.info("Successfully injected certificates to truststore");
    } catch (NoSuchAlgorithmException|java.security.KeyManagementException|KeyStoreException|IOException|CertificateException noSuchAlgorithmException) {
      LogHelper.error("Error while modify existing keystore");
    } 
  }
  
  private static Map<String, Certificate> getDefaultKeyStore() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    try {
      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      Path path = Paths.get(System.getProperty("java.home"), new String[] { "lib", "security", "cacerts" });
      keyStore.load(Files.newInputStream(path, new java.nio.file.OpenOption[0]), "changeit".toCharArray());
      extractAllCertsAndPutInMap(keyStore, (Map)hashMap);
    } catch (KeyStoreException|IOException|NoSuchAlgorithmException|CertificateException keyStoreException) {
      LogHelper.warning("Error while loading existing keystore");
    } 
    return (Map)hashMap;
  }
  
  private static void extractAllCertsAndPutInMap(KeyStore paramKeyStore, Map<String, Certificate> paramMap) {
    try {
      Collections.<String>list(paramKeyStore.aliases()).forEach(paramString -> extractCertAndPutInMap(paramKeyStore, paramString, paramMap));
    } catch (KeyStoreException keyStoreException) {
      LogHelper.error("Error during extraction certificates from default keystore");
    } 
  }
  
  private static void setCertificateEntry(KeyStore paramKeyStore, String paramString, Certificate paramCertificate) {
    try {
      paramKeyStore.setCertificateEntry(paramString, paramCertificate);
    } catch (KeyStoreException keyStoreException) {
      LogHelper.warning("Something went wrong while adding certificate " + paramString);
    } 
  }
  
  private static void extractCertAndPutInMap(KeyStore paramKeyStore, String paramString, Map<String, Certificate> paramMap) {
    try {
      if (paramKeyStore.containsAlias(paramString))
        paramMap.put(paramString, paramKeyStore.getCertificate(paramString)); 
    } catch (KeyStoreException keyStoreException) {
      LogHelper.warning("Error while extracting certificate " + paramString);
    } 
  }
  
  public CheckClassResult checkCertificates(X509Certificate[] paramArrayOfX509Certificate, CertificateChecker paramCertificateChecker) {
    if (paramArrayOfX509Certificate == null)
      return new CheckClassResult(CheckClassResultType.NOT_SIGNED, null, null); 
    X509Certificate x509Certificate1 = paramArrayOfX509Certificate[paramArrayOfX509Certificate.length - 1];
    X509Certificate x509Certificate2 = paramArrayOfX509Certificate[0];
    for (byte b = 0;; b++) {
      if (b < paramArrayOfX509Certificate.length) {
        X509Certificate x509Certificate3 = paramArrayOfX509Certificate[b];
        if (this.trustCache.contains(x509Certificate3)) {
          this.trustCache.addAll(Arrays.<X509Certificate>asList(paramArrayOfX509Certificate).subList(0, b));
          return new CheckClassResult(CheckClassResultType.SUCCESS, x509Certificate2, x509Certificate1);
        } 
        X509Certificate x509Certificate4 = (b + 1 < paramArrayOfX509Certificate.length) ? paramArrayOfX509Certificate[b + 1] : null;
        try {
          x509Certificate3.checkValidity();
        } catch (Exception exception) {
          return new CheckClassResult(CheckClassResultType.UNVERIFED, x509Certificate2, x509Certificate1, exception);
        } 
        if (x509Certificate4 != null) {
          try {
            x509Certificate3.verify(x509Certificate4.getPublicKey());
          } catch (Exception exception) {
            return new CheckClassResult(CheckClassResultType.UNVERIFED, x509Certificate2, x509Certificate1, exception);
          } 
        } else {
          try {
            if (!isTrusted(x509Certificate3))
              return new CheckClassResult(CheckClassResultType.UNTRUSTED, x509Certificate2, x509Certificate1); 
          } catch (CertificateEncodingException certificateEncodingException) {
            return new CheckClassResult(CheckClassResultType.UNVERIFED, x509Certificate2, x509Certificate1, certificateEncodingException);
          } 
          b++;
        } 
        try {
          paramCertificateChecker.check(x509Certificate3, x509Certificate4, b);
        } catch (Exception exception) {
          return new CheckClassResult(CheckClassResultType.UNCOMPAT, x509Certificate2, x509Certificate1, exception);
        } 
      } else {
        break;
      } 
    } 
    Collections.addAll(this.trustCache, paramArrayOfX509Certificate);
    return new CheckClassResult(CheckClassResultType.SUCCESS, x509Certificate2, x509Certificate1);
  }
  
  public void checkCertificatesSuccess(X509Certificate[] paramArrayOfX509Certificate, CertificateChecker paramCertificateChecker) throws Exception {
    CheckClassResult checkClassResult = checkCertificates(paramArrayOfX509Certificate, paramCertificateChecker);
    if (checkClassResult.type == CheckClassResultType.SUCCESS)
      return; 
    if (checkClassResult.exception != null)
      throw checkClassResult.exception; 
    throw new SecurityException(checkClassResult.type.name());
  }
  
  public boolean isTrusted(X509Certificate paramX509Certificate) throws CertificateEncodingException {
    for (X509Certificate x509Certificate : this.trustSigners) {
      if (x509Certificate.getSerialNumber().equals(paramX509Certificate.getSerialNumber()) && Arrays.equals(x509Certificate.getEncoded(), paramX509Certificate.getEncoded()))
        return true; 
    } 
    return false;
  }
  
  public X509Certificate[] getTrusted() {
    return Arrays.<X509Certificate>copyOf(this.trustSigners, this.trustSigners.length);
  }
  
  public void isCertificateCodeSign(X509Certificate paramX509Certificate) {
    try {
      List<String> list = paramX509Certificate.getExtendedKeyUsage();
      if (list == null)
        throw new SecurityException("Certificate extendedKeyUsage null"); 
      boolean bool = false;
      for (String str : list) {
        if (str.equals("1.3.6.1.5.5.7.3.3")) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        throw new SecurityException("Certificate extendedKeyUsage codeSign checkFailed"); 
    } catch (CertificateParsingException certificateParsingException) {
      throw new SecurityException(certificateParsingException);
    } 
  }
  
  public void isCertificateCA(X509Certificate paramX509Certificate) {
    if (paramX509Certificate.getBasicConstraints() < 0)
      throw new SecurityException("This certificate not CA"); 
  }
  
  public void stdCertificateChecker(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, int paramInt) {
    if (paramInt == 0) {
      isCertificateCodeSign(paramX509Certificate1);
    } else {
      isCertificateCA(paramX509Certificate1);
    } 
  }
  
  public static class CheckClassResult {
    public final LauncherTrustManager.CheckClassResultType type;
    
    public final X509Certificate endCertificate;
    
    public final X509Certificate rootCertificate;
    
    public final Exception exception;
    
    public CheckClassResult(LauncherTrustManager.CheckClassResultType param1CheckClassResultType, X509Certificate param1X509Certificate1, X509Certificate param1X509Certificate2) {
      this.type = param1CheckClassResultType;
      this.endCertificate = param1X509Certificate1;
      this.rootCertificate = param1X509Certificate2;
      this.exception = null;
    }
    
    public CheckClassResult(LauncherTrustManager.CheckClassResultType param1CheckClassResultType, X509Certificate param1X509Certificate1, X509Certificate param1X509Certificate2, Exception param1Exception) {
      this.type = param1CheckClassResultType;
      this.endCertificate = param1X509Certificate1;
      this.rootCertificate = param1X509Certificate2;
      this.exception = param1Exception;
    }
    
    public CheckClassResult(CheckClassResult param1CheckClassResult) {
      this.type = param1CheckClassResult.type;
      this.exception = param1CheckClassResult.exception;
      this.rootCertificate = param1CheckClassResult.rootCertificate;
      this.endCertificate = param1CheckClassResult.endCertificate;
    }
  }
  
  public enum CheckClassResultType {
    NOT_SIGNED, SUCCESS, UNTRUSTED, UNVERIFED, UNCOMPAT;
  }
  
  public static interface CertificateChecker {
    void check(X509Certificate param1X509Certificate1, X509Certificate param1X509Certificate2, int param1Int) throws SecurityException;
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\LauncherTrustManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
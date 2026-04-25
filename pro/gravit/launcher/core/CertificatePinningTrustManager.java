package pro.gravit.launcher.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import javax.net.ssl.TrustManagerFactory;
import pro.gravit.utils.helper.LogHelper;

public final class CertificatePinningTrustManager {
  private static List<byte[]> secureConfigCertificates;
  
  private static X509Certificate[] certs = null;
  
  private static volatile TrustManagerFactory INSTANCE;
  
  private static X509Certificate[] getInternalCertificates() {
    CertificateFactory certificateFactory1 = null;
    try {
      certificateFactory1 = CertificateFactory.getInstance("X.509");
    } catch (CertificateException certificateException) {
      return new X509Certificate[0];
    } 
    CertificateFactory certificateFactory2 = certificateFactory1;
    return (X509Certificate[])secureConfigCertificates.stream().map(paramArrayOfbyte -> {
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
  }
  
  public static X509Certificate[] getCertificates() {
    if (certs == null)
      certs = getInternalCertificates(); 
    return Arrays.<X509Certificate>copyOf(certs, certs.length);
  }
  
  public static TrustManagerFactory getTrustManager() throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
    if (INSTANCE != null)
      return INSTANCE; 
    if (certs == null)
      certs = getInternalCertificates(); 
    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X.509");
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    keyStore.load(null, null);
    byte b = 1;
    for (X509Certificate x509Certificate : certs) {
      String str = Integer.toString(b);
      keyStore.setCertificateEntry(str, x509Certificate);
      b++;
    } 
    trustManagerFactory.init(keyStore);
    INSTANCE = trustManagerFactory;
    return trustManagerFactory;
  }
  
  static {
    (new ArrayList<>(1)).add(Base64.getDecoder().decode("MIIBSTCB76ADAgECAgEAMAoGCCqGSM49BAMCMDYxIDAeBgNVBAMMF3pMYXVuY2hlciBBdXRvZ2VuZXJhdGVkMRIwEAYDVQQKDAl6TGF1bmNoZXIwHhcNMjYwNDE4MjEwMDAwWhcNMzYwNDE1MjEwMDAwWjANMQswCQYDVQQDDAJjYTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABM+qxDXHj3tYtvh9AMJG7EeldPIQmJP3U4Q6JQTksOKvugh+MkO697M9ph8Z2QbWTTNq+SXltrD84iNKPDqHTDWjFzAVMBMGA1UdJQQMMAoGCCsGAQUFBwMDMAoGCCqGSM49BAMCA0kAMEYCIQD1y01s8QaSR9pC6CMDGyOEqZ0Y771ACwmSxDZevqD9rAIhAPI5i5nsRsoiZ1naPlW+HVDMRXMbaVNkeY6EXB6YWjwB"));
    secureConfigCertificates = (List)new ArrayList<>(1);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\CertificatePinningTrustManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
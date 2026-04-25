package pro.gravit.launcher.core;

import java.security.cert.X509Certificate;

public interface CertificateChecker {
  void check(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, int paramInt) throws SecurityException;
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\LauncherTrustManager$CertificateChecker.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.runtime.debug;

import java.security.cert.X509Certificate;
import pro.gravit.launcher.core.LauncherTrustManager;

public class DebugLauncherTrustManager extends LauncherTrustManager {
  private final TrustDebugMode mode = null;
  
  public DebugLauncherTrustManager(X509Certificate[] paramArrayOfX509Certificate) {
    super(paramArrayOfX509Certificate);
  }
  
  public DebugLauncherTrustManager() {
    super(new X509Certificate[0]);
  }
  
  public DebugLauncherTrustManager(TrustDebugMode paramTrustDebugMode) {
    super(new X509Certificate[0]);
  }
  
  public LauncherTrustManager.CheckClassResult checkCertificates(X509Certificate[] paramArrayOfX509Certificate, LauncherTrustManager.CertificateChecker paramCertificateChecker) {
    return (this.mode == TrustDebugMode.TRUST_ALL) ? new LauncherTrustManager.CheckClassResult(LauncherTrustManager.CheckClassResultType.SUCCESS, null, null) : super.checkCertificates(paramArrayOfX509Certificate, paramCertificateChecker);
  }
  
  public enum TrustDebugMode {
    TRUST_ALL;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\debug\DebugLauncherTrustManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
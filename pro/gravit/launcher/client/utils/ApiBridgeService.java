package pro.gravit.launcher.client.utils;

import java.security.cert.X509Certificate;
import java.util.Objects;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.core.LauncherTrustManager;

public class ApiBridgeService {
  public static LauncherTrustManager.CheckClassResult checkCertificates(X509Certificate[] paramArrayOfX509Certificate) {
    LauncherTrustManager launcherTrustManager = (Launcher.getConfig()).trustManager;
    Objects.requireNonNull(launcherTrustManager);
    return launcherTrustManager.checkCertificates(paramArrayOfX509Certificate, launcherTrustManager::stdCertificateChecker);
  }
  
  public static void checkCertificatesSuccess(X509Certificate[] paramArrayOfX509Certificate) throws Exception {
    LauncherTrustManager launcherTrustManager = (Launcher.getConfig()).trustManager;
    Objects.requireNonNull(launcherTrustManager);
    launcherTrustManager.checkCertificatesSuccess(paramArrayOfX509Certificate, launcherTrustManager::stdCertificateChecker);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\clien\\utils\ApiBridgeService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
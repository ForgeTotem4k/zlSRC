package pro.gravit.launcher.core;

import java.security.cert.X509Certificate;

public class CheckClassResult {
  public final LauncherTrustManager.CheckClassResultType type;
  
  public final X509Certificate endCertificate;
  
  public final X509Certificate rootCertificate;
  
  public final Exception exception;
  
  public CheckClassResult(LauncherTrustManager.CheckClassResultType paramCheckClassResultType, X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2) {
    this.type = paramCheckClassResultType;
    this.endCertificate = paramX509Certificate1;
    this.rootCertificate = paramX509Certificate2;
    this.exception = null;
  }
  
  public CheckClassResult(LauncherTrustManager.CheckClassResultType paramCheckClassResultType, X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, Exception paramException) {
    this.type = paramCheckClassResultType;
    this.endCertificate = paramX509Certificate1;
    this.rootCertificate = paramX509Certificate2;
    this.exception = paramException;
  }
  
  public CheckClassResult(CheckClassResult paramCheckClassResult) {
    this.type = paramCheckClassResult.type;
    this.exception = paramCheckClassResult.exception;
    this.rootCertificate = paramCheckClassResult.rootCertificate;
    this.endCertificate = paramCheckClassResult.endCertificate;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\core\LauncherTrustManager$CheckClassResult.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
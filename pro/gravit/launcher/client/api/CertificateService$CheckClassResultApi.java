package pro.gravit.launcher.client.api;

import java.security.cert.X509Certificate;
import pro.gravit.launcher.core.LauncherTrustManager;

public class CheckClassResultApi {
  public final CertificateService.CheckClassResultTypeApi type;
  
  public final X509Certificate endCertificate;
  
  public final X509Certificate rootCertificate;
  
  public final Exception exception;
  
  private CheckClassResultApi(CertificateService.CheckClassResultTypeApi paramCheckClassResultTypeApi, X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2) {
    this.type = paramCheckClassResultTypeApi;
    this.endCertificate = paramX509Certificate1;
    this.rootCertificate = paramX509Certificate2;
    this.exception = null;
  }
  
  private CheckClassResultApi(CertificateService.CheckClassResultTypeApi paramCheckClassResultTypeApi, X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, Exception paramException) {
    this.type = paramCheckClassResultTypeApi;
    this.endCertificate = paramX509Certificate1;
    this.rootCertificate = paramX509Certificate2;
    this.exception = paramException;
  }
  
  private CheckClassResultApi(CheckClassResultApi paramCheckClassResultApi) {
    this.type = paramCheckClassResultApi.type;
    this.exception = paramCheckClassResultApi.exception;
    this.rootCertificate = paramCheckClassResultApi.rootCertificate;
    this.endCertificate = paramCheckClassResultApi.endCertificate;
  }
  
  private static CheckClassResultApi fromCheckClassResult(LauncherTrustManager.CheckClassResult paramCheckClassResult) {
    return (paramCheckClassResult == null) ? null : new CheckClassResultApi(fromType(paramCheckClassResult.type), paramCheckClassResult.endCertificate, paramCheckClassResult.rootCertificate, paramCheckClassResult.exception);
  }
  
  private static CertificateService.CheckClassResultTypeApi fromType(LauncherTrustManager.CheckClassResultType paramCheckClassResultType) {
    if (paramCheckClassResultType == null)
      return null; 
    switch (CertificateService.null.$SwitchMap$pro$gravit$launcher$core$LauncherTrustManager$CheckClassResultType[paramCheckClassResultType.ordinal()]) {
      default:
        throw new IncompatibleClassChangeError();
      case 1:
      
      case 2:
      
      case 3:
      
      case 4:
      
      case 5:
        break;
    } 
    return CertificateService.CheckClassResultTypeApi.UNCOMPAT;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\api\CertificateService$CheckClassResultApi.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
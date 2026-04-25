package pro.gravit.launcher.client.api;

import java.security.cert.X509Certificate;
import pro.gravit.launcher.client.ClientLauncherMethods;
import pro.gravit.launcher.client.utils.ApiBridgeService;
import pro.gravit.launcher.core.LauncherTrustManager;

public class CertificateService {
  private CertificateService() {
    throw new UnsupportedOperationException();
  }
  
  public static CheckClassResultApi checkClass(Class<?> paramClass) throws SecurityException {
    X509Certificate[] arrayOfX509Certificate = ClientLauncherMethods.getCertificates(paramClass);
    if (arrayOfX509Certificate == null)
      return new CheckClassResultApi(CheckClassResultTypeApi.NOT_SIGNED, null, null); 
    try {
      return CheckClassResultApi.fromCheckClassResult(ApiBridgeService.checkCertificates(arrayOfX509Certificate));
    } catch (Exception exception) {
      throw new SecurityException(exception);
    } 
  }
  
  public static void checkClassSuccess(Class<?> paramClass) {
    X509Certificate[] arrayOfX509Certificate = ClientLauncherMethods.getCertificates(paramClass);
    if (arrayOfX509Certificate == null)
      throw new SecurityException(String.format("Class %s not signed", new Object[] { paramClass.getName() })); 
    try {
      ApiBridgeService.checkCertificatesSuccess(arrayOfX509Certificate);
    } catch (Exception exception) {
      throw new SecurityException(exception);
    } 
  }
  
  static {
  
  }
  
  public static class CheckClassResultApi {
    public final CertificateService.CheckClassResultTypeApi type;
    
    public final X509Certificate endCertificate;
    
    public final X509Certificate rootCertificate;
    
    public final Exception exception;
    
    private CheckClassResultApi(CertificateService.CheckClassResultTypeApi param1CheckClassResultTypeApi, X509Certificate param1X509Certificate1, X509Certificate param1X509Certificate2) {
      this.type = param1CheckClassResultTypeApi;
      this.endCertificate = param1X509Certificate1;
      this.rootCertificate = param1X509Certificate2;
      this.exception = null;
    }
    
    private CheckClassResultApi(CertificateService.CheckClassResultTypeApi param1CheckClassResultTypeApi, X509Certificate param1X509Certificate1, X509Certificate param1X509Certificate2, Exception param1Exception) {
      this.type = param1CheckClassResultTypeApi;
      this.endCertificate = param1X509Certificate1;
      this.rootCertificate = param1X509Certificate2;
      this.exception = param1Exception;
    }
    
    private CheckClassResultApi(CheckClassResultApi param1CheckClassResultApi) {
      this.type = param1CheckClassResultApi.type;
      this.exception = param1CheckClassResultApi.exception;
      this.rootCertificate = param1CheckClassResultApi.rootCertificate;
      this.endCertificate = param1CheckClassResultApi.endCertificate;
    }
    
    private static CheckClassResultApi fromCheckClassResult(LauncherTrustManager.CheckClassResult param1CheckClassResult) {
      return (param1CheckClassResult == null) ? null : new CheckClassResultApi(fromType(param1CheckClassResult.type), param1CheckClassResult.endCertificate, param1CheckClassResult.rootCertificate, param1CheckClassResult.exception);
    }
    
    private static CertificateService.CheckClassResultTypeApi fromType(LauncherTrustManager.CheckClassResultType param1CheckClassResultType) {
      // Byte code:
      //   0: aload_0
      //   1: ifnonnull -> 6
      //   4: aconst_null
      //   5: areturn
      //   6: getstatic pro/gravit/launcher/client/api/CertificateService$1.$SwitchMap$pro$gravit$launcher$core$LauncherTrustManager$CheckClassResultType : [I
      //   9: aload_0
      //   10: invokevirtual ordinal : ()I
      //   13: iaload
      //   14: tableswitch default -> 48, 1 -> 56, 2 -> 62, 3 -> 68, 4 -> 74, 5 -> 80
      //   48: new java/lang/IncompatibleClassChangeError
      //   51: dup
      //   52: invokespecial <init> : ()V
      //   55: athrow
      //   56: getstatic pro/gravit/launcher/client/api/CertificateService$CheckClassResultTypeApi.NOT_SIGNED : Lpro/gravit/launcher/client/api/CertificateService$CheckClassResultTypeApi;
      //   59: goto -> 83
      //   62: getstatic pro/gravit/launcher/client/api/CertificateService$CheckClassResultTypeApi.SUCCESS : Lpro/gravit/launcher/client/api/CertificateService$CheckClassResultTypeApi;
      //   65: goto -> 83
      //   68: getstatic pro/gravit/launcher/client/api/CertificateService$CheckClassResultTypeApi.UNTRUSTED : Lpro/gravit/launcher/client/api/CertificateService$CheckClassResultTypeApi;
      //   71: goto -> 83
      //   74: getstatic pro/gravit/launcher/client/api/CertificateService$CheckClassResultTypeApi.UNVERIFED : Lpro/gravit/launcher/client/api/CertificateService$CheckClassResultTypeApi;
      //   77: goto -> 83
      //   80: getstatic pro/gravit/launcher/client/api/CertificateService$CheckClassResultTypeApi.UNCOMPAT : Lpro/gravit/launcher/client/api/CertificateService$CheckClassResultTypeApi;
      //   83: areturn
    }
  }
  
  public enum CheckClassResultTypeApi {
    NOT_SIGNED, SUCCESS, UNTRUSTED, UNVERIFED, UNCOMPAT, UNKNOWN;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\api\CertificateService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
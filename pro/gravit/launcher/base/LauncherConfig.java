package pro.gravit.launcher.base;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.security.cert.CertificateException;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.modules.LauncherModulesManager;
import pro.gravit.launcher.core.LauncherInjectionConstructor;
import pro.gravit.launcher.core.LauncherTrustManager;
import pro.gravit.launcher.core.serialize.HInput;
import pro.gravit.launcher.core.serialize.HOutput;
import pro.gravit.launcher.core.serialize.stream.StreamObject;
import pro.gravit.launcher.gui.JavaRuntimeModule;
import pro.gravit.launchermodules.sentryl.SentryModule;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.LogHelper;
import pro.gravit.utils.helper.SecurityHelper;
import pro.gravit.utils.helper.VerifyHelper;

public final class LauncherConfig extends StreamObject {
  private static final List<byte[]> secureConfigCertificates = (List)new ArrayList<>(1);
  
  private static final List<Class<?>> modulesClasses = new ArrayList<>(0);
  
  private static final MethodType VOID_TYPE = MethodType.methodType(void.class);
  
  public final String projectName;
  
  public final int clientPort;
  
  public final LauncherTrustManager trustManager;
  
  public final ECPublicKey ecdsaPublicKey;
  
  public final RSAPublicKey rsaPublicKey;
  
  public final Map<String, byte[]> runtime;
  
  public final String secureCheckHash;
  
  public final String secureCheckSalt;
  
  public final String passwordEncryptKey;
  
  public final String runtimeEncryptKey;
  
  public final String address;
  
  public String secretKeyClient;
  
  public String unlockSecret;
  
  public LauncherEnvironment environment;
  
  public long buildNumber;
  
  @LauncherInjectionConstructor
  public LauncherConfig(HInput paramHInput) throws IOException, InvalidKeySpecException {
    this.ecdsaPublicKey = SecurityHelper.toPublicECDSAKey(paramHInput.readByteArray(2048));
    this.rsaPublicKey = SecurityHelper.toPublicRSAKey(paramHInput.readByteArray(2048));
    null;
    this.secureCheckHash = "6Tp6WbosAeCdyqhy7EN+dr1Qy9V/6YYivvb6T0iSw08=";
    null;
    this.secureCheckSalt = "98ec57f29c1906fd25f6d2fa7619930b";
    null;
    this.passwordEncryptKey = "b03201e58069d59411ad6800c3ab2627";
    this.runtimeEncryptKey = null;
    null;
    this.projectName = "zLauncher";
    -1;
    this.clientPort = 32561;
    null;
    this.secretKeyClient = "807b848ae456188f625b42f23d8341eb";
    try {
      this.trustManager = new LauncherTrustManager(secureConfigCertificates);
    } catch (CertificateException certificateException) {
      throw new IOException(certificateException);
    } 
    null;
    this.address = "wss://launcher.endcraft.ru/api";
    this.environment = LauncherEnvironment.STD;
    Launcher.applyLauncherEnv(this.environment);
    int i = paramHInput.readLength(0);
    HashMap<Object, Object> hashMap = new HashMap<>(i);
    for (byte b = 0; b < i; b++) {
      String str = paramHInput.readString(255);
      VerifyHelper.putIfAbsent(hashMap, str, paramHInput.readByteArray(2048), String.format("Duplicate runtime resource: '%s'", new Object[] { str }));
    } 
    this.runtime = Collections.unmodifiableMap(hashMap);
    this.unlockSecret = "e974382b5ce7dd70e26fd8f3e221f9e3";
    this.buildNumber = 126L;
  }
  
  public LauncherConfig(String paramString1, ECPublicKey paramECPublicKey, RSAPublicKey paramRSAPublicKey, Map<String, byte[]> paramMap, String paramString2) {
    this.address = paramString1;
    this.ecdsaPublicKey = paramECPublicKey;
    this.rsaPublicKey = paramRSAPublicKey;
    this.runtime = Map.copyOf((Map)paramMap);
    this.projectName = paramString2;
    this.clientPort = 32148;
    this.environment = LauncherEnvironment.STD;
    this.secureCheckSalt = null;
    this.secureCheckHash = null;
    this.passwordEncryptKey = null;
    this.runtimeEncryptKey = null;
    this.trustManager = null;
  }
  
  public LauncherConfig(String paramString1, Map<String, byte[]> paramMap, String paramString2, LauncherEnvironment paramLauncherEnvironment, LauncherTrustManager paramLauncherTrustManager) {
    this.address = paramString1;
    this.runtime = Map.copyOf((Map)paramMap);
    this.projectName = paramString2;
    this.clientPort = 32148;
    this.trustManager = paramLauncherTrustManager;
    this.rsaPublicKey = null;
    this.ecdsaPublicKey = null;
    this.environment = paramLauncherEnvironment;
    this.secureCheckSalt = null;
    this.secureCheckHash = null;
    this.passwordEncryptKey = null;
    this.runtimeEncryptKey = null;
  }
  
  public static void initModules(LauncherModulesManager paramLauncherModulesManager) {
    if (JVMHelper.JVM_VERSION >= 17)
      modulesClasses.addAll(ModernModulesClass.modulesClasses); 
    for (Class<?> clazz : modulesClasses) {
      try {
        paramLauncherModulesManager.loadModule((LauncherModule)MethodHandles.publicLookup().findConstructor(clazz, VOID_TYPE).invokeWithArguments(Collections.emptyList()));
      } catch (Throwable throwable) {
        LogHelper.error(throwable);
      } 
    } 
    modulesClasses.clear();
  }
  
  public void write(HOutput paramHOutput) throws IOException {
    paramHOutput.writeByteArray(this.ecdsaPublicKey.getEncoded(), 2048);
    paramHOutput.writeByteArray(this.rsaPublicKey.getEncoded(), 2048);
    Set<Map.Entry<String, byte>> set = this.runtime.entrySet();
    paramHOutput.writeLength(set.size(), 0);
    for (Map.Entry<String, byte> entry : this.runtime.entrySet()) {
      paramHOutput.writeString((String)entry.getKey(), 255);
      paramHOutput.writeByteArray((byte[])entry.getValue(), 2048);
    } 
  }
  
  static {
    null;
    (new ArrayList<>(1)).add(Base64.getDecoder().decode("MIIBSTCB76ADAgECAgEAMAoGCCqGSM49BAMCMDYxIDAeBgNVBAMMF3pMYXVuY2hlciBBdXRvZ2VuZXJhdGVkMRIwEAYDVQQKDAl6TGF1bmNoZXIwHhcNMjYwNDE4MjEwMDAwWhcNMzYwNDE1MjEwMDAwWjANMQswCQYDVQQDDAJjYTBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABM+qxDXHj3tYtvh9AMJG7EeldPIQmJP3U4Q6JQTksOKvugh+MkO697M9ph8Z2QbWTTNq+SXltrD84iNKPDqHTDWjFzAVMBMGA1UdJQQMMAoGCCsGAQUFBwMDMAoGCCqGSM49BAMCA0kAMEYCIQD1y01s8QaSR9pC6CMDGyOEqZ0Y771ACwmSxDZevqD9rAIhAPI5i5nsRsoiZ1naPlW+HVDMRXMbaVNkeY6EXB6YWjwB"));
  }
  
  static {
    null;
  }
  
  public enum LauncherEnvironment {
    DEV, DEBUG, STD, PROD;
  }
  
  private static class ModernModulesClass {
    private static final List<Class<?>> modulesClasses = new ArrayList<>(2);
    
    static {
      null;
      (new ArrayList<>(2)).add(JavaRuntimeModule.class);
      (new ArrayList<>(2)).add(SentryModule.class);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\LauncherConfig.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
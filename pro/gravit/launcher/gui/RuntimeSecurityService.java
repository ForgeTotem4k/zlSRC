package pro.gravit.launcher.gui;

import java.io.IOException;
import pro.gravit.launcher.base.events.request.GetSecureLevelInfoRequestEvent;
import pro.gravit.launcher.base.events.request.HardwareReportRequestEvent;
import pro.gravit.launcher.base.events.request.VerifySecureLevelKeyRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.secure.GetSecureLevelInfoRequest;
import pro.gravit.launcher.base.request.secure.HardwareReportRequest;
import pro.gravit.launcher.base.request.secure.VerifySecureLevelKeyRequest;
import pro.gravit.launcher.runtime.utils.HWIDProvider;
import pro.gravit.utils.helper.CommonHelper;
import pro.gravit.utils.helper.LogHelper;
import pro.gravit.utils.helper.SecurityHelper;

public class RuntimeSecurityService {
  private final JavaFXApplication application;
  
  private final Boolean[] waitObject = new Boolean[] { null };
  
  public RuntimeSecurityService(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public void startRequest() throws IOException {
    this.application.service.request((Request)new GetSecureLevelInfoRequest()).thenAccept(paramGetSecureLevelInfoRequestEvent -> {
          if (!paramGetSecureLevelInfoRequestEvent.enabled || paramGetSecureLevelInfoRequestEvent.verifySecureKey == null) {
            LogHelper.info("Advanced security level disabled");
            notifyWaitObject(false);
            return;
          } 
          byte[] arrayOfByte = sign(paramGetSecureLevelInfoRequestEvent.verifySecureKey);
          try {
            this.application.service.request((Request)new VerifySecureLevelKeyRequest(JavaRuntimeModule.engine.publicKey.getEncoded(), arrayOfByte)).thenAccept(()).exceptionally(());
          } catch (IOException iOException) {
            LogHelper.error("VerifySecureLevel failed: %s", new Object[] { iOException.getMessage() });
            notifyWaitObject(false);
          } 
        }).exceptionally(paramThrowable -> {
          LogHelper.info("Advanced security level disabled(exception)");
          notifyWaitObject(false);
          return null;
        });
  }
  
  private void simpleGetHardwareToken() {
    try {
      this.application.service.request((Request)new HardwareReportRequest()).thenAccept(paramHardwareReportRequestEvent -> {
            LogHelper.info("Advanced security level success completed");
            notifyWaitObject(true);
          }).exceptionally(paramThrowable -> {
            this.application.messageManager.createNotification("Hardware Checker", paramThrowable.getCause().getMessage());
            notifyWaitObject(false);
            return null;
          });
    } catch (IOException iOException) {
      this.application.messageManager.createNotification("Hardware Checker", iOException.getCause().getMessage());
      notifyWaitObject(false);
    } 
  }
  
  private void doCollectHardwareInfo(boolean paramBoolean) {
    CommonHelper.newThread("HardwareInfo Collector Thread", true, () -> {
          try {
            HWIDProvider hWIDProvider = new HWIDProvider();
            HardwareReportRequest.HardwareInfo hardwareInfo = hWIDProvider.getHardwareInfo(paramBoolean);
            HardwareReportRequest hardwareReportRequest = new HardwareReportRequest();
            hardwareReportRequest.hardware = hardwareInfo;
            this.application.service.request((Request)hardwareReportRequest).thenAccept(()).exceptionally(());
          } catch (Throwable throwable) {
            LogHelper.error(throwable);
            notifyWaitObject(false);
          } 
        }).start();
  }
  
  private void notifyWaitObject(boolean paramBoolean) {
    synchronized (this.waitObject) {
      this.waitObject[0] = Boolean.valueOf(paramBoolean);
      this.waitObject.notifyAll();
    } 
  }
  
  public boolean getSecurityState() throws InterruptedException {
    synchronized (this.waitObject) {
      if (this.waitObject[0] == null)
        this.waitObject.wait(3000L); 
      return this.waitObject[0].booleanValue();
    } 
  }
  
  public byte[] sign(byte[] paramArrayOfbyte) {
    return SecurityHelper.sign(paramArrayOfbyte, JavaRuntimeModule.engine.privateKey);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\RuntimeSecurityService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
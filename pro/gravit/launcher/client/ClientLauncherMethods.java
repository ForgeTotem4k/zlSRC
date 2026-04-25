package pro.gravit.launcher.client;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.events.request.ExitRequestEvent;
import pro.gravit.launcher.base.events.request.FeaturesRequestEvent;
import pro.gravit.launcher.base.events.request.GetAvailabilityAuthRequestEvent;
import pro.gravit.launcher.base.events.request.GetSecureLevelInfoRequestEvent;
import pro.gravit.launcher.base.events.request.JoinServerRequestEvent;
import pro.gravit.launcher.base.events.request.LauncherRequestEvent;
import pro.gravit.launcher.base.events.request.ProfileByUUIDRequestEvent;
import pro.gravit.launcher.base.events.request.ProfileByUsernameRequestEvent;
import pro.gravit.launcher.base.events.request.SecurityReportRequestEvent;
import pro.gravit.launcher.base.events.request.SetProfileRequestEvent;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.modules.LauncherModulesManager;
import pro.gravit.launcher.base.modules.events.OfflineModeEvent;
import pro.gravit.launcher.base.profiles.optional.actions.OptionalAction;
import pro.gravit.launcher.base.profiles.optional.triggers.OptionalTrigger;
import pro.gravit.launcher.base.request.RequestException;
import pro.gravit.launcher.base.request.RequestService;
import pro.gravit.launcher.base.request.WebSocketEvent;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.base.request.auth.CheckServerRequest;
import pro.gravit.launcher.base.request.auth.ExitRequest;
import pro.gravit.launcher.base.request.auth.GetAvailabilityAuthRequest;
import pro.gravit.launcher.base.request.auth.JoinServerRequest;
import pro.gravit.launcher.base.request.auth.SetProfileRequest;
import pro.gravit.launcher.base.request.auth.details.AuthLoginOnlyDetails;
import pro.gravit.launcher.base.request.management.FeaturesRequest;
import pro.gravit.launcher.base.request.secure.GetSecureLevelInfoRequest;
import pro.gravit.launcher.base.request.secure.SecurityReportRequest;
import pro.gravit.launcher.base.request.update.LauncherRequest;
import pro.gravit.launcher.base.request.uuid.ProfileByUUIDRequest;
import pro.gravit.launcher.base.request.uuid.ProfileByUsernameRequest;
import pro.gravit.launcher.base.request.websockets.OfflineRequestService;
import pro.gravit.launcher.client.events.ClientExitPhase;
import pro.gravit.launcher.client.utils.NativeJVMHalt;
import pro.gravit.launcher.core.LauncherTrustManager;
import pro.gravit.utils.helper.JVMHelper;

public class ClientLauncherMethods {
  public static void verifyNoAgent() {
    if (JVMHelper.RUNTIME_MXBEAN.getInputArguments().stream().filter(paramString -> (paramString != null && !paramString.isEmpty())).anyMatch(paramString -> paramString.contains("javaagent")))
      throw new SecurityException("JavaAgent found"); 
  }
  
  public static X509Certificate[] getCertificates(Class<?> paramClass) {
    Object[] arrayOfObject = paramClass.getSigners();
    return (arrayOfObject == null) ? null : (X509Certificate[])Arrays.<Object>stream(arrayOfObject).filter(paramObject -> paramObject instanceof X509Certificate).map(paramObject -> (X509Certificate)paramObject).toArray(paramInt -> new X509Certificate[paramInt]);
  }
  
  public static void beforeExit(int paramInt) {
    try {
      ClientLauncherEntryPoint.modulesManager.invokeEvent((LauncherModule.Event)new ClientExitPhase(paramInt));
    } catch (Throwable throwable) {}
  }
  
  public static void forceExit(int paramInt) {
    try {
      System.exit(paramInt);
    } catch (Throwable throwable) {
      NativeJVMHalt.haltA(paramInt);
    } 
  }
  
  public static void exitLauncher(int paramInt) {
    beforeExit(paramInt);
    forceExit(paramInt);
  }
  
  public static void checkClass(Class<?> paramClass) throws SecurityException {
    LauncherTrustManager launcherTrustManager = (Launcher.getConfig()).trustManager;
    if (launcherTrustManager == null)
      return; 
    X509Certificate[] arrayOfX509Certificate = getCertificates(paramClass);
    if (arrayOfX509Certificate == null)
      throw new SecurityException(String.format("Class %s not signed", new Object[] { paramClass.getName() })); 
    try {
      Objects.requireNonNull(launcherTrustManager);
      launcherTrustManager.checkCertificatesSuccess(arrayOfX509Certificate, launcherTrustManager::stdCertificateChecker);
    } catch (Exception exception) {
      throw new SecurityException(exception);
    } 
  }
  
  public static void initGson(ClientModuleManager paramClientModuleManager) {
    AuthRequest.registerProviders();
    GetAvailabilityAuthRequest.registerProviders();
    OptionalAction.registerProviders();
    OptionalTrigger.registerProviders();
    Launcher.gsonManager = new ClientGsonManager(paramClientModuleManager);
    Launcher.gsonManager.initGson();
  }
  
  public static RequestService initOffline(LauncherModulesManager paramLauncherModulesManager, ClientParams paramClientParams) {
    OfflineRequestService offlineRequestService = new OfflineRequestService();
    applyBasicOfflineProcessors(offlineRequestService);
    applyClientOfflineProcessors(offlineRequestService, paramClientParams);
    OfflineModeEvent offlineModeEvent = new OfflineModeEvent((RequestService)offlineRequestService);
    paramLauncherModulesManager.invokeEvent((LauncherModule.Event)offlineModeEvent);
    return offlineModeEvent.service;
  }
  
  public static void applyClientOfflineProcessors(OfflineRequestService paramOfflineRequestService, ClientParams paramClientParams) {
    paramOfflineRequestService.registerRequestProcessor(ProfileByUsernameRequest.class, paramProfileByUsernameRequest -> {
          if (paramClientParams.playerProfile.username.equals(paramProfileByUsernameRequest.username))
            return new ProfileByUsernameRequestEvent(paramClientParams.playerProfile); 
          throw new RequestException("User not found");
        });
    paramOfflineRequestService.registerRequestProcessor(ProfileByUUIDRequest.class, paramProfileByUUIDRequest -> {
          if (paramClientParams.playerProfile.uuid.equals(paramProfileByUUIDRequest.uuid))
            return new ProfileByUUIDRequestEvent(paramClientParams.playerProfile); 
          throw new RequestException("User not found");
        });
  }
  
  public static void applyBasicOfflineProcessors(OfflineRequestService paramOfflineRequestService) {
    paramOfflineRequestService.registerRequestProcessor(LauncherRequest.class, paramLauncherRequest -> new LauncherRequestEvent(false, (String)null));
    paramOfflineRequestService.registerRequestProcessor(CheckServerRequest.class, paramCheckServerRequest -> {
          throw new RequestException("CheckServer disabled in offline mode");
        });
    paramOfflineRequestService.registerRequestProcessor(GetAvailabilityAuthRequest.class, paramGetAvailabilityAuthRequest -> {
          ArrayList<AuthLoginOnlyDetails> arrayList = new ArrayList();
          arrayList.add(new AuthLoginOnlyDetails());
          GetAvailabilityAuthRequestEvent.AuthAvailability authAvailability = new GetAvailabilityAuthRequestEvent.AuthAvailability(arrayList, "offline", "Offline Mode", true, new HashSet());
          ArrayList<GetAvailabilityAuthRequestEvent.AuthAvailability> arrayList1 = new ArrayList(1);
          arrayList1.add(authAvailability);
          return new GetAvailabilityAuthRequestEvent(arrayList1);
        });
    paramOfflineRequestService.registerRequestProcessor(JoinServerRequest.class, paramJoinServerRequest -> new JoinServerRequestEvent(false));
    paramOfflineRequestService.registerRequestProcessor(ExitRequest.class, paramExitRequest -> new ExitRequestEvent(ExitRequestEvent.ExitReason.CLIENT));
    paramOfflineRequestService.registerRequestProcessor(SetProfileRequest.class, paramSetProfileRequest -> new SetProfileRequestEvent(null));
    paramOfflineRequestService.registerRequestProcessor(FeaturesRequest.class, paramFeaturesRequest -> new FeaturesRequestEvent());
    paramOfflineRequestService.registerRequestProcessor(GetSecureLevelInfoRequest.class, paramGetSecureLevelInfoRequest -> new GetSecureLevelInfoRequestEvent(null, false));
    paramOfflineRequestService.registerRequestProcessor(SecurityReportRequest.class, paramSecurityReportRequest -> new SecurityReportRequestEvent(SecurityReportRequestEvent.ReportAction.NONE));
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\ClientLauncherMethods.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
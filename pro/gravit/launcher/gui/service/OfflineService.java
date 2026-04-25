package pro.gravit.launcher.gui.service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import pro.gravit.launcher.base.ClientPermissions;
import pro.gravit.launcher.base.events.request.AuthRequestEvent;
import pro.gravit.launcher.base.events.request.ProfilesRequestEvent;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.profiles.PlayerProfile;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.RequestException;
import pro.gravit.launcher.base.request.auth.AuthRequest;
import pro.gravit.launcher.base.request.auth.password.AuthOAuthPassword;
import pro.gravit.launcher.base.request.update.ProfilesRequest;
import pro.gravit.launcher.base.request.websockets.OfflineRequestService;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.runtime.client.DirBridge;
import pro.gravit.utils.helper.SecurityHelper;

public class OfflineService {
  private final JavaFXApplication application;
  
  public OfflineService(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public boolean isAvailableOfflineMode() {
    return this.application.guiModuleConfig.disableOfflineMode ? false : ((this.application.runtimeSettings.profiles != null));
  }
  
  public boolean isOfflineMode() {
    return Request.getRequestService() instanceof OfflineRequestService;
  }
  
  public static void applyRuntimeProcessors(OfflineRequestService paramOfflineRequestService) {
    paramOfflineRequestService.registerRequestProcessor(AuthRequest.class, paramAuthRequest -> {
          ClientPermissions clientPermissions = new ClientPermissions();
          String str = paramAuthRequest.login;
          if (str == null) {
            AuthRequest.AuthPasswordInterface authPasswordInterface = paramAuthRequest.password;
            if (authPasswordInterface instanceof AuthOAuthPassword) {
              AuthOAuthPassword authOAuthPassword = (AuthOAuthPassword)authPasswordInterface;
              str = authOAuthPassword.accessToken;
            } 
          } 
          if (str == null)
            str = "Player"; 
          return new AuthRequestEvent(clientPermissions, new PlayerProfile(UUID.nameUUIDFromBytes(str.getBytes(StandardCharsets.UTF_8)), str, new HashMap<>(), new HashMap<>()), SecurityHelper.randomStringToken(), "", null, new AuthRequestEvent.OAuthRequestEvent(str, null, 0L));
        });
    paramOfflineRequestService.registerRequestProcessor(ProfilesRequest.class, paramProfilesRequest -> {
          JavaFXApplication javaFXApplication = JavaFXApplication.getInstance();
          List list = (List)javaFXApplication.runtimeSettings.profiles.stream().filter(()).collect(Collectors.toList());
          return new ProfilesRequestEvent(list);
        });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\service\OfflineService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
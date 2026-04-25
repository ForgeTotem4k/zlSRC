package pro.gravit.launcher.gui.impl;

import java.util.Objects;
import java.util.UUID;
import javafx.application.Platform;
import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.base.events.request.AuthRequestEvent;
import pro.gravit.launcher.base.events.request.ProfilesRequestEvent;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.request.RequestService;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.scenes.AbstractScene;
import pro.gravit.launcher.gui.scenes.login.AuthFlow;
import pro.gravit.launcher.gui.scenes.login.LoginScene;
import pro.gravit.utils.helper.LogHelper;

public class GuiEventHandler implements RequestService.EventHandler {
  private final JavaFXApplication application;
  
  public GuiEventHandler(JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
  }
  
  public <T extends pro.gravit.launcher.base.request.WebSocketEvent> boolean eventHandle(T paramT) {
    LogHelper.dev("Processing event %s", new Object[] { paramT.getType() });
    if (paramT instanceof RequestEvent) {
      RequestEvent requestEvent = (RequestEvent)paramT;
      if (!requestEvent.requestUUID.equals(RequestEvent.eventUUID))
        return false; 
    } 
    try {
      if (paramT instanceof AuthRequestEvent) {
        AuthRequestEvent authRequestEvent = (AuthRequestEvent)paramT;
        boolean bool = this.application.getCurrentScene() instanceof LoginScene;
        LogHelper.dev("Receive auth event. Send next scene %s", new Object[] { bool ? "true" : "false" });
        this.application.authService.setAuthResult(null, authRequestEvent);
        if (bool)
          Platform.runLater(() -> {
                try {
                  ((LoginScene)this.application.getCurrentScene()).onSuccessLogin(new AuthFlow.SuccessAuth(paramAuthRequestEvent, (paramAuthRequestEvent.playerProfile != null) ? paramAuthRequestEvent.playerProfile.username : null, null));
                } catch (Throwable throwable) {
                  LogHelper.error(throwable);
                } 
              }); 
      } 
      if (paramT instanceof ProfilesRequestEvent) {
        ProfilesRequestEvent profilesRequestEvent = (ProfilesRequestEvent)paramT;
        this.application.profilesService.setProfilesResult(profilesRequestEvent);
        if (this.application.profilesService.getProfile() != null) {
          UUID uUID = this.application.profilesService.getProfile().getUUID();
          for (ClientProfile clientProfile : this.application.profilesService.getProfiles()) {
            if (clientProfile.getUUID().equals(uUID)) {
              this.application.profilesService.setProfile(clientProfile);
              break;
            } 
          } 
        } 
        AbstractScene abstractScene = this.application.getCurrentScene();
        if (abstractScene instanceof pro.gravit.launcher.gui.scenes.servermenu.ServerMenuScene || abstractScene instanceof pro.gravit.launcher.gui.scenes.serverinfo.ServerInfoScene || (abstractScene instanceof pro.gravit.launcher.gui.scenes.settings.SettingsScene | abstractScene instanceof pro.gravit.launcher.gui.scenes.options.OptionsScene) != 0) {
          Objects.requireNonNull(abstractScene);
          abstractScene.contextHelper.runInFxThread(abstractScene::reset);
        } 
      } 
    } catch (Throwable throwable) {
      LogHelper.error(throwable);
    } 
    return false;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\impl\GuiEventHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.gui.scenes.servermenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.components.ServerButton;
import pro.gravit.launcher.gui.components.UserBlock;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.scenes.AbstractScene;
import pro.gravit.launcher.gui.scenes.interfaces.SceneSupportUserBlock;
import pro.gravit.launcher.gui.service.LaunchService;
import pro.gravit.launcher.runtime.client.ServerPinger;
import pro.gravit.utils.helper.CommonHelper;
import pro.gravit.utils.helper.LogHelper;

public class ServerMenuScene extends AbstractScene implements SceneSupportUserBlock {
  private List<ClientProfile> lastProfiles;
  
  private UserBlock userBlock;
  
  public ServerMenuScene(JavaFXApplication paramJavaFXApplication) {
    super("scenes/servermenu/servermenu.fxml", paramJavaFXApplication);
  }
  
  public void doInit() {
    this.userBlock = new UserBlock(this.layout, new AbstractScene.SceneAccessor(this));
    ((ButtonBase)LookupHelper.lookup((Node)this.layout, new String[] { "#settingsb" })).setOnAction(paramActionEvent -> {
          try {
            switchScene((AbstractScene)this.application.gui.settingsScene);
          } catch (Exception exception) {
            errorHandle(exception);
          } 
        });
    ((ButtonBase)LookupHelper.lookup((Node)this.layout, new String[] { "#deauth" })).setOnAction(paramActionEvent -> userExit());
    ScrollPane scrollPane = (ScrollPane)LookupHelper.lookup((Node)this.layout, new String[] { "#servers" });
    scrollPane.setOnScroll(paramScrollEvent -> {
          double d1 = paramScrollPane.getWidth();
          double d2 = d1 * 0.15D / (paramScrollPane.getContent().getBoundsInLocal().getWidth() - d1) * Math.signum(paramScrollEvent.getDeltaY());
          paramScrollPane.setHvalue(paramScrollPane.getHvalue() - d2);
        });
    reset();
    this.isResetOnShow = true;
  }
  
  public void reset() {
    if (this.lastProfiles == this.application.profilesService.getProfiles())
      return; 
    this.lastProfiles = this.application.profilesService.getProfiles();
    LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
    ArrayList<ClientProfile> arrayList = new ArrayList<>(this.lastProfiles);
    arrayList.sort(Comparator.comparingInt(ClientProfile::getSortIndex).thenComparing(ClientProfile::getTitle));
    byte b = 0;
    for (ClientProfile clientProfile : arrayList) {
      ServerButtonCache serverButtonCache = new ServerButtonCache();
      serverButtonCache.serverButton = ServerButton.createServerButton(this.application, clientProfile);
      serverButtonCache.position = b;
      linkedHashMap.put(clientProfile, serverButtonCache);
      b++;
    } 
    ScrollPane scrollPane = (ScrollPane)LookupHelper.lookup((Node)this.layout, new String[] { "#servers" });
    VBox vBox = (VBox)scrollPane.getContent();
    vBox.setSpacing(20.0D);
    vBox.getChildren().clear();
    this.application.pingService.clear();
    linkedHashMap.forEach((paramClientProfile, paramServerButtonCache) -> {
          EventHandler eventHandler = ();
          paramServerButtonCache.serverButton.addTo((Pane)paramVBox, paramServerButtonCache.position);
          paramServerButtonCache.serverButton.setOnMouseClicked(eventHandler);
        });
    CommonHelper.newThread("ServerPinger", true, () -> {
          for (ClientProfile clientProfile : this.lastProfiles) {
            for (ClientProfile.ServerProfile serverProfile : clientProfile.getServers()) {
              if (!serverProfile.socketPing || serverProfile.serverAddress == null)
                continue; 
              try {
                ServerPinger serverPinger = new ServerPinger(serverProfile, clientProfile.getVersion());
                ServerPinger.Result result = serverPinger.ping();
                this.contextHelper.runInFxThread(());
              } catch (IOException iOException) {}
            } 
          } 
        }).start();
    this.userBlock.reset();
  }
  
  public UserBlock getUserBlock() {
    return this.userBlock;
  }
  
  public String getName() {
    return "serverMenu";
  }
  
  private void changeServer(ClientProfile paramClientProfile) {
    this.application.profilesService.setProfile(paramClientProfile);
    this.application.runtimeSettings.lastProfile = paramClientProfile.getUUID();
  }
  
  private void runClient() {
    this.application.launchService.launchClient().thenAccept(paramClientInstance -> {
          if (this.application.runtimeSettings.globalSettings.debugAllClients || (paramClientInstance.getSettings()).debug) {
            this.contextHelper.runInFxThread(());
          } else {
            paramClientInstance.start();
            paramClientInstance.getOnWriteParamsFuture().thenAccept(()).exceptionally(());
          } 
        }).exceptionally(paramThrowable -> {
          this.contextHelper.runInFxThread(());
          return null;
        });
  }
  
  static class ServerButtonCache {
    public ServerButton serverButton;
    
    public int position;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\servermenu\ServerMenuScene.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
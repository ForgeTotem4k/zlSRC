package pro.gravit.launcher.gui.scenes.serverinfo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.components.ServerButton;
import pro.gravit.launcher.gui.components.UserBlock;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.scenes.AbstractScene;
import pro.gravit.launcher.gui.scenes.interfaces.SceneSupportUserBlock;
import pro.gravit.launcher.gui.service.LaunchService;
import pro.gravit.utils.helper.LogHelper;

public class ServerInfoScene extends AbstractScene implements SceneSupportUserBlock {
  private ServerButton serverButton;
  
  private UserBlock userBlock;
  
  public ServerInfoScene(JavaFXApplication paramJavaFXApplication) {
    super("scenes/serverinfo/serverinfo.fxml", paramJavaFXApplication);
  }
  
  protected void doInit() {
    this.userBlock = new UserBlock(this.layout, new AbstractScene.SceneAccessor(this));
    ((Button)LookupHelper.lookup((Node)this.layout, new String[] { "#back" })).setOnAction(paramActionEvent -> {
          try {
            switchToBackScene();
          } catch (Exception exception) {
            errorHandle(exception);
          } 
        });
    ((ButtonBase)LookupHelper.lookup((Node)this.layout, new String[] { "#settingsb" })).setOnAction(paramActionEvent -> {
          try {
            switchScene((AbstractScene)this.application.gui.settingsScene);
            this.application.gui.settingsScene.reset();
          } catch (Exception exception) {
            errorHandle(exception);
          } 
        });
    reset();
  }
  
  public void reset() {
    ClientProfile clientProfile = this.application.profilesService.getProfile();
    (this.application.getProfileSettings(clientProfile)).javaPath = this.application.runtimeSettings.updatesDirPath;
    LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#serverName" }).ifPresent(paramLabel -> paramLabel.setText(paramClientProfile.getTitle()));
    LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#serverDescriptionPane" }).ifPresent(paramScrollPane -> {
          Label label = (Label)paramScrollPane.getContent();
          label.setText(paramClientProfile.getInfo());
        });
    Pane pane = (Pane)LookupHelper.lookup((Node)this.layout, new String[] { "#serverButton" });
    pane.getChildren().clear();
    this.serverButton = ServerButton.createServerButton(this.application, clientProfile);
    this.serverButton.addTo(pane);
    this.serverButton.enableSaveButton(this.application.getTranslation("runtime.scenes.serverinfo.serverButton.game"), paramActionEvent -> runClient());
    this.userBlock.reset();
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
  
  public String getName() {
    return "serverinfo";
  }
  
  public UserBlock getUserBlock() {
    return this.userBlock;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\serverinfo\ServerInfoScene.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
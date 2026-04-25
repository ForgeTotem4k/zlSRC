package pro.gravit.launcher.gui.scenes.options;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.base.profiles.optional.OptionalView;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.components.ServerButton;
import pro.gravit.launcher.gui.components.UserBlock;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.scenes.AbstractScene;
import pro.gravit.launcher.gui.scenes.interfaces.SceneSupportUserBlock;

public class OptionsScene extends AbstractScene implements SceneSupportUserBlock {
  private OptionsTab optionsTab;
  
  private UserBlock userBlock;
  
  public OptionsScene(JavaFXApplication paramJavaFXApplication) {
    super("scenes/options/options.fxml", paramJavaFXApplication);
  }
  
  protected void doInit() {
    this.userBlock = new UserBlock(this.layout, new AbstractScene.SceneAccessor(this));
    this.optionsTab = new OptionsTab(this.application, (TabPane)LookupHelper.lookup((Node)this.layout, new String[] { "#tabPane" }));
  }
  
  public void reset() {
    Pane pane = (Pane)LookupHelper.lookup((Node)this.layout, new String[] { "#serverButton" });
    pane.getChildren().clear();
    ClientProfile clientProfile = this.application.profilesService.getProfile();
    ServerButton serverButton = ServerButton.createServerButton(this.application, clientProfile);
    serverButton.addTo(pane);
    serverButton.enableSaveButton(null, paramActionEvent -> {
          try {
            this.application.profilesService.setOptionalView(paramClientProfile, this.optionsTab.getOptionalView());
            switchScene((AbstractScene)this.application.gui.serverInfoScene);
          } catch (Exception exception) {
            errorHandle(exception);
          } 
        });
    serverButton.enableResetButton(null, paramActionEvent -> {
          this.optionsTab.clear();
          this.application.profilesService.setOptionalView(paramClientProfile, new OptionalView(paramClientProfile));
          this.optionsTab.addProfileOptionals(this.application.profilesService.getOptionalView());
        });
    this.optionsTab.clear();
    LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#back" }).ifPresent(paramButton -> paramButton.setOnAction(()));
    this.optionsTab.addProfileOptionals(this.application.profilesService.getOptionalView());
    this.userBlock.reset();
  }
  
  public String getName() {
    return "options";
  }
  
  public UserBlock getUserBlock() {
    return this.userBlock;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\options\OptionsScene.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
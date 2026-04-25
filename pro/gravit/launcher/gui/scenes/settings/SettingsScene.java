package pro.gravit.launcher.gui.scenes.settings;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.config.RuntimeSettings;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.scenes.settings.components.RamSliderComponent;

public class SettingsScene extends BaseSettingsScene {
  private RamSliderComponent ramSlider;
  
  private RuntimeSettings.ProfileSettingsView profileSettings;
  
  public SettingsScene(JavaFXApplication paramJavaFXApplication) {
    super("scenes/settings/settings.fxml", paramJavaFXApplication);
  }
  
  protected void doInit() {
    super.doInit();
    this.ramSlider = new RamSliderComponent(this.application, this.layout);
    LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#save" }).ifPresent(paramButtonBase -> paramButtonBase.setOnAction(()));
    LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#back" }).ifPresent(paramButtonBase -> paramButtonBase.setOnMouseClicked(()));
    reset();
    Hyperlink hyperlink = (Hyperlink)LookupHelper.lookup((Node)this.componentList, new String[] { "#path" });
    if (this.application.runtimeSettings.updatesDirPath != null && this.application.runtimeSettings.updatesDir != null) {
      String str = this.application.runtimeSettings.updatesDir.toString();
      hyperlink.setText(str);
      hyperlink.setOnAction(paramActionEvent -> this.application.openURL(this.application.runtimeSettings.updatesDir.toAbsolutePath().toString()));
    } 
  }
  
  public void reset() {
    super.reset();
  }
  
  public String getName() {
    return "settings";
  }
  
  private void save() {
    this.ramSlider.save();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\settings\SettingsScene.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
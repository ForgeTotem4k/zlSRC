package pro.gravit.launcher.gui.scenes.settings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.config.RuntimeSettings;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.scenes.settings.components.LanguageSelectorComponent;
import pro.gravit.launcher.gui.scenes.settings.components.RamSliderComponent;
import pro.gravit.launcher.gui.scenes.settings.components.ThemeSelectorComponent;
import pro.gravit.launcher.runtime.client.DirBridge;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;

public class GlobalSettingsScene extends BaseSettingsScene {
  private ThemeSelectorComponent themeSelector;
  
  private LanguageSelectorComponent languageSelectorComponent;
  
  public GlobalSettingsScene(JavaFXApplication paramJavaFXApplication) {
    super("scenes/settings/globalsettings.fxml", paramJavaFXApplication);
  }
  
  public String getName() {
    return "globalsettings";
  }
  
  protected void doInit() {
    super.doInit();
    RamSliderComponent ramSliderComponent = new RamSliderComponent(this.application, this.layout);
    Hyperlink hyperlink = (Hyperlink)LookupHelper.lookup((Node)this.componentList, new String[] { "#folder", "#path" });
    String str = this.application.runtimeSettings.updatesDir.toString();
    hyperlink.setText(str);
    if (hyperlink.getTooltip() == null) {
      hyperlink.setTooltip(new Tooltip(str));
    } else {
      hyperlink.getTooltip().setText(str);
    } 
    hyperlink.setOnAction(paramActionEvent -> this.application.openURL(this.application.runtimeSettings.updatesDir.toAbsolutePath().toString()));
    ((ButtonBase)LookupHelper.lookup((Node)this.componentList, new String[] { "#changeDir" })).setOnAction(paramActionEvent -> {
          DirectoryChooser directoryChooser = new DirectoryChooser();
          directoryChooser.setTitle(this.application.getTranslation("runtime.scenes.settings.dirTitle"));
          directoryChooser.setInitialDirectory(DirBridge.dir.toFile());
          File file = directoryChooser.showDialog((Window)this.application.getMainStage().getStage());
          if (file == null)
            return; 
          Path path = file.toPath().toAbsolutePath();
          try {
            DirBridge.move(path);
          } catch (IOException iOException) {
            errorHandle(iOException);
          } 
          this.application.runtimeSettings.updatesDirPath = path.toString();
          this.application.runtimeSettings.updatesDir = path;
          String str = DirBridge.dirUpdates.toString();
          DirBridge.dirUpdates = path;
          this.application.javaService.update();
          paramHyperlink.setText(this.application.runtimeSettings.updatesDirPath);
        });
    LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#deleteDir" }).ifPresent(paramButtonBase -> paramButtonBase.setOnAction(()));
    LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#back" }).ifPresent(paramButtonBase -> paramButtonBase.setOnAction(()));
    reset();
  }
  
  public void reset() {
    super.reset();
    RuntimeSettings.GlobalSettings globalSettings = this.application.runtimeSettings.globalSettings;
    add("PrismVSync", globalSettings.prismVSync, paramBoolean -> paramGlobalSettings.prismVSync = paramBoolean.booleanValue(), false);
    add("DebugAllClients", globalSettings.debugAllClients, paramBoolean -> paramGlobalSettings.debugAllClients = paramBoolean.booleanValue(), false);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\settings\GlobalSettingsScene.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
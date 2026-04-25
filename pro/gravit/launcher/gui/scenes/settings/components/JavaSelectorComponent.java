package pro.gravit.launcher.gui.scenes.settings.components;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.stage.PopupWindow;
import javafx.util.Callback;
import javafx.util.StringConverter;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.gui.config.RuntimeSettings;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.service.JavaService;
import pro.gravit.utils.helper.JavaHelper;
import pro.gravit.utils.helper.LogHelper;

public class JavaSelectorComponent {
  private final ComboBox<JavaHelper.JavaVersion> comboBox;
  
  private final RuntimeSettings.ProfileSettingsView profileSettings;
  
  private final ClientProfile profile;
  
  private final JavaService javaService;
  
  public JavaSelectorComponent(JavaService paramJavaService, Pane paramPane, RuntimeSettings.ProfileSettingsView paramProfileSettingsView, ClientProfile paramClientProfile) {
    this.comboBox = (ComboBox<JavaHelper.JavaVersion>)LookupHelper.lookup((Node)paramPane, new String[] { "#javaCombo" });
    this.profile = paramClientProfile;
    this.comboBox.getItems().clear();
    this.profileSettings = paramProfileSettingsView;
    this.javaService = paramJavaService;
    this.comboBox.setConverter(new JavaVersionConverter(paramClientProfile));
    this.comboBox.setCellFactory(new JavaVersionCellFactory(this.comboBox.getConverter()));
    reset();
  }
  
  public void reset() {
    boolean bool = true;
    for (JavaHelper.JavaVersion javaVersion : this.javaService.javaVersions) {
      if (this.javaService.isIncompatibleJava(javaVersion, this.profile))
        continue; 
      this.comboBox.getItems().add(javaVersion);
      if (this.profileSettings.javaPath != null && this.profileSettings.javaPath.equals(javaVersion.jvmDir.toString())) {
        this.comboBox.setValue(javaVersion);
        bool = false;
      } 
    } 
    if (this.comboBox.getTooltip() != null)
      this.comboBox.getTooltip().setText(this.profileSettings.javaPath); 
    if (bool) {
      JavaHelper.JavaVersion javaVersion = this.javaService.getRecommendJavaVersion(this.profile);
      if (javaVersion != null) {
        LogHelper.warning("Selected Java Version not found. Using %s", new Object[] { javaVersion.jvmDir.toAbsolutePath().toString() });
        this.comboBox.getSelectionModel().select(javaVersion);
        this.profileSettings.javaPath = javaVersion.jvmDir.toAbsolutePath().toString();
      } 
    } 
    this.comboBox.setOnAction(paramActionEvent -> {
          JavaHelper.JavaVersion javaVersion = (JavaHelper.JavaVersion)this.comboBox.getValue();
          if (javaVersion == null)
            return; 
          String str = javaVersion.jvmDir.toAbsolutePath().toString();
          if (this.comboBox.getTooltip() != null)
            this.comboBox.getTooltip().setText(str); 
          LogHelper.info("Select Java %s", new Object[] { str });
          this.profileSettings.javaPath = str;
        });
  }
  
  public String getPath() {
    return ((JavaHelper.JavaVersion)this.comboBox.getValue()).jvmDir.toAbsolutePath().toString();
  }
  
  private static class JavaVersionConverter extends StringConverter<JavaHelper.JavaVersion> {
    private final ClientProfile profile;
    
    private JavaVersionConverter(ClientProfile param1ClientProfile) {
      this.profile = param1ClientProfile;
    }
    
    public String toString(JavaHelper.JavaVersion param1JavaVersion) {
      if (param1JavaVersion == null)
        return "Unknown"; 
      String str = "";
      if (param1JavaVersion.version == this.profile.getRecommendJavaVersion())
        str = "[RECOMMENDED]"; 
      return "Java %d b%d %s".formatted(new Object[] { Integer.valueOf(param1JavaVersion.version), Integer.valueOf(param1JavaVersion.build), str });
    }
    
    public JavaHelper.JavaVersion fromString(String param1String) {
      return null;
    }
  }
  
  private static class JavaVersionCellFactory implements Callback<ListView<JavaHelper.JavaVersion>, ListCell<JavaHelper.JavaVersion>> {
    private final StringConverter<JavaHelper.JavaVersion> converter;
    
    public JavaVersionCellFactory(StringConverter<JavaHelper.JavaVersion> param1StringConverter) {
      this.converter = param1StringConverter;
    }
    
    public ListCell<JavaHelper.JavaVersion> call(ListView<JavaHelper.JavaVersion> param1ListView) {
      return new JavaSelectorComponent.JavaVersionListCell(this.converter);
    }
  }
  
  private static class JavaVersionListCell extends ListCell<JavaHelper.JavaVersion> {
    private final StringConverter<JavaHelper.JavaVersion> converter;
    
    public JavaVersionListCell(StringConverter<JavaHelper.JavaVersion> param1StringConverter) {
      this.converter = param1StringConverter;
    }
    
    protected void updateItem(JavaHelper.JavaVersion param1JavaVersion, boolean param1Boolean) {
      super.updateItem(param1JavaVersion, param1Boolean);
      if (param1Boolean || param1JavaVersion == null) {
        setText(null);
        setTooltip(null);
      } else {
        setText(this.converter.toString(param1JavaVersion));
        Tooltip tooltip = new Tooltip(param1JavaVersion.jvmDir.toString());
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);
        setTooltip(tooltip);
      } 
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\settings\components\JavaSelectorComponent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.gui.scenes.settings.components;

import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.config.RuntimeSettings;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.utils.helper.LogHelper;

public class ThemeSelectorComponent {
  private final JavaFXApplication application;
  
  private final ComboBox<RuntimeSettings.LAUNCHER_THEME> comboBox;
  
  public ThemeSelectorComponent(JavaFXApplication paramJavaFXApplication, Pane paramPane) {
    this.application = paramJavaFXApplication;
    this.comboBox = (ComboBox<RuntimeSettings.LAUNCHER_THEME>)LookupHelper.lookup((Node)paramPane, new String[] { "#themeCombo" });
    this.comboBox.getItems().clear();
    this.comboBox.setConverter(new ThemeConverter());
    if (paramJavaFXApplication.isThemeSupport()) {
      for (RuntimeSettings.LAUNCHER_THEME lAUNCHER_THEME : RuntimeSettings.LAUNCHER_THEME.values())
        this.comboBox.getItems().add(lAUNCHER_THEME); 
    } else {
      this.comboBox.getItems().add(RuntimeSettings.LAUNCHER_THEME.COMMON);
    } 
    this.comboBox.getSelectionModel().select(Objects.<RuntimeSettings.LAUNCHER_THEME>requireNonNullElse(paramJavaFXApplication.runtimeSettings.theme, RuntimeSettings.LAUNCHER_THEME.COMMON));
    this.comboBox.setOnAction(paramActionEvent -> {
          RuntimeSettings.LAUNCHER_THEME lAUNCHER_THEME = (RuntimeSettings.LAUNCHER_THEME)this.comboBox.getValue();
          if (lAUNCHER_THEME == null || (lAUNCHER_THEME == RuntimeSettings.LAUNCHER_THEME.COMMON && paramJavaFXApplication.runtimeSettings.theme == null))
            return; 
          if (lAUNCHER_THEME == paramJavaFXApplication.runtimeSettings.theme)
            return; 
          paramJavaFXApplication.runtimeSettings.theme = lAUNCHER_THEME;
          try {
            paramJavaFXApplication.gui.reload();
          } catch (Exception exception) {
            LogHelper.error(exception);
          } 
        });
  }
  
  private class ThemeConverter extends StringConverter<RuntimeSettings.LAUNCHER_THEME> {
    public String toString(RuntimeSettings.LAUNCHER_THEME param1LAUNCHER_THEME) {
      return (param1LAUNCHER_THEME == null) ? "Unknown" : ThemeSelectorComponent.this.application.getTranslation(String.format("runtime.themes.%s", new Object[] { param1LAUNCHER_THEME.displayName }), param1LAUNCHER_THEME.displayName);
    }
    
    public RuntimeSettings.LAUNCHER_THEME fromString(String param1String) {
      return null;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\settings\components\ThemeSelectorComponent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
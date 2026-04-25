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

public class LanguageSelectorComponent {
  private final JavaFXApplication application;
  
  private final ComboBox<RuntimeSettings.LAUNCHER_LOCALE> comboBox;
  
  public LanguageSelectorComponent(JavaFXApplication paramJavaFXApplication, Pane paramPane) {
    this.application = paramJavaFXApplication;
    this.comboBox = (ComboBox<RuntimeSettings.LAUNCHER_LOCALE>)LookupHelper.lookup((Node)paramPane, new String[] { "#languageCombo" });
    this.comboBox.getItems().clear();
    this.comboBox.setConverter(new ThemeConverter());
    for (RuntimeSettings.LAUNCHER_LOCALE lAUNCHER_LOCALE : RuntimeSettings.LAUNCHER_LOCALE.values())
      this.comboBox.getItems().add(lAUNCHER_LOCALE); 
    this.comboBox.getSelectionModel().select(Objects.<RuntimeSettings.LAUNCHER_LOCALE>requireNonNullElse(paramJavaFXApplication.runtimeSettings.locale, RuntimeSettings.LAUNCHER_LOCALE.ENGLISH));
    this.comboBox.setOnAction(paramActionEvent -> {
          RuntimeSettings.LAUNCHER_LOCALE lAUNCHER_LOCALE = (RuntimeSettings.LAUNCHER_LOCALE)this.comboBox.getValue();
          if (lAUNCHER_LOCALE == null)
            return; 
          if (lAUNCHER_LOCALE == paramJavaFXApplication.runtimeSettings.locale)
            return; 
          try {
            paramJavaFXApplication.updateLocaleResources(lAUNCHER_LOCALE.name);
            paramJavaFXApplication.runtimeSettings.locale = lAUNCHER_LOCALE;
            paramJavaFXApplication.gui.reload();
          } catch (Exception exception) {
            LogHelper.error(exception);
          } 
        });
  }
  
  private class ThemeConverter extends StringConverter<RuntimeSettings.LAUNCHER_LOCALE> {
    public String toString(RuntimeSettings.LAUNCHER_LOCALE param1LAUNCHER_LOCALE) {
      return (param1LAUNCHER_LOCALE == null) ? "Unknown" : LanguageSelectorComponent.this.application.getTranslation(String.format("runtime.themes.%s", new Object[] { param1LAUNCHER_LOCALE.displayName }), param1LAUNCHER_LOCALE.displayName);
    }
    
    public RuntimeSettings.LAUNCHER_LOCALE fromString(String param1String) {
      return null;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\settings\components\LanguageSelectorComponent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
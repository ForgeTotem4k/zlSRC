package pro.gravit.launcher.gui.scenes.settings.components;

import javafx.util.StringConverter;
import pro.gravit.launcher.gui.config.RuntimeSettings;

class ThemeConverter extends StringConverter<RuntimeSettings.LAUNCHER_THEME> {
  public String toString(RuntimeSettings.LAUNCHER_THEME paramLAUNCHER_THEME) {
    return (paramLAUNCHER_THEME == null) ? "Unknown" : ThemeSelectorComponent.this.application.getTranslation(String.format("runtime.themes.%s", new Object[] { paramLAUNCHER_THEME.displayName }), paramLAUNCHER_THEME.displayName);
  }
  
  public RuntimeSettings.LAUNCHER_THEME fromString(String paramString) {
    return null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\settings\components\ThemeSelectorComponent$ThemeConverter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
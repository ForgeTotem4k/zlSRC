package pro.gravit.launcher.gui.scenes.settings.components;

import javafx.util.StringConverter;
import pro.gravit.launcher.gui.config.RuntimeSettings;

class ThemeConverter extends StringConverter<RuntimeSettings.LAUNCHER_LOCALE> {
  public String toString(RuntimeSettings.LAUNCHER_LOCALE paramLAUNCHER_LOCALE) {
    return (paramLAUNCHER_LOCALE == null) ? "Unknown" : LanguageSelectorComponent.this.application.getTranslation(String.format("runtime.themes.%s", new Object[] { paramLAUNCHER_LOCALE.displayName }), paramLAUNCHER_LOCALE.displayName);
  }
  
  public RuntimeSettings.LAUNCHER_LOCALE fromString(String paramString) {
    return null;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\settings\components\LanguageSelectorComponent$ThemeConverter.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
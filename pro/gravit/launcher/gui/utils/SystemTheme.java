package pro.gravit.launcher.gui.utils;

import javafx.application.ColorScheme;
import javafx.application.Platform;
import pro.gravit.launcher.gui.config.RuntimeSettings;

public class SystemTheme {
  public static RuntimeSettings.LAUNCHER_THEME getSystemTheme() {
    return (Platform.getPreferences().getColorScheme() == ColorScheme.DARK) ? RuntimeSettings.LAUNCHER_THEME.DARK : RuntimeSettings.LAUNCHER_THEME.COMMON;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gu\\utils\SystemTheme.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
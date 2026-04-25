package pro.gravit.launcher.gui.config;

import pro.gravit.launcher.runtime.NewLauncherSettings;
import pro.gravit.launcher.runtime.managers.SettingsManager;

public class StdSettingsManager extends SettingsManager {
  public NewLauncherSettings getDefaultConfig() {
    NewLauncherSettings newLauncherSettings = new NewLauncherSettings();
    newLauncherSettings.userSettings.put("stdruntime", RuntimeSettings.getDefault(new GuiModuleConfig()));
    return newLauncherSettings;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\config\StdSettingsManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
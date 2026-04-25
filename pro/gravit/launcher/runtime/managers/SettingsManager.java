package pro.gravit.launcher.runtime.managers;

import pro.gravit.launcher.base.config.JsonConfigurable;
import pro.gravit.launcher.runtime.NewLauncherSettings;
import pro.gravit.launcher.runtime.client.DirBridge;
import pro.gravit.utils.helper.LogHelper;

public class SettingsManager extends JsonConfigurable<NewLauncherSettings> {
  public static NewLauncherSettings settings;
  
  public SettingsManager() {
    super(NewLauncherSettings.class, DirBridge.dir.resolve("settings.json"));
  }
  
  public NewLauncherSettings getConfig() {
    return settings;
  }
  
  public void setConfig(NewLauncherSettings paramNewLauncherSettings) {
    settings = paramNewLauncherSettings;
    if (settings.consoleUnlockKey != null && !ConsoleManager.isConsoleUnlock && ConsoleManager.checkUnlockKey(settings.consoleUnlockKey)) {
      ConsoleManager.unlock();
      LogHelper.info("Console auto unlocked");
    } 
  }
  
  public NewLauncherSettings getDefaultConfig() {
    return new NewLauncherSettings();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\managers\SettingsManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
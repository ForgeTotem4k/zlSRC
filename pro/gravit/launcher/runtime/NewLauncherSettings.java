package pro.gravit.launcher.runtime;

import java.util.HashMap;
import java.util.Map;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.launcher.runtime.client.UserSettings;

public class NewLauncherSettings {
  @LauncherNetworkAPI
  public Map<String, UserSettings> userSettings = new HashMap<>();
  
  @LauncherNetworkAPI
  public String consoleUnlockKey;
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\NewLauncherSettings.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
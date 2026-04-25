package pro.gravit.launcher.gui.config;

import pro.gravit.launcher.core.LauncherNetworkAPI;

public enum LAUNCHER_THEME {
  COMMON(null, "default"),
  DARK("dark", "dark");
  
  public final String name;
  
  public final String displayName;
  
  LAUNCHER_THEME(String paramString1, String paramString2) {
    this.name = paramString1;
    this.displayName = paramString2;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\config\RuntimeSettings$LAUNCHER_THEME.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
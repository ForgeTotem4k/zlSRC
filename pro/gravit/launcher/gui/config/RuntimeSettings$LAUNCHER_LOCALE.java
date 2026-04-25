package pro.gravit.launcher.gui.config;

import pro.gravit.launcher.core.LauncherNetworkAPI;

public enum LAUNCHER_LOCALE {
  RUSSIAN("ru", "Русский"),
  BELARUSIAN("be", "Беларуская"),
  UKRAINIAN("uk", "Українська"),
  POLISH("pl", "Polska"),
  ENGLISH("en", "English");
  
  public final String name;
  
  public final String displayName;
  
  LAUNCHER_LOCALE(String paramString1, String paramString2) {
    this.name = paramString1;
    this.displayName = paramString2;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\config\RuntimeSettings$LAUNCHER_LOCALE.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
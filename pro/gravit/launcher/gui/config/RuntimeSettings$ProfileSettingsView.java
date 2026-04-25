package pro.gravit.launcher.gui.config;

public class ProfileSettingsView {
  private final transient RuntimeSettings.ProfileSettings settings;
  
  public int ram;
  
  public boolean debug;
  
  public boolean fullScreen;
  
  public boolean autoEnter;
  
  public String javaPath;
  
  public boolean waylandSupport;
  
  public boolean debugSkipUpdate;
  
  public boolean debugSkipFileMonitor;
  
  public ProfileSettingsView(RuntimeSettings.ProfileSettings paramProfileSettings) {
    this.ram = paramProfileSettings.ram;
    this.debug = paramProfileSettings.debug;
    this.fullScreen = paramProfileSettings.fullScreen;
    this.autoEnter = paramProfileSettings.autoEnter;
    this.javaPath = paramProfileSettings.javaPath;
    this.waylandSupport = paramProfileSettings.waylandSupport;
    this.debugSkipUpdate = paramProfileSettings.debugSkipUpdate;
    this.debugSkipFileMonitor = paramProfileSettings.debugSkipFileMonitor;
    this.settings = paramProfileSettings;
  }
  
  public void apply() {
    this.settings.ram = this.ram;
    this.settings.debug = this.debug;
    this.settings.autoEnter = this.autoEnter;
    this.settings.fullScreen = this.fullScreen;
    this.settings.javaPath = this.javaPath;
    this.settings.waylandSupport = this.waylandSupport;
    this.settings.debugSkipUpdate = this.debugSkipUpdate;
    this.settings.debugSkipFileMonitor = this.debugSkipFileMonitor;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\config\RuntimeSettings$ProfileSettingsView.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
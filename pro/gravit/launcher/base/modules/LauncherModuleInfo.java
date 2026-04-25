package pro.gravit.launcher.base.modules;

import pro.gravit.utils.Version;

public class LauncherModuleInfo {
  public final String name;
  
  public final Version version;
  
  public final int priority;
  
  public final String[] dependencies;
  
  public final String[] providers;
  
  public LauncherModuleInfo(String paramString, Version paramVersion) {
    this.name = paramString;
    this.version = paramVersion;
    this.priority = 0;
    this.dependencies = new String[0];
    this.providers = new String[0];
  }
  
  public LauncherModuleInfo(String paramString) {
    this.name = paramString;
    this.version = new Version(1, 0, 0);
    this.priority = 0;
    this.dependencies = new String[0];
    this.providers = new String[0];
  }
  
  public LauncherModuleInfo(String paramString, Version paramVersion, String[] paramArrayOfString) {
    this.name = paramString;
    this.version = paramVersion;
    this.priority = 0;
    this.dependencies = paramArrayOfString;
    this.providers = new String[0];
  }
  
  public LauncherModuleInfo(String paramString, Version paramVersion, int paramInt, String[] paramArrayOfString) {
    this.name = paramString;
    this.version = paramVersion;
    this.priority = paramInt;
    this.dependencies = paramArrayOfString;
    this.providers = new String[0];
  }
  
  public LauncherModuleInfo(String paramString, Version paramVersion, int paramInt, String[] paramArrayOfString1, String[] paramArrayOfString2) {
    this.name = paramString;
    this.version = paramVersion;
    this.priority = paramInt;
    this.dependencies = paramArrayOfString1;
    this.providers = paramArrayOfString2;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\LauncherModuleInfo.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
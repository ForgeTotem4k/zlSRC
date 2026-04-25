package pro.gravit.launcher.client;

import pro.gravit.launcher.base.modules.LauncherInitContext;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.modules.LauncherModuleInfo;
import pro.gravit.utils.Version;

public class ClientLauncherCoreModule extends LauncherModule {
  public ClientLauncherCoreModule() {
    super(new LauncherModuleInfo("ClientLauncherCore", Version.getVersion()));
  }
  
  public void init(LauncherInitContext paramLauncherInitContext) {}
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\ClientLauncherCoreModule.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
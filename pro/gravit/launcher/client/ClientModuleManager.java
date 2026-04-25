package pro.gravit.launcher.client;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.modules.impl.SimpleModuleManager;
import pro.gravit.launcher.core.LauncherTrustManager;

public final class ClientModuleManager extends SimpleModuleManager {
  public ClientModuleManager() {
    super(null, null, (Launcher.getConfig()).trustManager);
  }
  
  public void autoload() {
    throw new UnsupportedOperationException();
  }
  
  public void autoload(Path paramPath) {
    throw new UnsupportedOperationException();
  }
  
  public LauncherModule loadModule(LauncherModule paramLauncherModule) {
    return super.loadModule(paramLauncherModule);
  }
  
  public List<LauncherModule> getModules() {
    return Collections.unmodifiableList(this.modules);
  }
  
  protected SimpleModuleManager.ModulesClassLoader createClassLoader() {
    return null;
  }
  
  public boolean verifyClassCheckResult(LauncherTrustManager.CheckClassResult paramCheckClassResult) {
    return (paramCheckClassResult.type == LauncherTrustManager.CheckClassResultType.SUCCESS);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\ClientModuleManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
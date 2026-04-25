package pro.gravit.launcher.start;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.modules.impl.SimpleModuleManager;
import pro.gravit.launcher.core.LauncherTrustManager;

public final class RuntimeModuleManager extends SimpleModuleManager {
  public RuntimeModuleManager() {
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
  
  public boolean verifyClassCheckResult(LauncherTrustManager.CheckClassResult paramCheckClassResult) {
    return (paramCheckClassResult.type == LauncherTrustManager.CheckClassResultType.SUCCESS);
  }
  
  protected SimpleModuleManager.ModulesClassLoader createClassLoader() {
    return null;
  }
  
  public void callWrapper(ClientLauncherWrapper.ClientLauncherWrapperContext paramClientLauncherWrapperContext) {
    for (LauncherModule launcherModule : this.modules) {
      if (launcherModule instanceof ClientWrapperModule)
        ((ClientWrapperModule)launcherModule).wrapperPhase(paramClientLauncherWrapperContext); 
    } 
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\start\RuntimeModuleManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
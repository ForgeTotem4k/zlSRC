package pro.gravit.launcher.runtime.console;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.modules.LauncherModuleInfo;
import pro.gravit.launcher.core.LauncherTrustManager;
import pro.gravit.launcher.runtime.LauncherEngine;
import pro.gravit.launcher.runtime.managers.ConsoleManager;
import pro.gravit.utils.command.Command;
import pro.gravit.utils.helper.LogHelper;

public class ModulesCommand extends Command {
  public String getArgsDescription() {
    return "[]";
  }
  
  public String getUsageDescription() {
    return "show modules";
  }
  
  public void invoke(String... paramVarArgs) {
    for (LauncherModule launcherModule : LauncherEngine.modulesManager.getModules()) {
      LauncherModuleInfo launcherModuleInfo = launcherModule.getModuleInfo();
      LauncherTrustManager.CheckClassResult checkClassResult = launcherModule.getCheckResult();
      if (!ConsoleManager.isConsoleUnlock) {
        LogHelper.info("[MODULE] %s v: %s", new Object[] { launcherModuleInfo.name, launcherModuleInfo.version.getVersionString() });
        continue;
      } 
      LogHelper.info("[MODULE] %s v: %s p: %d deps: %s sig: %s", new Object[] { launcherModuleInfo.name, launcherModuleInfo.version.getVersionString(), Integer.valueOf(launcherModuleInfo.priority), Arrays.toString((Object[])launcherModuleInfo.dependencies), (checkClassResult == null) ? "null" : checkClassResult.type });
      printCheckStatusInfo(checkClassResult);
    } 
  }
  
  private void printCheckStatusInfo(LauncherTrustManager.CheckClassResult paramCheckClassResult) {
    if (paramCheckClassResult != null && paramCheckClassResult.endCertificate != null) {
      X509Certificate x509Certificate = paramCheckClassResult.endCertificate;
      LogHelper.info("[MODULE CERT] Module signer: %s", new Object[] { x509Certificate.getSubjectX500Principal().getName() });
    } 
    if (paramCheckClassResult != null && paramCheckClassResult.rootCertificate != null) {
      X509Certificate x509Certificate = paramCheckClassResult.rootCertificate;
      LogHelper.info("[MODULE CERT] Module signer CA: %s", new Object[] { x509Certificate.getSubjectX500Principal().getName() });
    } 
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\console\ModulesCommand.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
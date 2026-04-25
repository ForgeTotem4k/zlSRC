package pro.gravit.launcher.gui.helper;

import pro.gravit.utils.enfs.EnFS;
import pro.gravit.utils.helper.LogHelper;

class LauncherEnFsDebugOutput implements EnFS.DebugOutput {
  public void debug(String paramString) {
    LogHelper.debug(paramString);
  }
  
  public void debug(String paramString, Object... paramVarArgs) {
    LogHelper.debug(paramString, paramVarArgs);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\helper\EnFSHelper$LauncherEnFsDebugOutput.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.gui.service;

import pro.gravit.launcher.base.profiles.optional.OptionalFile;
import pro.gravit.launcher.base.profiles.optional.OptionalView;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class OptionalListEntryPair {
  @LauncherNetworkAPI
  public String name;
  
  @LauncherNetworkAPI
  public boolean mark;
  
  @LauncherNetworkAPI
  public OptionalView.OptionalFileInstallInfo installInfo;
  
  public OptionalListEntryPair(OptionalFile paramOptionalFile, boolean paramBoolean, OptionalView.OptionalFileInstallInfo paramOptionalFileInstallInfo) {
    this.name = paramOptionalFile.name;
    this.mark = paramBoolean;
    this.installInfo = paramOptionalFileInstallInfo;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\service\ProfilesService$OptionalListEntryPair.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
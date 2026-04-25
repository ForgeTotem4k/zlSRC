package pro.gravit.launcher.base.profiles.optional.triggers;

import pro.gravit.launcher.base.profiles.optional.OptionalFile;
import pro.gravit.utils.helper.JVMHelper;

public class ArchTrigger extends OptionalTrigger {
  public JVMHelper.ARCH arch;
  
  protected boolean isTriggered(OptionalFile paramOptionalFile, OptionalTriggerContext paramOptionalTriggerContext) {
    return ((paramOptionalTriggerContext.getJavaVersion()).arch == this.arch);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\optional\triggers\ArchTrigger.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.base.profiles.optional.triggers;

import pro.gravit.launcher.base.profiles.optional.OptionalFile;
import pro.gravit.utils.helper.JVMHelper;

public class OSTrigger extends OptionalTrigger {
  public JVMHelper.OS os;
  
  public OSTrigger(JVMHelper.OS paramOS) {
    this.os = paramOS;
  }
  
  public boolean isTriggered(OptionalFile paramOptionalFile, OptionalTriggerContext paramOptionalTriggerContext) {
    return (JVMHelper.OS_TYPE == this.os);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\optional\triggers\OSTrigger.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
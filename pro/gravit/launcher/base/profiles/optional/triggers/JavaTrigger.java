package pro.gravit.launcher.base.profiles.optional.triggers;

import pro.gravit.launcher.base.profiles.optional.OptionalFile;
import pro.gravit.utils.helper.JavaHelper;

public class JavaTrigger extends OptionalTrigger {
  public int minVersion;
  
  public int maxVersion;
  
  public boolean requireJavaFX;
  
  public JavaTrigger(int paramInt1, int paramInt2, boolean paramBoolean) {
    this.minVersion = paramInt1;
    this.maxVersion = paramInt2;
    this.requireJavaFX = paramBoolean;
  }
  
  public JavaTrigger(int paramInt1, int paramInt2) {
    this.minVersion = paramInt1;
    this.maxVersion = paramInt2;
    this.requireJavaFX = false;
  }
  
  public JavaTrigger() {
    this.minVersion = 8;
    this.maxVersion = 999;
    this.requireJavaFX = false;
  }
  
  public boolean isTriggered(OptionalFile paramOptionalFile, OptionalTriggerContext paramOptionalTriggerContext) {
    JavaHelper.JavaVersion javaVersion = paramOptionalTriggerContext.getJavaVersion();
    return (javaVersion.version < this.minVersion) ? false : ((javaVersion.version > this.maxVersion) ? false : ((!this.requireJavaFX || javaVersion.enabledJavaFX)));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\optional\triggers\JavaTrigger.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
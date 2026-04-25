package pro.gravit.launcher.base.profiles.optional.triggers;

import pro.gravit.launcher.base.profiles.optional.OptionalFile;
import pro.gravit.utils.ProviderMap;

public abstract class OptionalTrigger {
  public static ProviderMap<OptionalTrigger> providers = new ProviderMap("OptionalTriggers");
  
  private static boolean isRegisteredProviders = false;
  
  public boolean required;
  
  public boolean inverted;
  
  public static void registerProviders() {
    if (!isRegisteredProviders) {
      providers.register("java", JavaTrigger.class);
      providers.register("os", OSTrigger.class);
      providers.register("arch", ArchTrigger.class);
      isRegisteredProviders = true;
    } 
  }
  
  protected abstract boolean isTriggered(OptionalFile paramOptionalFile, OptionalTriggerContext paramOptionalTriggerContext);
  
  public boolean check(OptionalFile paramOptionalFile, OptionalTriggerContext paramOptionalTriggerContext) {
    boolean bool = isTriggered(paramOptionalFile, paramOptionalTriggerContext);
    if (this.inverted)
      bool = !bool; 
    return bool;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\profiles\optional\triggers\OptionalTrigger.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.base.modules.events;

import com.google.gson.GsonBuilder;
import pro.gravit.launcher.base.modules.LauncherModule;

public class PreGsonPhase extends LauncherModule.Event {
  public final GsonBuilder gsonBuilder;
  
  public PreGsonPhase(GsonBuilder paramGsonBuilder) {
    this.gsonBuilder = paramGsonBuilder;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\events\PreGsonPhase.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
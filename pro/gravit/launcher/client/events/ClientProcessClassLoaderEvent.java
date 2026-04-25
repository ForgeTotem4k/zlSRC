package pro.gravit.launcher.client.events;

import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.utils.launch.ClassLoaderControl;
import pro.gravit.utils.launch.Launch;

public class ClientProcessClassLoaderEvent extends LauncherModule.Event {
  public final Launch launch;
  
  public final ClassLoaderControl classLoaderControl;
  
  public final ClientProfile profile;
  
  public ClientProcessClassLoaderEvent(Launch paramLaunch, ClassLoaderControl paramClassLoaderControl, ClientProfile paramClientProfile) {
    this.launch = paramLaunch;
    this.classLoaderControl = paramClassLoaderControl;
    this.profile = paramClientProfile;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\events\ClientProcessClassLoaderEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
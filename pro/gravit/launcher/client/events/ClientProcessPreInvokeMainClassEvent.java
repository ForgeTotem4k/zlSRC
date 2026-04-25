package pro.gravit.launcher.client.events;

import java.util.Collection;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.client.ClientParams;

public class ClientProcessPreInvokeMainClassEvent extends LauncherModule.Event {
  public final ClientParams params;
  
  public final ClientProfile profile;
  
  public final Collection<String> args;
  
  public ClientProcessPreInvokeMainClassEvent(ClientParams paramClientParams, ClientProfile paramClientProfile, Collection<String> paramCollection) {
    this.params = paramClientParams;
    this.profile = paramClientProfile;
    this.args = paramCollection;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\events\ClientProcessPreInvokeMainClassEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
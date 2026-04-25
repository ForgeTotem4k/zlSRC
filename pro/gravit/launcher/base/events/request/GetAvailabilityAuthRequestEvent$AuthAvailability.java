package pro.gravit.launcher.base.events.request;

import java.util.List;
import java.util.Set;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class AuthAvailability {
  public final List<GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> details;
  
  @LauncherNetworkAPI
  public String name;
  
  @LauncherNetworkAPI
  public String displayName;
  
  @LauncherNetworkAPI
  public boolean visible;
  
  @LauncherNetworkAPI
  public Set<String> features;
  
  public AuthAvailability(List<GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> paramList, String paramString1, String paramString2, boolean paramBoolean, Set<String> paramSet) {
    this.details = paramList;
    this.name = paramString1;
    this.displayName = paramString2;
    this.visible = paramBoolean;
    this.features = paramSet;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\GetAvailabilityAuthRequestEvent$AuthAvailability.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.base.events.request;

import java.util.List;
import java.util.Set;
import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.utils.TypeSerializeInterface;

public class GetAvailabilityAuthRequestEvent extends RequestEvent {
  @LauncherNetworkAPI
  public final List<AuthAvailability> list;
  
  @LauncherNetworkAPI
  public final long features;
  
  public GetAvailabilityAuthRequestEvent(List<AuthAvailability> paramList) {
    this.list = paramList;
    this.features = ServerFeature.FEATURE_SUPPORT.val;
  }
  
  public GetAvailabilityAuthRequestEvent(List<AuthAvailability> paramList, long paramLong) {
    this.list = paramList;
    this.features = paramLong;
  }
  
  public String getType() {
    return "getAvailabilityAuth";
  }
  
  public enum ServerFeature {
    FEATURE_SUPPORT(1);
    
    public final int val;
    
    ServerFeature(int param1Int1) {
      this.val = param1Int1;
    }
  }
  
  public static class AuthAvailability {
    public final List<GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> details;
    
    @LauncherNetworkAPI
    public String name;
    
    @LauncherNetworkAPI
    public String displayName;
    
    @LauncherNetworkAPI
    public boolean visible;
    
    @LauncherNetworkAPI
    public Set<String> features;
    
    public AuthAvailability(List<GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> param1List, String param1String1, String param1String2, boolean param1Boolean, Set<String> param1Set) {
      this.details = param1List;
      this.name = param1String1;
      this.displayName = param1String2;
      this.visible = param1Boolean;
      this.features = param1Set;
    }
  }
  
  public static interface AuthAvailabilityDetails extends TypeSerializeInterface {
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\GetAvailabilityAuthRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
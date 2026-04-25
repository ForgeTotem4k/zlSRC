package pro.gravit.launcher.base.request.auth;

import java.util.UUID;
import pro.gravit.launcher.base.events.request.AdditionalDataRequestEvent;
import pro.gravit.launcher.base.request.Request;

public class AdditionalDataRequest extends Request<AdditionalDataRequestEvent> {
  public String username;
  
  public UUID uuid;
  
  public AdditionalDataRequest(String paramString) {
    this.username = paramString;
  }
  
  public AdditionalDataRequest(UUID paramUUID) {
    this.uuid = paramUUID;
  }
  
  public String getType() {
    return "additionalData";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\AdditionalDataRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
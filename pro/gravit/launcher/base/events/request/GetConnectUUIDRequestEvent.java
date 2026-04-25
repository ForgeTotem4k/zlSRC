package pro.gravit.launcher.base.events.request;

import java.util.UUID;
import pro.gravit.launcher.base.events.RequestEvent;

public class GetConnectUUIDRequestEvent extends RequestEvent {
  public UUID connectUUID;
  
  public int shardId;
  
  public GetConnectUUIDRequestEvent(UUID paramUUID, int paramInt) {
    this.connectUUID = paramUUID;
    this.shardId = paramInt;
  }
  
  public String getType() {
    return "getConnectUUID";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\GetConnectUUIDRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
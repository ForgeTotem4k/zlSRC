package pro.gravit.launcher.base.request.uuid;

import java.io.IOException;
import pro.gravit.launcher.base.events.request.BatchProfileByUsernameRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.websockets.WebSocketRequest;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.utils.helper.IOHelper;

public final class BatchProfileByUsernameRequest extends Request<BatchProfileByUsernameRequestEvent> implements WebSocketRequest {
  @LauncherNetworkAPI
  public final Entry[] list;
  
  public BatchProfileByUsernameRequest(String... paramVarArgs) throws IOException {
    this.list = new Entry[paramVarArgs.length];
    for (byte b = 0; b < paramVarArgs.length; b++) {
      this.list[b] = new Entry();
      (this.list[b]).client = "";
      (this.list[b]).username = paramVarArgs[b];
    } 
    IOHelper.verifyLength(paramVarArgs.length, 128);
  }
  
  public String getType() {
    return "batchProfileByUsername";
  }
  
  public static class Entry {
    @LauncherNetworkAPI
    String username;
    
    @LauncherNetworkAPI
    String client;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\reques\\uuid\BatchProfileByUsernameRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.base.request.websockets;

import pro.gravit.launcher.base.request.RequestException;

public interface RequestProcessor<T extends pro.gravit.launcher.base.request.WebSocketEvent, V extends WebSocketRequest> {
  T process(V paramV) throws RequestException;
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\websockets\OfflineRequestService$RequestProcessor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
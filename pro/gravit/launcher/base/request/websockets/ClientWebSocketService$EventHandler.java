package pro.gravit.launcher.base.request.websockets;

@FunctionalInterface
public interface EventHandler {
  <T extends pro.gravit.launcher.base.request.WebSocketEvent> boolean eventHandle(T paramT);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\websockets\ClientWebSocketService$EventHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
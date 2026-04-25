package pro.gravit.launcher.base.request;

@FunctionalInterface
public interface EventHandler {
  <T extends WebSocketEvent> boolean eventHandle(T paramT);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\RequestService$EventHandler.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.base.events.request;

import pro.gravit.launcher.base.events.RequestEvent;

public class ExitRequestEvent extends RequestEvent {
  public final ExitReason reason;
  
  public ExitRequestEvent(ExitReason paramExitReason) {
    this.reason = paramExitReason;
  }
  
  public String getType() {
    return "exit";
  }
  
  public enum ExitReason {
    SERVER, CLIENT, TIMEOUT, NO_EXIT;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\ExitRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
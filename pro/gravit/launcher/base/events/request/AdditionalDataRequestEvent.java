package pro.gravit.launcher.base.events.request;

import java.util.Map;
import pro.gravit.launcher.base.events.RequestEvent;

public class AdditionalDataRequestEvent extends RequestEvent {
  public Map<String, String> data;
  
  public AdditionalDataRequestEvent() {}
  
  public AdditionalDataRequestEvent(Map<String, String> paramMap) {
    this.data = paramMap;
  }
  
  public String getType() {
    return "additionalData";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\AdditionalDataRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
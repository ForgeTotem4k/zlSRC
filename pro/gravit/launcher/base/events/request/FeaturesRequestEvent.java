package pro.gravit.launcher.base.events.request;

import java.util.Map;
import pro.gravit.launcher.base.events.RequestEvent;

public class FeaturesRequestEvent extends RequestEvent {
  public Map<String, String> features;
  
  public FeaturesRequestEvent() {}
  
  public FeaturesRequestEvent(Map<String, String> paramMap) {
    this.features = paramMap;
  }
  
  public String getType() {
    return "features";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\FeaturesRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
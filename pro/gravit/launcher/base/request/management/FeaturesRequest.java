package pro.gravit.launcher.base.request.management;

import pro.gravit.launcher.base.events.request.FeaturesRequestEvent;
import pro.gravit.launcher.base.request.Request;

public class FeaturesRequest extends Request<FeaturesRequestEvent> {
  public String getType() {
    return "features";
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\management\FeaturesRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
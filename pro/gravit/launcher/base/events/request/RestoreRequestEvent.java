package pro.gravit.launcher.base.events.request;

import java.util.List;
import pro.gravit.launcher.base.events.RequestEvent;

public class RestoreRequestEvent extends RequestEvent {
  public CurrentUserRequestEvent.UserInfo userInfo;
  
  public List<String> invalidTokens;
  
  public RestoreRequestEvent() {}
  
  public RestoreRequestEvent(CurrentUserRequestEvent.UserInfo paramUserInfo) {
    this.userInfo = paramUserInfo;
  }
  
  public RestoreRequestEvent(CurrentUserRequestEvent.UserInfo paramUserInfo, List<String> paramList) {
    this.userInfo = paramUserInfo;
    this.invalidTokens = paramList;
  }
  
  public RestoreRequestEvent(List<String> paramList) {
    this.invalidTokens = paramList;
  }
  
  public String getType() {
    return "restore";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\RestoreRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
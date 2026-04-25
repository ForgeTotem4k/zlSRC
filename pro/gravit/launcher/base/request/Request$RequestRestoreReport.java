package pro.gravit.launcher.base.request;

import java.util.List;
import pro.gravit.launcher.base.events.request.CurrentUserRequestEvent;

public class RequestRestoreReport {
  public final boolean refreshed;
  
  public final List<String> invalidExtendedTokens;
  
  public final CurrentUserRequestEvent.UserInfo userInfo;
  
  public RequestRestoreReport(boolean paramBoolean, List<String> paramList, CurrentUserRequestEvent.UserInfo paramUserInfo) {
    this.refreshed = paramBoolean;
    this.invalidExtendedTokens = paramList;
    this.userInfo = paramUserInfo;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\Request$RequestRestoreReport.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
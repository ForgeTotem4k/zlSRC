package pro.gravit.launcher.base.request.auth;

import java.util.Map;
import pro.gravit.launcher.base.events.request.RestoreRequestEvent;
import pro.gravit.launcher.base.request.Request;

public class RestoreRequest extends Request<RestoreRequestEvent> {
  public String authId;
  
  public String accessToken;
  
  public Map<String, String> extended;
  
  public boolean needUserInfo;
  
  public RestoreRequest() {}
  
  public RestoreRequest(String paramString1, String paramString2, Map<String, String> paramMap, boolean paramBoolean) {
    this.authId = paramString1;
    this.accessToken = paramString2;
    this.extended = paramMap;
    this.needUserInfo = paramBoolean;
  }
  
  public String getType() {
    return "restore";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\auth\RestoreRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.base.events.request;

import pro.gravit.launcher.base.events.RequestEvent;

public class GetAssetUploadUrlRequestEvent extends RequestEvent {
  public static final String FEATURE_NAME = "assetupload";
  
  public String url;
  
  public AuthRequestEvent.OAuthRequestEvent token;
  
  public GetAssetUploadUrlRequestEvent() {}
  
  public GetAssetUploadUrlRequestEvent(String paramString, AuthRequestEvent.OAuthRequestEvent paramOAuthRequestEvent) {
    this.url = paramString;
    this.token = paramOAuthRequestEvent;
  }
  
  public String getType() {
    return "getAssetUploadUrl";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\GetAssetUploadUrlRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
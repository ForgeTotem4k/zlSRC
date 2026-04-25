package pro.gravit.launcher.base.request.cabinet;

import pro.gravit.launcher.base.events.request.GetAssetUploadUrlRequestEvent;
import pro.gravit.launcher.base.request.Request;

public class GetAssetUploadUrl extends Request<GetAssetUploadUrlRequestEvent> {
  public String name;
  
  public GetAssetUploadUrl() {}
  
  public GetAssetUploadUrl(String paramString) {
    this.name = paramString;
  }
  
  public String getType() {
    return "getAssetUploadUrl";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\cabinet\GetAssetUploadUrl.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
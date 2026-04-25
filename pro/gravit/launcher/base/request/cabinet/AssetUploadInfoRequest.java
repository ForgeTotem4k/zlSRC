package pro.gravit.launcher.base.request.cabinet;

import pro.gravit.launcher.base.events.request.AssetUploadInfoRequestEvent;
import pro.gravit.launcher.base.request.Request;

public class AssetUploadInfoRequest extends Request<AssetUploadInfoRequestEvent> {
  public String getType() {
    return "assetUploadInfo";
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\request\cabinet\AssetUploadInfoRequest.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
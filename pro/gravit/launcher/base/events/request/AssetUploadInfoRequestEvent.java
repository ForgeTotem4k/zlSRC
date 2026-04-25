package pro.gravit.launcher.base.events.request;

import java.util.Set;
import pro.gravit.launcher.base.events.RequestEvent;

public class AssetUploadInfoRequestEvent extends RequestEvent {
  public Set<String> available;
  
  public SlimSupportConf slimSupportConf;
  
  public AssetUploadInfoRequestEvent(Set<String> paramSet, SlimSupportConf paramSlimSupportConf) {
    this.available = paramSet;
    this.slimSupportConf = paramSlimSupportConf;
  }
  
  public String getType() {
    return "assetUploadInfo";
  }
  
  public enum SlimSupportConf {
    UNSUPPORTED, USER, SERVER;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\AssetUploadInfoRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
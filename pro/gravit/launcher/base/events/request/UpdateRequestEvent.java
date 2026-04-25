package pro.gravit.launcher.base.events.request;

import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.launcher.core.hasher.HashedDir;

public class UpdateRequestEvent extends RequestEvent {
  @LauncherNetworkAPI
  public final HashedDir hdir;
  
  @LauncherNetworkAPI
  public final boolean zip;
  
  @LauncherNetworkAPI
  public String url;
  
  @LauncherNetworkAPI
  public boolean fullDownload;
  
  public UpdateRequestEvent(HashedDir paramHashedDir) {
    this.hdir = paramHashedDir;
    this.zip = false;
  }
  
  public UpdateRequestEvent(HashedDir paramHashedDir, String paramString) {
    this.hdir = paramHashedDir;
    this.url = paramString;
    this.zip = false;
  }
  
  public UpdateRequestEvent(HashedDir paramHashedDir, String paramString, boolean paramBoolean) {
    this.hdir = paramHashedDir;
    this.url = paramString;
    this.zip = paramBoolean;
  }
  
  public String getType() {
    return "update";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\UpdateRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.base.events.request;

import java.util.UUID;
import pro.gravit.launcher.base.events.ExtendedTokenRequestEvent;
import pro.gravit.launcher.base.events.RequestEvent;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class LauncherRequestEvent extends RequestEvent implements ExtendedTokenRequestEvent {
  public static final String LAUNCHER_EXTENDED_TOKEN_NAME = "launcher";
  
  private static final UUID uuid = UUID.fromString("d54cc12a-4f59-4f23-9b10-f527fdd2e38f");
  
  @LauncherNetworkAPI
  public String url;
  
  @LauncherNetworkAPI
  public byte[] digest;
  
  @LauncherNetworkAPI
  public byte[] binary;
  
  @LauncherNetworkAPI
  public boolean needUpdate;
  
  public String launcherExtendedToken;
  
  public long launcherExtendedTokenExpire;
  
  public LauncherRequestEvent(boolean paramBoolean, String paramString) {
    this.needUpdate = paramBoolean;
    this.url = paramString;
  }
  
  public LauncherRequestEvent(boolean paramBoolean, byte[] paramArrayOfbyte) {
    this.needUpdate = paramBoolean;
    this.digest = paramArrayOfbyte;
  }
  
  public LauncherRequestEvent(boolean paramBoolean, String paramString1, String paramString2, long paramLong) {
    this.url = paramString1;
    this.needUpdate = paramBoolean;
    this.launcherExtendedToken = paramString2;
    this.launcherExtendedTokenExpire = paramLong;
  }
  
  public LauncherRequestEvent(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.binary = paramArrayOfbyte1;
    this.digest = paramArrayOfbyte2;
  }
  
  public String getType() {
    return "launcher";
  }
  
  public String getExtendedTokenName() {
    return "launcher";
  }
  
  public String getExtendedToken() {
    return this.launcherExtendedToken;
  }
  
  public long getExtendedTokenExpire() {
    return this.launcherExtendedTokenExpire;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\events\request\LauncherRequestEvent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
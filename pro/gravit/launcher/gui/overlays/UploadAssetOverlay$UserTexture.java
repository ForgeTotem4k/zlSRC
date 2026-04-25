package pro.gravit.launcher.gui.overlays;

import java.util.Map;
import pro.gravit.launcher.base.profiles.Texture;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.utils.helper.SecurityHelper;

public final class UserTexture extends Record {
  @LauncherNetworkAPI
  private final String url;
  
  @LauncherNetworkAPI
  private final String digest;
  
  @LauncherNetworkAPI
  private final Map<String, String> metadata;
  
  public UserTexture(String paramString1, String paramString2, Map<String, String> paramMap) {
    this.url = paramString1;
    this.digest = paramString2;
    this.metadata = paramMap;
  }
  
  Texture toLauncherTexture() {
    return new Texture(this.url, SecurityHelper.fromHex(this.digest), this.metadata);
  }
  
  public final String toString() {
    // Byte code:
    //   0: aload_0
    //   1: <illegal opcode> toString : (Lpro/gravit/launcher/gui/overlays/UploadAssetOverlay$UserTexture;)Ljava/lang/String;
    //   6: areturn
  }
  
  public final int hashCode() {
    // Byte code:
    //   0: aload_0
    //   1: <illegal opcode> hashCode : (Lpro/gravit/launcher/gui/overlays/UploadAssetOverlay$UserTexture;)I
    //   6: ireturn
  }
  
  public final boolean equals(Object paramObject) {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: <illegal opcode> equals : (Lpro/gravit/launcher/gui/overlays/UploadAssetOverlay$UserTexture;Ljava/lang/Object;)Z
    //   7: ireturn
  }
  
  @LauncherNetworkAPI
  public String url() {
    return this.url;
  }
  
  @LauncherNetworkAPI
  public String digest() {
    return this.digest;
  }
  
  @LauncherNetworkAPI
  public Map<String, String> metadata() {
    return this.metadata;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\overlays\UploadAssetOverlay$UserTexture.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
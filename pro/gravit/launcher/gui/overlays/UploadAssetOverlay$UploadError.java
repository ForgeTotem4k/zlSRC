package pro.gravit.launcher.gui.overlays;

import pro.gravit.launcher.core.LauncherNetworkAPI;

public final class UploadError extends Record {
  @LauncherNetworkAPI
  private final String error;
  
  public UploadError(String paramString) {
    this.error = paramString;
  }
  
  public final String toString() {
    // Byte code:
    //   0: aload_0
    //   1: <illegal opcode> toString : (Lpro/gravit/launcher/gui/overlays/UploadAssetOverlay$UploadError;)Ljava/lang/String;
    //   6: areturn
  }
  
  public final int hashCode() {
    // Byte code:
    //   0: aload_0
    //   1: <illegal opcode> hashCode : (Lpro/gravit/launcher/gui/overlays/UploadAssetOverlay$UploadError;)I
    //   6: ireturn
  }
  
  public final boolean equals(Object paramObject) {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: <illegal opcode> equals : (Lpro/gravit/launcher/gui/overlays/UploadAssetOverlay$UploadError;Ljava/lang/Object;)Z
    //   7: ireturn
  }
  
  @LauncherNetworkAPI
  public String error() {
    return this.error;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\overlays\UploadAssetOverlay$UploadError.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
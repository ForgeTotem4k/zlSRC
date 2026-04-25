package pro.gravit.launcher.gui.overlays;

import pro.gravit.launcher.core.LauncherNetworkAPI;

public final class AssetOptions {
  @LauncherNetworkAPI
  private final boolean modelSlim;
  
  public AssetOptions(boolean paramBoolean) {
    this.modelSlim = paramBoolean;
  }
  
  public boolean modelSlim() {
    return this.modelSlim;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\overlays\UploadAssetOverlay$AssetOptions.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
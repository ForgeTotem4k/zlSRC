package pro.gravit.launcher.gui.utils;

import java.util.Map;
import pro.gravit.launcher.core.LauncherNetworkAPI;

public class AssetIndex {
  @LauncherNetworkAPI
  public boolean virtual;
  
  @LauncherNetworkAPI
  public Map<String, AssetIndexHelper.AssetIndexObject> objects;
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gu\\utils\AssetIndexHelper$AssetIndex.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
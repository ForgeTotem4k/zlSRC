package pro.gravit.launcher.gui.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.core.LauncherNetworkAPI;
import pro.gravit.launcher.core.hasher.HashedDir;
import pro.gravit.launcher.core.hasher.HashedEntry;
import pro.gravit.utils.helper.IOHelper;

public class AssetIndexHelper {
  public static AssetIndex parse(Path paramPath) throws IOException {
    BufferedReader bufferedReader = IOHelper.newReader(paramPath);
    try {
      AssetIndex assetIndex = (AssetIndex)Launcher.gsonManager.gson.fromJson(bufferedReader, AssetIndex.class);
      if (bufferedReader != null)
        bufferedReader.close(); 
      return assetIndex;
    } catch (Throwable throwable) {
      if (bufferedReader != null)
        try {
          bufferedReader.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static void modifyHashedDir(AssetIndex paramAssetIndex, HashedDir paramHashedDir) {
    HashSet<String> hashSet = new HashSet();
    for (AssetIndexObject assetIndexObject : paramAssetIndex.objects.values())
      hashSet.add(assetIndexObject.hash); 
    HashedDir hashedDir = (HashedDir)paramHashedDir.getEntry("objects");
    ArrayList<String> arrayList = new ArrayList(16);
    for (Map.Entry entry : hashedDir.map().entrySet()) {
      if (((HashedEntry)entry.getValue()).getType() != HashedEntry.Type.DIR)
        continue; 
      HashedDir hashedDir1 = (HashedDir)entry.getValue();
      ArrayList<String> arrayList1 = new ArrayList(16);
      for (String str : hashedDir1.map().keySet()) {
        if (!hashSet.contains(str))
          arrayList1.add(str); 
      } 
      for (String str : arrayList1)
        hashedDir1.remove(str); 
      if (hashedDir1.map().isEmpty())
        arrayList.add((String)entry.getKey()); 
    } 
    for (String str : arrayList)
      hashedDir.remove(str); 
  }
  
  static {
  
  }
  
  public static class AssetIndex {
    @LauncherNetworkAPI
    public boolean virtual;
    
    @LauncherNetworkAPI
    public Map<String, AssetIndexHelper.AssetIndexObject> objects;
  }
  
  public static class AssetIndexObject {
    @LauncherNetworkAPI
    public String hash;
    
    @LauncherNetworkAPI
    public long size;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gu\\utils\AssetIndexHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
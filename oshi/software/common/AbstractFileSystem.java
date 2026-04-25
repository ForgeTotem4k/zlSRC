package oshi.software.common;

import java.util.Arrays;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.util.GlobalConfig;

@ThreadSafe
public abstract class AbstractFileSystem implements FileSystem {
  protected static final List<String> NETWORK_FS_TYPES = Arrays.asList(GlobalConfig.get("oshi.network.filesystem.types", "").split(","));
  
  protected static final List<String> PSEUDO_FS_TYPES = Arrays.asList(GlobalConfig.get("oshi.pseudo.filesystem.types", "").split(","));
  
  public List<OSFileStore> getFileStores() {
    return getFileStores(false);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\common\AbstractFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
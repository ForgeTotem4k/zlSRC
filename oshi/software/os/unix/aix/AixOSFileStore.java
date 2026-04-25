package oshi.software.os.unix.aix;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.common.AbstractOSFileStore;
import oshi.software.os.OSFileStore;

@ThreadSafe
public class AixOSFileStore extends AbstractOSFileStore {
  private String logicalVolume;
  
  private String description;
  
  private String fsType;
  
  private long freeSpace;
  
  private long usableSpace;
  
  private long totalSpace;
  
  private long freeInodes;
  
  private long totalInodes;
  
  public AixOSFileStore(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5) {
    super(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6);
    this.logicalVolume = paramString7;
    this.description = paramString8;
    this.fsType = paramString9;
    this.freeSpace = paramLong1;
    this.usableSpace = paramLong2;
    this.totalSpace = paramLong3;
    this.freeInodes = paramLong4;
    this.totalInodes = paramLong5;
  }
  
  public String getLogicalVolume() {
    return this.logicalVolume;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public String getType() {
    return this.fsType;
  }
  
  public long getFreeSpace() {
    return this.freeSpace;
  }
  
  public long getUsableSpace() {
    return this.usableSpace;
  }
  
  public long getTotalSpace() {
    return this.totalSpace;
  }
  
  public long getFreeInodes() {
    return this.freeInodes;
  }
  
  public long getTotalInodes() {
    return this.totalInodes;
  }
  
  public boolean updateAttributes() {
    for (OSFileStore oSFileStore : AixFileSystem.getFileStoreMatching(getName())) {
      if (getVolume().equals(oSFileStore.getVolume()) && getMount().equals(oSFileStore.getMount())) {
        this.logicalVolume = oSFileStore.getLogicalVolume();
        this.description = oSFileStore.getDescription();
        this.fsType = oSFileStore.getType();
        this.freeSpace = oSFileStore.getFreeSpace();
        this.usableSpace = oSFileStore.getUsableSpace();
        this.totalSpace = oSFileStore.getTotalSpace();
        this.freeInodes = oSFileStore.getFreeInodes();
        this.totalInodes = oSFileStore.getTotalInodes();
        return true;
      } 
    } 
    return false;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\o\\unix\aix\AixOSFileStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
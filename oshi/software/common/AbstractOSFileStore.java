package oshi.software.common;

import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.OSFileStore;

@ThreadSafe
public abstract class AbstractOSFileStore implements OSFileStore {
  private String name;
  
  private String volume;
  
  private String label;
  
  private String mount;
  
  private String options;
  
  private String uuid;
  
  protected AbstractOSFileStore(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6) {
    this.name = paramString1;
    this.volume = paramString2;
    this.label = paramString3;
    this.mount = paramString4;
    this.options = paramString5;
    this.uuid = paramString6;
  }
  
  protected AbstractOSFileStore() {}
  
  public String getName() {
    return this.name;
  }
  
  public String getVolume() {
    return this.volume;
  }
  
  public String getLabel() {
    return this.label;
  }
  
  public String getMount() {
    return this.mount;
  }
  
  public String getOptions() {
    return this.options;
  }
  
  public String getUUID() {
    return this.uuid;
  }
  
  public String toString() {
    return "OSFileStore [name=" + getName() + ", volume=" + getVolume() + ", label=" + getLabel() + ", logicalVolume=" + getLogicalVolume() + ", mount=" + getMount() + ", description=" + getDescription() + ", fsType=" + getType() + ", options=\"" + getOptions() + "\", uuid=" + getUUID() + ", freeSpace=" + getFreeSpace() + ", usableSpace=" + getUsableSpace() + ", totalSpace=" + getTotalSpace() + ", freeInodes=" + getFreeInodes() + ", totalInodes=" + getTotalInodes() + "]";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\software\common\AbstractOSFileStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
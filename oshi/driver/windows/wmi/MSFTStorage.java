package oshi.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.platform.windows.WmiQueryHandler;

@ThreadSafe
public final class MSFTStorage {
  private static final String STORAGE_NAMESPACE = "ROOT\\Microsoft\\Windows\\Storage";
  
  private static final String MSFT_STORAGE_POOL_WHERE_IS_PRIMORDIAL_FALSE = "MSFT_StoragePool WHERE IsPrimordial=FALSE";
  
  private static final String MSFT_STORAGE_POOL_TO_PHYSICAL_DISK = "MSFT_StoragePoolToPhysicalDisk";
  
  private static final String MSFT_PHYSICAL_DISK = "MSFT_PhysicalDisk";
  
  private static final String MSFT_VIRTUAL_DISK = "MSFT_VirtualDisk";
  
  public static WbemcliUtil.WmiResult<StoragePoolProperty> queryStoragePools(WmiQueryHandler paramWmiQueryHandler) {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("ROOT\\Microsoft\\Windows\\Storage", "MSFT_StoragePool WHERE IsPrimordial=FALSE", StoragePoolProperty.class);
    return paramWmiQueryHandler.queryWMI(wmiQuery, false);
  }
  
  public static WbemcliUtil.WmiResult<StoragePoolToPhysicalDiskProperty> queryStoragePoolPhysicalDisks(WmiQueryHandler paramWmiQueryHandler) {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("ROOT\\Microsoft\\Windows\\Storage", "MSFT_StoragePoolToPhysicalDisk", StoragePoolToPhysicalDiskProperty.class);
    return paramWmiQueryHandler.queryWMI(wmiQuery, false);
  }
  
  public static WbemcliUtil.WmiResult<PhysicalDiskProperty> queryPhysicalDisks(WmiQueryHandler paramWmiQueryHandler) {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("ROOT\\Microsoft\\Windows\\Storage", "MSFT_PhysicalDisk", PhysicalDiskProperty.class);
    return paramWmiQueryHandler.queryWMI(wmiQuery, false);
  }
  
  public static WbemcliUtil.WmiResult<VirtualDiskProperty> queryVirtualDisks(WmiQueryHandler paramWmiQueryHandler) {
    WbemcliUtil.WmiQuery wmiQuery = new WbemcliUtil.WmiQuery("ROOT\\Microsoft\\Windows\\Storage", "MSFT_VirtualDisk", VirtualDiskProperty.class);
    return paramWmiQueryHandler.queryWMI(wmiQuery, false);
  }
  
  public enum StoragePoolProperty {
    FRIENDLYNAME, OBJECTID;
  }
  
  public enum StoragePoolToPhysicalDiskProperty {
    STORAGEPOOL, PHYSICALDISK;
  }
  
  public enum PhysicalDiskProperty {
    FRIENDLYNAME, PHYSICALLOCATION, OBJECTID;
  }
  
  public enum VirtualDiskProperty {
    FRIENDLYNAME, OBJECTID;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\wmi\MSFTStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
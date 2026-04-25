package com.sun.jna.platform.linux;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.PointerType;

public interface Udev extends Library {
  public static final Udev INSTANCE = (Udev)Native.load("udev", Udev.class);
  
  UdevContext udev_new();
  
  UdevContext udev_ref(UdevContext paramUdevContext);
  
  UdevContext udev_unref(UdevContext paramUdevContext);
  
  UdevDevice udev_device_new_from_syspath(UdevContext paramUdevContext, String paramString);
  
  UdevEnumerate udev_enumerate_new(UdevContext paramUdevContext);
  
  UdevEnumerate udev_enumerate_ref(UdevEnumerate paramUdevEnumerate);
  
  UdevEnumerate udev_enumerate_unref(UdevEnumerate paramUdevEnumerate);
  
  int udev_enumerate_add_match_subsystem(UdevEnumerate paramUdevEnumerate, String paramString);
  
  int udev_enumerate_scan_devices(UdevEnumerate paramUdevEnumerate);
  
  UdevListEntry udev_enumerate_get_list_entry(UdevEnumerate paramUdevEnumerate);
  
  UdevListEntry udev_list_entry_get_next(UdevListEntry paramUdevListEntry);
  
  String udev_list_entry_get_name(UdevListEntry paramUdevListEntry);
  
  UdevDevice udev_device_ref(UdevDevice paramUdevDevice);
  
  UdevDevice udev_device_unref(UdevDevice paramUdevDevice);
  
  UdevDevice udev_device_get_parent(UdevDevice paramUdevDevice);
  
  UdevDevice udev_device_get_parent_with_subsystem_devtype(UdevDevice paramUdevDevice, String paramString1, String paramString2);
  
  String udev_device_get_syspath(UdevDevice paramUdevDevice);
  
  String udev_device_get_sysname(UdevDevice paramUdevDevice);
  
  String udev_device_get_devnode(UdevDevice paramUdevDevice);
  
  String udev_device_get_devtype(UdevDevice paramUdevDevice);
  
  String udev_device_get_subsystem(UdevDevice paramUdevDevice);
  
  String udev_device_get_sysattr_value(UdevDevice paramUdevDevice, String paramString);
  
  String udev_device_get_property_value(UdevDevice paramUdevDevice, String paramString);
  
  public static class UdevDevice extends PointerType {
    public UdevDevice ref() {
      return Udev.INSTANCE.udev_device_ref(this);
    }
    
    public void unref() {
      Udev.INSTANCE.udev_device_unref(this);
    }
    
    public UdevDevice getParent() {
      return Udev.INSTANCE.udev_device_get_parent(this);
    }
    
    public UdevDevice getParentWithSubsystemDevtype(String param1String1, String param1String2) {
      return Udev.INSTANCE.udev_device_get_parent_with_subsystem_devtype(this, param1String1, param1String2);
    }
    
    public String getSyspath() {
      return Udev.INSTANCE.udev_device_get_syspath(this);
    }
    
    public String getSysname() {
      return Udev.INSTANCE.udev_device_get_syspath(this);
    }
    
    public String getDevnode() {
      return Udev.INSTANCE.udev_device_get_devnode(this);
    }
    
    public String getDevtype() {
      return Udev.INSTANCE.udev_device_get_devtype(this);
    }
    
    public String getSubsystem() {
      return Udev.INSTANCE.udev_device_get_subsystem(this);
    }
    
    public String getSysattrValue(String param1String) {
      return Udev.INSTANCE.udev_device_get_sysattr_value(this, param1String);
    }
    
    public String getPropertyValue(String param1String) {
      return Udev.INSTANCE.udev_device_get_property_value(this, param1String);
    }
  }
  
  public static class UdevListEntry extends PointerType {
    public UdevListEntry getNext() {
      return Udev.INSTANCE.udev_list_entry_get_next(this);
    }
    
    public String getName() {
      return Udev.INSTANCE.udev_list_entry_get_name(this);
    }
  }
  
  public static class UdevEnumerate extends PointerType {
    public UdevEnumerate ref() {
      return Udev.INSTANCE.udev_enumerate_ref(this);
    }
    
    public void unref() {
      Udev.INSTANCE.udev_enumerate_unref(this);
    }
    
    public int addMatchSubsystem(String param1String) {
      return Udev.INSTANCE.udev_enumerate_add_match_subsystem(this, param1String);
    }
    
    public int scanDevices() {
      return Udev.INSTANCE.udev_enumerate_scan_devices(this);
    }
    
    public Udev.UdevListEntry getListEntry() {
      return Udev.INSTANCE.udev_enumerate_get_list_entry(this);
    }
  }
  
  public static class UdevContext extends PointerType {
    public UdevContext ref() {
      return Udev.INSTANCE.udev_ref(this);
    }
    
    public void unref() {
      Udev.INSTANCE.udev_unref(this);
    }
    
    public Udev.UdevEnumerate enumerateNew() {
      return Udev.INSTANCE.udev_enumerate_new(this);
    }
    
    public Udev.UdevDevice deviceNewFromSyspath(String param1String) {
      return Udev.INSTANCE.udev_device_new_from_syspath(this, param1String);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\linux\Udev.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
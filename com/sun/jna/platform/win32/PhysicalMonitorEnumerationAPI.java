package com.sun.jna.platform.win32;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

public interface PhysicalMonitorEnumerationAPI {
  public static final int PHYSICAL_MONITOR_DESCRIPTION_SIZE = 128;
  
  @FieldOrder({"hPhysicalMonitor", "szPhysicalMonitorDescription"})
  public static class PHYSICAL_MONITOR extends Structure {
    public WinNT.HANDLE hPhysicalMonitor;
    
    public char[] szPhysicalMonitorDescription = new char[128];
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\PhysicalMonitorEnumerationAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.EnumUtils;

public interface LowLevelMonitorConfigurationAPI {
  public enum MC_VCP_CODE_TYPE {
    MC_MOMENTARY, MC_SET_PARAMETER;
    
    public static class ByReference extends com.sun.jna.ptr.ByReference {
      public ByReference() {
        super(4);
      }
      
      public ByReference(LowLevelMonitorConfigurationAPI.MC_VCP_CODE_TYPE param2MC_VCP_CODE_TYPE) {
        super(4);
        setValue(param2MC_VCP_CODE_TYPE);
      }
      
      public void setValue(LowLevelMonitorConfigurationAPI.MC_VCP_CODE_TYPE param2MC_VCP_CODE_TYPE) {
        getPointer().setInt(0L, EnumUtils.toInteger(param2MC_VCP_CODE_TYPE));
      }
      
      public LowLevelMonitorConfigurationAPI.MC_VCP_CODE_TYPE getValue() {
        return (LowLevelMonitorConfigurationAPI.MC_VCP_CODE_TYPE)EnumUtils.fromInteger(getPointer().getInt(0L), LowLevelMonitorConfigurationAPI.MC_VCP_CODE_TYPE.class);
      }
    }
  }
  
  @FieldOrder({"dwHorizontalFrequencyInHZ", "dwVerticalFrequencyInHZ", "bTimingStatusByte"})
  public static class MC_TIMING_REPORT extends Structure {
    public WinDef.DWORD dwHorizontalFrequencyInHZ;
    
    public WinDef.DWORD dwVerticalFrequencyInHZ;
    
    public WinDef.BYTE bTimingStatusByte;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\win32\LowLevelMonitorConfigurationAPI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
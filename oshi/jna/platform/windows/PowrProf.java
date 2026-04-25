package oshi.jna.platform.windows;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.PowrProf;
import oshi.util.Util;

public interface PowrProf extends PowrProf {
  public static final PowrProf INSTANCE = (PowrProf)Native.load("PowrProf", PowrProf.class);
  
  @FieldOrder({"Day", "Month", "Year"})
  public static class BATTERY_MANUFACTURE_DATE extends Structure implements AutoCloseable {
    public byte Day;
    
    public byte Month;
    
    public short Year;
    
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  @FieldOrder({"PowerState", "Capacity", "Voltage", "Rate"})
  public static class BATTERY_STATUS extends Structure implements AutoCloseable {
    public int PowerState;
    
    public int Capacity;
    
    public int Voltage;
    
    public int Rate;
    
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  @FieldOrder({"BatteryTag", "Timeout", "PowerState", "LowCapacity", "HighCapacity"})
  public static class BATTERY_WAIT_STATUS extends Structure implements AutoCloseable {
    public int BatteryTag;
    
    public int Timeout;
    
    public int PowerState;
    
    public int LowCapacity;
    
    public int HighCapacity;
    
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  @FieldOrder({"Capabilities", "Technology", "Reserved", "Chemistry", "DesignedCapacity", "FullChargedCapacity", "DefaultAlert1", "DefaultAlert2", "CriticalBias", "CycleCount"})
  public static class BATTERY_INFORMATION extends Structure implements AutoCloseable {
    public int Capabilities;
    
    public byte Technology;
    
    public byte[] Reserved = new byte[3];
    
    public byte[] Chemistry = new byte[4];
    
    public int DesignedCapacity;
    
    public int FullChargedCapacity;
    
    public int DefaultAlert1;
    
    public int DefaultAlert2;
    
    public int CriticalBias;
    
    public int CycleCount;
    
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  public enum BATTERY_QUERY_INFORMATION_LEVEL {
    BatteryInformation, BatteryGranularityInformation, BatteryTemperature, BatteryEstimatedTime, BatteryDeviceName, BatteryManufactureDate, BatteryManufactureName, BatteryUniqueID, BatterySerialNumber;
  }
  
  @FieldOrder({"BatteryTag", "InformationLevel", "AtRate"})
  public static class BATTERY_QUERY_INFORMATION extends Structure implements AutoCloseable {
    public int BatteryTag;
    
    public int InformationLevel;
    
    public int AtRate;
    
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
  
  @FieldOrder({"number", "maxMhz", "currentMhz", "mhzLimit", "maxIdleState", "currentIdleState"})
  public static class ProcessorPowerInformation extends Structure {
    public int number;
    
    public int maxMhz;
    
    public int currentMhz;
    
    public int mhzLimit;
    
    public int maxIdleState;
    
    public int currentIdleState;
    
    public ProcessorPowerInformation(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public ProcessorPowerInformation() {}
  }
  
  @FieldOrder({"acOnLine", "batteryPresent", "charging", "discharging", "spare1", "tag", "maxCapacity", "remainingCapacity", "rate", "estimatedTime", "defaultAlert1", "defaultAlert2"})
  public static class SystemBatteryState extends Structure implements AutoCloseable {
    public byte acOnLine;
    
    public byte batteryPresent;
    
    public byte charging;
    
    public byte discharging;
    
    public byte[] spare1 = new byte[3];
    
    public byte tag;
    
    public int maxCapacity;
    
    public int remainingCapacity;
    
    public int rate;
    
    public int estimatedTime;
    
    public int defaultAlert1;
    
    public int defaultAlert2;
    
    public SystemBatteryState(Pointer param1Pointer) {
      super(param1Pointer);
      read();
    }
    
    public SystemBatteryState() {}
    
    public void close() {
      Util.freeMemory(getPointer());
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\jna\platform\windows\PowrProf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
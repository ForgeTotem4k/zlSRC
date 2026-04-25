package oshi.hardware.platform.windows;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APITypeMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.jna.ByRef;
import oshi.jna.Struct;
import oshi.jna.platform.windows.PowrProf;

@ThreadSafe
public final class WindowsPowerSource extends AbstractPowerSource {
  private static final Guid.GUID GUID_DEVCLASS_BATTERY = Guid.GUID.fromString("{72631E54-78A4-11D0-BCF7-00AA00B7B32A}");
  
  private static final int CHAR_WIDTH = (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) ? 2 : 1;
  
  private static final boolean X64 = Platform.is64Bit();
  
  private static final int BATTERY_SYSTEM_BATTERY = -2147483648;
  
  private static final int BATTERY_IS_SHORT_TERM = 536870912;
  
  private static final int BATTERY_POWER_ON_LINE = 1;
  
  private static final int BATTERY_DISCHARGING = 2;
  
  private static final int BATTERY_CHARGING = 4;
  
  private static final int BATTERY_CAPACITY_RELATIVE = 1073741824;
  
  private static final int IOCTL_BATTERY_QUERY_TAG = 2703424;
  
  private static final int IOCTL_BATTERY_QUERY_STATUS = 2703436;
  
  private static final int IOCTL_BATTERY_QUERY_INFORMATION = 2703428;
  
  public WindowsPowerSource(String paramString1, String paramString2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PowerSource.CapacityUnits paramCapacityUnits, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString3, LocalDate paramLocalDate, String paramString4, String paramString5, double paramDouble7) {
    super(paramString1, paramString2, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramBoolean1, paramBoolean2, paramBoolean3, paramCapacityUnits, paramInt1, paramInt2, paramInt3, paramInt4, paramString3, paramLocalDate, paramString4, paramString5, paramDouble7);
  }
  
  public static List<PowerSource> getPowerSources() {
    return Arrays.asList(new PowerSource[] { (PowerSource)getPowerSource("System Battery") });
  }
  
  private static WindowsPowerSource getPowerSource(String paramString) {
    String str1 = paramString;
    String str2 = "unknown";
    double d1 = 1.0D;
    double d2 = -1.0D;
    double d3 = 0.0D;
    int i = 0;
    double d4 = -1.0D;
    double d5 = 0.0D;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    PowerSource.CapacityUnits capacityUnits = PowerSource.CapacityUnits.RELATIVE;
    int j = 0;
    int k = 1;
    int m = 1;
    int n = -1;
    String str3 = "unknown";
    LocalDate localDate = null;
    String str4 = "unknown";
    String str5 = "unknown";
    double d6 = 0.0D;
    PowrProf.SystemBatteryState systemBatteryState = new PowrProf.SystemBatteryState();
    try {
      if (0 == PowrProf.INSTANCE.CallNtPowerInformation(5, null, 0, systemBatteryState.getPointer(), systemBatteryState.size()) && systemBatteryState.batteryPresent > 0) {
        if (systemBatteryState.acOnLine == 0 && systemBatteryState.charging == 0 && systemBatteryState.discharging > 0) {
          d2 = systemBatteryState.estimatedTime;
        } else if (systemBatteryState.charging > 0) {
          d2 = -2.0D;
        } 
        k = systemBatteryState.maxCapacity;
        j = systemBatteryState.remainingCapacity;
        d1 = Math.min(1.0D, j / k);
        i = systemBatteryState.rate;
      } 
      systemBatteryState.close();
    } catch (Throwable throwable) {
      try {
        systemBatteryState.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    WinNT.HANDLE hANDLE = SetupApi.INSTANCE.SetupDiGetClassDevs(GUID_DEVCLASS_BATTERY, null, null, 18);
    if (!WinBase.INVALID_HANDLE_VALUE.equals(hANDLE)) {
      boolean bool = false;
      for (byte b = 0; !bool && b < 100; b++) {
        Struct.CloseableSpDeviceInterfaceData closeableSpDeviceInterfaceData = new Struct.CloseableSpDeviceInterfaceData();
        try {
          ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
          try {
            ByRef.CloseableIntByReference closeableIntByReference1 = new ByRef.CloseableIntByReference();
            try {
              ByRef.CloseableIntByReference closeableIntByReference2 = new ByRef.CloseableIntByReference();
              try {
                ByRef.CloseableIntByReference closeableIntByReference3 = new ByRef.CloseableIntByReference();
                try {
                  closeableSpDeviceInterfaceData.cbSize = closeableSpDeviceInterfaceData.size();
                  if (SetupApi.INSTANCE.SetupDiEnumDeviceInterfaces(hANDLE, null, GUID_DEVCLASS_BATTERY, b, (SetupApi.SP_DEVICE_INTERFACE_DATA)closeableSpDeviceInterfaceData)) {
                    SetupApi.INSTANCE.SetupDiGetDeviceInterfaceDetail(hANDLE, (SetupApi.SP_DEVICE_INTERFACE_DATA)closeableSpDeviceInterfaceData, null, 0, (IntByReference)closeableIntByReference, null);
                    if (122 == Kernel32.INSTANCE.GetLastError()) {
                      Memory memory = new Memory(closeableIntByReference.getValue());
                      try {
                        memory.setInt(0L, 4 + (X64 ? 4 : CHAR_WIDTH));
                        if (SetupApi.INSTANCE.SetupDiGetDeviceInterfaceDetail(hANDLE, (SetupApi.SP_DEVICE_INTERFACE_DATA)closeableSpDeviceInterfaceData, (Pointer)memory, (int)memory.size(), (IntByReference)closeableIntByReference, null)) {
                          String str = (CHAR_WIDTH > 1) ? memory.getWideString(4L) : memory.getString(4L);
                          WinNT.HANDLE hANDLE1 = Kernel32.INSTANCE.CreateFile(str, -1073741824, 3, null, 3, 128, null);
                          if (!WinBase.INVALID_HANDLE_VALUE.equals(hANDLE1)) {
                            PowrProf.BATTERY_QUERY_INFORMATION bATTERY_QUERY_INFORMATION = new PowrProf.BATTERY_QUERY_INFORMATION();
                            try {
                              PowrProf.BATTERY_INFORMATION bATTERY_INFORMATION = new PowrProf.BATTERY_INFORMATION();
                              try {
                                PowrProf.BATTERY_WAIT_STATUS bATTERY_WAIT_STATUS = new PowrProf.BATTERY_WAIT_STATUS();
                                try {
                                  PowrProf.BATTERY_STATUS bATTERY_STATUS = new PowrProf.BATTERY_STATUS();
                                  try {
                                    PowrProf.BATTERY_MANUFACTURE_DATE bATTERY_MANUFACTURE_DATE = new PowrProf.BATTERY_MANUFACTURE_DATE();
                                    try {
                                      if (Kernel32.INSTANCE.DeviceIoControl(hANDLE1, 2703424, closeableIntByReference1.getPointer(), 4, closeableIntByReference2.getPointer(), 4, (IntByReference)closeableIntByReference3, null)) {
                                        bATTERY_QUERY_INFORMATION.BatteryTag = closeableIntByReference2.getValue();
                                        if (bATTERY_QUERY_INFORMATION.BatteryTag > 0) {
                                          bATTERY_QUERY_INFORMATION.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryInformation.ordinal();
                                          bATTERY_QUERY_INFORMATION.write();
                                          if (Kernel32.INSTANCE.DeviceIoControl(hANDLE1, 2703428, bATTERY_QUERY_INFORMATION.getPointer(), bATTERY_QUERY_INFORMATION.size(), bATTERY_INFORMATION.getPointer(), bATTERY_INFORMATION.size(), (IntByReference)closeableIntByReference3, null)) {
                                            bATTERY_INFORMATION.read();
                                            if (0 != (bATTERY_INFORMATION.Capabilities & Integer.MIN_VALUE) && 0 == (bATTERY_INFORMATION.Capabilities & 0x20000000)) {
                                              if (0 == (bATTERY_INFORMATION.Capabilities & 0x40000000))
                                                capacityUnits = PowerSource.CapacityUnits.MWH; 
                                              str3 = Native.toString(bATTERY_INFORMATION.Chemistry, StandardCharsets.US_ASCII);
                                              m = bATTERY_INFORMATION.DesignedCapacity;
                                              k = bATTERY_INFORMATION.FullChargedCapacity;
                                              n = bATTERY_INFORMATION.CycleCount;
                                              bATTERY_WAIT_STATUS.BatteryTag = bATTERY_QUERY_INFORMATION.BatteryTag;
                                              bATTERY_WAIT_STATUS.write();
                                              if (Kernel32.INSTANCE.DeviceIoControl(hANDLE1, 2703436, bATTERY_WAIT_STATUS.getPointer(), bATTERY_WAIT_STATUS.size(), bATTERY_STATUS.getPointer(), bATTERY_STATUS.size(), (IntByReference)closeableIntByReference3, null)) {
                                                bATTERY_STATUS.read();
                                                if (0 != (bATTERY_STATUS.PowerState & 0x1))
                                                  bool1 = true; 
                                                if (0 != (bATTERY_STATUS.PowerState & 0x2))
                                                  bool3 = true; 
                                                if (0 != (bATTERY_STATUS.PowerState & 0x4))
                                                  bool2 = true; 
                                                j = bATTERY_STATUS.Capacity;
                                                d4 = (bATTERY_STATUS.Voltage > 0) ? (bATTERY_STATUS.Voltage / 1000.0D) : bATTERY_STATUS.Voltage;
                                                i = bATTERY_STATUS.Rate;
                                                if (d4 > 0.0D)
                                                  d5 = i / d4; 
                                              } 
                                            } 
                                            str2 = batteryQueryString(hANDLE1, closeableIntByReference2.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryDeviceName.ordinal());
                                            str4 = batteryQueryString(hANDLE1, closeableIntByReference2.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryManufactureName.ordinal());
                                            str5 = batteryQueryString(hANDLE1, closeableIntByReference2.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatterySerialNumber.ordinal());
                                            bATTERY_QUERY_INFORMATION.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryManufactureDate.ordinal();
                                            bATTERY_QUERY_INFORMATION.write();
                                            if (Kernel32.INSTANCE.DeviceIoControl(hANDLE1, 2703428, bATTERY_QUERY_INFORMATION.getPointer(), bATTERY_QUERY_INFORMATION.size(), bATTERY_MANUFACTURE_DATE.getPointer(), bATTERY_MANUFACTURE_DATE.size(), (IntByReference)closeableIntByReference3, null)) {
                                              bATTERY_MANUFACTURE_DATE.read();
                                              if (bATTERY_MANUFACTURE_DATE.Year > 1900 && bATTERY_MANUFACTURE_DATE.Month > 0 && bATTERY_MANUFACTURE_DATE.Day > 0)
                                                localDate = LocalDate.of(bATTERY_MANUFACTURE_DATE.Year, bATTERY_MANUFACTURE_DATE.Month, bATTERY_MANUFACTURE_DATE.Day); 
                                            } 
                                            bATTERY_QUERY_INFORMATION.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryTemperature.ordinal();
                                            bATTERY_QUERY_INFORMATION.write();
                                            ByRef.CloseableIntByReference closeableIntByReference4 = new ByRef.CloseableIntByReference();
                                            try {
                                              if (Kernel32.INSTANCE.DeviceIoControl(hANDLE1, 2703428, bATTERY_QUERY_INFORMATION.getPointer(), bATTERY_QUERY_INFORMATION.size(), closeableIntByReference4.getPointer(), 4, (IntByReference)closeableIntByReference3, null))
                                                d6 = closeableIntByReference4.getValue() / 10.0D - 273.15D; 
                                              closeableIntByReference4.close();
                                            } catch (Throwable throwable) {
                                              try {
                                                closeableIntByReference4.close();
                                              } catch (Throwable throwable1) {
                                                throwable.addSuppressed(throwable1);
                                              } 
                                              throw throwable;
                                            } 
                                            bATTERY_QUERY_INFORMATION.InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryEstimatedTime.ordinal();
                                            if (i != 0)
                                              bATTERY_QUERY_INFORMATION.AtRate = i; 
                                            bATTERY_QUERY_INFORMATION.write();
                                            closeableIntByReference4 = new ByRef.CloseableIntByReference();
                                            try {
                                              if (Kernel32.INSTANCE.DeviceIoControl(hANDLE1, 2703428, bATTERY_QUERY_INFORMATION.getPointer(), bATTERY_QUERY_INFORMATION.size(), closeableIntByReference4.getPointer(), 4, (IntByReference)closeableIntByReference3, null))
                                                d3 = closeableIntByReference4.getValue(); 
                                              closeableIntByReference4.close();
                                            } catch (Throwable throwable) {
                                              try {
                                                closeableIntByReference4.close();
                                              } catch (Throwable throwable1) {
                                                throwable.addSuppressed(throwable1);
                                              } 
                                              throw throwable;
                                            } 
                                            if (d3 < 0.0D && i != 0) {
                                              d3 = (k - j) * 3600.0D / i;
                                              if (d3 < 0.0D)
                                                d3 *= -1.0D; 
                                            } 
                                            bool = true;
                                          } 
                                        } 
                                      } 
                                      bATTERY_MANUFACTURE_DATE.close();
                                    } catch (Throwable throwable) {
                                      try {
                                        bATTERY_MANUFACTURE_DATE.close();
                                      } catch (Throwable throwable1) {
                                        throwable.addSuppressed(throwable1);
                                      } 
                                      throw throwable;
                                    } 
                                    bATTERY_STATUS.close();
                                  } catch (Throwable throwable) {
                                    try {
                                      bATTERY_STATUS.close();
                                    } catch (Throwable throwable1) {
                                      throwable.addSuppressed(throwable1);
                                    } 
                                    throw throwable;
                                  } 
                                  bATTERY_WAIT_STATUS.close();
                                } catch (Throwable throwable) {
                                  try {
                                    bATTERY_WAIT_STATUS.close();
                                  } catch (Throwable throwable1) {
                                    throwable.addSuppressed(throwable1);
                                  } 
                                  throw throwable;
                                } 
                                bATTERY_INFORMATION.close();
                              } catch (Throwable throwable) {
                                try {
                                  bATTERY_INFORMATION.close();
                                } catch (Throwable throwable1) {
                                  throwable.addSuppressed(throwable1);
                                } 
                                throw throwable;
                              } 
                              bATTERY_QUERY_INFORMATION.close();
                            } catch (Throwable throwable) {
                              try {
                                bATTERY_QUERY_INFORMATION.close();
                              } catch (Throwable throwable1) {
                                throwable.addSuppressed(throwable1);
                              } 
                              throw throwable;
                            } 
                            Kernel32.INSTANCE.CloseHandle(hANDLE1);
                          } 
                        } 
                        memory.close();
                      } catch (Throwable throwable) {
                        try {
                          memory.close();
                        } catch (Throwable throwable1) {
                          throwable.addSuppressed(throwable1);
                        } 
                        throw throwable;
                      } 
                    } 
                  } else if (259 == Kernel32.INSTANCE.GetLastError()) {
                    closeableIntByReference3.close();
                    closeableIntByReference2.close();
                    closeableIntByReference1.close();
                    closeableIntByReference.close();
                    closeableSpDeviceInterfaceData.close();
                    break;
                  } 
                  closeableIntByReference3.close();
                } catch (Throwable throwable) {
                  try {
                    closeableIntByReference3.close();
                  } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                  } 
                  throw throwable;
                } 
                closeableIntByReference2.close();
              } catch (Throwable throwable) {
                try {
                  closeableIntByReference2.close();
                } catch (Throwable throwable1) {
                  throwable.addSuppressed(throwable1);
                } 
                throw throwable;
              } 
              closeableIntByReference1.close();
            } catch (Throwable throwable) {
              try {
                closeableIntByReference1.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              } 
              throw throwable;
            } 
            closeableIntByReference.close();
          } catch (Throwable throwable) {
            try {
              closeableIntByReference.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
          closeableSpDeviceInterfaceData.close();
        } catch (Throwable throwable) {
          try {
            closeableSpDeviceInterfaceData.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } 
      SetupApi.INSTANCE.SetupDiDestroyDeviceInfoList(hANDLE);
    } 
    return new WindowsPowerSource(str1, str2, d1, d2, d3, i, d4, d5, bool1, bool2, bool3, capacityUnits, j, k, m, n, str3, localDate, str4, str5, d6);
  }
  
  private static String batteryQueryString(WinNT.HANDLE paramHANDLE, int paramInt1, int paramInt2) {
    PowrProf.BATTERY_QUERY_INFORMATION bATTERY_QUERY_INFORMATION = new PowrProf.BATTERY_QUERY_INFORMATION();
    try {
      ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
      try {
        bATTERY_QUERY_INFORMATION.BatteryTag = paramInt1;
        bATTERY_QUERY_INFORMATION.InformationLevel = paramInt2;
        bATTERY_QUERY_INFORMATION.write();
        boolean bool = false;
        long l = 256L;
        Memory memory = new Memory(l);
        while (true) {
          bool = Kernel32.INSTANCE.DeviceIoControl(paramHANDLE, 2703428, bATTERY_QUERY_INFORMATION.getPointer(), bATTERY_QUERY_INFORMATION.size(), (Pointer)memory, (int)memory.size(), (IntByReference)closeableIntByReference, null);
          if (!bool) {
            l += 256L;
            memory.close();
            if (l > 4096L) {
              String str = "";
              closeableIntByReference.close();
              bATTERY_QUERY_INFORMATION.close();
              return str;
            } 
            memory = new Memory(l);
          } 
          if (bool) {
            String str1 = (CHAR_WIDTH > 1) ? memory.getWideString(0L) : memory.getString(0L);
            memory.close();
            String str2 = str1;
            closeableIntByReference.close();
            bATTERY_QUERY_INFORMATION.close();
            return str2;
          } 
        } 
      } catch (Throwable throwable) {
        try {
          closeableIntByReference.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      try {
        bATTERY_QUERY_INFORMATION.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\windows\WindowsPowerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
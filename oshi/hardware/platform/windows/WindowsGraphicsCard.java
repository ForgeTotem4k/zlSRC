package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.windows.wmi.Win32VideoController;
import oshi.hardware.GraphicsCard;
import oshi.hardware.common.AbstractGraphicsCard;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Triplet;

@Immutable
final class WindowsGraphicsCard extends AbstractGraphicsCard {
  private static final Logger LOG = LoggerFactory.getLogger(WindowsGraphicsCard.class);
  
  private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
  
  public static final String ADAPTER_STRING = "HardwareInformation.AdapterString";
  
  public static final String DRIVER_DESC = "DriverDesc";
  
  public static final String DRIVER_VERSION = "DriverVersion";
  
  public static final String VENDOR = "ProviderName";
  
  public static final String QW_MEMORY_SIZE = "HardwareInformation.qwMemorySize";
  
  public static final String MEMORY_SIZE = "HardwareInformation.MemorySize";
  
  public static final String DISPLAY_DEVICES_REGISTRY_PATH = "SYSTEM\\CurrentControlSet\\Control\\Class\\{4d36e968-e325-11ce-bfc1-08002be10318}\\";
  
  WindowsGraphicsCard(String paramString1, String paramString2, String paramString3, String paramString4, long paramLong) {
    super(paramString1, paramString2, paramString3, paramString4, paramLong);
  }
  
  public static List<GraphicsCard> getGraphicsCards() {
    ArrayList<WindowsGraphicsCard> arrayList = new ArrayList();
    byte b = 1;
    String[] arrayOfString = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Class\\{4d36e968-e325-11ce-bfc1-08002be10318}\\");
    for (String str : arrayOfString) {
      if (str.startsWith("0"))
        try {
          String str1 = "SYSTEM\\CurrentControlSet\\Control\\Class\\{4d36e968-e325-11ce-bfc1-08002be10318}\\" + str;
          if (Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, str1, "HardwareInformation.AdapterString")) {
            String str2 = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, str1, "DriverDesc");
            String str3 = "VideoController" + b++;
            String str4 = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, str1, "ProviderName");
            String str5 = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, str1, "DriverVersion");
            long l = 0L;
            if (Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, str1, "HardwareInformation.qwMemorySize")) {
              l = Advapi32Util.registryGetLongValue(WinReg.HKEY_LOCAL_MACHINE, str1, "HardwareInformation.qwMemorySize");
            } else if (Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, str1, "HardwareInformation.MemorySize")) {
              Object object = Advapi32Util.registryGetValue(WinReg.HKEY_LOCAL_MACHINE, str1, "HardwareInformation.MemorySize");
              if (object instanceof Long) {
                l = ((Long)object).longValue();
              } else if (object instanceof Integer) {
                l = Integer.toUnsignedLong(((Integer)object).intValue());
              } else if (object instanceof byte[]) {
                byte[] arrayOfByte = (byte[])object;
                l = ParseUtil.byteArrayToLong(arrayOfByte, arrayOfByte.length, false);
              } 
            } 
            arrayList.add(new WindowsGraphicsCard(Util.isBlank(str2) ? "unknown" : str2, Util.isBlank(str3) ? "unknown" : str3, Util.isBlank(str4) ? "unknown" : str4, Util.isBlank(str5) ? "unknown" : str5, l));
          } 
        } catch (Win32Exception win32Exception) {
          if (win32Exception.getErrorCode() != 5)
            throw win32Exception; 
        }  
    } 
    return (List<GraphicsCard>)(arrayList.isEmpty() ? getGraphicsCardsFromWmi() : arrayList);
  }
  
  private static List<GraphicsCard> getGraphicsCardsFromWmi() {
    ArrayList<WindowsGraphicsCard> arrayList = new ArrayList();
    if (IS_VISTA_OR_GREATER) {
      WbemcliUtil.WmiResult wmiResult = Win32VideoController.queryVideoController();
      for (byte b = 0; b < wmiResult.getResultCount(); b++) {
        String str1 = WmiUtil.getString(wmiResult, (Enum)Win32VideoController.VideoControllerProperty.NAME, b);
        Triplet triplet = ParseUtil.parseDeviceIdToVendorProductSerial(WmiUtil.getString(wmiResult, (Enum)Win32VideoController.VideoControllerProperty.PNPDEVICEID, b));
        String str2 = (triplet == null) ? "unknown" : (String)triplet.getB();
        String str3 = WmiUtil.getString(wmiResult, (Enum)Win32VideoController.VideoControllerProperty.ADAPTERCOMPATIBILITY, b);
        if (triplet != null)
          if (Util.isBlank(str3)) {
            str2 = (String)triplet.getA();
          } else {
            str3 = str3 + " (" + (String)triplet.getA() + ")";
          }  
        String str4 = WmiUtil.getString(wmiResult, (Enum)Win32VideoController.VideoControllerProperty.DRIVERVERSION, b);
        if (!Util.isBlank(str4)) {
          str4 = "DriverVersion=" + str4;
        } else {
          str4 = "unknown";
        } 
        long l = WmiUtil.getUint32asLong(wmiResult, (Enum)Win32VideoController.VideoControllerProperty.ADAPTERRAM, b);
        arrayList.add(new WindowsGraphicsCard(Util.isBlank(str1) ? "unknown" : str1, str2, Util.isBlank(str3) ? "unknown" : str3, str4, l));
      } 
    } 
    return (List)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\windows\WindowsGraphicsCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
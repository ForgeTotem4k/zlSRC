package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Display;
import oshi.hardware.common.AbstractDisplay;
import oshi.jna.ByRef;
import oshi.jna.Struct;

@Immutable
final class WindowsDisplay extends AbstractDisplay {
  private static final Logger LOG = LoggerFactory.getLogger(WindowsDisplay.class);
  
  private static final SetupApi SU = SetupApi.INSTANCE;
  
  private static final Advapi32 ADV = Advapi32.INSTANCE;
  
  private static final Guid.GUID GUID_DEVINTERFACE_MONITOR = new Guid.GUID("E6F07B5F-EE97-4a90-B076-33F57BF4EAA7");
  
  WindowsDisplay(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte);
    LOG.debug("Initialized WindowsDisplay");
  }
  
  public static List<Display> getDisplays() {
    ArrayList<WindowsDisplay> arrayList = new ArrayList();
    WinNT.HANDLE hANDLE = SU.SetupDiGetClassDevs(GUID_DEVINTERFACE_MONITOR, null, null, 18);
    if (!hANDLE.equals(WinBase.INVALID_HANDLE_VALUE)) {
      Struct.CloseableSpDeviceInterfaceData closeableSpDeviceInterfaceData = new Struct.CloseableSpDeviceInterfaceData();
      try {
        Struct.CloseableSpDevinfoData closeableSpDevinfoData = new Struct.CloseableSpDevinfoData();
        try {
          closeableSpDeviceInterfaceData.cbSize = closeableSpDeviceInterfaceData.size();
          for (byte b = 0; SU.SetupDiEnumDeviceInfo(hANDLE, b, (SetupApi.SP_DEVINFO_DATA)closeableSpDevinfoData); b++) {
            WinReg.HKEY hKEY = SU.SetupDiOpenDevRegKey(hANDLE, (SetupApi.SP_DEVINFO_DATA)closeableSpDevinfoData, 1, 0, 1, 1);
            byte[] arrayOfByte = new byte[1];
            ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
            try {
              ByRef.CloseableIntByReference closeableIntByReference1 = new ByRef.CloseableIntByReference();
              try {
                if (ADV.RegQueryValueEx(hKEY, "EDID", 0, (IntByReference)closeableIntByReference, arrayOfByte, (IntByReference)closeableIntByReference1) == 234) {
                  arrayOfByte = new byte[closeableIntByReference1.getValue()];
                  if (ADV.RegQueryValueEx(hKEY, "EDID", 0, (IntByReference)closeableIntByReference, arrayOfByte, (IntByReference)closeableIntByReference1) == 0) {
                    WindowsDisplay windowsDisplay = new WindowsDisplay(arrayOfByte);
                    arrayList.add(windowsDisplay);
                  } 
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
            Advapi32.INSTANCE.RegCloseKey(hKEY);
          } 
          closeableSpDevinfoData.close();
        } catch (Throwable throwable) {
          try {
            closeableSpDevinfoData.close();
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
      SU.SetupDiDestroyDeviceInfoList(hANDLE);
    } 
    return (List)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\windows\WindowsDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
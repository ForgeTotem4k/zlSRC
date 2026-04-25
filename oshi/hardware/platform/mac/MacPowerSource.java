package oshi.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.PowerSource;
import oshi.hardware.common.AbstractPowerSource;
import oshi.util.platform.mac.CFUtil;

@ThreadSafe
public final class MacPowerSource extends AbstractPowerSource {
  private static final CoreFoundation CF = CoreFoundation.INSTANCE;
  
  private static final IOKit IO = IOKit.INSTANCE;
  
  public MacPowerSource(String paramString1, String paramString2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, PowerSource.CapacityUnits paramCapacityUnits, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString3, LocalDate paramLocalDate, String paramString4, String paramString5, double paramDouble7) {
    super(paramString1, paramString2, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramBoolean1, paramBoolean2, paramBoolean3, paramCapacityUnits, paramInt1, paramInt2, paramInt3, paramInt4, paramString3, paramLocalDate, paramString4, paramString5, paramDouble7);
  }
  
  public static List<PowerSource> getPowerSources() {
    String str1 = "unknown";
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = -1.0D;
    double d4 = 0.0D;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool = false;
    PowerSource.CapacityUnits capacityUnits = PowerSource.CapacityUnits.RELATIVE;
    int i = 0;
    int j = 1;
    int k = 1;
    int m = -1;
    String str2 = "unknown";
    LocalDate localDate = null;
    String str3 = "unknown";
    String str4 = "unknown";
    double d5 = 0.0D;
    IOKit.IOService iOService = IOKitUtil.getMatchingService("AppleSmartBattery");
    if (iOService != null) {
      String str = iOService.getStringProperty("DeviceName");
      if (str != null)
        str1 = str; 
      str = iOService.getStringProperty("Manufacturer");
      if (str != null)
        str3 = str; 
      str = iOService.getStringProperty("BatterySerialNumber");
      if (str != null)
        str4 = str; 
      Integer integer = iOService.getIntegerProperty("ManufactureDate");
      if (integer != null) {
        int i1 = integer.intValue() & 0x1F;
        int i2 = integer.intValue() >> 5 & 0xF;
        int i3 = integer.intValue() >> 9 & 0x7F;
        localDate = LocalDate.of(1980 + i3, i2, i1);
      } 
      integer = iOService.getIntegerProperty("DesignCapacity");
      if (integer != null)
        k = integer.intValue(); 
      integer = iOService.getIntegerProperty("MaxCapacity");
      if (integer != null)
        j = integer.intValue(); 
      integer = iOService.getIntegerProperty("CurrentCapacity");
      if (integer != null)
        i = integer.intValue(); 
      capacityUnits = PowerSource.CapacityUnits.MAH;
      integer = iOService.getIntegerProperty("TimeRemaining");
      if (integer != null)
        d1 = integer.intValue() * 60.0D; 
      integer = iOService.getIntegerProperty("CycleCount");
      if (integer != null)
        m = integer.intValue(); 
      integer = iOService.getIntegerProperty("Temperature");
      if (integer != null)
        d5 = integer.intValue() / 100.0D; 
      integer = iOService.getIntegerProperty("Voltage");
      if (integer != null)
        d3 = integer.intValue() / 1000.0D; 
      integer = iOService.getIntegerProperty("Amperage");
      if (integer != null)
        d4 = integer.intValue(); 
      d2 = d3 * d4;
      Boolean bool3 = iOService.getBooleanProperty("ExternalConnected");
      if (bool3 != null)
        bool1 = bool3.booleanValue(); 
      bool3 = iOService.getBooleanProperty("IsCharging");
      if (bool3 != null)
        bool2 = bool3.booleanValue(); 
      bool = !bool2 ? true : false;
      iOService.release();
    } 
    CoreFoundation.CFTypeRef cFTypeRef = IO.IOPSCopyPowerSourcesInfo();
    CoreFoundation.CFArrayRef cFArrayRef = IO.IOPSCopyPowerSourcesList(cFTypeRef);
    int n = cFArrayRef.getCount();
    double d6 = IO.IOPSGetTimeRemainingEstimate();
    CoreFoundation.CFStringRef cFStringRef1 = CoreFoundation.CFStringRef.createCFString("Name");
    CoreFoundation.CFStringRef cFStringRef2 = CoreFoundation.CFStringRef.createCFString("Is Present");
    CoreFoundation.CFStringRef cFStringRef3 = CoreFoundation.CFStringRef.createCFString("Current Capacity");
    CoreFoundation.CFStringRef cFStringRef4 = CoreFoundation.CFStringRef.createCFString("Max Capacity");
    ArrayList<MacPowerSource> arrayList = new ArrayList(n);
    for (byte b = 0; b < n; b++) {
      Pointer pointer1 = cFArrayRef.getValueAtIndex(b);
      CoreFoundation.CFTypeRef cFTypeRef1 = new CoreFoundation.CFTypeRef();
      cFTypeRef1.setPointer(pointer1);
      CoreFoundation.CFDictionaryRef cFDictionaryRef = IO.IOPSGetPowerSourceDescription(cFTypeRef, cFTypeRef1);
      Pointer pointer2 = cFDictionaryRef.getValue((PointerType)cFStringRef2);
      if (pointer2 != null) {
        CoreFoundation.CFBooleanRef cFBooleanRef = new CoreFoundation.CFBooleanRef(pointer2);
        if (0 != CF.CFBooleanGetValue(cFBooleanRef)) {
          pointer2 = cFDictionaryRef.getValue((PointerType)cFStringRef1);
          String str = CFUtil.cfPointerToString(pointer2);
          double d7 = 0.0D;
          if (cFDictionaryRef.getValueIfPresent((PointerType)cFStringRef3, null)) {
            pointer2 = cFDictionaryRef.getValue((PointerType)cFStringRef3);
            CoreFoundation.CFNumberRef cFNumberRef = new CoreFoundation.CFNumberRef(pointer2);
            d7 = cFNumberRef.intValue();
          } 
          double d8 = 1.0D;
          if (cFDictionaryRef.getValueIfPresent((PointerType)cFStringRef4, null)) {
            pointer2 = cFDictionaryRef.getValue((PointerType)cFStringRef4);
            CoreFoundation.CFNumberRef cFNumberRef = new CoreFoundation.CFNumberRef(pointer2);
            d8 = cFNumberRef.intValue();
          } 
          double d9 = Math.min(1.0D, d7 / d8);
          arrayList.add(new MacPowerSource(str, str1, d9, d6, d1, d2, d3, d4, bool1, bool2, bool, capacityUnits, i, j, k, m, str2, localDate, str3, str4, d5));
        } 
      } 
    } 
    cFStringRef2.release();
    cFStringRef1.release();
    cFStringRef3.release();
    cFStringRef4.release();
    cFArrayRef.release();
    cFTypeRef.release();
    return (List)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacPowerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
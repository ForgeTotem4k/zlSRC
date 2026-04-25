package com.sun.jna.platform.mac;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class IOKitUtil {
  private static final IOKit IO = IOKit.INSTANCE;
  
  private static final SystemB SYS = SystemB.INSTANCE;
  
  public static int getMasterPort() {
    IntByReference intByReference = new IntByReference();
    IO.IOMasterPort(0, intByReference);
    return intByReference.getValue();
  }
  
  public static IOKit.IORegistryEntry getRoot() {
    int i = getMasterPort();
    IOKit.IORegistryEntry iORegistryEntry = IO.IORegistryGetRootEntry(i);
    SYS.mach_port_deallocate(SYS.mach_task_self(), i);
    return iORegistryEntry;
  }
  
  public static IOKit.IOService getMatchingService(String paramString) {
    CoreFoundation.CFMutableDictionaryRef cFMutableDictionaryRef = IO.IOServiceMatching(paramString);
    return (cFMutableDictionaryRef != null) ? getMatchingService(cFMutableDictionaryRef) : null;
  }
  
  public static IOKit.IOService getMatchingService(CoreFoundation.CFDictionaryRef paramCFDictionaryRef) {
    int i = getMasterPort();
    IOKit.IOService iOService = IO.IOServiceGetMatchingService(i, paramCFDictionaryRef);
    SYS.mach_port_deallocate(SYS.mach_task_self(), i);
    return iOService;
  }
  
  public static IOKit.IOIterator getMatchingServices(String paramString) {
    CoreFoundation.CFMutableDictionaryRef cFMutableDictionaryRef = IO.IOServiceMatching(paramString);
    return (cFMutableDictionaryRef != null) ? getMatchingServices(cFMutableDictionaryRef) : null;
  }
  
  public static IOKit.IOIterator getMatchingServices(CoreFoundation.CFDictionaryRef paramCFDictionaryRef) {
    int i = getMasterPort();
    PointerByReference pointerByReference = new PointerByReference();
    int j = IO.IOServiceGetMatchingServices(i, paramCFDictionaryRef, pointerByReference);
    SYS.mach_port_deallocate(SYS.mach_task_self(), i);
    return (j == 0 && pointerByReference.getValue() != null) ? new IOKit.IOIterator(pointerByReference.getValue()) : null;
  }
  
  public static CoreFoundation.CFMutableDictionaryRef getBSDNameMatchingDict(String paramString) {
    int i = getMasterPort();
    CoreFoundation.CFMutableDictionaryRef cFMutableDictionaryRef = IO.IOBSDNameMatching(i, 0, paramString);
    SYS.mach_port_deallocate(SYS.mach_task_self(), i);
    return cFMutableDictionaryRef;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\com\sun\jna\platform\mac\IOKitUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package oshi.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Display;
import oshi.hardware.common.AbstractDisplay;

@Immutable
final class MacDisplay extends AbstractDisplay {
  private static final Logger LOG = LoggerFactory.getLogger(MacDisplay.class);
  
  MacDisplay(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte);
    LOG.debug("Initialized MacDisplay");
  }
  
  public static List<Display> getDisplays() {
    ArrayList<MacDisplay> arrayList = new ArrayList();
    IOKit.IOIterator iOIterator = IOKitUtil.getMatchingServices("IODisplayConnect");
    if (iOIterator != null) {
      CoreFoundation.CFStringRef cFStringRef = CoreFoundation.CFStringRef.createCFString("IODisplayEDID");
      for (IOKit.IORegistryEntry iORegistryEntry = iOIterator.next(); iORegistryEntry != null; iORegistryEntry = iOIterator.next()) {
        IOKit.IORegistryEntry iORegistryEntry1 = iORegistryEntry.getChildEntry("IOService");
        if (iORegistryEntry1 != null) {
          CoreFoundation.CFTypeRef cFTypeRef = iORegistryEntry1.createCFProperty(cFStringRef);
          if (cFTypeRef != null) {
            CoreFoundation.CFDataRef cFDataRef = new CoreFoundation.CFDataRef(cFTypeRef.getPointer());
            int i = cFDataRef.getLength();
            Pointer pointer = cFDataRef.getBytePtr();
            arrayList.add(new MacDisplay(pointer.getByteArray(0L, i)));
            cFDataRef.release();
          } 
          iORegistryEntry1.release();
        } 
        iORegistryEntry.release();
      } 
      iOIterator.release();
      cFStringRef.release();
    } 
    return (List)arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\hardware\platform\mac\MacDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
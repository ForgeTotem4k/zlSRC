package oshi.driver.mac;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.mac.CoreFoundation;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.platform.mac.CoreGraphics;
import oshi.software.os.OSDesktopWindow;
import oshi.util.FormatUtil;
import oshi.util.platform.mac.CFUtil;

@ThreadSafe
public final class WindowInfo {
  public static List<OSDesktopWindow> queryDesktopWindows(boolean paramBoolean) {
    CoreFoundation.CFArrayRef cFArrayRef = CoreGraphics.INSTANCE.CGWindowListCopyWindowInfo(paramBoolean ? 17 : 0, 0);
    int i = cFArrayRef.getCount();
    ArrayList<OSDesktopWindow> arrayList = new ArrayList();
    CoreFoundation.CFStringRef cFStringRef1 = CoreFoundation.CFStringRef.createCFString("kCGWindowIsOnscreen");
    CoreFoundation.CFStringRef cFStringRef2 = CoreFoundation.CFStringRef.createCFString("kCGWindowNumber");
    CoreFoundation.CFStringRef cFStringRef3 = CoreFoundation.CFStringRef.createCFString("kCGWindowOwnerPID");
    CoreFoundation.CFStringRef cFStringRef4 = CoreFoundation.CFStringRef.createCFString("kCGWindowLayer");
    CoreFoundation.CFStringRef cFStringRef5 = CoreFoundation.CFStringRef.createCFString("kCGWindowBounds");
    CoreFoundation.CFStringRef cFStringRef6 = CoreFoundation.CFStringRef.createCFString("kCGWindowName");
    CoreFoundation.CFStringRef cFStringRef7 = CoreFoundation.CFStringRef.createCFString("kCGWindowOwnerName");
    try {
      for (byte b = 0; b < i; b++) {
        Pointer pointer = cFArrayRef.getValueAtIndex(b);
        CoreFoundation.CFDictionaryRef cFDictionaryRef = new CoreFoundation.CFDictionaryRef(pointer);
        pointer = cFDictionaryRef.getValue((PointerType)cFStringRef1);
        boolean bool = (pointer == null || (new CoreFoundation.CFBooleanRef(pointer)).booleanValue()) ? true : false;
        if (!paramBoolean || bool) {
          pointer = cFDictionaryRef.getValue((PointerType)cFStringRef2);
          long l1 = (new CoreFoundation.CFNumberRef(pointer)).longValue();
          pointer = cFDictionaryRef.getValue((PointerType)cFStringRef3);
          long l2 = (new CoreFoundation.CFNumberRef(pointer)).longValue();
          pointer = cFDictionaryRef.getValue((PointerType)cFStringRef4);
          int j = (new CoreFoundation.CFNumberRef(pointer)).intValue();
          pointer = cFDictionaryRef.getValue((PointerType)cFStringRef5);
          CoreGraphics.CGRect cGRect = new CoreGraphics.CGRect();
          try {
            CoreGraphics.INSTANCE.CGRectMakeWithDictionaryRepresentation(new CoreFoundation.CFDictionaryRef(pointer), cGRect);
            Rectangle rectangle = new Rectangle(FormatUtil.roundToInt(cGRect.origin.x), FormatUtil.roundToInt(cGRect.origin.y), FormatUtil.roundToInt(cGRect.size.width), FormatUtil.roundToInt(cGRect.size.height));
            pointer = cFDictionaryRef.getValue((PointerType)cFStringRef6);
            String str1 = CFUtil.cfPointerToString(pointer, false);
            pointer = cFDictionaryRef.getValue((PointerType)cFStringRef7);
            String str2 = CFUtil.cfPointerToString(pointer, false);
            if (str1.isEmpty()) {
              str1 = str2;
            } else {
              str1 = str1 + "(" + str2 + ")";
            } 
            arrayList.add(new OSDesktopWindow(l1, str1, str2, rectangle, l2, j, bool));
            cGRect.close();
          } catch (Throwable throwable) {
            try {
              cGRect.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        } 
      } 
    } finally {
      cFStringRef1.release();
      cFStringRef2.release();
      cFStringRef3.release();
      cFStringRef4.release();
      cFStringRef5.release();
      cFStringRef6.release();
      cFStringRef7.release();
      cFArrayRef.release();
    } 
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\mac\WindowInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
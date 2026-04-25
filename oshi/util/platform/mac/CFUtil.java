package oshi.util.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class CFUtil {
  public static String cfPointerToString(Pointer paramPointer) {
    return cfPointerToString(paramPointer, true);
  }
  
  public static String cfPointerToString(Pointer paramPointer, boolean paramBoolean) {
    String str = "";
    if (paramPointer != null) {
      CoreFoundation.CFStringRef cFStringRef = new CoreFoundation.CFStringRef(paramPointer);
      str = cFStringRef.stringValue();
    } 
    return (paramBoolean && str.isEmpty()) ? "unknown" : str;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\platform\mac\CFUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
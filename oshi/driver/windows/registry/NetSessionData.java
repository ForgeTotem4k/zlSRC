package oshi.driver.windows.registry;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Netapi32;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.ArrayList;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.software.os.OSSession;

@ThreadSafe
public final class NetSessionData {
  private static final Netapi32 NET = Netapi32.INSTANCE;
  
  public static List<OSSession> queryUserSessions() {
    ArrayList<OSSession> arrayList = new ArrayList();
    ByRef.CloseablePointerByReference closeablePointerByReference = new ByRef.CloseablePointerByReference();
    try {
      ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
      try {
        ByRef.CloseableIntByReference closeableIntByReference1 = new ByRef.CloseableIntByReference();
        try {
          if (0 == NET.NetSessionEnum(null, null, null, 10, (PointerByReference)closeablePointerByReference, -1, (IntByReference)closeableIntByReference, (IntByReference)closeableIntByReference1, null)) {
            Pointer pointer = closeablePointerByReference.getValue();
            Netapi32.SESSION_INFO_10 sESSION_INFO_10 = new Netapi32.SESSION_INFO_10(pointer);
            if (closeableIntByReference.getValue() > 0) {
              Netapi32.SESSION_INFO_10[] arrayOfSESSION_INFO_10 = (Netapi32.SESSION_INFO_10[])sESSION_INFO_10.toArray(closeableIntByReference.getValue());
              for (Netapi32.SESSION_INFO_10 sESSION_INFO_101 : arrayOfSESSION_INFO_10) {
                long l = System.currentTimeMillis() - 1000L * sESSION_INFO_101.sesi10_time;
                arrayList.add(new OSSession(sESSION_INFO_101.sesi10_username, "Network session", l, sESSION_INFO_101.sesi10_cname));
              } 
            } 
            NET.NetApiBufferFree(pointer);
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
      closeablePointerByReference.close();
    } catch (Throwable throwable) {
      try {
        closeablePointerByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return arrayList;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\registry\NetSessionData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
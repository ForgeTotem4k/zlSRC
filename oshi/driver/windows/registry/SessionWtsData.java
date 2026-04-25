package oshi.driver.windows.registry;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Wtsapi32;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.software.os.OSSession;
import oshi.util.ParseUtil;

@ThreadSafe
public final class SessionWtsData {
  private static final int WTS_ACTIVE = 0;
  
  private static final int WTS_CLIENTADDRESS = 14;
  
  private static final int WTS_SESSIONINFO = 24;
  
  private static final int WTS_CLIENTPROTOCOLTYPE = 16;
  
  private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
  
  private static final Wtsapi32 WTS = Wtsapi32.INSTANCE;
  
  public static List<OSSession> queryUserSessions() {
    ArrayList<OSSession> arrayList = new ArrayList();
    if (IS_VISTA_OR_GREATER) {
      ByRef.CloseablePointerByReference closeablePointerByReference = new ByRef.CloseablePointerByReference();
      try {
        ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
        try {
          ByRef.CloseablePointerByReference closeablePointerByReference1 = new ByRef.CloseablePointerByReference();
          try {
            ByRef.CloseableIntByReference closeableIntByReference1 = new ByRef.CloseableIntByReference();
            try {
              if (WTS.WTSEnumerateSessions(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, 0, 1, (PointerByReference)closeablePointerByReference, (IntByReference)closeableIntByReference)) {
                Pointer pointer = closeablePointerByReference.getValue();
                if (closeableIntByReference.getValue() > 0) {
                  Wtsapi32.WTS_SESSION_INFO wTS_SESSION_INFO = new Wtsapi32.WTS_SESSION_INFO(pointer);
                  Wtsapi32.WTS_SESSION_INFO[] arrayOfWTS_SESSION_INFO = (Wtsapi32.WTS_SESSION_INFO[])wTS_SESSION_INFO.toArray(closeableIntByReference.getValue());
                  for (Wtsapi32.WTS_SESSION_INFO wTS_SESSION_INFO1 : arrayOfWTS_SESSION_INFO) {
                    if (wTS_SESSION_INFO1.State == 0) {
                      WTS.WTSQuerySessionInformation(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, wTS_SESSION_INFO1.SessionId, 16, (PointerByReference)closeablePointerByReference1, (IntByReference)closeableIntByReference1);
                      Pointer pointer1 = closeablePointerByReference1.getValue();
                      short s = pointer1.getShort(0L);
                      WTS.WTSFreeMemory(pointer1);
                      if (s > 0) {
                        String str1 = wTS_SESSION_INFO1.pWinStationName;
                        WTS.WTSQuerySessionInformation(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, wTS_SESSION_INFO1.SessionId, 24, (PointerByReference)closeablePointerByReference1, (IntByReference)closeableIntByReference1);
                        pointer1 = closeablePointerByReference1.getValue();
                        Wtsapi32.WTSINFO wTSINFO = new Wtsapi32.WTSINFO(pointer1);
                        long l = (new WinBase.FILETIME(new WinNT.LARGE_INTEGER(wTSINFO.LogonTime.getValue()))).toTime();
                        String str2 = wTSINFO.getUserName();
                        WTS.WTSFreeMemory(pointer1);
                        WTS.WTSQuerySessionInformation(Wtsapi32.WTS_CURRENT_SERVER_HANDLE, wTS_SESSION_INFO1.SessionId, 14, (PointerByReference)closeablePointerByReference1, (IntByReference)closeableIntByReference1);
                        pointer1 = closeablePointerByReference1.getValue();
                        Wtsapi32.WTS_CLIENT_ADDRESS wTS_CLIENT_ADDRESS = new Wtsapi32.WTS_CLIENT_ADDRESS(pointer1);
                        WTS.WTSFreeMemory(pointer1);
                        String str3 = "::";
                        if (wTS_CLIENT_ADDRESS.AddressFamily == 2) {
                          try {
                            str3 = InetAddress.getByAddress(Arrays.copyOfRange(wTS_CLIENT_ADDRESS.Address, 2, 6)).getHostAddress();
                          } catch (UnknownHostException unknownHostException) {
                            str3 = "Illegal length IP Array";
                          } 
                        } else if (wTS_CLIENT_ADDRESS.AddressFamily == 23) {
                          int[] arrayOfInt = convertBytesToInts(wTS_CLIENT_ADDRESS.Address);
                          str3 = ParseUtil.parseUtAddrV6toIP(arrayOfInt);
                        } 
                        arrayList.add(new OSSession(str2, str1, l, str3));
                      } 
                    } 
                  } 
                } 
                WTS.WTSFreeMemory(pointer);
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
            closeablePointerByReference1.close();
          } catch (Throwable throwable) {
            try {
              closeablePointerByReference1.close();
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
    } 
    return arrayList;
  }
  
  private static int[] convertBytesToInts(byte[] paramArrayOfbyte) {
    IntBuffer intBuffer = ByteBuffer.wrap(Arrays.copyOfRange(paramArrayOfbyte, 2, 18)).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
    int[] arrayOfInt = new int[intBuffer.remaining()];
    intBuffer.get(arrayOfInt);
    return arrayOfInt;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\registry\SessionWtsData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
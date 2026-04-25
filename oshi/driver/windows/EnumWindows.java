package oshi.driver.windows;

import com.sun.jna.Pointer;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.software.os.OSDesktopWindow;

@ThreadSafe
public final class EnumWindows {
  private static final WinDef.DWORD GW_HWNDNEXT = new WinDef.DWORD(2L);
  
  public static List<OSDesktopWindow> queryDesktopWindows(boolean paramBoolean) {
    List list = WindowUtils.getAllWindows(true);
    ArrayList<OSDesktopWindow> arrayList = new ArrayList();
    HashMap<Object, Object> hashMap = new HashMap<>();
    for (DesktopWindow desktopWindow : list) {
      WinDef.HWND hWND = desktopWindow.getHWND();
      if (hWND != null) {
        boolean bool = User32.INSTANCE.IsWindowVisible(hWND);
        if (!paramBoolean || bool) {
          if (!hashMap.containsKey(hWND))
            updateWindowZOrderMap(hWND, (Map)hashMap); 
          ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference();
          try {
            User32.INSTANCE.GetWindowThreadProcessId(hWND, (IntByReference)closeableIntByReference);
            arrayList.add(new OSDesktopWindow(Pointer.nativeValue(hWND.getPointer()), desktopWindow.getTitle(), desktopWindow.getFilePath(), desktopWindow.getLocAndSize(), closeableIntByReference.getValue(), ((Integer)hashMap.get(hWND)).intValue(), bool));
            closeableIntByReference.close();
          } catch (Throwable throwable) {
            try {
              closeableIntByReference.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            } 
            throw throwable;
          } 
        } 
      } 
    } 
    return arrayList;
  }
  
  private static void updateWindowZOrderMap(WinDef.HWND paramHWND, Map<WinDef.HWND, Integer> paramMap) {
    if (paramHWND != null) {
      byte b = 1;
      WinDef.HWND hWND = new WinDef.HWND(paramHWND.getPointer());
      do {
        paramMap.put(hWND, Integer.valueOf(--b));
      } while ((hWND = User32.INSTANCE.GetWindow(hWND, GW_HWNDNEXT)) != null);
      int i = b * -1;
      paramMap.replaceAll((paramHWND, paramInteger) -> Integer.valueOf(paramInteger.intValue() + paramInt));
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\EnumWindows.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package oshi.driver.windows;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Cfgmgr32;
import com.sun.jna.platform.win32.Cfgmgr32Util;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.jna.Struct;
import oshi.util.tuples.Quintet;

@ThreadSafe
public final class DeviceTree {
  private static final int MAX_PATH = 260;
  
  private static final SetupApi SA = SetupApi.INSTANCE;
  
  private static final Cfgmgr32 C32 = Cfgmgr32.INSTANCE;
  
  public static Quintet<Set<Integer>, Map<Integer, Integer>, Map<Integer, String>, Map<Integer, String>, Map<Integer, String>> queryDeviceTree(Guid.GUID paramGUID) {
    HashMap<Object, Object> hashMap1 = new HashMap<>();
    HashMap<Object, Object> hashMap2 = new HashMap<>();
    HashMap<Object, Object> hashMap3 = new HashMap<>();
    HashMap<Object, Object> hashMap4 = new HashMap<>();
    WinNT.HANDLE hANDLE = SA.SetupDiGetClassDevs(paramGUID, null, null, 18);
    if (!WinBase.INVALID_HANDLE_VALUE.equals(hANDLE))
      try {
        Memory memory = new Memory(260L);
        try {
          ByRef.CloseableIntByReference closeableIntByReference = new ByRef.CloseableIntByReference(260);
          try {
            ByRef.CloseableIntByReference closeableIntByReference1 = new ByRef.CloseableIntByReference();
            try {
              ByRef.CloseableIntByReference closeableIntByReference2 = new ByRef.CloseableIntByReference();
              try {
                Struct.CloseableSpDevinfoData closeableSpDevinfoData = new Struct.CloseableSpDevinfoData();
                try {
                  closeableSpDevinfoData.cbSize = closeableSpDevinfoData.size();
                  ArrayDeque<Integer> arrayDeque = new ArrayDeque();
                  for (byte b = 0; SA.SetupDiEnumDeviceInfo(hANDLE, b, (SetupApi.SP_DEVINFO_DATA)closeableSpDevinfoData); b++) {
                    arrayDeque.add(Integer.valueOf(closeableSpDevinfoData.DevInst));
                    int i = 0;
                    while (!arrayDeque.isEmpty()) {
                      i = ((Integer)arrayDeque.poll()).intValue();
                      String str1 = Cfgmgr32Util.CM_Get_Device_ID(i);
                      hashMap3.put(Integer.valueOf(i), str1);
                      String str2 = getDevNodeProperty(i, 13, memory, (IntByReference)closeableIntByReference);
                      if (str2.isEmpty())
                        str2 = getDevNodeProperty(i, 1, memory, (IntByReference)closeableIntByReference); 
                      if (str2.isEmpty()) {
                        str2 = getDevNodeProperty(i, 8, memory, (IntByReference)closeableIntByReference);
                        String str = getDevNodeProperty(i, 5, memory, (IntByReference)closeableIntByReference);
                        if (!str.isEmpty())
                          str2 = str2 + " (" + str + ")"; 
                      } 
                      hashMap2.put(Integer.valueOf(i), str2);
                      hashMap4.put(Integer.valueOf(i), getDevNodeProperty(i, 12, memory, (IntByReference)closeableIntByReference));
                      if (0 == C32.CM_Get_Child((IntByReference)closeableIntByReference1, i, 0)) {
                        hashMap1.put(Integer.valueOf(closeableIntByReference1.getValue()), Integer.valueOf(i));
                        arrayDeque.add(Integer.valueOf(closeableIntByReference1.getValue()));
                        while (0 == C32.CM_Get_Sibling((IntByReference)closeableIntByReference2, closeableIntByReference1.getValue(), 0)) {
                          hashMap1.put(Integer.valueOf(closeableIntByReference2.getValue()), Integer.valueOf(i));
                          arrayDeque.add(Integer.valueOf(closeableIntByReference2.getValue()));
                          closeableIntByReference1.setValue(closeableIntByReference2.getValue());
                        } 
                      } 
                    } 
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
                closeableIntByReference2.close();
              } catch (Throwable throwable) {
                try {
                  closeableIntByReference2.close();
                } catch (Throwable throwable1) {
                  throwable.addSuppressed(throwable1);
                } 
                throw throwable;
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
          memory.close();
        } catch (Throwable throwable) {
          try {
            memory.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } finally {
        SA.SetupDiDestroyDeviceInfoList(hANDLE);
      }  
    Set set = (Set)hashMap3.keySet().stream().filter(paramInteger -> !paramMap.containsKey(paramInteger)).collect(Collectors.toSet());
    return new Quintet(set, hashMap1, hashMap2, hashMap3, hashMap4);
  }
  
  private static String getDevNodeProperty(int paramInt1, int paramInt2, Memory paramMemory, IntByReference paramIntByReference) {
    paramMemory.clear();
    paramIntByReference.setValue((int)paramMemory.size());
    C32.CM_Get_DevNode_Registry_Property(paramInt1, paramInt2, null, (Pointer)paramMemory, paramIntByReference, 0);
    return paramMemory.getWideString(0L);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\oshi\driver\windows\DeviceTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
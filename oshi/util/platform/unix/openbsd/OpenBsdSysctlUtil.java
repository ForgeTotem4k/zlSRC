package oshi.util.platform.unix.openbsd;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.unix.LibCAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.jna.platform.unix.OpenBsdLibc;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@ThreadSafe
public final class OpenBsdSysctlUtil {
  private static final String SYSCTL_N = "sysctl -n ";
  
  private static final Logger LOG = LoggerFactory.getLogger(OpenBsdSysctlUtil.class);
  
  private static final String SYSCTL_FAIL = "Failed sysctl call: {}, Error code: {}";
  
  public static int sysctl(int[] paramArrayOfint, int paramInt) {
    int i = OpenBsdLibc.INT_SIZE;
    Memory memory = new Memory(i);
    try {
      ByRef.CloseableSizeTByReference closeableSizeTByReference = new ByRef.CloseableSizeTByReference(i);
      try {
        if (0 != OpenBsdLibc.INSTANCE.sysctl(paramArrayOfint, paramArrayOfint.length, (Pointer)memory, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO)) {
          LOG.warn("Failed sysctl call: {}, Error code: {}", paramArrayOfint, Integer.valueOf(Native.getLastError()));
          int k = paramInt;
          closeableSizeTByReference.close();
          memory.close();
          return k;
        } 
        int j = memory.getInt(0L);
        closeableSizeTByReference.close();
        memory.close();
        return j;
      } catch (Throwable throwable) {
        try {
          closeableSizeTByReference.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      try {
        memory.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public static long sysctl(int[] paramArrayOfint, long paramLong) {
    int i = OpenBsdLibc.UINT64_SIZE;
    Memory memory = new Memory(i);
    try {
      ByRef.CloseableSizeTByReference closeableSizeTByReference = new ByRef.CloseableSizeTByReference(i);
      try {
        if (0 != OpenBsdLibc.INSTANCE.sysctl(paramArrayOfint, paramArrayOfint.length, (Pointer)memory, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO)) {
          LOG.warn("Failed sysctl call: {}, Error code: {}", paramArrayOfint, Integer.valueOf(Native.getLastError()));
          long l1 = paramLong;
          closeableSizeTByReference.close();
          memory.close();
          return l1;
        } 
        long l = memory.getLong(0L);
        closeableSizeTByReference.close();
        memory.close();
        return l;
      } catch (Throwable throwable) {
        try {
          closeableSizeTByReference.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      try {
        memory.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public static String sysctl(int[] paramArrayOfint, String paramString) {
    ByRef.CloseableSizeTByReference closeableSizeTByReference = new ByRef.CloseableSizeTByReference();
    try {
      if (0 != OpenBsdLibc.INSTANCE.sysctl(paramArrayOfint, paramArrayOfint.length, null, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO)) {
        LOG.warn("Failed sysctl call: {}, Error code: {}", paramArrayOfint, Integer.valueOf(Native.getLastError()));
        String str = paramString;
        closeableSizeTByReference.close();
        return str;
      } 
      Memory memory = new Memory(closeableSizeTByReference.longValue() + 1L);
      try {
        if (0 != OpenBsdLibc.INSTANCE.sysctl(paramArrayOfint, paramArrayOfint.length, (Pointer)memory, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO)) {
          LOG.warn("Failed sysctl call: {}, Error code: {}", paramArrayOfint, Integer.valueOf(Native.getLastError()));
          String str1 = paramString;
          memory.close();
          closeableSizeTByReference.close();
          return str1;
        } 
        String str = memory.getString(0L);
        memory.close();
        closeableSizeTByReference.close();
        return str;
      } catch (Throwable throwable) {
        try {
          memory.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      try {
        closeableSizeTByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public static boolean sysctl(int[] paramArrayOfint, Structure paramStructure) {
    ByRef.CloseableSizeTByReference closeableSizeTByReference = new ByRef.CloseableSizeTByReference(paramStructure.size());
    try {
      if (0 != OpenBsdLibc.INSTANCE.sysctl(paramArrayOfint, paramArrayOfint.length, paramStructure.getPointer(), (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO)) {
        LOG.error("Failed sysctl call: {}, Error code: {}", paramArrayOfint, Integer.valueOf(Native.getLastError()));
        boolean bool = false;
        closeableSizeTByReference.close();
        return bool;
      } 
      closeableSizeTByReference.close();
    } catch (Throwable throwable) {
      try {
        closeableSizeTByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    paramStructure.read();
    return true;
  }
  
  public static Memory sysctl(int[] paramArrayOfint) {
    ByRef.CloseableSizeTByReference closeableSizeTByReference = new ByRef.CloseableSizeTByReference();
    try {
      if (0 != OpenBsdLibc.INSTANCE.sysctl(paramArrayOfint, paramArrayOfint.length, null, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO)) {
        LOG.error("Failed sysctl call: {}, Error code: {}", paramArrayOfint, Integer.valueOf(Native.getLastError()));
        Memory memory = null;
        closeableSizeTByReference.close();
        return memory;
      } 
      Memory memory1 = new Memory(closeableSizeTByReference.longValue());
      if (0 != OpenBsdLibc.INSTANCE.sysctl(paramArrayOfint, paramArrayOfint.length, (Pointer)memory1, (LibCAPI.size_t.ByReference)closeableSizeTByReference, null, LibCAPI.size_t.ZERO)) {
        LOG.error("Failed sysctl call: {}, Error code: {}", paramArrayOfint, Integer.valueOf(Native.getLastError()));
        memory1.close();
        Memory memory = null;
        closeableSizeTByReference.close();
        return memory;
      } 
      Memory memory2 = memory1;
      closeableSizeTByReference.close();
      return memory2;
    } catch (Throwable throwable) {
      try {
        closeableSizeTByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public static int sysctl(String paramString, int paramInt) {
    return ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("sysctl -n " + paramString), paramInt);
  }
  
  public static long sysctl(String paramString, long paramLong) {
    return ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("sysctl -n " + paramString), paramLong);
  }
  
  public static String sysctl(String paramString1, String paramString2) {
    String str = ExecutingCommand.getFirstAnswer("sysctl -n " + paramString1);
    return (null == str || str.isEmpty()) ? paramString2 : str;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\platfor\\unix\openbsd\OpenBsdSysctlUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
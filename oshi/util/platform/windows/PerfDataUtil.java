package oshi.util.platform.windows;

import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Pdh;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.jna.Struct;
import oshi.util.FormatUtil;
import oshi.util.ParseUtil;
import oshi.util.Util;

@ThreadSafe
public final class PerfDataUtil {
  private static final Logger LOG = LoggerFactory.getLogger(PerfDataUtil.class);
  
  private static final BaseTSD.DWORD_PTR PZERO = new BaseTSD.DWORD_PTR(0L);
  
  private static final WinDef.DWORDByReference PDH_FMT_RAW = new WinDef.DWORDByReference(new WinDef.DWORD(16L));
  
  private static final Pdh PDH = Pdh.INSTANCE;
  
  private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
  
  public static PerfCounter createCounter(String paramString1, String paramString2, String paramString3) {
    return new PerfCounter(paramString1, paramString2, paramString3);
  }
  
  public static long updateQueryTimestamp(WinNT.HANDLEByReference paramHANDLEByReference) {
    ByRef.CloseableLONGLONGByReference closeableLONGLONGByReference = new ByRef.CloseableLONGLONGByReference();
    try {
      int i = IS_VISTA_OR_GREATER ? PDH.PdhCollectQueryDataWithTime(paramHANDLEByReference.getValue(), (WinDef.LONGLONGByReference)closeableLONGLONGByReference) : PDH.PdhCollectQueryData(paramHANDLEByReference.getValue());
      byte b = 0;
      while (i == -2147481643 && b++ < 3) {
        Util.sleep((1 << b));
        i = IS_VISTA_OR_GREATER ? PDH.PdhCollectQueryDataWithTime(paramHANDLEByReference.getValue(), (WinDef.LONGLONGByReference)closeableLONGLONGByReference) : PDH.PdhCollectQueryData(paramHANDLEByReference.getValue());
      } 
      if (i != 0) {
        if (LOG.isWarnEnabled())
          LOG.warn("Failed to update counter. Error code: {}", String.format(Locale.ROOT, FormatUtil.formatError(i), new Object[0])); 
        long l1 = 0L;
        closeableLONGLONGByReference.close();
        return l1;
      } 
      long l = IS_VISTA_OR_GREATER ? ParseUtil.filetimeToUtcMs(closeableLONGLONGByReference.getValue().longValue(), true) : System.currentTimeMillis();
      closeableLONGLONGByReference.close();
      return l;
    } catch (Throwable throwable) {
      try {
        closeableLONGLONGByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public static boolean openQuery(WinNT.HANDLEByReference paramHANDLEByReference) {
    int i = PDH.PdhOpenQuery(null, PZERO, paramHANDLEByReference);
    if (i != 0) {
      if (LOG.isErrorEnabled())
        LOG.error("Failed to open PDH Query. Error code: {}", String.format(Locale.ROOT, FormatUtil.formatError(i), new Object[0])); 
      return false;
    } 
    return true;
  }
  
  public static boolean closeQuery(WinNT.HANDLEByReference paramHANDLEByReference) {
    return (0 == PDH.PdhCloseQuery(paramHANDLEByReference.getValue()));
  }
  
  public static long queryCounter(WinNT.HANDLEByReference paramHANDLEByReference) {
    Struct.CloseablePdhRawCounter closeablePdhRawCounter = new Struct.CloseablePdhRawCounter();
    try {
      int i = PDH.PdhGetRawCounterValue(paramHANDLEByReference.getValue(), PDH_FMT_RAW, (Pdh.PDH_RAW_COUNTER)closeablePdhRawCounter);
      if (i != 0) {
        if (LOG.isWarnEnabled())
          LOG.warn("Failed to get counter. Error code: {}", String.format(Locale.ROOT, FormatUtil.formatError(i), new Object[0])); 
        long l1 = i;
        closeablePdhRawCounter.close();
        return l1;
      } 
      long l = closeablePdhRawCounter.FirstValue;
      closeablePdhRawCounter.close();
      return l;
    } catch (Throwable throwable) {
      try {
        closeablePdhRawCounter.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public static long querySecondCounter(WinNT.HANDLEByReference paramHANDLEByReference) {
    Struct.CloseablePdhRawCounter closeablePdhRawCounter = new Struct.CloseablePdhRawCounter();
    try {
      int i = PDH.PdhGetRawCounterValue(paramHANDLEByReference.getValue(), PDH_FMT_RAW, (Pdh.PDH_RAW_COUNTER)closeablePdhRawCounter);
      if (i != 0) {
        if (LOG.isWarnEnabled())
          LOG.warn("Failed to get counter. Error code: {}", String.format(Locale.ROOT, FormatUtil.formatError(i), new Object[0])); 
        long l1 = i;
        closeablePdhRawCounter.close();
        return l1;
      } 
      long l = closeablePdhRawCounter.SecondValue;
      closeablePdhRawCounter.close();
      return l;
    } catch (Throwable throwable) {
      try {
        closeablePdhRawCounter.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public static boolean addCounter(WinNT.HANDLEByReference paramHANDLEByReference1, String paramString, WinNT.HANDLEByReference paramHANDLEByReference2) {
    int i = IS_VISTA_OR_GREATER ? PDH.PdhAddEnglishCounter(paramHANDLEByReference1.getValue(), paramString, PZERO, paramHANDLEByReference2) : PDH.PdhAddCounter(paramHANDLEByReference1.getValue(), paramString, PZERO, paramHANDLEByReference2);
    if (i != 0) {
      if (LOG.isWarnEnabled())
        LOG.warn("Failed to add PDH Counter: {}, Error code: {}", paramString, String.format(Locale.ROOT, FormatUtil.formatError(i), new Object[0])); 
      return false;
    } 
    return true;
  }
  
  public static boolean removeCounter(WinNT.HANDLEByReference paramHANDLEByReference) {
    return (0 == PDH.PdhRemoveCounter(paramHANDLEByReference.getValue()));
  }
  
  @Immutable
  public static class PerfCounter {
    private final String object;
    
    private final String instance;
    
    private final String counter;
    
    private final boolean baseCounter;
    
    public PerfCounter(String param1String1, String param1String2, String param1String3) {
      this.object = param1String1;
      this.instance = param1String2;
      int i = param1String3.indexOf("_Base");
      if (i > 0) {
        this.counter = param1String3.substring(0, i);
        this.baseCounter = true;
      } else {
        this.counter = param1String3;
        this.baseCounter = false;
      } 
    }
    
    public String getObject() {
      return this.object;
    }
    
    public String getInstance() {
      return this.instance;
    }
    
    public String getCounter() {
      return this.counter;
    }
    
    public boolean isBaseCounter() {
      return this.baseCounter;
    }
    
    public String getCounterPath() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append('\\').append(this.object);
      if (this.instance != null)
        stringBuilder.append('(').append(this.instance).append(')'); 
      stringBuilder.append('\\').append(this.counter);
      return stringBuilder.toString();
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\platform\windows\PerfDataUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
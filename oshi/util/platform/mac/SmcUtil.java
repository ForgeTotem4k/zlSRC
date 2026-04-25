package oshi.util.platform.mac;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.jna.ByRef;
import oshi.jna.platform.mac.IOKit;
import oshi.jna.platform.mac.SystemB;
import oshi.util.ParseUtil;

@ThreadSafe
public final class SmcUtil {
  private static final Logger LOG = LoggerFactory.getLogger(SmcUtil.class);
  
  private static final IOKit IO = IOKit.INSTANCE;
  
  private static Map<Integer, IOKit.SMCKeyDataKeyInfo> keyInfoCache = new ConcurrentHashMap<>();
  
  private static final byte[] DATATYPE_SP78 = ParseUtil.asciiStringToByteArray("sp78", 5);
  
  private static final byte[] DATATYPE_FPE2 = ParseUtil.asciiStringToByteArray("fpe2", 5);
  
  private static final byte[] DATATYPE_FLT = ParseUtil.asciiStringToByteArray("flt ", 5);
  
  public static final String SMC_KEY_FAN_NUM = "FNum";
  
  public static final String SMC_KEY_FAN_SPEED = "F%dAc";
  
  public static final String SMC_KEY_CPU_TEMP = "TC0P";
  
  public static final String SMC_KEY_CPU_VOLTAGE = "VC0C";
  
  public static final byte SMC_CMD_READ_BYTES = 5;
  
  public static final byte SMC_CMD_READ_KEYINFO = 9;
  
  public static final int KERNEL_INDEX_SMC = 2;
  
  public static IOKit.IOConnect smcOpen() {
    IOKit.IOService iOService = IOKitUtil.getMatchingService("AppleSMC");
    if (iOService != null) {
      try {
        ByRef.CloseablePointerByReference closeablePointerByReference = new ByRef.CloseablePointerByReference();
        try {
          int i = IO.IOServiceOpen(iOService, SystemB.INSTANCE.mach_task_self(), 0, (PointerByReference)closeablePointerByReference);
          if (i == 0) {
            IOKit.IOConnect iOConnect = new IOKit.IOConnect(closeablePointerByReference.getValue());
            closeablePointerByReference.close();
            return iOConnect;
          } 
          if (LOG.isErrorEnabled())
            LOG.error(String.format(Locale.ROOT, "Unable to open connection to AppleSMC service. Error: 0x%08x", new Object[] { Integer.valueOf(i) })); 
          closeablePointerByReference.close();
        } catch (Throwable throwable) {
          try {
            closeablePointerByReference.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } finally {
        iOService.release();
      } 
    } else {
      LOG.error("Unable to locate AppleSMC service");
    } 
    return null;
  }
  
  public static int smcClose(IOKit.IOConnect paramIOConnect) {
    return IO.IOServiceClose(paramIOConnect);
  }
  
  public static double smcGetFloat(IOKit.IOConnect paramIOConnect, String paramString) {
    IOKit.SMCVal sMCVal = new IOKit.SMCVal();
    try {
      int i = smcReadKey(paramIOConnect, paramString, sMCVal);
      if (i == 0 && sMCVal.dataSize > 0) {
        if (Arrays.equals(sMCVal.dataType, DATATYPE_SP78) && sMCVal.dataSize == 2) {
          double d = sMCVal.bytes[0] + sMCVal.bytes[1] / 256.0D;
          sMCVal.close();
          return d;
        } 
        if (Arrays.equals(sMCVal.dataType, DATATYPE_FPE2) && sMCVal.dataSize == 2) {
          double d = ParseUtil.byteArrayToFloat(sMCVal.bytes, sMCVal.dataSize, 2);
          sMCVal.close();
          return d;
        } 
        if (Arrays.equals(sMCVal.dataType, DATATYPE_FLT) && sMCVal.dataSize == 4) {
          double d = ByteBuffer.wrap(sMCVal.bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
          sMCVal.close();
          return d;
        } 
      } 
      sMCVal.close();
    } catch (Throwable throwable) {
      try {
        sMCVal.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return 0.0D;
  }
  
  public static long smcGetLong(IOKit.IOConnect paramIOConnect, String paramString) {
    IOKit.SMCVal sMCVal = new IOKit.SMCVal();
    try {
      int i = smcReadKey(paramIOConnect, paramString, sMCVal);
      if (i == 0) {
        long l = ParseUtil.byteArrayToLong(sMCVal.bytes, sMCVal.dataSize);
        sMCVal.close();
        return l;
      } 
      sMCVal.close();
    } catch (Throwable throwable) {
      try {
        sMCVal.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return 0L;
  }
  
  public static int smcGetKeyInfo(IOKit.IOConnect paramIOConnect, IOKit.SMCKeyData paramSMCKeyData1, IOKit.SMCKeyData paramSMCKeyData2) {
    if (keyInfoCache.containsKey(Integer.valueOf(paramSMCKeyData1.key))) {
      IOKit.SMCKeyDataKeyInfo sMCKeyDataKeyInfo = keyInfoCache.get(Integer.valueOf(paramSMCKeyData1.key));
      paramSMCKeyData2.keyInfo.dataSize = sMCKeyDataKeyInfo.dataSize;
      paramSMCKeyData2.keyInfo.dataType = sMCKeyDataKeyInfo.dataType;
      paramSMCKeyData2.keyInfo.dataAttributes = sMCKeyDataKeyInfo.dataAttributes;
    } else {
      paramSMCKeyData1.data8 = 9;
      int i = smcCall(paramIOConnect, 2, paramSMCKeyData1, paramSMCKeyData2);
      if (i != 0)
        return i; 
      IOKit.SMCKeyDataKeyInfo sMCKeyDataKeyInfo = new IOKit.SMCKeyDataKeyInfo();
      sMCKeyDataKeyInfo.dataSize = paramSMCKeyData2.keyInfo.dataSize;
      sMCKeyDataKeyInfo.dataType = paramSMCKeyData2.keyInfo.dataType;
      sMCKeyDataKeyInfo.dataAttributes = paramSMCKeyData2.keyInfo.dataAttributes;
      keyInfoCache.put(Integer.valueOf(paramSMCKeyData1.key), sMCKeyDataKeyInfo);
    } 
    return 0;
  }
  
  public static int smcReadKey(IOKit.IOConnect paramIOConnect, String paramString, IOKit.SMCVal paramSMCVal) {
    IOKit.SMCKeyData sMCKeyData = new IOKit.SMCKeyData();
    try {
      IOKit.SMCKeyData sMCKeyData1 = new IOKit.SMCKeyData();
      try {
        sMCKeyData.key = (int)ParseUtil.strToLong(paramString, 4);
        int i = smcGetKeyInfo(paramIOConnect, sMCKeyData, sMCKeyData1);
        if (i == 0) {
          paramSMCVal.dataSize = sMCKeyData1.keyInfo.dataSize;
          paramSMCVal.dataType = ParseUtil.longToByteArray(sMCKeyData1.keyInfo.dataType, 4, 5);
          sMCKeyData.keyInfo.dataSize = paramSMCVal.dataSize;
          sMCKeyData.data8 = 5;
          i = smcCall(paramIOConnect, 2, sMCKeyData, sMCKeyData1);
          if (i == 0) {
            System.arraycopy(sMCKeyData1.bytes, 0, paramSMCVal.bytes, 0, paramSMCVal.bytes.length);
            boolean bool = false;
            sMCKeyData1.close();
            sMCKeyData.close();
            return bool;
          } 
        } 
        int j = i;
        sMCKeyData1.close();
        sMCKeyData.close();
        return j;
      } catch (Throwable throwable) {
        try {
          sMCKeyData1.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      try {
        sMCKeyData.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  public static int smcCall(IOKit.IOConnect paramIOConnect, int paramInt, IOKit.SMCKeyData paramSMCKeyData1, IOKit.SMCKeyData paramSMCKeyData2) {
    ByRef.CloseableNativeLongByReference closeableNativeLongByReference = new ByRef.CloseableNativeLongByReference(new NativeLong(paramSMCKeyData2.size()));
    try {
      int i = IO.IOConnectCallStructMethod(paramIOConnect, paramInt, (Structure)paramSMCKeyData1, new NativeLong(paramSMCKeyData1.size()), (Structure)paramSMCKeyData2, (NativeLongByReference)closeableNativeLongByReference);
      closeableNativeLongByReference.close();
      return i;
    } catch (Throwable throwable) {
      try {
        closeableNativeLongByReference.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\platform\mac\SmcUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package oshi.util.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil;
import java.time.OffsetDateTime;
import java.util.Locale;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.Constants;
import oshi.util.ParseUtil;

@ThreadSafe
public final class WmiUtil {
  public static final String OHM_NAMESPACE = "ROOT\\OpenHardwareMonitor";
  
  private static final String CLASS_CAST_MSG = "%s is not a %s type. CIM Type is %d and VT type is %d";
  
  public static <T extends Enum<T>> String queryToString(WbemcliUtil.WmiQuery<T> paramWmiQuery) {
    Enum[] arrayOfEnum = paramWmiQuery.getPropertyEnum().getEnumConstants();
    StringBuilder stringBuilder = new StringBuilder("SELECT ");
    stringBuilder.append(arrayOfEnum[0].name());
    for (byte b = 1; b < arrayOfEnum.length; b++)
      stringBuilder.append(',').append(arrayOfEnum[b].name()); 
    stringBuilder.append(" FROM ").append(paramWmiQuery.getWmiClassName());
    return stringBuilder.toString();
  }
  
  public static <T extends Enum<T>> String getString(WbemcliUtil.WmiResult<T> paramWmiResult, T paramT, int paramInt) {
    if (paramWmiResult.getCIMType((Enum)paramT) == 8)
      return getStr(paramWmiResult, paramT, paramInt); 
    throw new ClassCastException(String.format(Locale.ROOT, "%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { paramT.name(), "String", Integer.valueOf(paramWmiResult.getCIMType(paramT)), Integer.valueOf(paramWmiResult.getVtType(paramT)) }));
  }
  
  public static <T extends Enum<T>> String getDateString(WbemcliUtil.WmiResult<T> paramWmiResult, T paramT, int paramInt) {
    OffsetDateTime offsetDateTime = getDateTime(paramWmiResult, paramT, paramInt);
    return offsetDateTime.equals(Constants.UNIX_EPOCH) ? "" : offsetDateTime.toLocalDate().toString();
  }
  
  public static <T extends Enum<T>> OffsetDateTime getDateTime(WbemcliUtil.WmiResult<T> paramWmiResult, T paramT, int paramInt) {
    if (paramWmiResult.getCIMType((Enum)paramT) == 101)
      return ParseUtil.parseCimDateTimeToOffset(getStr(paramWmiResult, paramT, paramInt)); 
    throw new ClassCastException(String.format(Locale.ROOT, "%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { paramT.name(), "DateTime", Integer.valueOf(paramWmiResult.getCIMType(paramT)), Integer.valueOf(paramWmiResult.getVtType(paramT)) }));
  }
  
  public static <T extends Enum<T>> String getRefString(WbemcliUtil.WmiResult<T> paramWmiResult, T paramT, int paramInt) {
    if (paramWmiResult.getCIMType((Enum)paramT) == 102)
      return getStr(paramWmiResult, paramT, paramInt); 
    throw new ClassCastException(String.format(Locale.ROOT, "%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { paramT.name(), "Reference", Integer.valueOf(paramWmiResult.getCIMType(paramT)), Integer.valueOf(paramWmiResult.getVtType(paramT)) }));
  }
  
  private static <T extends Enum<T>> String getStr(WbemcliUtil.WmiResult<T> paramWmiResult, T paramT, int paramInt) {
    Object object = paramWmiResult.getValue((Enum)paramT, paramInt);
    if (object == null)
      return ""; 
    if (paramWmiResult.getVtType((Enum)paramT) == 8)
      return (String)object; 
    throw new ClassCastException(String.format(Locale.ROOT, "%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { paramT.name(), "String-mapped", Integer.valueOf(paramWmiResult.getCIMType(paramT)), Integer.valueOf(paramWmiResult.getVtType(paramT)) }));
  }
  
  public static <T extends Enum<T>> long getUint64(WbemcliUtil.WmiResult<T> paramWmiResult, T paramT, int paramInt) {
    Object object = paramWmiResult.getValue((Enum)paramT, paramInt);
    if (object == null)
      return 0L; 
    if (paramWmiResult.getCIMType((Enum)paramT) == 21 && paramWmiResult.getVtType((Enum)paramT) == 8)
      return ParseUtil.parseLongOrDefault((String)object, 0L); 
    throw new ClassCastException(String.format(Locale.ROOT, "%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { paramT.name(), "UINT64", Integer.valueOf(paramWmiResult.getCIMType(paramT)), Integer.valueOf(paramWmiResult.getVtType(paramT)) }));
  }
  
  public static <T extends Enum<T>> int getUint32(WbemcliUtil.WmiResult<T> paramWmiResult, T paramT, int paramInt) {
    if (paramWmiResult.getCIMType((Enum)paramT) == 19)
      return getInt(paramWmiResult, paramT, paramInt); 
    throw new ClassCastException(String.format(Locale.ROOT, "%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { paramT.name(), "UINT32", Integer.valueOf(paramWmiResult.getCIMType(paramT)), Integer.valueOf(paramWmiResult.getVtType(paramT)) }));
  }
  
  public static <T extends Enum<T>> long getUint32asLong(WbemcliUtil.WmiResult<T> paramWmiResult, T paramT, int paramInt) {
    if (paramWmiResult.getCIMType((Enum)paramT) == 19)
      return getInt(paramWmiResult, paramT, paramInt) & 0xFFFFFFFFL; 
    throw new ClassCastException(String.format(Locale.ROOT, "%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { paramT.name(), "UINT32", Integer.valueOf(paramWmiResult.getCIMType(paramT)), Integer.valueOf(paramWmiResult.getVtType(paramT)) }));
  }
  
  public static <T extends Enum<T>> int getSint32(WbemcliUtil.WmiResult<T> paramWmiResult, T paramT, int paramInt) {
    if (paramWmiResult.getCIMType((Enum)paramT) == 3)
      return getInt(paramWmiResult, paramT, paramInt); 
    throw new ClassCastException(String.format(Locale.ROOT, "%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { paramT.name(), "SINT32", Integer.valueOf(paramWmiResult.getCIMType(paramT)), Integer.valueOf(paramWmiResult.getVtType(paramT)) }));
  }
  
  public static <T extends Enum<T>> int getUint16(WbemcliUtil.WmiResult<T> paramWmiResult, T paramT, int paramInt) {
    if (paramWmiResult.getCIMType((Enum)paramT) == 18)
      return getInt(paramWmiResult, paramT, paramInt); 
    throw new ClassCastException(String.format(Locale.ROOT, "%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { paramT.name(), "UINT16", Integer.valueOf(paramWmiResult.getCIMType(paramT)), Integer.valueOf(paramWmiResult.getVtType(paramT)) }));
  }
  
  private static <T extends Enum<T>> int getInt(WbemcliUtil.WmiResult<T> paramWmiResult, T paramT, int paramInt) {
    Object object = paramWmiResult.getValue((Enum)paramT, paramInt);
    if (object == null)
      return 0; 
    if (paramWmiResult.getVtType((Enum)paramT) == 3)
      return ((Integer)object).intValue(); 
    throw new ClassCastException(String.format(Locale.ROOT, "%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { paramT.name(), "32-bit integer", Integer.valueOf(paramWmiResult.getCIMType(paramT)), Integer.valueOf(paramWmiResult.getVtType(paramT)) }));
  }
  
  public static <T extends Enum<T>> float getFloat(WbemcliUtil.WmiResult<T> paramWmiResult, T paramT, int paramInt) {
    Object object = paramWmiResult.getValue((Enum)paramT, paramInt);
    if (object == null)
      return 0.0F; 
    if (paramWmiResult.getCIMType((Enum)paramT) == 4 && paramWmiResult.getVtType((Enum)paramT) == 4)
      return ((Float)object).floatValue(); 
    throw new ClassCastException(String.format(Locale.ROOT, "%s is not a %s type. CIM Type is %d and VT type is %d", new Object[] { paramT.name(), "Float", Integer.valueOf(paramWmiResult.getCIMType(paramT)), Integer.valueOf(paramWmiResult.getVtType(paramT)) }));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\platform\windows\WmiUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
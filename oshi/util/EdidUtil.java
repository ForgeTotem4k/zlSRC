package oshi.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.SuppressForbidden;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class EdidUtil {
  private static final Logger LOG = LoggerFactory.getLogger(EdidUtil.class);
  
  @SuppressForbidden(reason = "customized base 2 parsing not in Util class")
  public static String getManufacturerID(byte[] paramArrayOfbyte) {
    String str = String.format(Locale.ROOT, "%8s%8s", new Object[] { Integer.toBinaryString(paramArrayOfbyte[8] & 0xFF), Integer.toBinaryString(paramArrayOfbyte[9] & 0xFF) }).replace(' ', '0');
    LOG.debug("Manufacurer ID: {}", str);
    return String.format(Locale.ROOT, "%s%s%s", new Object[] { Character.valueOf((char)(64 + Integer.parseInt(str.substring(1, 6), 2))), Character.valueOf((char)(64 + Integer.parseInt(str.substring(6, 11), 2))), Character.valueOf((char)(64 + Integer.parseInt(str.substring(11, 16), 2))) }).replace("@", "");
  }
  
  public static String getProductID(byte[] paramArrayOfbyte) {
    return Integer.toHexString(ByteBuffer.wrap(Arrays.copyOfRange(paramArrayOfbyte, 10, 12)).order(ByteOrder.LITTLE_ENDIAN).getShort() & 0xFFFF);
  }
  
  public static String getSerialNo(byte[] paramArrayOfbyte) {
    if (LOG.isDebugEnabled())
      LOG.debug("Serial number: {}", Arrays.toString(Arrays.copyOfRange(paramArrayOfbyte, 12, 16))); 
    return String.format(Locale.ROOT, "%s%s%s%s", new Object[] { getAlphaNumericOrHex(paramArrayOfbyte[15]), getAlphaNumericOrHex(paramArrayOfbyte[14]), getAlphaNumericOrHex(paramArrayOfbyte[13]), getAlphaNumericOrHex(paramArrayOfbyte[12]) });
  }
  
  private static String getAlphaNumericOrHex(byte paramByte) {
    return Character.isLetterOrDigit((char)paramByte) ? String.format(Locale.ROOT, "%s", new Object[] { Character.valueOf((char)paramByte) }) : String.format(Locale.ROOT, "%02X", new Object[] { Byte.valueOf(paramByte) });
  }
  
  public static byte getWeek(byte[] paramArrayOfbyte) {
    return paramArrayOfbyte[16];
  }
  
  public static int getYear(byte[] paramArrayOfbyte) {
    byte b = paramArrayOfbyte[17];
    LOG.debug("Year-1990: {}", Byte.valueOf(b));
    return b + 1990;
  }
  
  public static String getVersion(byte[] paramArrayOfbyte) {
    return paramArrayOfbyte[18] + "." + paramArrayOfbyte[19];
  }
  
  public static boolean isDigital(byte[] paramArrayOfbyte) {
    return (1 == (paramArrayOfbyte[20] & 0xFF) >> 7);
  }
  
  public static int getHcm(byte[] paramArrayOfbyte) {
    return paramArrayOfbyte[21];
  }
  
  public static int getVcm(byte[] paramArrayOfbyte) {
    return paramArrayOfbyte[22];
  }
  
  public static byte[][] getDescriptors(byte[] paramArrayOfbyte) {
    byte[][] arrayOfByte = new byte[4][18];
    for (byte b = 0; b < arrayOfByte.length; b++)
      System.arraycopy(paramArrayOfbyte, 54 + 18 * b, arrayOfByte[b], 0, 18); 
    return arrayOfByte;
  }
  
  public static int getDescriptorType(byte[] paramArrayOfbyte) {
    return ByteBuffer.wrap(Arrays.copyOfRange(paramArrayOfbyte, 0, 4)).getInt();
  }
  
  public static String getTimingDescriptor(byte[] paramArrayOfbyte) {
    int i = ByteBuffer.wrap(Arrays.copyOfRange(paramArrayOfbyte, 0, 2)).order(ByteOrder.LITTLE_ENDIAN).getShort() / 100;
    int j = (paramArrayOfbyte[2] & 0xFF) + ((paramArrayOfbyte[4] & 0xF0) << 4);
    int k = (paramArrayOfbyte[5] & 0xFF) + ((paramArrayOfbyte[7] & 0xF0) << 4);
    return String.format(Locale.ROOT, "Clock %dMHz, Active Pixels %dx%d ", new Object[] { Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(k) });
  }
  
  public static String getDescriptorRangeLimits(byte[] paramArrayOfbyte) {
    return String.format(Locale.ROOT, "Field Rate %d-%d Hz vertical, %d-%d Hz horizontal, Max clock: %d MHz", new Object[] { Byte.valueOf(paramArrayOfbyte[5]), Byte.valueOf(paramArrayOfbyte[6]), Byte.valueOf(paramArrayOfbyte[7]), Byte.valueOf(paramArrayOfbyte[8]), Integer.valueOf(paramArrayOfbyte[9] * 10) });
  }
  
  public static String getDescriptorText(byte[] paramArrayOfbyte) {
    return (new String(Arrays.copyOfRange(paramArrayOfbyte, 4, 18), StandardCharsets.US_ASCII)).trim();
  }
  
  public static String toString(byte[] paramArrayOfbyte) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("  Manuf. ID=").append(getManufacturerID(paramArrayOfbyte));
    stringBuilder.append(", Product ID=").append(getProductID(paramArrayOfbyte));
    stringBuilder.append(", ").append(isDigital(paramArrayOfbyte) ? "Digital" : "Analog");
    stringBuilder.append(", Serial=").append(getSerialNo(paramArrayOfbyte));
    stringBuilder.append(", ManufDate=").append(getWeek(paramArrayOfbyte) * 12 / 52 + 1).append('/').append(getYear(paramArrayOfbyte));
    stringBuilder.append(", EDID v").append(getVersion(paramArrayOfbyte));
    int i = getHcm(paramArrayOfbyte);
    int j = getVcm(paramArrayOfbyte);
    stringBuilder.append(String.format(Locale.ROOT, "%n  %d x %d cm (%.1f x %.1f in)", new Object[] { Integer.valueOf(i), Integer.valueOf(j), Double.valueOf(i / 2.54D), Double.valueOf(j / 2.54D) }));
    byte[][] arrayOfByte = getDescriptors(paramArrayOfbyte);
    for (byte[] arrayOfByte1 : arrayOfByte) {
      switch (getDescriptorType(arrayOfByte1)) {
        case 255:
          stringBuilder.append("\n  Serial Number: ").append(getDescriptorText(arrayOfByte1));
          break;
        case 254:
          stringBuilder.append("\n  Unspecified Text: ").append(getDescriptorText(arrayOfByte1));
          break;
        case 253:
          stringBuilder.append("\n  Range Limits: ").append(getDescriptorRangeLimits(arrayOfByte1));
          break;
        case 252:
          stringBuilder.append("\n  Monitor Name: ").append(getDescriptorText(arrayOfByte1));
          break;
        case 251:
          stringBuilder.append("\n  White Point Data: ").append(ParseUtil.byteArrayToHexString(arrayOfByte1));
          break;
        case 250:
          stringBuilder.append("\n  Standard Timing ID: ").append(ParseUtil.byteArrayToHexString(arrayOfByte1));
          break;
        default:
          if (getDescriptorType(arrayOfByte1) <= 15 && getDescriptorType(arrayOfByte1) >= 0) {
            stringBuilder.append("\n  Manufacturer Data: ").append(ParseUtil.byteArrayToHexString(arrayOfByte1));
            break;
          } 
          stringBuilder.append("\n  Preferred Timing: ").append(getTimingDescriptor(arrayOfByte1));
          break;
      } 
    } 
    return stringBuilder.toString();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\EdidUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
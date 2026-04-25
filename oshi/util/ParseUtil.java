package oshi.util;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.SuppressForbidden;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
@SuppressForbidden(reason = "Require parse methods to parse in utility class")
public final class ParseUtil {
  private static final Logger LOG = LoggerFactory.getLogger(ParseUtil.class);
  
  private static final String DEFAULT_LOG_MSG = "{} didn't parse. Returning default. {}";
  
  private static final Pattern HERTZ_PATTERN = Pattern.compile("(\\d+(.\\d+)?) ?([kKMGT]?Hz).*");
  
  private static final Pattern BYTES_PATTERN = Pattern.compile("(\\d+) ?([kKMGT]?B?).*");
  
  private static final Pattern UNITS_PATTERN = Pattern.compile("(\\d+(.\\d+)?)[\\s]?([kKMGT])?");
  
  private static final Pattern VALID_HEX = Pattern.compile("[0-9a-fA-F]+");
  
  private static final Pattern DHMS = Pattern.compile("(?:(\\d+)-)?(?:(\\d+):)??(?:(\\d+):)?(\\d+)(?:\\.(\\d+))?");
  
  private static final Pattern UUID_PATTERN = Pattern.compile(".*([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}).*");
  
  private static final Pattern VENDOR_PRODUCT_ID_SERIAL = Pattern.compile(".*(?:VID|VEN)_(\\p{XDigit}{4})&(?:PID|DEV)_(\\p{XDigit}{4})(.*)\\\\(.*)");
  
  private static final Pattern LSPCI_MACHINE_READABLE = Pattern.compile("(.+)\\s\\[(.*?)\\]");
  
  private static final Pattern LSPCI_MEMORY_SIZE = Pattern.compile(".+\\s\\[size=(\\d+)([kKMGT])\\]");
  
  private static final String HZ = "Hz";
  
  private static final String KHZ = "kHz";
  
  private static final String MHZ = "MHz";
  
  private static final String GHZ = "GHz";
  
  private static final String THZ = "THz";
  
  private static final String PHZ = "PHz";
  
  private static final Map<String, Long> multipliers;
  
  private static final long EPOCH_DIFF = 11644473600000L;
  
  private static final int TZ_OFFSET = TimeZone.getDefault().getOffset(System.currentTimeMillis());
  
  public static final Pattern whitespacesColonWhitespace = Pattern.compile("\\s+:\\s");
  
  public static final Pattern whitespaces = Pattern.compile("\\s+");
  
  public static final Pattern notDigits = Pattern.compile("[^0-9]+");
  
  public static final Pattern startWithNotDigits = Pattern.compile("^[^0-9]*");
  
  public static final Pattern slash = Pattern.compile("\\/");
  
  private static final long[] POWERS_OF_TEN;
  
  private static final DateTimeFormatter CIM_FORMAT;
  
  public static long parseSpeed(String paramString) {
    return paramString.contains("T/s") ? parseHertz(paramString.replace("T/s", "Hz")) : parseHertz(paramString);
  }
  
  public static long parseHertz(String paramString) {
    Matcher matcher = HERTZ_PATTERN.matcher(paramString.trim());
    if (matcher.find()) {
      double d = Double.valueOf(matcher.group(1)).doubleValue() * ((Long)multipliers.getOrDefault(matcher.group(3), Long.valueOf(-1L))).longValue();
      if (d >= 0.0D)
        return (long)d; 
    } 
    return -1L;
  }
  
  public static int parseLastInt(String paramString, int paramInt) {
    try {
      String str = parseLastString(paramString);
      return str.toLowerCase(Locale.ROOT).startsWith("0x") ? Integer.decode(str).intValue() : Integer.parseInt(str);
    } catch (NumberFormatException numberFormatException) {
      LOG.trace("{} didn't parse. Returning default. {}", paramString, numberFormatException);
      return paramInt;
    } 
  }
  
  public static long parseLastLong(String paramString, long paramLong) {
    try {
      String str = parseLastString(paramString);
      return str.toLowerCase(Locale.ROOT).startsWith("0x") ? Long.decode(str).longValue() : Long.parseLong(str);
    } catch (NumberFormatException numberFormatException) {
      LOG.trace("{} didn't parse. Returning default. {}", paramString, numberFormatException);
      return paramLong;
    } 
  }
  
  public static double parseLastDouble(String paramString, double paramDouble) {
    try {
      return Double.parseDouble(parseLastString(paramString));
    } catch (NumberFormatException numberFormatException) {
      LOG.trace("{} didn't parse. Returning default. {}", paramString, numberFormatException);
      return paramDouble;
    } 
  }
  
  public static String parseLastString(String paramString) {
    String[] arrayOfString = whitespaces.split(paramString);
    return arrayOfString[arrayOfString.length - 1];
  }
  
  public static String byteArrayToHexString(byte[] paramArrayOfbyte) {
    StringBuilder stringBuilder = new StringBuilder(paramArrayOfbyte.length * 2);
    for (byte b : paramArrayOfbyte) {
      stringBuilder.append(Character.forDigit((b & 0xF0) >>> 4, 16));
      stringBuilder.append(Character.forDigit(b & 0xF, 16));
    } 
    return stringBuilder.toString().toUpperCase(Locale.ROOT);
  }
  
  public static byte[] hexStringToByteArray(String paramString) {
    int i = paramString.length();
    if (!VALID_HEX.matcher(paramString).matches() || (i & 0x1) != 0) {
      LOG.warn("Invalid hexadecimal string: {}", paramString);
      return new byte[0];
    } 
    byte[] arrayOfByte = new byte[i / 2];
    for (byte b = 0; b < i; b += 2)
      arrayOfByte[b / 2] = (byte)(Character.digit(paramString.charAt(b), 16) << 4 | Character.digit(paramString.charAt(b + 1), 16)); 
    return arrayOfByte;
  }
  
  public static byte[] asciiStringToByteArray(String paramString, int paramInt) {
    return Arrays.copyOf(paramString.getBytes(StandardCharsets.US_ASCII), paramInt);
  }
  
  public static byte[] longToByteArray(long paramLong, int paramInt1, int paramInt2) {
    long l = paramLong;
    byte[] arrayOfByte = new byte[8];
    for (byte b = 7; b >= 0 && l != 0L; b--) {
      arrayOfByte[b] = (byte)(int)l;
      l >>>= 8L;
    } 
    return Arrays.copyOfRange(arrayOfByte, 8 - paramInt1, 8 + paramInt2 - paramInt1);
  }
  
  public static long strToLong(String paramString, int paramInt) {
    return byteArrayToLong(paramString.getBytes(StandardCharsets.US_ASCII), paramInt);
  }
  
  public static long byteArrayToLong(byte[] paramArrayOfbyte, int paramInt) {
    return byteArrayToLong(paramArrayOfbyte, paramInt, true);
  }
  
  public static long byteArrayToLong(byte[] paramArrayOfbyte, int paramInt, boolean paramBoolean) {
    if (paramInt > 8)
      throw new IllegalArgumentException("Can't convert more than 8 bytes."); 
    if (paramInt > paramArrayOfbyte.length)
      throw new IllegalArgumentException("Size can't be larger than array length."); 
    long l = 0L;
    for (byte b = 0; b < paramInt; b++) {
      if (paramBoolean) {
        l = l << 8L | (paramArrayOfbyte[b] & 0xFF);
      } else {
        l = l << 8L | (paramArrayOfbyte[paramInt - b - 1] & 0xFF);
      } 
    } 
    return l;
  }
  
  public static float byteArrayToFloat(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    return (float)byteArrayToLong(paramArrayOfbyte, paramInt1) / (1 << paramInt2);
  }
  
  public static long unsignedIntToLong(int paramInt) {
    long l = paramInt;
    return l & 0xFFFFFFFFL;
  }
  
  public static long unsignedLongToSignedLong(long paramLong) {
    return paramLong & Long.MAX_VALUE;
  }
  
  public static String hexStringToString(String paramString) {
    if (paramString.length() % 2 > 0)
      return paramString; 
    StringBuilder stringBuilder = new StringBuilder();
    try {
      for (byte b = 0; b < paramString.length(); b += 2) {
        int i = Integer.parseInt(paramString.substring(b, b + 2), 16);
        if (i < 32 || i > 127)
          return paramString; 
        stringBuilder.append((char)i);
      } 
    } catch (NumberFormatException numberFormatException) {
      LOG.trace("{} didn't parse. Returning default. {}", paramString, numberFormatException);
      return paramString;
    } 
    return stringBuilder.toString();
  }
  
  public static int parseIntOrDefault(String paramString, int paramInt) {
    try {
      return Integer.parseInt(paramString);
    } catch (NumberFormatException numberFormatException) {
      LOG.trace("{} didn't parse. Returning default. {}", paramString, numberFormatException);
      return paramInt;
    } 
  }
  
  public static long parseLongOrDefault(String paramString, long paramLong) {
    try {
      return Long.parseLong(paramString);
    } catch (NumberFormatException numberFormatException) {
      LOG.trace("{} didn't parse. Returning default. {}", paramString, numberFormatException);
      return paramLong;
    } 
  }
  
  public static long parseUnsignedLongOrDefault(String paramString, long paramLong) {
    try {
      return (new BigInteger(paramString)).longValue();
    } catch (NumberFormatException numberFormatException) {
      LOG.trace("{} didn't parse. Returning default. {}", paramString, numberFormatException);
      return paramLong;
    } 
  }
  
  public static double parseDoubleOrDefault(String paramString, double paramDouble) {
    try {
      return Double.parseDouble(paramString);
    } catch (NumberFormatException numberFormatException) {
      LOG.trace("{} didn't parse. Returning default. {}", paramString, numberFormatException);
      return paramDouble;
    } 
  }
  
  public static long parseDHMSOrDefault(String paramString, long paramLong) {
    Matcher matcher = DHMS.matcher(paramString);
    if (matcher.matches()) {
      long l = 0L;
      if (matcher.group(1) != null)
        l += parseLongOrDefault(matcher.group(1), 0L) * 86400000L; 
      if (matcher.group(2) != null)
        l += parseLongOrDefault(matcher.group(2), 0L) * 3600000L; 
      if (matcher.group(3) != null)
        l += parseLongOrDefault(matcher.group(3), 0L) * 60000L; 
      l += parseLongOrDefault(matcher.group(4), 0L) * 1000L;
      if (matcher.group(5) != null)
        l += (long)(1000.0D * parseDoubleOrDefault("0." + matcher.group(5), 0.0D)); 
      return l;
    } 
    return paramLong;
  }
  
  public static String parseUuidOrDefault(String paramString1, String paramString2) {
    Matcher matcher = UUID_PATTERN.matcher(paramString1.toLowerCase(Locale.ROOT));
    return matcher.matches() ? matcher.group(1) : paramString2;
  }
  
  public static String getSingleQuoteStringValue(String paramString) {
    return getStringBetween(paramString, '\'');
  }
  
  public static String getDoubleQuoteStringValue(String paramString) {
    return getStringBetween(paramString, '"');
  }
  
  public static String getStringBetween(String paramString, char paramChar) {
    int i = paramString.indexOf(paramChar);
    return (i < 0) ? "" : paramString.substring(i + 1, paramString.lastIndexOf(paramChar)).trim();
  }
  
  public static int getFirstIntValue(String paramString) {
    return getNthIntValue(paramString, 1);
  }
  
  public static int getNthIntValue(String paramString, int paramInt) {
    String[] arrayOfString = notDigits.split(startWithNotDigits.matcher(paramString).replaceFirst(""));
    return (arrayOfString.length >= paramInt) ? parseIntOrDefault(arrayOfString[paramInt - 1], 0) : 0;
  }
  
  public static String removeMatchingString(String paramString1, String paramString2) {
    if (paramString1 == null || paramString1.isEmpty() || paramString2 == null || paramString2.isEmpty())
      return paramString1; 
    int i = paramString1.indexOf(paramString2, 0);
    if (i == -1)
      return paramString1; 
    StringBuilder stringBuilder = new StringBuilder(paramString1.length() - paramString2.length());
    int j = 0;
    while (true) {
      stringBuilder.append(paramString1.substring(j, i));
      j = i + paramString2.length();
      i = paramString1.indexOf(paramString2, j);
      if (i == -1) {
        stringBuilder.append(paramString1.substring(j));
        return stringBuilder.toString();
      } 
    } 
  }
  
  public static long[] parseStringToLongArray(String paramString, int[] paramArrayOfint, int paramInt, char paramChar) {
    paramString = paramString.trim();
    long[] arrayOfLong = new long[paramArrayOfint.length];
    int i = paramString.length();
    int j = paramArrayOfint.length - 1;
    int k = paramInt - 1;
    byte b = 0;
    boolean bool1 = false;
    boolean bool2 = true;
    boolean bool3 = false;
    boolean bool4 = false;
    while (--i >= 0 && j >= 0) {
      char c = paramString.charAt(i);
      if (c == paramChar) {
        if (!bool3 && bool2)
          bool3 = true; 
        if (!bool1) {
          if (bool3 && paramArrayOfint[j] == k--)
            j--; 
          bool1 = true;
          b = 0;
          bool4 = false;
          bool2 = true;
        } 
        continue;
      } 
      if (paramArrayOfint[j] != k || c == '+' || !bool2) {
        bool1 = false;
        continue;
      } 
      if (c >= '0' && c <= '9' && !bool4) {
        if (b > 18 || (b == 17 && c == '9' && arrayOfLong[j] > 223372036854775807L)) {
          arrayOfLong[j] = Long.MAX_VALUE;
        } else {
          arrayOfLong[j] = arrayOfLong[j] + (c - 48) * POWERS_OF_TEN[b++];
        } 
        bool1 = false;
        continue;
      } 
      if (c == '-') {
        arrayOfLong[j] = arrayOfLong[j] * -1L;
        bool1 = false;
        bool4 = true;
        continue;
      } 
      if (bool3) {
        if (!noLog(paramString))
          LOG.error("Illegal character parsing string '{}' to long array: {}", paramString, Character.valueOf(paramString.charAt(i))); 
        return new long[paramArrayOfint.length];
      } 
      arrayOfLong[j] = 0L;
      bool2 = false;
    } 
    if (j > 0) {
      if (!noLog(paramString))
        LOG.error("Not enough fields in string '{}' parsing to long array: {}", paramString, Integer.valueOf(paramArrayOfint.length - j)); 
      return new long[paramArrayOfint.length];
    } 
    return arrayOfLong;
  }
  
  private static boolean noLog(String paramString) {
    return paramString.startsWith("NOLOG: ");
  }
  
  public static int countStringToLongArray(String paramString, char paramChar) {
    paramString = paramString.trim();
    int i = paramString.length();
    byte b = 0;
    boolean bool1 = false;
    boolean bool2 = true;
    boolean bool3 = false;
    while (--i >= 0) {
      char c = paramString.charAt(i);
      if (c == paramChar) {
        if (!bool1) {
          if (bool2)
            b++; 
          bool1 = true;
          bool3 = false;
          bool2 = true;
        } 
        continue;
      } 
      if (c == '+' || !bool2) {
        bool1 = false;
        continue;
      } 
      if (c >= '0' && c <= '9' && !bool3) {
        bool1 = false;
        continue;
      } 
      if (c == '-') {
        bool1 = false;
        bool3 = true;
        continue;
      } 
      if (b > 0)
        return b; 
      bool2 = false;
    } 
    return b + 1;
  }
  
  public static String getTextBetweenStrings(String paramString1, String paramString2, String paramString3) {
    String str = "";
    if (paramString1.indexOf(paramString2) >= 0 && paramString1.indexOf(paramString3) >= 0) {
      str = paramString1.substring(paramString1.indexOf(paramString2) + paramString2.length(), paramString1.length());
      str = str.substring(0, str.indexOf(paramString3));
    } 
    return str;
  }
  
  public static long filetimeToUtcMs(long paramLong, boolean paramBoolean) {
    return paramLong / 10000L - 11644473600000L - (paramBoolean ? TZ_OFFSET : 0L);
  }
  
  public static String parseMmDdYyyyToYyyyMmDD(String paramString) {
    try {
      return String.format(Locale.ROOT, "%s-%s-%s", new Object[] { paramString.substring(6, 10), paramString.substring(0, 2), paramString.substring(3, 5) });
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      return paramString;
    } 
  }
  
  public static OffsetDateTime parseCimDateTimeToOffset(String paramString) {
    try {
      int i = Integer.parseInt(paramString.substring(22));
      LocalTime localTime = LocalTime.MIDNIGHT.plusMinutes(i);
      return OffsetDateTime.parse(paramString.substring(0, 22) + localTime.format(DateTimeFormatter.ISO_LOCAL_TIME), CIM_FORMAT);
    } catch (IndexOutOfBoundsException|NumberFormatException|DateTimeParseException indexOutOfBoundsException) {
      LOG.trace("Unable to parse {} to CIM DateTime.", paramString);
      return Constants.UNIX_EPOCH;
    } 
  }
  
  public static boolean filePathStartsWith(List<String> paramList, String paramString) {
    for (String str : paramList) {
      if (paramString.equals(str) || paramString.startsWith(str + "/"))
        return true; 
    } 
    return false;
  }
  
  public static long parseMultipliedToLongs(String paramString) {
    String[] arrayOfString;
    Matcher matcher = UNITS_PATTERN.matcher(paramString.trim());
    if (matcher.find() && matcher.groupCount() == 3) {
      arrayOfString = new String[2];
      arrayOfString[0] = matcher.group(1);
      arrayOfString[1] = matcher.group(3);
    } else {
      arrayOfString = new String[] { paramString };
    } 
    double d = parseDoubleOrDefault(arrayOfString[0], 0.0D);
    if (arrayOfString.length == 2 && arrayOfString[1] != null && arrayOfString[1].length() >= 1)
      switch (arrayOfString[1].charAt(0)) {
        case 'T':
          d *= 1.0E12D;
          break;
        case 'G':
          d *= 1.0E9D;
          break;
        case 'M':
          d *= 1000000.0D;
          break;
        case 'K':
        case 'k':
          d *= 1000.0D;
          break;
      }  
    return (long)d;
  }
  
  public static long parseDecimalMemorySizeToBinary(String paramString) {
    String[] arrayOfString = whitespaces.split(paramString);
    if (arrayOfString.length < 2) {
      Matcher matcher = BYTES_PATTERN.matcher(paramString.trim());
      if (matcher.find() && matcher.groupCount() == 2) {
        arrayOfString = new String[2];
        arrayOfString[0] = matcher.group(1);
        arrayOfString[1] = matcher.group(2);
      } 
    } 
    long l = parseLongOrDefault(arrayOfString[0], 0L);
    if (arrayOfString.length == 2 && arrayOfString[1].length() > 1)
      switch (arrayOfString[1].charAt(0)) {
        case 'T':
          l <<= 40L;
          break;
        case 'G':
          l <<= 30L;
          break;
        case 'M':
          l <<= 20L;
          break;
        case 'K':
        case 'k':
          l <<= 10L;
          break;
      }  
    return l;
  }
  
  public static Triplet<String, String, String> parseDeviceIdToVendorProductSerial(String paramString) {
    Matcher matcher = VENDOR_PRODUCT_ID_SERIAL.matcher(paramString);
    if (matcher.matches()) {
      String str1 = "0x" + matcher.group(1).toLowerCase(Locale.ROOT);
      String str2 = "0x" + matcher.group(2).toLowerCase(Locale.ROOT);
      String str3 = matcher.group(4);
      return new Triplet(str1, str2, (!matcher.group(3).isEmpty() || str3.contains("&")) ? "" : str3);
    } 
    return null;
  }
  
  public static long parseLshwResourceString(String paramString) {
    long l = 0L;
    String[] arrayOfString = whitespaces.split(paramString);
    for (String str : arrayOfString) {
      if (str.startsWith("memory:")) {
        String[] arrayOfString1 = str.substring(7).split("-");
        if (arrayOfString1.length == 2)
          try {
            l += Long.parseLong(arrayOfString1[1], 16) - Long.parseLong(arrayOfString1[0], 16) + 1L;
          } catch (NumberFormatException numberFormatException) {
            LOG.trace("{} didn't parse. Returning default. {}", str, numberFormatException);
          }  
      } 
    } 
    return l;
  }
  
  public static Pair<String, String> parseLspciMachineReadable(String paramString) {
    Matcher matcher = LSPCI_MACHINE_READABLE.matcher(paramString);
    return matcher.matches() ? new Pair(matcher.group(1), matcher.group(2)) : null;
  }
  
  public static long parseLspciMemorySize(String paramString) {
    Matcher matcher = LSPCI_MEMORY_SIZE.matcher(paramString);
    return matcher.matches() ? parseDecimalMemorySizeToBinary(matcher.group(1) + " " + matcher.group(2) + "B") : 0L;
  }
  
  public static List<Integer> parseHyphenatedIntList(String paramString) {
    ArrayList<Integer> arrayList = new ArrayList();
    String[] arrayOfString = paramString.split(",");
    for (String str : arrayOfString) {
      str = str.trim();
      for (String str1 : whitespaces.split(str)) {
        if (str1.contains("-")) {
          int i = getFirstIntValue(str1);
          int j = getNthIntValue(str1, 2);
          for (int k = i; k <= j; k++)
            arrayList.add(Integer.valueOf(k)); 
        } else {
          int i = parseIntOrDefault(str1, -1);
          if (i >= 0)
            arrayList.add(Integer.valueOf(i)); 
        } 
      } 
    } 
    return arrayList;
  }
  
  public static byte[] parseIntToIP(int paramInt) {
    return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(paramInt).array();
  }
  
  public static byte[] parseIntArrayToIP(int[] paramArrayOfint) {
    ByteBuffer byteBuffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
    for (int i : paramArrayOfint)
      byteBuffer.putInt(i); 
    return byteBuffer.array();
  }
  
  public static int bigEndian16ToLittleEndian(int paramInt) {
    return paramInt >> 8 & 0xFF | paramInt << 8 & 0xFF00;
  }
  
  public static String parseUtAddrV6toIP(int[] paramArrayOfint) {
    if (paramArrayOfint.length != 4)
      throw new IllegalArgumentException("ut_addr_v6 must have exactly 4 elements"); 
    if (paramArrayOfint[1] == 0 && paramArrayOfint[2] == 0 && paramArrayOfint[3] == 0) {
      if (paramArrayOfint[0] == 0)
        return "::"; 
      byte[] arrayOfByte1 = ByteBuffer.allocate(4).putInt(paramArrayOfint[0]).array();
      try {
        return InetAddress.getByAddress(arrayOfByte1).getHostAddress();
      } catch (UnknownHostException unknownHostException) {
        return "unknown";
      } 
    } 
    byte[] arrayOfByte = ByteBuffer.allocate(16).putInt(paramArrayOfint[0]).putInt(paramArrayOfint[1]).putInt(paramArrayOfint[2]).putInt(paramArrayOfint[3]).array();
    try {
      return InetAddress.getByAddress(arrayOfByte).getHostAddress().replaceAll("((?:(?:^|:)0+\\b){2,8}):?(?!\\S*\\b\\1:0+\\b)(\\S*)", "::$2");
    } catch (UnknownHostException unknownHostException) {
      return "unknown";
    } 
  }
  
  public static int hexStringToInt(String paramString, int paramInt) {
    if (paramString != null)
      try {
        return paramString.startsWith("0x") ? (new BigInteger(paramString.substring(2), 16)).intValue() : (new BigInteger(paramString, 16)).intValue();
      } catch (NumberFormatException numberFormatException) {
        LOG.trace("{} didn't parse. Returning default. {}", paramString, numberFormatException);
      }  
    return paramInt;
  }
  
  public static long hexStringToLong(String paramString, long paramLong) {
    if (paramString != null)
      try {
        return paramString.startsWith("0x") ? (new BigInteger(paramString.substring(2), 16)).longValue() : (new BigInteger(paramString, 16)).longValue();
      } catch (NumberFormatException numberFormatException) {
        LOG.trace("{} didn't parse. Returning default. {}", paramString, numberFormatException);
      }  
    return paramLong;
  }
  
  public static String removeLeadingDots(String paramString) {
    byte b;
    for (b = 0; b < paramString.length() && paramString.charAt(b) == '.'; b++);
    return (b < paramString.length()) ? paramString.substring(b) : "";
  }
  
  public static List<String> parseByteArrayToStrings(byte[] paramArrayOfbyte) {
    ArrayList<String> arrayList = new ArrayList();
    int i = 0;
    byte b = 0;
    do {
      if (b != paramArrayOfbyte.length && paramArrayOfbyte[b] != 0 && paramArrayOfbyte[b] != 10)
        continue; 
      if (i == b)
        break; 
      arrayList.add(new String(paramArrayOfbyte, i, b - i, StandardCharsets.UTF_8));
      i = b + 1;
    } while (b++ < paramArrayOfbyte.length);
    return arrayList;
  }
  
  public static Map<String, String> parseByteArrayToStringMap(byte[] paramArrayOfbyte) {
    LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
    int i = 0;
    byte b = 0;
    String str = null;
    do {
      if (b == paramArrayOfbyte.length || paramArrayOfbyte[b] == 0) {
        if (i == b && str == null)
          break; 
        linkedHashMap.put(str, new String(paramArrayOfbyte, i, b - i, StandardCharsets.UTF_8));
        str = null;
        i = b + 1;
      } else if (paramArrayOfbyte[b] == 61 && str == null) {
        str = new String(paramArrayOfbyte, i, b - i, StandardCharsets.UTF_8);
        i = b + 1;
      } 
    } while (b++ < paramArrayOfbyte.length);
    return (Map)linkedHashMap;
  }
  
  public static Map<String, String> parseCharArrayToStringMap(char[] paramArrayOfchar) {
    LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
    int i = 0;
    byte b = 0;
    String str = null;
    do {
      if (b == paramArrayOfchar.length || paramArrayOfchar[b] == '\000') {
        if (i == b && str == null)
          break; 
        linkedHashMap.put(str, new String(paramArrayOfchar, i, b - i));
        str = null;
        i = b + 1;
      } else if (paramArrayOfchar[b] == '=' && str == null) {
        str = new String(paramArrayOfchar, i, b - i);
        i = b + 1;
      } 
    } while (b++ < paramArrayOfchar.length);
    return (Map)linkedHashMap;
  }
  
  public static <K extends Enum<K>> Map<K, String> stringToEnumMap(Class<K> paramClass, String paramString, char paramChar) {
    EnumMap<K, Object> enumMap = new EnumMap<>(paramClass);
    int i = 0;
    int j = paramString.length();
    EnumSet<K> enumSet = EnumSet.allOf(paramClass);
    int k = enumSet.size();
    for (Enum enum_ : enumSet) {
      int m = (--k == 0) ? j : paramString.indexOf(paramChar, i);
      if (m >= 0) {
        enumMap.put((K)enum_, paramString.substring(i, m));
        i = m;
        do {
        
        } while (++i < j && paramString.charAt(i) == paramChar);
        continue;
      } 
      enumMap.put((K)enum_, paramString.substring(i));
    } 
    return (Map)enumMap;
  }
  
  public static String getValueOrUnknown(Map<String, String> paramMap, String paramString) {
    String str = paramMap.getOrDefault(paramString, "");
    return str.isEmpty() ? "unknown" : str;
  }
  
  public static String getValueOrUnknown(Map<?, String> paramMap, Object paramObject) {
    return getStringValueOrUnknown(paramMap.get(paramObject));
  }
  
  public static String getStringValueOrUnknown(String paramString) {
    return (paramString == null || paramString.isEmpty()) ? "unknown" : paramString;
  }
  
  public static long parseDateToEpoch(String paramString1, String paramString2) {
    if (paramString1.equals("unknown") || paramString1.isEmpty())
      return 0L; 
    try {
      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(paramString2, Locale.ROOT);
      if (paramString2.contains("H") || paramString2.contains("m") || paramString2.contains("s")) {
        LocalDateTime localDateTime = LocalDateTime.parse(paramString1, dateTimeFormatter);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
      } 
      LocalDate localDate = LocalDate.parse(paramString1, dateTimeFormatter);
      return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    } catch (DateTimeParseException dateTimeParseException) {
      LOG.trace("Unable to parse date string: " + paramString1);
      return 0L;
    } 
  }
  
  static {
    multipliers = new HashMap<>();
    multipliers.put("Hz", Long.valueOf(1L));
    multipliers.put("kHz", Long.valueOf(1000L));
    multipliers.put("MHz", Long.valueOf(1000000L));
    multipliers.put("GHz", Long.valueOf(1000000000L));
    multipliers.put("THz", Long.valueOf(1000000000000L));
    multipliers.put("PHz", Long.valueOf(1000000000000000L));
    POWERS_OF_TEN = new long[] { 
        1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 
        10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L };
    CIM_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSSSSSZZZZZ", Locale.US);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\ParseUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
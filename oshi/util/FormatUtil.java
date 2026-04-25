package oshi.util;

import java.math.BigInteger;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class FormatUtil {
  private static final long KIBI = 1024L;
  
  private static final long MEBI = 1048576L;
  
  private static final long GIBI = 1073741824L;
  
  private static final long TEBI = 1099511627776L;
  
  private static final long PEBI = 1125899906842624L;
  
  private static final long EXBI = 1152921504606846976L;
  
  private static final long KILO = 1000L;
  
  private static final long MEGA = 1000000L;
  
  private static final long GIGA = 1000000000L;
  
  private static final long TERA = 1000000000000L;
  
  private static final long PETA = 1000000000000000L;
  
  private static final long EXA = 1000000000000000000L;
  
  private static final BigInteger TWOS_COMPLEMENT_REF = BigInteger.ONE.shiftLeft(64);
  
  public static final String HEX_ERROR = "0x%08X";
  
  public static String formatBytes(long paramLong) {
    return (paramLong == 1L) ? String.format(Locale.ROOT, "%d byte", new Object[] { Long.valueOf(paramLong) }) : ((paramLong < 1024L) ? String.format(Locale.ROOT, "%d bytes", new Object[] { Long.valueOf(paramLong) }) : ((paramLong < 1048576L) ? formatUnits(paramLong, 1024L, "KiB") : ((paramLong < 1073741824L) ? formatUnits(paramLong, 1048576L, "MiB") : ((paramLong < 1099511627776L) ? formatUnits(paramLong, 1073741824L, "GiB") : ((paramLong < 1125899906842624L) ? formatUnits(paramLong, 1099511627776L, "TiB") : ((paramLong < 1152921504606846976L) ? formatUnits(paramLong, 1125899906842624L, "PiB") : formatUnits(paramLong, 1152921504606846976L, "EiB")))))));
  }
  
  private static String formatUnits(long paramLong1, long paramLong2, String paramString) {
    return (paramLong1 % paramLong2 == 0L) ? String.format(Locale.ROOT, "%d %s", new Object[] { Long.valueOf(paramLong1 / paramLong2), paramString }) : String.format(Locale.ROOT, "%.1f %s", new Object[] { Double.valueOf(paramLong1 / paramLong2), paramString });
  }
  
  public static String formatBytesDecimal(long paramLong) {
    return (paramLong == 1L) ? String.format(Locale.ROOT, "%d byte", new Object[] { Long.valueOf(paramLong) }) : ((paramLong < 1000L) ? String.format(Locale.ROOT, "%d bytes", new Object[] { Long.valueOf(paramLong) }) : formatValue(paramLong, "B"));
  }
  
  public static String formatHertz(long paramLong) {
    return formatValue(paramLong, "Hz");
  }
  
  public static String formatValue(long paramLong, String paramString) {
    return (paramLong < 1000L) ? String.format(Locale.ROOT, "%d %s", new Object[] { Long.valueOf(paramLong), paramString }).trim() : ((paramLong < 1000000L) ? formatUnits(paramLong, 1000L, "K" + paramString) : ((paramLong < 1000000000L) ? formatUnits(paramLong, 1000000L, "M" + paramString) : ((paramLong < 1000000000000L) ? formatUnits(paramLong, 1000000000L, "G" + paramString) : ((paramLong < 1000000000000000L) ? formatUnits(paramLong, 1000000000000L, "T" + paramString) : ((paramLong < 1000000000000000000L) ? formatUnits(paramLong, 1000000000000000L, "P" + paramString) : formatUnits(paramLong, 1000000000000000000L, "E" + paramString))))));
  }
  
  public static String formatElapsedSecs(long paramLong) {
    long l1 = paramLong;
    long l2 = TimeUnit.SECONDS.toDays(l1);
    l1 -= TimeUnit.DAYS.toSeconds(l2);
    long l3 = TimeUnit.SECONDS.toHours(l1);
    l1 -= TimeUnit.HOURS.toSeconds(l3);
    long l4 = TimeUnit.SECONDS.toMinutes(l1);
    l1 -= TimeUnit.MINUTES.toSeconds(l4);
    long l5 = l1;
    return String.format(Locale.ROOT, "%d days, %02d:%02d:%02d", new Object[] { Long.valueOf(l2), Long.valueOf(l3), Long.valueOf(l4), Long.valueOf(l5) });
  }
  
  public static long getUnsignedInt(int paramInt) {
    return paramInt & 0xFFFFFFFFL;
  }
  
  public static String toUnsignedString(int paramInt) {
    return (paramInt >= 0) ? Integer.toString(paramInt) : Long.toString(getUnsignedInt(paramInt));
  }
  
  public static String toUnsignedString(long paramLong) {
    return (paramLong >= 0L) ? Long.toString(paramLong) : BigInteger.valueOf(paramLong).add(TWOS_COMPLEMENT_REF).toString();
  }
  
  public static String formatError(int paramInt) {
    return String.format(Locale.ROOT, "0x%08X", new Object[] { Integer.valueOf(paramInt) });
  }
  
  public static int roundToInt(double paramDouble) {
    return (int)Math.round(paramDouble);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\FormatUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
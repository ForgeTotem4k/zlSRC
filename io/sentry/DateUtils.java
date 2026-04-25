package io.sentry;

import io.sentry.vendor.gson.internal.bind.util.ISO8601Utils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class DateUtils {
  @NotNull
  public static Date getCurrentDateTime() {
    Calendar calendar = Calendar.getInstance(ISO8601Utils.TIMEZONE_UTC);
    return calendar.getTime();
  }
  
  @NotNull
  public static Date getDateTime(@NotNull String paramString) throws IllegalArgumentException {
    try {
      return ISO8601Utils.parse(paramString, new ParsePosition(0));
    } catch (ParseException parseException) {
      throw new IllegalArgumentException("timestamp is not ISO format " + paramString);
    } 
  }
  
  @NotNull
  public static Date getDateTimeWithMillisPrecision(@NotNull String paramString) throws IllegalArgumentException {
    try {
      return getDateTime((new BigDecimal(paramString)).setScale(3, RoundingMode.DOWN).movePointRight(3).longValue());
    } catch (NumberFormatException numberFormatException) {
      throw new IllegalArgumentException("timestamp is not millis format " + paramString);
    } 
  }
  
  @NotNull
  public static String getTimestamp(@NotNull Date paramDate) {
    return ISO8601Utils.format(paramDate, true);
  }
  
  @NotNull
  public static Date getDateTime(long paramLong) {
    Calendar calendar = Calendar.getInstance(ISO8601Utils.TIMEZONE_UTC);
    calendar.setTimeInMillis(paramLong);
    return calendar.getTime();
  }
  
  public static double millisToSeconds(double paramDouble) {
    return paramDouble / 1000.0D;
  }
  
  public static long millisToNanos(long paramLong) {
    return paramLong * 1000000L;
  }
  
  public static double nanosToMillis(double paramDouble) {
    return paramDouble / 1000000.0D;
  }
  
  public static Date nanosToDate(long paramLong) {
    Double double_ = Double.valueOf(nanosToMillis(Double.valueOf(paramLong).doubleValue()));
    return getDateTime(double_.longValue());
  }
  
  @Nullable
  public static Date toUtilDate(@Nullable SentryDate paramSentryDate) {
    return (paramSentryDate == null) ? null : toUtilDateNotNull(paramSentryDate);
  }
  
  @NotNull
  public static Date toUtilDateNotNull(@NotNull SentryDate paramSentryDate) {
    return nanosToDate(paramSentryDate.nanoTimestamp());
  }
  
  public static double nanosToSeconds(long paramLong) {
    return Double.valueOf(paramLong).doubleValue() / 1.0E9D;
  }
  
  public static double dateToSeconds(@NotNull Date paramDate) {
    return millisToSeconds(paramDate.getTime());
  }
  
  public static long dateToNanos(@NotNull Date paramDate) {
    return millisToNanos(paramDate.getTime());
  }
  
  public static long secondsToNanos(@NotNull long paramLong) {
    return paramLong * 1000000000L;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\DateUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
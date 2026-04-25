package io.sentry.util;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.StringCharacterIterator;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class StringUtils {
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  
  private static final String CORRUPTED_NIL_UUID = "0000-0000";
  
  private static final String PROPER_NIL_UUID = "00000000-0000-0000-0000-000000000000";
  
  @NotNull
  private static final Pattern PATTERN_WORD_SNAKE_CASE = Pattern.compile("[\\W_]+");
  
  @Nullable
  public static String getStringAfterDot(@Nullable String paramString) {
    if (paramString != null) {
      int i = paramString.lastIndexOf(".");
      return (i >= 0 && paramString.length() > i + 1) ? paramString.substring(i + 1) : paramString;
    } 
    return null;
  }
  
  @Nullable
  public static String capitalize(@Nullable String paramString) {
    return (paramString == null || paramString.isEmpty()) ? paramString : (paramString.substring(0, 1).toUpperCase(Locale.ROOT) + paramString.substring(1).toLowerCase(Locale.ROOT));
  }
  
  @Nullable
  public static String camelCase(@Nullable String paramString) {
    if (paramString == null || paramString.isEmpty())
      return paramString; 
    String[] arrayOfString = PATTERN_WORD_SNAKE_CASE.split(paramString, -1);
    StringBuilder stringBuilder = new StringBuilder();
    for (String str : arrayOfString)
      stringBuilder.append(capitalize(str)); 
    return stringBuilder.toString();
  }
  
  @Nullable
  public static String removeSurrounding(@Nullable String paramString1, @Nullable String paramString2) {
    return (paramString1 != null && paramString2 != null && paramString1.startsWith(paramString2) && paramString1.endsWith(paramString2)) ? paramString1.substring(paramString2.length(), paramString1.length() - paramString2.length()) : paramString1;
  }
  
  @NotNull
  public static String byteCountToString(long paramLong) {
    if (-1000L < paramLong && paramLong < 1000L)
      return paramLong + " B"; 
    StringCharacterIterator stringCharacterIterator = new StringCharacterIterator("kMGTPE");
    while (true) {
      if (paramLong <= -999950L || paramLong >= 999950L) {
        paramLong /= 1000L;
        stringCharacterIterator.next();
        continue;
      } 
      return String.format(Locale.ROOT, "%.1f %cB", new Object[] { Double.valueOf(paramLong / 1000.0D), Character.valueOf(stringCharacterIterator.current()) });
    } 
  }
  
  @Nullable
  public static String calculateStringHash(@Nullable String paramString, @NotNull ILogger paramILogger) {
    if (paramString == null || paramString.isEmpty())
      return null; 
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
      byte[] arrayOfByte = messageDigest.digest(paramString.getBytes(UTF_8));
      BigInteger bigInteger = new BigInteger(1, arrayOfByte);
      StringBuilder stringBuilder = new StringBuilder(bigInteger.toString(16));
      return stringBuilder.toString();
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      paramILogger.log(SentryLevel.INFO, "SHA-1 isn't available to calculate the hash.", noSuchAlgorithmException);
    } catch (Throwable throwable) {
      paramILogger.log(SentryLevel.INFO, "string: %s could not calculate its hash", new Object[] { throwable, paramString });
    } 
    return null;
  }
  
  public static int countOf(@NotNull String paramString, char paramChar) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramString.length(); b2++) {
      if (paramString.charAt(b2) == paramChar)
        b1++; 
    } 
    return b1;
  }
  
  public static String normalizeUUID(@NotNull String paramString) {
    return paramString.equals("0000-0000") ? "00000000-0000-0000-0000-000000000000" : paramString;
  }
  
  public static String join(@NotNull CharSequence paramCharSequence, @NotNull Iterable<? extends CharSequence> paramIterable) {
    StringBuilder stringBuilder = new StringBuilder();
    Iterator<? extends CharSequence> iterator = paramIterable.iterator();
    if (iterator.hasNext()) {
      stringBuilder.append(iterator.next());
      while (iterator.hasNext()) {
        stringBuilder.append(paramCharSequence);
        stringBuilder.append(iterator.next());
      } 
    } 
    return stringBuilder.toString();
  }
  
  @Nullable
  public static String toString(@Nullable Object paramObject) {
    return (paramObject == null) ? null : paramObject.toString();
  }
  
  @NotNull
  public static String removePrefix(@Nullable String paramString1, @NotNull String paramString2) {
    if (paramString1 == null)
      return ""; 
    int i = paramString1.indexOf(paramString2);
    return (i == 0) ? paramString1.substring(paramString2.length()) : paramString1;
  }
  
  @NotNull
  public static String substringBefore(@Nullable String paramString1, @NotNull String paramString2) {
    if (paramString1 == null)
      return ""; 
    int i = paramString1.indexOf(paramString2);
    return (i >= 0) ? paramString1.substring(0, i) : paramString1;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\StringUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
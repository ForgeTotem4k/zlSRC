package pro.gravit.utils.helper;

import java.util.Map;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class VerifyHelper {
  public static final IntPredicate POSITIVE;
  
  public static final IntPredicate NOT_NEGATIVE;
  
  public static final LongPredicate L_POSITIVE;
  
  public static final LongPredicate L_NOT_NEGATIVE;
  
  public static final Predicate<String> NOT_EMPTY;
  
  public static final Pattern USERNAME_PATTERN = Pattern.compile(Boolean.parseBoolean(System.getProperty("username.russian", "true")) ? "[a-zA-Zа-яА-Я0-9_.\\-]{1,16}" : "[a-zA-Z0-9-_\\\\.]{1,16}");
  
  private static final Pattern SERVERID_PATTERN = Pattern.compile("-?[0-9a-f]{1,40}");
  
  public static <K, V> V getMapValue(Map<K, V> paramMap, K paramK, String paramString) {
    return verify(paramMap.get(paramK), Objects::nonNull, paramString);
  }
  
  public static boolean isValidIDName(String paramString) {
    return (!paramString.isEmpty() && paramString.length() <= 255 && paramString.chars().allMatch(VerifyHelper::isValidIDNameChar));
  }
  
  public static boolean isValidIDNameChar(int paramInt) {
    return ((paramInt >= 97 && paramInt <= 122) || (paramInt >= 65 && paramInt <= 90) || (paramInt >= 48 && paramInt <= 57) || paramInt == 45 || paramInt == 95);
  }
  
  public static boolean isValidServerID(CharSequence paramCharSequence) {
    return SERVERID_PATTERN.matcher(paramCharSequence).matches();
  }
  
  public static boolean isValidUsername(CharSequence paramCharSequence) {
    return USERNAME_PATTERN.matcher(paramCharSequence).matches();
  }
  
  public static <K, V> void putIfAbsent(Map<K, V> paramMap, K paramK, V paramV, String paramString) {
    verify(paramMap.putIfAbsent(paramK, paramV), Objects::isNull, paramString);
  }
  
  public static IntPredicate range(int paramInt1, int paramInt2) {
    return paramInt3 -> (paramInt3 >= paramInt1 && paramInt3 <= paramInt2);
  }
  
  public static <T> T verify(T paramT, Predicate<T> paramPredicate, String paramString) {
    if (paramPredicate.test(paramT))
      return paramT; 
    throw new IllegalArgumentException(paramString);
  }
  
  public static double verifyDouble(double paramDouble, DoublePredicate paramDoublePredicate, String paramString) {
    if (paramDoublePredicate.test(paramDouble))
      return paramDouble; 
    throw new IllegalArgumentException(paramString);
  }
  
  public static void verifyIDName(String paramString) {
    verify(paramString, VerifyHelper::isValidIDName, String.format("Invalid name: '%s'", new Object[] { paramString }));
  }
  
  public static int verifyInt(int paramInt, IntPredicate paramIntPredicate, String paramString) {
    if (paramIntPredicate.test(paramInt))
      return paramInt; 
    throw new IllegalArgumentException(paramString);
  }
  
  public static long verifyLong(long paramLong, LongPredicate paramLongPredicate, String paramString) {
    if (paramLongPredicate.test(paramLong))
      return paramLong; 
    throw new IllegalArgumentException(paramString);
  }
  
  public static String verifyServerID(String paramString) {
    return verify(paramString, VerifyHelper::isValidServerID, String.format("Invalid server ID: '%s'", new Object[] { paramString }));
  }
  
  public static String verifyUsername(String paramString) {
    return verify(paramString, VerifyHelper::isValidUsername, String.format("Invalid username: '%s'", new Object[] { paramString }));
  }
  
  static {
    POSITIVE = (paramInt -> (paramInt > 0));
    NOT_NEGATIVE = (paramInt -> (paramInt >= 0));
    L_POSITIVE = (paramLong -> (paramLong > 0L));
    L_NOT_NEGATIVE = (paramLong -> (paramLong >= 0L));
    NOT_EMPTY = (paramString -> !paramString.isEmpty());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\VerifyHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
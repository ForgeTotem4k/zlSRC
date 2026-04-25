package oshi.util;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Util {
  private static final Logger LOG = LoggerFactory.getLogger(Util.class);
  
  public static void sleep(long paramLong) {
    try {
      LOG.trace("Sleeping for {} ms", Long.valueOf(paramLong));
      Thread.sleep(paramLong);
    } catch (InterruptedException interruptedException) {
      LOG.warn("Interrupted while sleeping for {} ms: {}", Long.valueOf(paramLong), interruptedException.getMessage());
      Thread.currentThread().interrupt();
    } 
  }
  
  public static boolean wildcardMatch(String paramString1, String paramString2) {
    return (paramString2.length() > 0 && paramString2.charAt(0) == '^') ? (!wildcardMatch(paramString1, paramString2.substring(1))) : paramString1.matches(paramString2.replace("?", ".?").replace("*", ".*?"));
  }
  
  public static boolean isBlank(String paramString) {
    return (paramString == null || paramString.isEmpty());
  }
  
  public static boolean isBlankOrUnknown(String paramString) {
    return (isBlank(paramString) || "unknown".equals(paramString));
  }
  
  public static void freeMemory(Pointer paramPointer) {
    if (paramPointer instanceof Memory)
      ((Memory)paramPointer).close(); 
  }
  
  public static boolean isSessionValid(String paramString1, String paramString2, Long paramLong) {
    return (!paramString1.isEmpty() && !paramString2.isEmpty() && paramLong.longValue() >= 0L && paramLong.longValue() <= System.currentTimeMillis());
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\osh\\util\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
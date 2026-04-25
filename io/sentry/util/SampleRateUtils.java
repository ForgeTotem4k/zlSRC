package io.sentry.util;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
public final class SampleRateUtils {
  public static boolean isValidSampleRate(@Nullable Double paramDouble) {
    return isValidRate(paramDouble, true);
  }
  
  public static boolean isValidTracesSampleRate(@Nullable Double paramDouble) {
    return isValidTracesSampleRate(paramDouble, true);
  }
  
  public static boolean isValidTracesSampleRate(@Nullable Double paramDouble, boolean paramBoolean) {
    return isValidRate(paramDouble, paramBoolean);
  }
  
  public static boolean isValidProfilesSampleRate(@Nullable Double paramDouble) {
    return isValidRate(paramDouble, true);
  }
  
  private static boolean isValidRate(@Nullable Double paramDouble, boolean paramBoolean) {
    return (paramDouble == null) ? paramBoolean : ((!paramDouble.isNaN() && paramDouble.doubleValue() >= 0.0D && paramDouble.doubleValue() <= 1.0D));
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\SampleRateUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry.util;

import java.util.Arrays;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class Objects {
  public static <T> T requireNonNull(@Nullable T paramT, @NotNull String paramString) {
    if (paramT == null)
      throw new IllegalArgumentException(paramString); 
    return paramT;
  }
  
  public static boolean equals(@Nullable Object paramObject1, @Nullable Object paramObject2) {
    return (paramObject1 == paramObject2 || (paramObject1 != null && paramObject1.equals(paramObject2)));
  }
  
  public static int hash(@Nullable Object... paramVarArgs) {
    return Arrays.hashCode(paramVarArgs);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\Objects.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
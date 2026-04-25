package io.sentry;

import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
public final class IpAddressUtils {
  public static final String DEFAULT_IP_ADDRESS = "{{auto}}";
  
  private static final List<String> DEFAULT_IP_ADDRESS_VALID_VALUES = Arrays.asList(new String[] { "{{auto}}", "{{ auto }}" });
  
  public static boolean isDefault(@Nullable String paramString) {
    return (paramString != null && DEFAULT_IP_ADDRESS_VALID_VALUES.contains(paramString));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\IpAddressUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
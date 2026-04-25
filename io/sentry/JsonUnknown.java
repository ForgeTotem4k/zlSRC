package io.sentry;

import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
public interface JsonUnknown {
  @Nullable
  Map<String, Object> getUnknown();
  
  void setUnknown(@Nullable Map<String, Object> paramMap);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\JsonUnknown.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
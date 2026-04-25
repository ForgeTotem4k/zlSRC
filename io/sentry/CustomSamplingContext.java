package io.sentry;

import io.sentry.util.Objects;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CustomSamplingContext {
  @NotNull
  private final Map<String, Object> data = new HashMap<>();
  
  public void set(@NotNull String paramString, @Nullable Object paramObject) {
    Objects.requireNonNull(paramString, "key is required");
    this.data.put(paramString, paramObject);
  }
  
  @Nullable
  public Object get(@NotNull String paramString) {
    Objects.requireNonNull(paramString, "key is required");
    return this.data.get(paramString);
  }
  
  @NotNull
  public Map<String, Object> getData() {
    return this.data;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\CustomSamplingContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
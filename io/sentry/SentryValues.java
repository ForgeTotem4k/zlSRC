package io.sentry;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class SentryValues<T> {
  private final List<T> values;
  
  SentryValues(@Nullable List<T> paramList) {
    if (paramList == null)
      paramList = new ArrayList<>(0); 
    this.values = new ArrayList<>(paramList);
  }
  
  @NotNull
  public List<T> getValues() {
    return this.values;
  }
  
  public static final class JsonKeys {
    public static final String VALUES = "values";
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryValues.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
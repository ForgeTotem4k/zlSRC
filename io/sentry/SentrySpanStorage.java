package io.sentry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
@Internal
public final class SentrySpanStorage {
  @Nullable
  private static volatile SentrySpanStorage INSTANCE;
  
  @NotNull
  private final Map<String, ISpan> spans = new ConcurrentHashMap<>();
  
  @NotNull
  public static SentrySpanStorage getInstance() {
    if (INSTANCE == null)
      synchronized (SentrySpanStorage.class) {
        if (INSTANCE == null)
          INSTANCE = new SentrySpanStorage(); 
      }  
    return INSTANCE;
  }
  
  public void store(@NotNull String paramString, @NotNull ISpan paramISpan) {
    this.spans.put(paramString, paramISpan);
  }
  
  @Nullable
  public ISpan get(@Nullable String paramString) {
    return this.spans.get(paramString);
  }
  
  @Nullable
  public ISpan removeAndGet(@Nullable String paramString) {
    return this.spans.remove(paramString);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentrySpanStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
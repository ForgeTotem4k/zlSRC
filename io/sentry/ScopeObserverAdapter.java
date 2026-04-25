package io.sentry;

import io.sentry.protocol.Contexts;
import io.sentry.protocol.Request;
import io.sentry.protocol.User;
import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ScopeObserverAdapter implements IScopeObserver {
  public void setUser(@Nullable User paramUser) {}
  
  public void addBreadcrumb(@NotNull Breadcrumb paramBreadcrumb) {}
  
  public void setBreadcrumbs(@NotNull Collection<Breadcrumb> paramCollection) {}
  
  public void setTag(@NotNull String paramString1, @NotNull String paramString2) {}
  
  public void removeTag(@NotNull String paramString) {}
  
  public void setTags(@NotNull Map<String, String> paramMap) {}
  
  public void setExtra(@NotNull String paramString1, @NotNull String paramString2) {}
  
  public void removeExtra(@NotNull String paramString) {}
  
  public void setExtras(@NotNull Map<String, Object> paramMap) {}
  
  public void setRequest(@Nullable Request paramRequest) {}
  
  public void setFingerprint(@NotNull Collection<String> paramCollection) {}
  
  public void setLevel(@Nullable SentryLevel paramSentryLevel) {}
  
  public void setContexts(@NotNull Contexts paramContexts) {}
  
  public void setTransaction(@Nullable String paramString) {}
  
  public void setTrace(@Nullable SpanContext paramSpanContext) {}
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\ScopeObserverAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
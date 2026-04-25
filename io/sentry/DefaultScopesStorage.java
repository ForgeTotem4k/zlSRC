package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class DefaultScopesStorage implements IScopesStorage {
  @NotNull
  private static final ThreadLocal<IScopes> currentScopes = new ThreadLocal<>();
  
  public ISentryLifecycleToken set(@Nullable IScopes paramIScopes) {
    IScopes iScopes = get();
    currentScopes.set(paramIScopes);
    return new DefaultScopesLifecycleToken(iScopes);
  }
  
  @Nullable
  public IScopes get() {
    return currentScopes.get();
  }
  
  public void close() {
    currentScopes.remove();
  }
  
  static final class DefaultScopesLifecycleToken implements ISentryLifecycleToken {
    @Nullable
    private final IScopes oldValue;
    
    DefaultScopesLifecycleToken(@Nullable IScopes param1IScopes) {
      this.oldValue = param1IScopes;
    }
    
    public void close() {
      DefaultScopesStorage.currentScopes.set(this.oldValue);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\DefaultScopesStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
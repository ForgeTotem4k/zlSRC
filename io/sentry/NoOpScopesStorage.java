package io.sentry;

import org.jetbrains.annotations.Nullable;

public final class NoOpScopesStorage implements IScopesStorage {
  private static final NoOpScopesStorage instance = new NoOpScopesStorage();
  
  public static NoOpScopesStorage getInstance() {
    return instance;
  }
  
  public ISentryLifecycleToken set(@Nullable IScopes paramIScopes) {
    return NoOpScopesLifecycleToken.getInstance();
  }
  
  @Nullable
  public IScopes get() {
    return NoOpScopes.getInstance();
  }
  
  public void close() {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpScopesStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
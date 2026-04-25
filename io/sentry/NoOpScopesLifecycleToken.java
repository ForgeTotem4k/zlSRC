package io.sentry;

public final class NoOpScopesLifecycleToken implements ISentryLifecycleToken {
  private static final NoOpScopesLifecycleToken instance = new NoOpScopesLifecycleToken();
  
  public static NoOpScopesLifecycleToken getInstance() {
    return instance;
  }
  
  public void close() {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpScopesLifecycleToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
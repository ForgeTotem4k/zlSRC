package io.sentry;

public interface ISentryLifecycleToken extends AutoCloseable {
  void close();
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\ISentryLifecycleToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
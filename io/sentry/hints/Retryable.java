package io.sentry.hints;

public interface Retryable {
  boolean isRetry();
  
  void setRetry(boolean paramBoolean);
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\hints\Retryable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
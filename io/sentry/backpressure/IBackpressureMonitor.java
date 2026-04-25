package io.sentry.backpressure;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IBackpressureMonitor {
  void start();
  
  int getDownsampleFactor();
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\backpressure\IBackpressureMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
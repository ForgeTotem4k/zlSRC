package io.sentry.backpressure;

public final class NoOpBackpressureMonitor implements IBackpressureMonitor {
  private static final NoOpBackpressureMonitor instance = new NoOpBackpressureMonitor();
  
  public static NoOpBackpressureMonitor getInstance() {
    return instance;
  }
  
  public void start() {}
  
  public int getDownsampleFactor() {
    return 0;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\backpressure\NoOpBackpressureMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
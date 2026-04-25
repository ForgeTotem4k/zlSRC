package io.sentry.transport;

public final class NoOpTransportGate implements ITransportGate {
  private static final NoOpTransportGate instance = new NoOpTransportGate();
  
  public static NoOpTransportGate getInstance() {
    return instance;
  }
  
  public boolean isConnected() {
    return true;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\NoOpTransportGate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
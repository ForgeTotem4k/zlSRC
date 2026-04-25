package io.sentry.transport;

import org.jetbrains.annotations.NotNull;

public abstract class TransportResult {
  @NotNull
  public static TransportResult success() {
    return SuccessTransportResult.INSTANCE;
  }
  
  @NotNull
  public static TransportResult error(int paramInt) {
    return new ErrorTransportResult(paramInt);
  }
  
  @NotNull
  public static TransportResult error() {
    return error(-1);
  }
  
  private TransportResult() {}
  
  public abstract boolean isSuccess();
  
  public abstract int getResponseCode();
  
  static {
  
  }
  
  private static final class SuccessTransportResult extends TransportResult {
    static final SuccessTransportResult INSTANCE = new SuccessTransportResult();
    
    public boolean isSuccess() {
      return true;
    }
    
    public int getResponseCode() {
      return -1;
    }
  }
  
  private static final class ErrorTransportResult extends TransportResult {
    private final int responseCode;
    
    ErrorTransportResult(int param1Int) {
      this.responseCode = param1Int;
    }
    
    public boolean isSuccess() {
      return false;
    }
    
    public int getResponseCode() {
      return this.responseCode;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\TransportResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
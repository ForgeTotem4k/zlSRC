package io.sentry;

public final class HttpStatusCodeRange {
  public static final int DEFAULT_MIN = 500;
  
  public static final int DEFAULT_MAX = 599;
  
  private final int min;
  
  private final int max;
  
  public HttpStatusCodeRange(int paramInt1, int paramInt2) {
    this.min = paramInt1;
    this.max = paramInt2;
  }
  
  public HttpStatusCodeRange(int paramInt) {
    this.min = paramInt;
    this.max = paramInt;
  }
  
  public boolean isInRange(int paramInt) {
    return (paramInt >= this.min && paramInt <= this.max);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\HttpStatusCodeRange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry;

public final class SentryLongDate extends SentryDate {
  private final long nanos;
  
  public SentryLongDate(long paramLong) {
    this.nanos = paramLong;
  }
  
  public long nanoTimestamp() {
    return this.nanos;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryLongDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
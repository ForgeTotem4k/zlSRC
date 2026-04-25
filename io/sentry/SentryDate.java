package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SentryDate implements Comparable<SentryDate> {
  public abstract long nanoTimestamp();
  
  public long laterDateNanosTimestampByDiff(@Nullable SentryDate paramSentryDate) {
    return (paramSentryDate != null && compareTo(paramSentryDate) < 0) ? paramSentryDate.nanoTimestamp() : nanoTimestamp();
  }
  
  public long diff(@NotNull SentryDate paramSentryDate) {
    return nanoTimestamp() - paramSentryDate.nanoTimestamp();
  }
  
  public final boolean isBefore(@NotNull SentryDate paramSentryDate) {
    return (diff(paramSentryDate) < 0L);
  }
  
  public final boolean isAfter(@NotNull SentryDate paramSentryDate) {
    return (diff(paramSentryDate) > 0L);
  }
  
  public int compareTo(@NotNull SentryDate paramSentryDate) {
    return Long.valueOf(nanoTimestamp()).compareTo(Long.valueOf(paramSentryDate.nanoTimestamp()));
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
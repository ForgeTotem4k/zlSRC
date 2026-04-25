package io.sentry;

import java.time.Instant;
import org.jetbrains.annotations.NotNull;

public final class SentryInstantDate extends SentryDate {
  @NotNull
  private final Instant date;
  
  public SentryInstantDate() {
    this(Instant.now());
  }
  
  public SentryInstantDate(@NotNull Instant paramInstant) {
    this.date = paramInstant;
  }
  
  public long nanoTimestamp() {
    return DateUtils.secondsToNanos(this.date.getEpochSecond()) + this.date.getNano();
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryInstantDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry;

import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryNanotimeDate extends SentryDate {
  @NotNull
  private final Date date;
  
  private final long nanos;
  
  public SentryNanotimeDate() {
    this(DateUtils.getCurrentDateTime(), System.nanoTime());
  }
  
  public SentryNanotimeDate(@NotNull Date paramDate, long paramLong) {
    this.date = paramDate;
    this.nanos = paramLong;
  }
  
  public long diff(@NotNull SentryDate paramSentryDate) {
    if (paramSentryDate instanceof SentryNanotimeDate) {
      SentryNanotimeDate sentryNanotimeDate = (SentryNanotimeDate)paramSentryDate;
      return this.nanos - sentryNanotimeDate.nanos;
    } 
    return super.diff(paramSentryDate);
  }
  
  public long nanoTimestamp() {
    return DateUtils.dateToNanos(this.date);
  }
  
  public long laterDateNanosTimestampByDiff(@Nullable SentryDate paramSentryDate) {
    if (paramSentryDate != null && paramSentryDate instanceof SentryNanotimeDate) {
      SentryNanotimeDate sentryNanotimeDate = (SentryNanotimeDate)paramSentryDate;
      return (compareTo(paramSentryDate) < 0) ? nanotimeDiff(this, sentryNanotimeDate) : nanotimeDiff(sentryNanotimeDate, this);
    } 
    return super.laterDateNanosTimestampByDiff(paramSentryDate);
  }
  
  public int compareTo(@NotNull SentryDate paramSentryDate) {
    if (paramSentryDate instanceof SentryNanotimeDate) {
      SentryNanotimeDate sentryNanotimeDate = (SentryNanotimeDate)paramSentryDate;
      long l1 = this.date.getTime();
      long l2 = sentryNanotimeDate.date.getTime();
      return (l1 == l2) ? Long.valueOf(this.nanos).compareTo(Long.valueOf(sentryNanotimeDate.nanos)) : Long.valueOf(l1).compareTo(Long.valueOf(l2));
    } 
    return super.compareTo(paramSentryDate);
  }
  
  private long nanotimeDiff(@NotNull SentryNanotimeDate paramSentryNanotimeDate1, @NotNull SentryNanotimeDate paramSentryNanotimeDate2) {
    long l = paramSentryNanotimeDate2.nanos - paramSentryNanotimeDate1.nanos;
    return paramSentryNanotimeDate1.nanoTimestamp() + l;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SentryNanotimeDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
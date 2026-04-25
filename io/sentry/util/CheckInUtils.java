package io.sentry.util;

import io.sentry.CheckIn;
import io.sentry.CheckInStatus;
import io.sentry.DateUtils;
import io.sentry.IScopes;
import io.sentry.ISentryLifecycleToken;
import io.sentry.MonitorConfig;
import io.sentry.Sentry;
import io.sentry.protocol.SentryId;
import java.util.List;
import java.util.concurrent.Callable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Experimental
public final class CheckInUtils {
  public static <U> U withCheckIn(@NotNull String paramString, @Nullable MonitorConfig paramMonitorConfig, @NotNull Callable<U> paramCallable) throws Exception {
    ISentryLifecycleToken iSentryLifecycleToken = Sentry.forkedScopes("CheckInUtils").makeCurrent();
    try {
      IScopes iScopes = Sentry.getCurrentScopes();
      long l = System.currentTimeMillis();
      boolean bool = false;
      TracingUtils.startNewTrace(iScopes);
      CheckIn checkIn = new CheckIn(paramString, CheckInStatus.IN_PROGRESS);
      if (paramMonitorConfig != null)
        checkIn.setMonitorConfig(paramMonitorConfig); 
      SentryId sentryId = iScopes.captureCheckIn(checkIn);
      try {
        U u = paramCallable.call();
        CheckInStatus checkInStatus = bool ? CheckInStatus.ERROR : CheckInStatus.OK;
        return u;
      } catch (Throwable throwable) {
        bool = true;
        throw throwable;
      } finally {
        CheckInStatus checkInStatus = bool ? CheckInStatus.ERROR : CheckInStatus.OK;
        CheckIn checkIn1 = new CheckIn(sentryId, paramString, checkInStatus);
        checkIn1.setDuration(Double.valueOf(DateUtils.millisToSeconds((System.currentTimeMillis() - l))));
        iScopes.captureCheckIn(checkIn1);
      } 
    } catch (Throwable throwable) {
      if (iSentryLifecycleToken != null)
        try {
          iSentryLifecycleToken.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static <U> U withCheckIn(@NotNull String paramString, @NotNull Callable<U> paramCallable) throws Exception {
    return withCheckIn(paramString, null, paramCallable);
  }
  
  @Internal
  public static boolean isIgnored(@Nullable List<String> paramList, @NotNull String paramString) {
    if (paramList == null || paramList.isEmpty())
      return false; 
    for (String str : paramList) {
      if (str.equalsIgnoreCase(paramString))
        return true; 
      try {
        if (paramString.matches(str))
          return true; 
      } catch (Throwable throwable) {}
    } 
    return false;
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\CheckInUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
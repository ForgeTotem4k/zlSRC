package io.sentry;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SystemOutLogger implements ILogger {
  public void log(@NotNull SentryLevel paramSentryLevel, @NotNull String paramString, @Nullable Object... paramVarArgs) {
    System.out.println(String.format("%s: %s", new Object[] { paramSentryLevel, String.format(paramString, paramVarArgs) }));
  }
  
  public void log(@NotNull SentryLevel paramSentryLevel, @NotNull String paramString, @Nullable Throwable paramThrowable) {
    if (paramThrowable == null) {
      log(paramSentryLevel, paramString, new Object[0]);
    } else {
      System.out.println(String.format("%s: %s\n%s", new Object[] { paramSentryLevel, String.format(paramString, new Object[] { paramThrowable.toString() }), captureStackTrace(paramThrowable) }));
    } 
  }
  
  public void log(@NotNull SentryLevel paramSentryLevel, @Nullable Throwable paramThrowable, @NotNull String paramString, @Nullable Object... paramVarArgs) {
    if (paramThrowable == null) {
      log(paramSentryLevel, paramString, paramVarArgs);
    } else {
      System.out.println(String.format("%s: %s \n %s\n%s", new Object[] { paramSentryLevel, String.format(paramString, paramVarArgs), paramThrowable.toString(), captureStackTrace(paramThrowable) }));
    } 
  }
  
  public boolean isEnabled(@Nullable SentryLevel paramSentryLevel) {
    return true;
  }
  
  @NotNull
  private String captureStackTrace(@NotNull Throwable paramThrowable) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    paramThrowable.printStackTrace(printWriter);
    return stringWriter.toString();
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\SystemOutLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry;

import java.net.URI;
import java.util.Locale;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
public final class DsnUtil {
  public static boolean urlContainsDsnHost(@Nullable SentryOptions paramSentryOptions, @Nullable String paramString) {
    if (paramSentryOptions == null)
      return false; 
    if (paramString == null)
      return false; 
    String str1 = paramSentryOptions.getDsn();
    if (str1 == null)
      return false; 
    Dsn dsn = new Dsn(str1);
    URI uRI = dsn.getSentryUri();
    String str2 = uRI.getHost();
    return (str2 == null) ? false : paramString.toLowerCase(Locale.ROOT).contains(str2.toLowerCase(Locale.ROOT));
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\DsnUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
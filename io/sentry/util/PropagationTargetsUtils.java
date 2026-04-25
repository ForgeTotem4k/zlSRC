package io.sentry.util;

import java.net.URI;
import java.util.List;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public final class PropagationTargetsUtils {
  public static boolean contain(@NotNull List<String> paramList, @NotNull String paramString) {
    if (paramList.isEmpty())
      return false; 
    for (String str : paramList) {
      if (paramString.contains(str))
        return true; 
      try {
        if (paramString.matches(str))
          return true; 
      } catch (Exception exception) {}
    } 
    return false;
  }
  
  public static boolean contain(@NotNull List<String> paramList, URI paramURI) {
    return contain(paramList, paramURI.toString());
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\PropagationTargetsUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
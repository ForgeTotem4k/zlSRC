package io.sentry.util;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SpanUtils {
  @NotNull
  public static List<String> ignoredSpanOriginsForOpenTelemetry() {
    ArrayList<String> arrayList = new ArrayList();
    arrayList.add("auto.http.spring_jakarta.webmvc");
    arrayList.add("auto.http.spring.webmvc");
    arrayList.add("auto.spring_jakarta.webflux");
    arrayList.add("auto.spring.webflux");
    arrayList.add("auto.http.spring_jakarta.webclient");
    arrayList.add("auto.http.spring.webclient");
    arrayList.add("auto.http.spring_jakarta.restclient");
    arrayList.add("auto.http.spring.restclient");
    arrayList.add("auto.http.spring_jakarta.resttemplate");
    arrayList.add("auto.http.spring.resttemplate");
    arrayList.add("auto.http.openfeign");
    arrayList.add("auto.graphql.graphql");
    arrayList.add("auto.db.jdbc");
    return arrayList;
  }
  
  @Internal
  public static boolean isIgnored(@Nullable List<String> paramList, @Nullable String paramString) {
    if (paramString == null || paramList == null || paramList.isEmpty())
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


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\SpanUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
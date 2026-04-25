package io.sentry.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class HttpUtils {
  public static final String COOKIE_HEADER_NAME = "Cookie";
  
  private static final List<String> SENSITIVE_HEADERS = Arrays.asList(new String[] { 
        "X-FORWARDED-FOR", "AUTHORIZATION", "COOKIE", "SET-COOKIE", "X-API-KEY", "X-REAL-IP", "REMOTE-ADDR", "FORWARDED", "PROXY-AUTHORIZATION", "X-CSRF-TOKEN", 
        "X-CSRFTOKEN", "X-XSRF-TOKEN" });
  
  private static final List<String> SECURITY_COOKIES = Arrays.asList(new String[] { "JSESSIONID", "JSESSIONIDSSO", "JSSOSESSIONID", "SESSIONID", "SID", "CSRFTOKEN", "XSRF-TOKEN" });
  
  public static boolean containsSensitiveHeader(@NotNull String paramString) {
    return SENSITIVE_HEADERS.contains(paramString.toUpperCase(Locale.ROOT));
  }
  
  @Nullable
  public static List<String> filterOutSecurityCookiesFromHeader(@Nullable Enumeration<String> paramEnumeration, @Nullable String paramString, @Nullable List<String> paramList) {
    return (paramEnumeration == null) ? null : filterOutSecurityCookiesFromHeader(Collections.list(paramEnumeration), paramString, paramList);
  }
  
  @Nullable
  public static List<String> filterOutSecurityCookiesFromHeader(@Nullable List<String> paramList1, @Nullable String paramString, @Nullable List<String> paramList2) {
    if (paramList1 == null)
      return null; 
    if (paramString != null && !"Cookie".equalsIgnoreCase(paramString))
      return paramList1; 
    ArrayList<String> arrayList = new ArrayList();
    for (String str : paramList1)
      arrayList.add(filterOutSecurityCookies(str, paramList2)); 
    return arrayList;
  }
  
  @Nullable
  public static String filterOutSecurityCookies(@Nullable String paramString, @Nullable List<String> paramList) {
    if (paramString == null)
      return null; 
    try {
      String[] arrayOfString = paramString.split(";", -1);
      StringBuilder stringBuilder = new StringBuilder();
      boolean bool = true;
      for (String str1 : arrayOfString) {
        if (!bool)
          stringBuilder.append(";"); 
        String[] arrayOfString1 = str1.split("=", -1);
        String str2 = arrayOfString1[0];
        if (isSecurityCookie(str2.trim(), paramList)) {
          stringBuilder.append(str2 + "=" + "[Filtered]");
        } else {
          stringBuilder.append(str1);
        } 
        bool = false;
      } 
      return stringBuilder.toString();
    } catch (Throwable throwable) {
      return null;
    } 
  }
  
  public static boolean isSecurityCookie(@NotNull String paramString, @Nullable List<String> paramList) {
    String str = paramString.toUpperCase(Locale.ROOT);
    if (SECURITY_COOKIES.contains(str))
      return true; 
    if (paramList != null)
      for (String str1 : paramList) {
        if (str1.toUpperCase(Locale.ROOT).equals(str))
          return true; 
      }  
    return false;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\HttpUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
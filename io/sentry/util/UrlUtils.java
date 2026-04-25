package io.sentry.util;

import io.sentry.ISpan;
import io.sentry.protocol.Request;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class UrlUtils {
  @NotNull
  public static final String SENSITIVE_DATA_SUBSTITUTE = "[Filtered]";
  
  @NotNull
  private static final Pattern AUTH_REGEX = Pattern.compile("(.+://)(.*@)(.*)");
  
  @Nullable
  public static UrlDetails parseNullable(@Nullable String paramString) {
    return (paramString == null) ? null : parse(paramString);
  }
  
  @NotNull
  public static UrlDetails parse(@NotNull String paramString) {
    return isAbsoluteUrl(paramString) ? splitAbsoluteUrl(paramString) : splitRelativeUrl(paramString);
  }
  
  private static boolean isAbsoluteUrl(@NotNull String paramString) {
    return paramString.contains("://");
  }
  
  @NotNull
  private static UrlDetails splitRelativeUrl(@NotNull String paramString) {
    int i = paramString.indexOf("?");
    int j = paramString.indexOf("#");
    String str1 = extractBaseUrl(paramString, i, j);
    String str2 = extractQuery(paramString, i, j);
    String str3 = extractFragment(paramString, j);
    return new UrlDetails(str1, str2, str3);
  }
  
  @Nullable
  private static String extractBaseUrl(@NotNull String paramString, int paramInt1, int paramInt2) {
    return (paramInt1 >= 0) ? paramString.substring(0, paramInt1).trim() : ((paramInt2 >= 0) ? paramString.substring(0, paramInt2).trim() : paramString);
  }
  
  @Nullable
  private static String extractQuery(@NotNull String paramString, int paramInt1, int paramInt2) {
    return (paramInt1 > 0) ? ((paramInt2 > 0 && paramInt2 > paramInt1) ? paramString.substring(paramInt1 + 1, paramInt2).trim() : paramString.substring(paramInt1 + 1).trim()) : null;
  }
  
  @Nullable
  private static String extractFragment(@NotNull String paramString, int paramInt) {
    return (paramInt > 0) ? paramString.substring(paramInt + 1).trim() : null;
  }
  
  @NotNull
  private static UrlDetails splitAbsoluteUrl(@NotNull String paramString) {
    try {
      String str1 = urlWithAuthRemoved(paramString);
      URL uRL = new URL(paramString);
      String str2 = baseUrlOnly(str1);
      if (str2.contains("#"))
        return new UrlDetails(null, null, null); 
      String str3 = uRL.getQuery();
      String str4 = uRL.getRef();
      return new UrlDetails(str2, str3, str4);
    } catch (MalformedURLException malformedURLException) {
      return new UrlDetails(null, null, null);
    } 
  }
  
  @NotNull
  private static String urlWithAuthRemoved(@NotNull String paramString) {
    Matcher matcher = AUTH_REGEX.matcher(paramString);
    if (matcher.matches() && matcher.groupCount() == 3) {
      String str1 = matcher.group(2);
      String str2 = str1.contains(":") ? "[Filtered]:[Filtered]@" : "[Filtered]@";
      return matcher.group(1) + str2 + matcher.group(3);
    } 
    return paramString;
  }
  
  @NotNull
  private static String baseUrlOnly(@NotNull String paramString) {
    int i = paramString.indexOf("?");
    if (i >= 0)
      return paramString.substring(0, i).trim(); 
    int j = paramString.indexOf("#");
    return (j >= 0) ? paramString.substring(0, j).trim() : paramString;
  }
  
  public static final class UrlDetails {
    @Nullable
    private final String url;
    
    @Nullable
    private final String query;
    
    @Nullable
    private final String fragment;
    
    public UrlDetails(@Nullable String param1String1, @Nullable String param1String2, @Nullable String param1String3) {
      this.url = param1String1;
      this.query = param1String2;
      this.fragment = param1String3;
    }
    
    @Nullable
    public String getUrl() {
      return this.url;
    }
    
    @NotNull
    public String getUrlOrFallback() {
      return (this.url == null) ? "unknown" : this.url;
    }
    
    @Nullable
    public String getQuery() {
      return this.query;
    }
    
    @Nullable
    public String getFragment() {
      return this.fragment;
    }
    
    public void applyToRequest(@Nullable Request param1Request) {
      if (param1Request == null)
        return; 
      param1Request.setUrl(this.url);
      param1Request.setQueryString(this.query);
      param1Request.setFragment(this.fragment);
    }
    
    public void applyToSpan(@Nullable ISpan param1ISpan) {
      if (param1ISpan == null)
        return; 
      if (this.query != null)
        param1ISpan.setData("http.query", this.query); 
      if (this.fragment != null)
        param1ISpan.setData("http.fragment", this.fragment); 
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\UrlUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
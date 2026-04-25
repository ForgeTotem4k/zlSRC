package io.sentry;

import io.sentry.util.Objects;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public final class RequestDetails {
  @NotNull
  private final URL url;
  
  @NotNull
  private final Map<String, String> headers;
  
  public RequestDetails(@NotNull String paramString, @NotNull Map<String, String> paramMap) {
    Objects.requireNonNull(paramString, "url is required");
    Objects.requireNonNull(paramMap, "headers is required");
    try {
      this.url = URI.create(paramString).toURL();
    } catch (MalformedURLException malformedURLException) {
      throw new IllegalArgumentException("Failed to compose the Sentry's server URL.", malformedURLException);
    } 
    this.headers = paramMap;
  }
  
  @NotNull
  public URL getUrl() {
    return this.url;
  }
  
  @NotNull
  public Map<String, String> getHeaders() {
    return this.headers;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\RequestDetails.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
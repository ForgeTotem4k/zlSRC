package io.sentry;

import io.sentry.util.Objects;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

final class RequestDetailsResolver {
  private static final String USER_AGENT = "User-Agent";
  
  private static final String SENTRY_AUTH = "X-Sentry-Auth";
  
  @NotNull
  private final SentryOptions options;
  
  public RequestDetailsResolver(@NotNull SentryOptions paramSentryOptions) {
    this.options = (SentryOptions)Objects.requireNonNull(paramSentryOptions, "options is required");
  }
  
  @NotNull
  RequestDetails resolve() {
    Dsn dsn = new Dsn(this.options.getDsn());
    URI uRI = dsn.getSentryUri();
    String str1 = uRI.resolve(uRI.getPath() + "/envelope/").toString();
    String str2 = dsn.getPublicKey();
    String str3 = dsn.getSecretKey();
    String str4 = "Sentry sentry_version=7,sentry_client=" + this.options.getSentryClientName() + ",sentry_key=" + str2 + ((str3 != null && str3.length() > 0) ? (",sentry_secret=" + str3) : "");
    String str5 = this.options.getSentryClientName();
    HashMap<Object, Object> hashMap = new HashMap<>();
    hashMap.put("User-Agent", str5);
    hashMap.put("X-Sentry-Auth", str4);
    return new RequestDetails(str1, (Map)hashMap);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\RequestDetailsResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
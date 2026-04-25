package io.sentry.transport;

import io.sentry.util.Objects;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ProxyAuthenticator extends Authenticator {
  @NotNull
  private final String user;
  
  @NotNull
  private final String password;
  
  ProxyAuthenticator(@NotNull String paramString1, @NotNull String paramString2) {
    this.user = (String)Objects.requireNonNull(paramString1, "user is required");
    this.password = (String)Objects.requireNonNull(paramString2, "password is required");
  }
  
  @Nullable
  protected PasswordAuthentication getPasswordAuthentication() {
    return (getRequestorType() == Authenticator.RequestorType.PROXY) ? new PasswordAuthentication(this.user, this.password.toCharArray()) : null;
  }
  
  @NotNull
  String getUser() {
    return this.user;
  }
  
  @NotNull
  String getPassword() {
    return this.password;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\ProxyAuthenticator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
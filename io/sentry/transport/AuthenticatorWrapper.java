package io.sentry.transport;

import java.net.Authenticator;
import org.jetbrains.annotations.NotNull;

final class AuthenticatorWrapper {
  private static final AuthenticatorWrapper instance = new AuthenticatorWrapper();
  
  public static AuthenticatorWrapper getInstance() {
    return instance;
  }
  
  public void setDefault(@NotNull Authenticator paramAuthenticator) {
    Authenticator.setDefault(paramAuthenticator);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\transport\AuthenticatorWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
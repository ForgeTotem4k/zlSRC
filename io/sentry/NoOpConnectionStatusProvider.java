package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public final class NoOpConnectionStatusProvider implements IConnectionStatusProvider {
  @NotNull
  public IConnectionStatusProvider.ConnectionStatus getConnectionStatus() {
    return IConnectionStatusProvider.ConnectionStatus.UNKNOWN;
  }
  
  @Nullable
  public String getConnectionType() {
    return null;
  }
  
  public boolean addConnectionStatusObserver(@NotNull IConnectionStatusProvider.IConnectionStatusObserver paramIConnectionStatusObserver) {
    return false;
  }
  
  public void removeConnectionStatusObserver(@NotNull IConnectionStatusProvider.IConnectionStatusObserver paramIConnectionStatusObserver) {}
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\NoOpConnectionStatusProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
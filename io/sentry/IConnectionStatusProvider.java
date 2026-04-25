package io.sentry;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public interface IConnectionStatusProvider {
  @NotNull
  ConnectionStatus getConnectionStatus();
  
  @Nullable
  String getConnectionType();
  
  boolean addConnectionStatusObserver(@NotNull IConnectionStatusObserver paramIConnectionStatusObserver);
  
  void removeConnectionStatusObserver(@NotNull IConnectionStatusObserver paramIConnectionStatusObserver);
  
  static {
  
  }
  
  public static interface IConnectionStatusObserver {
    void onConnectionStatusChanged(@NotNull IConnectionStatusProvider.ConnectionStatus param1ConnectionStatus);
    
    static {
    
    }
  }
  
  public enum ConnectionStatus {
    UNKNOWN, CONNECTED, DISCONNECTED, NO_PERMISSION;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\IConnectionStatusProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.client.api;

import pro.gravit.launcher.client.ClientLauncherMethods;

public class SystemService {
  private SystemService() {
    throw new UnsupportedOperationException();
  }
  
  public static void exit(int paramInt) {
    ClientLauncherMethods.exitLauncher(paramInt);
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\client\api\SystemService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
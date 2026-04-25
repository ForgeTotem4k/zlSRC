package pro.gravit.launcher.base.api;

public class ConfigService {
  public static boolean disableLogging;
  
  public static String serverName;
  
  public static CheckServerConfig checkServerConfig = new CheckServerConfig();
  
  public static class CheckServerConfig {
    public boolean needProperties;
    
    public boolean needHardware;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\api\ConfigService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
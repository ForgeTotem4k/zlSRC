package pro.gravit.launchermodules.sentryl;

import java.util.ArrayList;
import java.util.List;

public class Config {
  public String dsn = "https://0a42408d0aa2a4959d117f4070f8f9cd@o4510566278430720.ingest.de.sentry.io/4510566285770832";
  
  public boolean collectSystemInfo = true;
  
  public boolean collectMemoryInfo = true;
  
  public List<String> ignoreErrors;
  
  public Config() {
    (new ArrayList<>(1)).add("auth.wrongpassword");
    this.ignoreErrors = new ArrayList<>(1);
  }
  
  public static Object getDefault() {
    Config config = new Config();
    config.dsn = "YOUR_DSN";
    config.collectSystemInfo = true;
    config.collectMemoryInfo = true;
    config.ignoreErrors = new ArrayList<>();
    config.ignoreErrors.add("auth.wrongpassword");
    return config;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launchermodules\sentryl\Config.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
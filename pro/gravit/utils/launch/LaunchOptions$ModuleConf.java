package pro.gravit.utils.launch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModuleConf {
  public List<String> modules = new ArrayList<>();
  
  public List<String> modulePath = new ArrayList<>();
  
  public Map<String, String> exports = new HashMap<>();
  
  public Map<String, String> opens = new HashMap<>();
  
  public Map<String, String> reads = new HashMap<>();
  
  public List<String> enableNativeAccess = new ArrayList<>();
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\launch\LaunchOptions$ModuleConf.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
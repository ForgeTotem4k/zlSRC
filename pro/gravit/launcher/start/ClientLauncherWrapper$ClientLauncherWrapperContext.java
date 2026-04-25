package pro.gravit.launcher.start;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pro.gravit.utils.helper.JavaHelper;

public class ClientLauncherWrapperContext {
  public JavaHelper.JavaVersion javaVersion;
  
  public Path executePath;
  
  public String mainClass;
  
  public int memoryLimit;
  
  public boolean useLegacyClasspathProperty;
  
  public ProcessBuilder processBuilder;
  
  public List<String> args = new ArrayList<>(8);
  
  public Map<String, String> jvmProperties = new HashMap<>();
  
  public List<String> classpath = new ArrayList<>();
  
  public List<String> jvmModules = new ArrayList<>();
  
  public List<String> clientArgs = new ArrayList<>();
  
  public List<Path> javaFXPaths = new ArrayList<>();
  
  public void addSystemProperty(String paramString) {
    String str = System.getProperty(paramString);
    if (str != null)
      this.jvmProperties.put(paramString, str); 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\start\ClientLauncherWrapper$ClientLauncherWrapperContext.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.utils.launch;

import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.nio.file.Path;
import java.security.ProtectionDomain;

public interface ClassLoaderControl {
  void addLauncherPackage(String paramString);
  
  void clearLauncherPackages();
  
  void addTransformer(ClassTransformer paramClassTransformer);
  
  void addURL(URL paramURL);
  
  void addJar(Path paramPath);
  
  URL[] getURLs();
  
  Class<?> getClass(String paramString) throws ClassNotFoundException;
  
  ClassLoader getClassLoader();
  
  Object getJava9ModuleController();
  
  MethodHandles.Lookup getHackLookup();
  
  static {
  
  }
  
  public static interface ClassTransformer {
    boolean filter(String param1String1, String param1String2);
    
    byte[] transform(String param1String1, String param1String2, ProtectionDomain param1ProtectionDomain, byte[] param1ArrayOfbyte);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\launch\ClassLoaderControl.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
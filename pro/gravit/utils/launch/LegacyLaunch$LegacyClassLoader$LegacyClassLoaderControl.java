package pro.gravit.utils.launch;

import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

public class LegacyClassLoaderControl implements ClassLoaderControl {
  public void addLauncherPackage(String paramString) {
    LegacyLaunch.LegacyClassLoader.this.addAllowedPackage(paramString);
  }
  
  public void clearLauncherPackages() {
    LegacyLaunch.LegacyClassLoader.this.clearAllowedPackages();
  }
  
  public void addTransformer(ClassLoaderControl.ClassTransformer paramClassTransformer) {
    LegacyLaunch.LegacyClassLoader.this.transformers.add(paramClassTransformer);
  }
  
  public void addURL(URL paramURL) {
    LegacyLaunch.LegacyClassLoader.access$000(LegacyLaunch.LegacyClassLoader.this, paramURL);
  }
  
  public void addJar(Path paramPath) {
    try {
      LegacyLaunch.LegacyClassLoader.access$100(LegacyLaunch.LegacyClassLoader.this, paramPath.toUri().toURL());
    } catch (MalformedURLException malformedURLException) {
      throw new RuntimeException(malformedURLException);
    } 
  }
  
  public URL[] getURLs() {
    return LegacyLaunch.LegacyClassLoader.this.getURLs();
  }
  
  public Class<?> getClass(String paramString) throws ClassNotFoundException {
    return Class.forName(paramString, false, LegacyLaunch.LegacyClassLoader.this);
  }
  
  public ClassLoader getClassLoader() {
    return LegacyLaunch.LegacyClassLoader.this;
  }
  
  public Object getJava9ModuleController() {
    return null;
  }
  
  public MethodHandles.Lookup getHackLookup() {
    return this.this$1.this$0.hackLookup;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\launch\LegacyLaunch$LegacyClassLoader$LegacyClassLoaderControl.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
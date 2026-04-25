package pro.gravit.utils.launch;

import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

class ModuleClassLoaderControl implements ClassLoaderControl {
  public void addLauncherPackage(String paramString) {
    ModuleLaunch.ModuleClassLoader.this.addAllowedPackage(paramString);
  }
  
  public void clearLauncherPackages() {
    ModuleLaunch.ModuleClassLoader.this.clearAllowedPackages();
  }
  
  public void addTransformer(ClassLoaderControl.ClassTransformer paramClassTransformer) {
    ModuleLaunch.ModuleClassLoader.this.transformers.add(paramClassTransformer);
  }
  
  public void addURL(URL paramURL) {
    ModuleLaunch.ModuleClassLoader.access$000(ModuleLaunch.ModuleClassLoader.this, paramURL);
  }
  
  public void addJar(Path paramPath) {
    try {
      ModuleLaunch.ModuleClassLoader.access$100(ModuleLaunch.ModuleClassLoader.this, paramPath.toUri().toURL());
    } catch (MalformedURLException malformedURLException) {
      throw new RuntimeException(malformedURLException);
    } 
  }
  
  public URL[] getURLs() {
    return ModuleLaunch.ModuleClassLoader.this.getURLs();
  }
  
  public Class<?> getClass(String paramString) throws ClassNotFoundException {
    return Class.forName(paramString, false, ModuleLaunch.ModuleClassLoader.this);
  }
  
  public ClassLoader getClassLoader() {
    return ModuleLaunch.ModuleClassLoader.this;
  }
  
  public Object getJava9ModuleController() {
    return this.this$1.this$0.controller;
  }
  
  public MethodHandles.Lookup getHackLookup() {
    return this.this$1.this$0.hackLookup;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\launch\ModuleLaunch$ModuleClassLoader$ModuleClassLoaderControl.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
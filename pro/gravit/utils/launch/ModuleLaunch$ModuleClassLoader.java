package pro.gravit.utils.launch;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.JVMHelper;

class ModuleClassLoader extends URLClassLoader {
  private final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();
  
  private final List<ClassLoaderControl.ClassTransformer> transformers = new ArrayList<>();
  
  private final Map<String, Class<?>> classMap = new ConcurrentHashMap<>();
  
  private final Map<String, Module> packageToModule = new HashMap<>();
  
  private String nativePath;
  
  private final List<String> packages = new ArrayList<>();
  
  public ModuleClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader) {
    super("LAUNCHER", paramArrayOfURL, paramClassLoader);
    this.packages.add("pro.gravit.launcher.");
    this.packages.add("pro.gravit.utils.");
  }
  
  private void initializeWithLayer(ModuleLayer paramModuleLayer) {
    for (Module module : paramModuleLayer.modules()) {
      for (String str : module.getPackages())
        this.packageToModule.put(str, module); 
    } 
  }
  
  protected Class<?> loadClass(String paramString, boolean paramBoolean) throws ClassNotFoundException {
    if (paramString != null && !ModuleLaunch.this.disablePackageDelegateSupport)
      for (String str : this.packages) {
        if (paramString.startsWith(str))
          return this.SYSTEM_CLASS_LOADER.loadClass(paramString); 
      }  
    return super.loadClass(paramString, paramBoolean);
  }
  
  protected Class<?> findClass(String paramString) throws ClassNotFoundException {
    Class<?> clazz = findClass((String)null, paramString);
    if (clazz == null)
      throw new ClassNotFoundException(paramString); 
    return clazz;
  }
  
  protected Class<?> findClass(String paramString1, String paramString2) {
    Class<?> clazz = this.classMap.get(paramString2);
    if (clazz != null)
      return clazz; 
    if (paramString2 != null && !this.transformers.isEmpty()) {
      boolean bool = false;
      for (ClassLoaderControl.ClassTransformer classTransformer : this.transformers) {
        if (classTransformer.filter(paramString1, paramString2)) {
          bool = true;
          break;
        } 
      } 
      if (bool) {
        String str = paramString2.replace(".", "/").concat(".class");
        try {
          InputStream inputStream = getResourceAsStream(str);
          try {
            byte[] arrayOfByte = IOHelper.read(inputStream);
            for (ClassLoaderControl.ClassTransformer classTransformer : this.transformers)
              arrayOfByte = classTransformer.transform(paramString1, paramString2, null, arrayOfByte); 
            clazz = defineClass(paramString2, arrayOfByte, 0, arrayOfByte.length);
            if (inputStream != null)
              inputStream.close(); 
          } catch (Throwable throwable) {
            if (inputStream != null)
              try {
                inputStream.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              }  
            throw throwable;
          } 
        } catch (IOException iOException) {
          return null;
        } 
      } 
    } 
    if (clazz == null && ModuleLaunch.this.layer != null && paramString2 != null) {
      String str = ModuleLaunch.getPackageFromClass(paramString2);
      Module module = this.packageToModule.get(str);
      if (module != null)
        try {
          clazz = module.getClassLoader().loadClass(paramString2);
        } catch (ClassNotFoundException classNotFoundException) {
          return null;
        }  
    } 
    if (clazz == null)
      try {
        clazz = super.findClass(paramString2);
      } catch (ClassNotFoundException classNotFoundException) {
        return null;
      }  
    if (clazz != null) {
      this.classMap.put(paramString2, clazz);
      return clazz;
    } 
    return null;
  }
  
  public String findLibrary(String paramString) {
    return (this.nativePath == null) ? null : this.nativePath.concat(IOHelper.PLATFORM_SEPARATOR).concat(JVMHelper.NATIVE_PREFIX).concat(paramString).concat(JVMHelper.NATIVE_EXTENSION);
  }
  
  public void addAllowedPackage(String paramString) {
    this.packages.add(paramString);
  }
  
  public void clearAllowedPackages() {
    this.packages.clear();
  }
  
  private ModuleClassLoaderControl makeControl() {
    return new ModuleClassLoaderControl();
  }
  
  static {
    ClassLoader.registerAsParallelCapable();
  }
  
  private class ModuleClassLoaderControl implements ClassLoaderControl {
    public void addLauncherPackage(String param2String) {
      ModuleLaunch.ModuleClassLoader.this.addAllowedPackage(param2String);
    }
    
    public void clearLauncherPackages() {
      ModuleLaunch.ModuleClassLoader.this.clearAllowedPackages();
    }
    
    public void addTransformer(ClassLoaderControl.ClassTransformer param2ClassTransformer) {
      ModuleLaunch.ModuleClassLoader.this.transformers.add(param2ClassTransformer);
    }
    
    public void addURL(URL param2URL) {
      ModuleLaunch.ModuleClassLoader.this.addURL(param2URL);
    }
    
    public void addJar(Path param2Path) {
      try {
        ModuleLaunch.ModuleClassLoader.this.addURL(param2Path.toUri().toURL());
      } catch (MalformedURLException malformedURLException) {
        throw new RuntimeException(malformedURLException);
      } 
    }
    
    public URL[] getURLs() {
      return ModuleLaunch.ModuleClassLoader.this.getURLs();
    }
    
    public Class<?> getClass(String param2String) throws ClassNotFoundException {
      return Class.forName(param2String, false, ModuleLaunch.ModuleClassLoader.this);
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
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\launch\ModuleLaunch$ModuleClassLoader.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
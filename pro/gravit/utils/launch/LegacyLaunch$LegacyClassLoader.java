package pro.gravit.utils.launch;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.JVMHelper;

class LegacyClassLoader extends URLClassLoader {
  private final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();
  
  private final List<ClassLoaderControl.ClassTransformer> transformers = new ArrayList<>();
  
  private final Map<String, Class<?>> classMap = new ConcurrentHashMap<>();
  
  private String nativePath;
  
  private final List<String> packages = new ArrayList<>();
  
  public LegacyClassLoader(URL[] paramArrayOfURL) {
    super(paramArrayOfURL);
    this.packages.add("pro.gravit.launcher.");
    this.packages.add("pro.gravit.utils.");
  }
  
  public LegacyClassLoader(URL[] paramArrayOfURL, ClassLoader paramClassLoader) {
    super(paramArrayOfURL, paramClassLoader);
  }
  
  protected Class<?> loadClass(String paramString, boolean paramBoolean) throws ClassNotFoundException {
    if (paramString != null)
      for (String str : this.packages) {
        if (paramString.startsWith(str))
          return this.SYSTEM_CLASS_LOADER.loadClass(paramString); 
      }  
    return super.loadClass(paramString, paramBoolean);
  }
  
  protected Class<?> findClass(String paramString) throws ClassNotFoundException {
    Class<?> clazz = this.classMap.get(paramString);
    if (clazz != null)
      return clazz; 
    if (paramString != null && !this.transformers.isEmpty()) {
      boolean bool = false;
      for (ClassLoaderControl.ClassTransformer classTransformer : this.transformers) {
        if (classTransformer.filter(null, paramString)) {
          bool = true;
          break;
        } 
      } 
      if (bool) {
        String str = paramString.replace(".", "/").concat(".class");
        try {
          InputStream inputStream = getResourceAsStream(str);
          try {
            byte[] arrayOfByte = IOHelper.read(inputStream);
            for (ClassLoaderControl.ClassTransformer classTransformer : this.transformers)
              arrayOfByte = classTransformer.transform(null, paramString, null, arrayOfByte); 
            clazz = defineClass(paramString, arrayOfByte, 0, arrayOfByte.length);
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
          throw new ClassNotFoundException(paramString, iOException);
        } 
      } 
    } 
    if (clazz == null)
      clazz = super.findClass(paramString); 
    if (clazz != null) {
      this.classMap.put(paramString, clazz);
      return clazz;
    } 
    throw new ClassNotFoundException(paramString);
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
  
  private LegacyClassLoaderControl makeControl() {
    return new LegacyClassLoaderControl();
  }
  
  static {
    ClassLoader.registerAsParallelCapable();
  }
  
  public class LegacyClassLoaderControl implements ClassLoaderControl {
    public void addLauncherPackage(String param2String) {
      LegacyLaunch.LegacyClassLoader.this.addAllowedPackage(param2String);
    }
    
    public void clearLauncherPackages() {
      LegacyLaunch.LegacyClassLoader.this.clearAllowedPackages();
    }
    
    public void addTransformer(ClassLoaderControl.ClassTransformer param2ClassTransformer) {
      LegacyLaunch.LegacyClassLoader.this.transformers.add(param2ClassTransformer);
    }
    
    public void addURL(URL param2URL) {
      LegacyLaunch.LegacyClassLoader.this.addURL(param2URL);
    }
    
    public void addJar(Path param2Path) {
      try {
        LegacyLaunch.LegacyClassLoader.this.addURL(param2Path.toUri().toURL());
      } catch (MalformedURLException malformedURLException) {
        throw new RuntimeException(malformedURLException);
      } 
    }
    
    public URL[] getURLs() {
      return LegacyLaunch.LegacyClassLoader.this.getURLs();
    }
    
    public Class<?> getClass(String param2String) throws ClassNotFoundException {
      return Class.forName(param2String, false, LegacyLaunch.LegacyClassLoader.this);
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
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\launch\LegacyLaunch$LegacyClassLoader.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
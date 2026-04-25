package pro.gravit.utils.launch;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import pro.gravit.utils.helper.HackHelper;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.JVMHelper;

public class LegacyLaunch implements Launch {
  private LegacyClassLoader legacyClassLoader;
  
  private MethodHandles.Lookup hackLookup;
  
  public ClassLoaderControl init(List<Path> paramList, String paramString, LaunchOptions paramLaunchOptions) {
    this.legacyClassLoader = new LegacyClassLoader((URL[])paramList.stream().map(paramPath -> {
            try {
              return paramPath.toUri().toURL();
            } catch (MalformedURLException malformedURLException) {
              throw new RuntimeException(malformedURLException);
            } 
          }).toArray(paramInt -> new URL[paramInt]), BasicLaunch.class.getClassLoader());
    this.legacyClassLoader.nativePath = paramString;
    if (paramLaunchOptions.enableHacks)
      this.hackLookup = HackHelper.createHackLookup(BasicLaunch.class); 
    return this.legacyClassLoader.makeControl();
  }
  
  public void launch(String paramString1, String paramString2, Collection<String> paramCollection) throws Throwable {
    Thread.currentThread().setContextClassLoader(this.legacyClassLoader);
    Class<?> clazz = Class.forName(paramString1, true, this.legacyClassLoader);
    MethodHandle methodHandle = MethodHandles.lookup().findStatic(clazz, "main", MethodType.methodType(void.class, String[].class)).asFixedArity();
    JVMHelper.fullGC();
    methodHandle.asFixedArity().invokeWithArguments(new Object[] { paramCollection.toArray(new String[0]) });
  }
  
  private class LegacyClassLoader extends URLClassLoader {
    private final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();
    
    private final List<ClassLoaderControl.ClassTransformer> transformers = new ArrayList<>();
    
    private final Map<String, Class<?>> classMap = new ConcurrentHashMap<>();
    
    private String nativePath;
    
    private final List<String> packages = new ArrayList<>();
    
    public LegacyClassLoader(URL[] param1ArrayOfURL) {
      super(param1ArrayOfURL);
      this.packages.add("pro.gravit.launcher.");
      this.packages.add("pro.gravit.utils.");
    }
    
    public LegacyClassLoader(URL[] param1ArrayOfURL, ClassLoader param1ClassLoader) {
      super(param1ArrayOfURL, param1ClassLoader);
    }
    
    protected Class<?> loadClass(String param1String, boolean param1Boolean) throws ClassNotFoundException {
      if (param1String != null)
        for (String str : this.packages) {
          if (param1String.startsWith(str))
            return this.SYSTEM_CLASS_LOADER.loadClass(param1String); 
        }  
      return super.loadClass(param1String, param1Boolean);
    }
    
    protected Class<?> findClass(String param1String) throws ClassNotFoundException {
      Class<?> clazz = this.classMap.get(param1String);
      if (clazz != null)
        return clazz; 
      if (param1String != null && !this.transformers.isEmpty()) {
        boolean bool = false;
        for (ClassLoaderControl.ClassTransformer classTransformer : this.transformers) {
          if (classTransformer.filter(null, param1String)) {
            bool = true;
            break;
          } 
        } 
        if (bool) {
          String str = param1String.replace(".", "/").concat(".class");
          try {
            InputStream inputStream = getResourceAsStream(str);
            try {
              byte[] arrayOfByte = IOHelper.read(inputStream);
              for (ClassLoaderControl.ClassTransformer classTransformer : this.transformers)
                arrayOfByte = classTransformer.transform(null, param1String, null, arrayOfByte); 
              clazz = defineClass(param1String, arrayOfByte, 0, arrayOfByte.length);
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
            throw new ClassNotFoundException(param1String, iOException);
          } 
        } 
      } 
      if (clazz == null)
        clazz = super.findClass(param1String); 
      if (clazz != null) {
        this.classMap.put(param1String, clazz);
        return clazz;
      } 
      throw new ClassNotFoundException(param1String);
    }
    
    public String findLibrary(String param1String) {
      return (this.nativePath == null) ? null : this.nativePath.concat(IOHelper.PLATFORM_SEPARATOR).concat(JVMHelper.NATIVE_PREFIX).concat(param1String).concat(JVMHelper.NATIVE_EXTENSION);
    }
    
    public void addAllowedPackage(String param1String) {
      this.packages.add(param1String);
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
        return LegacyLaunch.this.hackLookup;
      }
    }
  }
  
  public class LegacyClassLoaderControl implements ClassLoaderControl {
    public void addLauncherPackage(String param1String) {
      this.this$1.addAllowedPackage(param1String);
    }
    
    public void clearLauncherPackages() {
      this.this$1.clearAllowedPackages();
    }
    
    public void addTransformer(ClassLoaderControl.ClassTransformer param1ClassTransformer) {
      this.this$1.transformers.add(param1ClassTransformer);
    }
    
    public void addURL(URL param1URL) {
      this.this$1.addURL(param1URL);
    }
    
    public void addJar(Path param1Path) {
      try {
        this.this$1.addURL(param1Path.toUri().toURL());
      } catch (MalformedURLException malformedURLException) {
        throw new RuntimeException(malformedURLException);
      } 
    }
    
    public URL[] getURLs() {
      return this.this$1.getURLs();
    }
    
    public Class<?> getClass(String param1String) throws ClassNotFoundException {
      return Class.forName(param1String, false, this.this$1);
    }
    
    public ClassLoader getClassLoader() {
      return this.this$1;
    }
    
    public Object getJava9ModuleController() {
      return null;
    }
    
    public MethodHandles.Lookup getHackLookup() {
      return LegacyLaunch.this.hackLookup;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\launch\LegacyLaunch.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
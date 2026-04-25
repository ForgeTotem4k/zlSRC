package pro.gravit.utils.launch;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import pro.gravit.utils.helper.HackHelper;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.LogHelper;

public class ModuleLaunch implements Launch {
  private ModuleClassLoader moduleClassLoader;
  
  private Configuration configuration;
  
  private ModuleLayer.Controller controller;
  
  private ModuleFinder moduleFinder;
  
  private ModuleLayer layer;
  
  private MethodHandles.Lookup hackLookup;
  
  private boolean disablePackageDelegateSupport;
  
  private static final MethodHandle ENABLE_NATIVE_ACCESS;
  
  public ClassLoaderControl init(List<Path> paramList, String paramString, LaunchOptions paramLaunchOptions) {
    this.disablePackageDelegateSupport = paramLaunchOptions.disablePackageDelegateSupport;
    this.moduleClassLoader = new ModuleClassLoader((URL[])paramList.stream().map(paramPath -> {
            try {
              return paramPath.toUri().toURL();
            } catch (MalformedURLException malformedURLException) {
              throw new RuntimeException(malformedURLException);
            } 
          }).toArray(paramInt -> new URL[paramInt]), ClassLoader.getPlatformClassLoader());
    this.moduleClassLoader.nativePath = paramString;
    if (paramLaunchOptions.enableHacks)
      this.hackLookup = HackHelper.createHackLookup(ModuleLaunch.class); 
    if (paramLaunchOptions.moduleConf != null) {
      this.moduleFinder = ModuleFinder.of((Path[])paramLaunchOptions.moduleConf.modulePath.stream().map(paramString -> Paths.get(paramString, new String[0])).map(Path::toAbsolutePath).toArray(paramInt -> new Path[paramInt]));
      ModuleLayer moduleLayer = ModuleLayer.boot();
      if (paramLaunchOptions.moduleConf.modules.contains("ALL-MODULE-PATH")) {
        Set<ModuleReference> set = this.moduleFinder.findAll();
        if (LogHelper.isDevEnabled()) {
          for (ModuleReference moduleReference : set) {
            LogHelper.dev("Found module %s in %s", new Object[] { moduleReference.descriptor().name(), moduleReference.location().<String>map(URI::toString).orElse("unknown") });
          } 
          LogHelper.dev("Found %d modules", new Object[] { Integer.valueOf(set.size()) });
        } 
        for (ModuleReference moduleReference : set)
          paramLaunchOptions.moduleConf.modules.add(moduleReference.descriptor().name()); 
        paramLaunchOptions.moduleConf.modules.remove("ALL-MODULE-PATH");
      } 
      this.configuration = moduleLayer.configuration().resolveAndBind(this.moduleFinder, ModuleFinder.of(new Path[0]), paramLaunchOptions.moduleConf.modules);
      this.controller = ModuleLayer.defineModulesWithOneLoader(this.configuration, List.of(moduleLayer), this.moduleClassLoader);
      this.layer = this.controller.layer();
      for (Map.Entry<String, String> entry : paramLaunchOptions.moduleConf.exports.entrySet()) {
        String[] arrayOfString = ((String)entry.getKey()).split("/");
        String str1 = arrayOfString[0];
        String str2 = arrayOfString[1];
        LogHelper.dev("Export module: %s package: %s to %s", new Object[] { str1, str2, entry.getValue() });
        Module module1 = this.layer.findModule(arrayOfString[0]).orElse(null);
        if (module1 == null)
          throw new RuntimeException(String.format("Module %s not found", new Object[] { str1 })); 
        Module module2 = this.layer.findModule((String)entry.getValue()).orElse(null);
        if (module2 == null)
          throw new RuntimeException(String.format("Module %s not found", new Object[] { entry.getValue() })); 
        if (paramLaunchOptions.enableHacks && module1.getLayer() != this.layer) {
          ModuleHacks.createController(this.hackLookup, module1.getLayer()).addExports(module1, str2, module2);
          continue;
        } 
        this.controller.addExports(module1, str2, module2);
      } 
      for (Map.Entry<String, String> entry : paramLaunchOptions.moduleConf.opens.entrySet()) {
        String[] arrayOfString = ((String)entry.getKey()).split("/");
        String str1 = arrayOfString[0];
        String str2 = arrayOfString[1];
        LogHelper.dev("Open module: %s package: %s to %s", new Object[] { str1, str2, entry.getValue() });
        Module module1 = this.layer.findModule(arrayOfString[0]).orElse(null);
        if (module1 == null)
          throw new RuntimeException(String.format("Module %s not found", new Object[] { str1 })); 
        Module module2 = this.layer.findModule((String)entry.getValue()).orElse(null);
        if (module2 == null)
          throw new RuntimeException(String.format("Module %s not found", new Object[] { entry.getValue() })); 
        if (paramLaunchOptions.enableHacks && module1.getLayer() != this.layer) {
          ModuleHacks.createController(this.hackLookup, module1.getLayer()).addOpens(module1, str2, module2);
          continue;
        } 
        this.controller.addOpens(module1, str2, module2);
      } 
      for (Map.Entry<String, String> entry : paramLaunchOptions.moduleConf.reads.entrySet()) {
        LogHelper.dev("Read module %s to %s", new Object[] { entry.getKey(), entry.getValue() });
        Module module1 = this.layer.findModule((String)entry.getKey()).orElse(null);
        if (module1 == null)
          throw new RuntimeException(String.format("Module %s not found", new Object[] { entry.getKey() })); 
        Module module2 = this.layer.findModule((String)entry.getValue()).orElse(null);
        if (module2 == null)
          throw new RuntimeException(String.format("Module %s not found", new Object[] { entry.getValue() })); 
        if (paramLaunchOptions.enableHacks && module1.getLayer() != this.layer) {
          ModuleHacks.createController(this.hackLookup, module1.getLayer()).addReads(module1, module2);
          continue;
        } 
        this.controller.addReads(module1, module2);
      } 
      for (String str : paramLaunchOptions.moduleConf.enableNativeAccess) {
        LogHelper.dev("Enable Native Access %s", new Object[] { str });
        Module module = this.layer.findModule(str).orElse(null);
        if (module == null)
          throw new RuntimeException(String.format("Module %s not found", new Object[] { str })); 
        if (ENABLE_NATIVE_ACCESS != null)
          try {
            ENABLE_NATIVE_ACCESS.invoke(this.controller, module);
          } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
          }  
      } 
      this.moduleClassLoader.initializeWithLayer(this.layer);
    } 
    return this.moduleClassLoader.makeControl();
  }
  
  public void launch(String paramString1, String paramString2, Collection<String> paramCollection) throws Throwable {
    Thread.currentThread().setContextClassLoader(this.moduleClassLoader);
    if (paramString2 == null) {
      Class<?> clazz1 = Class.forName(paramString1, true, this.moduleClassLoader);
      MethodHandle methodHandle1 = MethodHandles.lookup().findStatic(clazz1, "main", MethodType.methodType(void.class, String[].class)).asFixedArity();
      JVMHelper.fullGC();
      methodHandle1.asFixedArity().invokeWithArguments(new Object[] { paramCollection.toArray(new String[0]) });
      return;
    } 
    Module module1 = this.layer.findModule(paramString2).orElseThrow();
    Module module2 = ModuleLaunch.class.getClassLoader().getUnnamedModule();
    if (module2 != null)
      this.controller.addOpens(module1, getPackageFromClass(paramString1), module2); 
    ClassLoader classLoader = module1.getClassLoader();
    Class<?> clazz = Class.forName(paramString1, true, classLoader);
    MethodHandle methodHandle = MethodHandles.lookup().findStatic(clazz, "main", MethodType.methodType(void.class, String[].class));
    methodHandle.asFixedArity().invokeWithArguments(new Object[] { paramCollection.toArray(new String[0]) });
  }
  
  private static String getPackageFromClass(String paramString) {
    int i = paramString.lastIndexOf(".");
    return (i >= 0) ? paramString.substring(0, i) : paramString;
  }
  
  static {
    MethodHandle methodHandle;
    try {
      methodHandle = MethodHandles.lookup().findVirtual(ModuleLayer.Controller.class, "enableNativeAccess", MethodType.methodType(ModuleLayer.Controller.class, Module.class));
    } catch (NoSuchMethodException|IllegalAccessException noSuchMethodException) {
      methodHandle = null;
    } 
    ENABLE_NATIVE_ACCESS = methodHandle;
  }
  
  private class ModuleClassLoader extends URLClassLoader {
    private final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();
    
    private final List<ClassLoaderControl.ClassTransformer> transformers = new ArrayList<>();
    
    private final Map<String, Class<?>> classMap = new ConcurrentHashMap<>();
    
    private final Map<String, Module> packageToModule = new HashMap<>();
    
    private String nativePath;
    
    private final List<String> packages = new ArrayList<>();
    
    public ModuleClassLoader(URL[] param1ArrayOfURL, ClassLoader param1ClassLoader) {
      super("LAUNCHER", param1ArrayOfURL, param1ClassLoader);
      this.packages.add("pro.gravit.launcher.");
      this.packages.add("pro.gravit.utils.");
    }
    
    private void initializeWithLayer(ModuleLayer param1ModuleLayer) {
      for (Module module : param1ModuleLayer.modules()) {
        for (String str : module.getPackages())
          this.packageToModule.put(str, module); 
      } 
    }
    
    protected Class<?> loadClass(String param1String, boolean param1Boolean) throws ClassNotFoundException {
      if (param1String != null && !ModuleLaunch.this.disablePackageDelegateSupport)
        for (String str : this.packages) {
          if (param1String.startsWith(str))
            return this.SYSTEM_CLASS_LOADER.loadClass(param1String); 
        }  
      return super.loadClass(param1String, param1Boolean);
    }
    
    protected Class<?> findClass(String param1String) throws ClassNotFoundException {
      Class<?> clazz = findClass((String)null, param1String);
      if (clazz == null)
        throw new ClassNotFoundException(param1String); 
      return clazz;
    }
    
    protected Class<?> findClass(String param1String1, String param1String2) {
      Class<?> clazz = this.classMap.get(param1String2);
      if (clazz != null)
        return clazz; 
      if (param1String2 != null && !this.transformers.isEmpty()) {
        boolean bool = false;
        for (ClassLoaderControl.ClassTransformer classTransformer : this.transformers) {
          if (classTransformer.filter(param1String1, param1String2)) {
            bool = true;
            break;
          } 
        } 
        if (bool) {
          String str = param1String2.replace(".", "/").concat(".class");
          try {
            InputStream inputStream = getResourceAsStream(str);
            try {
              byte[] arrayOfByte = IOHelper.read(inputStream);
              for (ClassLoaderControl.ClassTransformer classTransformer : this.transformers)
                arrayOfByte = classTransformer.transform(param1String1, param1String2, null, arrayOfByte); 
              clazz = defineClass(param1String2, arrayOfByte, 0, arrayOfByte.length);
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
      if (clazz == null && ModuleLaunch.this.layer != null && param1String2 != null) {
        String str = ModuleLaunch.getPackageFromClass(param1String2);
        Module module = this.packageToModule.get(str);
        if (module != null)
          try {
            clazz = module.getClassLoader().loadClass(param1String2);
          } catch (ClassNotFoundException classNotFoundException) {
            return null;
          }  
      } 
      if (clazz == null)
        try {
          clazz = super.findClass(param1String2);
        } catch (ClassNotFoundException classNotFoundException) {
          return null;
        }  
      if (clazz != null) {
        this.classMap.put(param1String2, clazz);
        return clazz;
      } 
      return null;
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
        return ModuleLaunch.this.controller;
      }
      
      public MethodHandles.Lookup getHackLookup() {
        return ModuleLaunch.this.hackLookup;
      }
    }
  }
  
  private class ModuleClassLoaderControl implements ClassLoaderControl {
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
      return ModuleLaunch.this.controller;
    }
    
    public MethodHandles.Lookup getHackLookup() {
      return ModuleLaunch.this.hackLookup;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\launch\ModuleLaunch.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
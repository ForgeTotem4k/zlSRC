package pro.gravit.launcher.base.modules.impl;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import pro.gravit.launcher.base.modules.LauncherInitContext;
import pro.gravit.launcher.base.modules.LauncherModule;
import pro.gravit.launcher.base.modules.LauncherModuleInfo;
import pro.gravit.launcher.base.modules.LauncherModulesManager;
import pro.gravit.launcher.base.modules.ModulesConfigManager;
import pro.gravit.launcher.core.LauncherTrustManager;
import pro.gravit.utils.Version;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;

public class SimpleModuleManager implements LauncherModulesManager {
  private static final MethodType VOID_TYPE = MethodType.methodType(void.class);
  
  protected final List<LauncherModule> modules = new ArrayList<>();
  
  protected final List<String> moduleNames = new ArrayList<>();
  
  protected final SimpleModuleContext context;
  
  protected final ModulesConfigManager modulesConfigManager;
  
  protected final Path modulesDir;
  
  protected final LauncherTrustManager trustManager;
  
  protected final ModulesClassLoader classLoader = createClassLoader();
  
  protected LauncherInitContext initContext;
  
  protected ModulesClassLoader createClassLoader() {
    return new ModulesClassLoader(new URL[0], SimpleModuleManager.class.getClassLoader());
  }
  
  public SimpleModuleManager(Path paramPath1, Path paramPath2) {
    this.modulesConfigManager = new SimpleModulesConfigManager(paramPath2);
    this.context = new SimpleModuleContext(this, this.modulesConfigManager);
    this.modulesDir = paramPath1;
    this.trustManager = null;
  }
  
  public SimpleModuleManager(Path paramPath1, Path paramPath2, LauncherTrustManager paramLauncherTrustManager) {
    this.modulesConfigManager = new SimpleModulesConfigManager(paramPath2);
    this.context = new SimpleModuleContext(this, this.modulesConfigManager);
    this.modulesDir = paramPath1;
    this.trustManager = paramLauncherTrustManager;
  }
  
  private static X509Certificate[] getCertificates(Class<?> paramClass) {
    Object[] arrayOfObject = paramClass.getSigners();
    return (arrayOfObject == null) ? null : (X509Certificate[])Arrays.<Object>stream(arrayOfObject).filter(paramObject -> paramObject instanceof X509Certificate).map(paramObject -> (X509Certificate)paramObject).toArray(paramInt -> new X509Certificate[paramInt]);
  }
  
  public void autoload() throws IOException {
    autoload(this.modulesDir);
  }
  
  public void autoload(Path paramPath) throws IOException {
    if (Files.notExists(paramPath, new java.nio.file.LinkOption[0])) {
      Files.createDirectory(paramPath, (FileAttribute<?>[])new FileAttribute[0]);
    } else {
      IOHelper.walk(paramPath, new ModulesVisitor(), true);
    } 
  }
  
  public void initModules(LauncherInitContext paramLauncherInitContext) {
    boolean bool = true;
    this.modules.sort((paramLauncherModule1, paramLauncherModule2) -> {
          int i = (paramLauncherModule1.getModuleInfo()).priority;
          int j = (paramLauncherModule2.getModuleInfo()).priority;
          return Integer.compare(i, j);
        });
    while (bool) {
      bool = false;
      for (LauncherModule launcherModule : this.modules) {
        if (launcherModule.getInitStatus().equals(LauncherModule.InitStatus.INIT_WAIT) && checkDepend(launcherModule)) {
          bool = true;
          launcherModule.setInitStatus(LauncherModule.InitStatus.INIT);
          launcherModule.init(paramLauncherInitContext);
          launcherModule.setInitStatus(LauncherModule.InitStatus.FINISH);
        } 
      } 
    } 
    for (LauncherModule launcherModule : this.modules) {
      if (launcherModule.getInitStatus().equals(LauncherModule.InitStatus.INIT_WAIT)) {
        LauncherModuleInfo launcherModuleInfo = launcherModule.getModuleInfo();
        LogHelper.warning("Module %s required %s. Cyclic dependencies?", new Object[] { launcherModuleInfo.name, Arrays.toString((Object[])launcherModuleInfo.dependencies) });
        launcherModule.setInitStatus(LauncherModule.InitStatus.INIT);
        launcherModule.init(paramLauncherInitContext);
        launcherModule.setInitStatus(LauncherModule.InitStatus.FINISH);
        continue;
      } 
      if (launcherModule.getInitStatus().equals(LauncherModule.InitStatus.PRE_INIT_WAIT)) {
        LauncherModuleInfo launcherModuleInfo = launcherModule.getModuleInfo();
        LogHelper.error("Module %s skip pre-init phase. This module NOT finish loading", new Object[] { launcherModuleInfo.name, Arrays.toString((Object[])launcherModuleInfo.dependencies) });
      } 
    } 
  }
  
  private boolean checkDepend(LauncherModule paramLauncherModule) {
    LauncherModuleInfo launcherModuleInfo = paramLauncherModule.getModuleInfo();
    for (String str : launcherModuleInfo.dependencies) {
      LauncherModule launcherModule = getModule(str);
      if (launcherModule == null)
        throw new RuntimeException(String.format("Module %s required %s. %s not found", new Object[] { launcherModuleInfo.name, str, str })); 
      if (!launcherModule.getInitStatus().equals(LauncherModule.InitStatus.FINISH))
        return false; 
    } 
    return true;
  }
  
  public LauncherModule loadModule(LauncherModule paramLauncherModule) {
    if (this.modules.contains(paramLauncherModule))
      return paramLauncherModule; 
    if (paramLauncherModule.getCheckStatus() == null) {
      LauncherTrustManager.CheckClassResult checkClassResult = checkModuleClass((Class)paramLauncherModule.getClass());
      verifyClassCheckResult(checkClassResult);
      paramLauncherModule.setCheckResult(checkClassResult);
    } 
    this.modules.add(paramLauncherModule);
    LauncherModuleInfo launcherModuleInfo = paramLauncherModule.getModuleInfo();
    this.moduleNames.add(launcherModuleInfo.name);
    paramLauncherModule.setContext(this.context);
    paramLauncherModule.preInit();
    if (this.initContext != null) {
      paramLauncherModule.setInitStatus(LauncherModule.InitStatus.INIT);
      paramLauncherModule.init(this.initContext);
      paramLauncherModule.setInitStatus(LauncherModule.InitStatus.FINISH);
    } 
    return paramLauncherModule;
  }
  
  public LauncherModule loadModule(Path paramPath) throws IOException {
    try {
      JarFile jarFile = new JarFile(paramPath.toFile());
      try {
        LauncherModule launcherModule1;
        String str = (jarFile.getManifest() != null) ? jarFile.getManifest().getMainAttributes().getValue("Module-Main-Class") : null;
        if (str == null) {
          LogHelper.error("In module %s Module-Main-Class not found", new Object[] { paramPath.toString() });
          LauncherModule launcherModule = null;
          jarFile.close();
          return launcherModule;
        } 
        this.classLoader.addURL(paramPath.toUri().toURL());
        Class<?> clazz = Class.forName(str, false, this.classLoader);
        LauncherTrustManager.CheckClassResult checkClassResult = checkModuleClass((Class)clazz);
        try {
          verifyClassCheckResultExceptional(checkClassResult);
        } catch (Exception exception) {
          LogHelper.error(exception);
          LogHelper.error("In module %s signature check failed", new Object[] { paramPath.toString() });
          LauncherModule launcherModule = null;
          jarFile.close();
          return launcherModule;
        } 
        if (!LauncherModule.class.isAssignableFrom(clazz))
          throw new ClassNotFoundException("Invalid module class... Not contains LauncherModule in hierarchy."); 
        try {
          launcherModule1 = (LauncherModule)MethodHandles.publicLookup().findConstructor(clazz, VOID_TYPE).invokeWithArguments(Collections.emptyList());
          launcherModule1.setCheckResult(checkClassResult);
        } catch (Throwable throwable) {
          throw (InstantiationException)(new InstantiationException("Error on instancing...")).initCause(throwable);
        } 
        loadModule(launcherModule1);
        LauncherModule launcherModule2 = launcherModule1;
        jarFile.close();
        return launcherModule2;
      } catch (Throwable throwable) {
        try {
          jarFile.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (ClassNotFoundException|InstantiationException classNotFoundException) {
      LogHelper.error(classNotFoundException);
      LogHelper.error("In module %s Module-Main-Class incorrect", new Object[] { paramPath.toString() });
      return null;
    } 
  }
  
  public LauncherTrustManager.CheckClassResult checkModuleClass(Class<? extends LauncherModule> paramClass) {
    if (this.trustManager == null)
      return null; 
    X509Certificate[] arrayOfX509Certificate = getCertificates(paramClass);
    Objects.requireNonNull(this.trustManager);
    return this.trustManager.checkCertificates(arrayOfX509Certificate, this.trustManager::stdCertificateChecker);
  }
  
  public boolean verifyClassCheckResult(LauncherTrustManager.CheckClassResult paramCheckClassResult) {
    return (paramCheckClassResult == null) ? false : ((paramCheckClassResult.type == LauncherTrustManager.CheckClassResultType.SUCCESS));
  }
  
  public void verifyClassCheckResultExceptional(LauncherTrustManager.CheckClassResult paramCheckClassResult) throws Exception {
    if (verifyClassCheckResult(paramCheckClassResult))
      return; 
    if (paramCheckClassResult.exception != null)
      throw paramCheckClassResult.exception; 
    throw new SecurityException(paramCheckClassResult.type.name());
  }
  
  public LauncherModule getModule(String paramString) {
    for (LauncherModule launcherModule : this.modules) {
      LauncherModuleInfo launcherModuleInfo = launcherModule.getModuleInfo();
      if (launcherModuleInfo.name.equals(paramString) || (launcherModuleInfo.providers.length > 0 && Arrays.<String>asList(launcherModuleInfo.providers).contains(paramString)))
        return launcherModule; 
    } 
    return null;
  }
  
  public LauncherModule getCoreModule() {
    return null;
  }
  
  public ClassLoader getModuleClassLoader() {
    return this.classLoader;
  }
  
  public <T extends LauncherModule> T getModule(Class<? extends T> paramClass) {
    for (LauncherModule launcherModule : this.modules) {
      if (paramClass.isAssignableFrom(launcherModule.getClass()))
        return (T)launcherModule; 
    } 
    return null;
  }
  
  public <T> T getModuleByInterface(Class<T> paramClass) {
    for (LauncherModule launcherModule : this.modules) {
      if (paramClass.isAssignableFrom(launcherModule.getClass()))
        return (T)launcherModule; 
    } 
    return null;
  }
  
  public <T> List<T> getModulesByInterface(Class<T> paramClass) {
    ArrayList<LauncherModule> arrayList = new ArrayList();
    for (LauncherModule launcherModule : this.modules) {
      if (paramClass.isAssignableFrom(launcherModule.getClass()))
        arrayList.add(launcherModule); 
    } 
    return (List)arrayList;
  }
  
  public <T extends LauncherModule> T findModule(Class<? extends T> paramClass, Predicate<Version> paramPredicate) {
    for (LauncherModule launcherModule : this.modules) {
      LauncherModuleInfo launcherModuleInfo = launcherModule.getModuleInfo();
      if (paramPredicate.test(launcherModuleInfo.version) && paramClass.isAssignableFrom(launcherModule.getClass()))
        return (T)launcherModule; 
    } 
    return null;
  }
  
  public LauncherModule findModule(String paramString, Predicate<Version> paramPredicate) {
    for (LauncherModule launcherModule : this.modules) {
      LauncherModuleInfo launcherModuleInfo = launcherModule.getModuleInfo();
      if (paramPredicate.test(launcherModuleInfo.version) && paramString.equals(launcherModuleInfo.name))
        return launcherModule; 
    } 
    return null;
  }
  
  public <T extends LauncherModule.Event> void invokeEvent(T paramT) {
    for (LauncherModule launcherModule : this.modules) {
      launcherModule.callEvent((LauncherModule.Event)paramT);
      if (paramT.isCancel())
        return; 
    } 
  }
  
  public ModulesConfigManager getConfigManager() {
    return this.modulesConfigManager;
  }
  
  void addUrlToClassLoader(URL paramURL) {
    this.classLoader.addURL(paramURL);
  }
  
  protected static class ModulesClassLoader extends URLClassLoader {
    public ModulesClassLoader(URL[] param1ArrayOfURL, ClassLoader param1ClassLoader) {
      super("MODULES", param1ArrayOfURL, param1ClassLoader);
    }
    
    public void addURL(URL param1URL) {
      super.addURL(param1URL);
    }
    
    static {
    
    }
  }
  
  protected final class ModulesVisitor extends SimpleFileVisitor<Path> {
    public FileVisitResult visitFile(Path param1Path, BasicFileAttributes param1BasicFileAttributes) throws IOException {
      if (param1Path.toFile().getName().endsWith(".jar"))
        SimpleModuleManager.this.loadModule(param1Path); 
      return super.visitFile(param1Path, param1BasicFileAttributes);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\modules\impl\SimpleModuleManager.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
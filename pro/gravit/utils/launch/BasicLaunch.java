package pro.gravit.utils.launch;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.List;
import java.util.jar.JarFile;
import pro.gravit.utils.helper.HackHelper;
import pro.gravit.utils.helper.JVMHelper;

public class BasicLaunch implements Launch {
  private Instrumentation instrumentation;
  
  private MethodHandles.Lookup hackLookup;
  
  public BasicLaunch(Instrumentation paramInstrumentation) {
    this.instrumentation = paramInstrumentation;
  }
  
  public BasicLaunch() {}
  
  public ClassLoaderControl init(List<Path> paramList, String paramString, LaunchOptions paramLaunchOptions) {
    if (paramLaunchOptions.enableHacks)
      this.hackLookup = HackHelper.createHackLookup(BasicLaunch.class); 
    return new BasicClassLoaderControl();
  }
  
  public void launch(String paramString1, String paramString2, Collection<String> paramCollection) throws Throwable {
    Class<?> clazz = Class.forName(paramString1);
    MethodHandle methodHandle = MethodHandles.lookup().findStatic(clazz, "main", MethodType.methodType(void.class, String[].class)).asFixedArity();
    JVMHelper.fullGC();
    methodHandle.asFixedArity().invokeWithArguments(new Object[] { paramCollection.toArray(new String[0]) });
  }
  
  private class BasicClassLoaderControl implements ClassLoaderControl {
    public void addLauncherPackage(String param1String) {
      throw new UnsupportedOperationException();
    }
    
    public void clearLauncherPackages() {
      throw new UnsupportedOperationException();
    }
    
    public void addTransformer(final ClassLoaderControl.ClassTransformer transformer) {
      if (BasicLaunch.this.instrumentation == null)
        throw new UnsupportedOperationException(); 
      BasicLaunch.this.instrumentation.addTransformer(new ClassFileTransformer() {
            public byte[] transform(ClassLoader param2ClassLoader, String param2String, Class<?> param2Class, ProtectionDomain param2ProtectionDomain, byte[] param2ArrayOfbyte) {
              return transformer.filter(null, param2String) ? transformer.transform(null, param2String, param2ProtectionDomain, param2ArrayOfbyte) : param2ArrayOfbyte;
            }
          });
    }
    
    public void addURL(URL param1URL) {
      if (BasicLaunch.this.instrumentation == null)
        throw new UnsupportedOperationException(); 
      try {
        BasicLaunch.this.instrumentation.appendToSystemClassLoaderSearch(new JarFile(new File(param1URL.toURI())));
      } catch (URISyntaxException|IOException uRISyntaxException) {
        throw new RuntimeException(uRISyntaxException);
      } 
    }
    
    public void addJar(Path param1Path) {
      if (BasicLaunch.this.instrumentation == null)
        throw new UnsupportedOperationException(); 
      try {
        BasicLaunch.this.instrumentation.appendToSystemClassLoaderSearch(new JarFile(param1Path.toFile()));
      } catch (IOException iOException) {
        throw new RuntimeException(iOException);
      } 
    }
    
    public URL[] getURLs() {
      String str = System.getProperty("java.class.path");
      String[] arrayOfString = str.split(File.pathSeparator);
      URL[] arrayOfURL = new URL[arrayOfString.length];
      try {
        for (byte b = 0; b < arrayOfString.length; b++)
          arrayOfURL[b] = Paths.get(arrayOfString[b], new String[0]).toAbsolutePath().toUri().toURL(); 
      } catch (MalformedURLException malformedURLException) {
        throw new RuntimeException(malformedURLException);
      } 
      return arrayOfURL;
    }
    
    public Class<?> getClass(String param1String) throws ClassNotFoundException {
      return Class.forName(param1String);
    }
    
    public ClassLoader getClassLoader() {
      return BasicLaunch.class.getClassLoader();
    }
    
    public Object getJava9ModuleController() {
      return null;
    }
    
    public MethodHandles.Lookup getHackLookup() {
      return BasicLaunch.this.hackLookup;
    }
  }
  
  class null implements ClassFileTransformer {
    public byte[] transform(ClassLoader param1ClassLoader, String param1String, Class<?> param1Class, ProtectionDomain param1ProtectionDomain, byte[] param1ArrayOfbyte) {
      return transformer.filter(null, param1String) ? transformer.transform(null, param1String, param1ProtectionDomain, param1ArrayOfbyte) : param1ArrayOfbyte;
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\launch\BasicLaunch.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
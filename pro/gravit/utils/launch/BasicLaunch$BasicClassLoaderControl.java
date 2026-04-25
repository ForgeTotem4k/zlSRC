package pro.gravit.utils.launch;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;

class BasicClassLoaderControl implements ClassLoaderControl {
  public void addLauncherPackage(String paramString) {
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
  
  public void addURL(URL paramURL) {
    if (BasicLaunch.this.instrumentation == null)
      throw new UnsupportedOperationException(); 
    try {
      BasicLaunch.this.instrumentation.appendToSystemClassLoaderSearch(new JarFile(new File(paramURL.toURI())));
    } catch (URISyntaxException|IOException uRISyntaxException) {
      throw new RuntimeException(uRISyntaxException);
    } 
  }
  
  public void addJar(Path paramPath) {
    if (BasicLaunch.this.instrumentation == null)
      throw new UnsupportedOperationException(); 
    try {
      BasicLaunch.this.instrumentation.appendToSystemClassLoaderSearch(new JarFile(paramPath.toFile()));
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
  
  public Class<?> getClass(String paramString) throws ClassNotFoundException {
    return Class.forName(paramString);
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


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\launch\BasicLaunch$BasicClassLoaderControl.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
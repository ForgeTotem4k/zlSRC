package pro.gravit.launcher.base.api;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.launch.ClassLoaderControl;

public class ClientService {
  public static Instrumentation instrumentation;
  
  public static ClassLoaderControl classLoaderControl;
  
  public static String nativePath;
  
  public static URL[] baseURLs;
  
  public static String findLibrary(String paramString) {
    if (paramString == null)
      return null; 
    boolean bool1 = !paramString.endsWith(JVMHelper.NATIVE_EXTENSION) ? true : false;
    boolean bool2 = !paramString.startsWith(JVMHelper.NATIVE_PREFIX) ? true : false;
    return (bool1 && bool2) ? nativePath.concat(IOHelper.PLATFORM_SEPARATOR).concat(JVMHelper.NATIVE_PREFIX).concat(paramString).concat(JVMHelper.NATIVE_EXTENSION) : (bool1 ? nativePath.concat(IOHelper.PLATFORM_SEPARATOR).concat(paramString).concat(JVMHelper.NATIVE_EXTENSION) : (bool2 ? nativePath.concat(IOHelper.PLATFORM_SEPARATOR).concat(JVMHelper.NATIVE_PREFIX).concat(paramString) : nativePath.concat(IOHelper.PLATFORM_SEPARATOR).concat(paramString)));
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\api\ClientService.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
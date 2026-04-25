package pro.gravit.launcher.base;

import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import pro.gravit.launcher.base.profiles.ClientProfile;
import pro.gravit.launcher.core.managers.GsonManager;
import pro.gravit.launcher.core.serialize.HInput;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.LogHelper;

public final class Launcher {
  public static final AtomicBoolean LAUNCHED = new AtomicBoolean(false);
  
  public static final String RUNTIME_DIR = "runtime";
  
  public static final String CONFIG_FILE = "config.bin";
  
  private static final AtomicReference<LauncherConfig> CONFIG = new AtomicReference<>();
  
  private static final Pattern UUID_PATTERN = Pattern.compile("-", 16);
  
  public static ClientProfile profile;
  
  public static GsonManager gsonManager;
  
  public static LauncherConfig getConfig() {
    LauncherConfig launcherConfig = CONFIG.get();
    if (launcherConfig == null) {
      try {
        HInput hInput = new HInput(IOHelper.newInput(IOHelper.getResourceURL("config.bin")));
        try {
          launcherConfig = new LauncherConfig(hInput);
          hInput.close();
        } catch (Throwable throwable) {
          try {
            hInput.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } catch (IOException|java.security.spec.InvalidKeySpecException iOException) {
        throw new SecurityException(iOException);
      } 
      CONFIG.set(launcherConfig);
    } 
    return launcherConfig;
  }
  
  public static void setConfig(LauncherConfig paramLauncherConfig) {
    CONFIG.set(paramLauncherConfig);
  }
  
  public static URL getResourceURL(String paramString) throws IOException {
    LauncherConfig launcherConfig = getConfig();
    byte[] arrayOfByte = launcherConfig.runtime.get(paramString);
    if (arrayOfByte == null)
      throw new NoSuchFileException(paramString); 
    return IOHelper.getResourceURL("runtime/" + paramString);
  }
  
  public static URL getResourceURL(String paramString1, String paramString2) throws IOException {
    LauncherConfig launcherConfig = getConfig();
    byte[] arrayOfByte = launcherConfig.runtime.get(paramString1);
    if (arrayOfByte == null)
      throw new NoSuchFileException(paramString1); 
    return IOHelper.getResourceURL(paramString2 + "/" + paramString2);
  }
  
  public static String toHash(UUID paramUUID) {
    return UUID_PATTERN.matcher(paramUUID.toString()).replaceAll("");
  }
  
  public static void applyLauncherEnv(LauncherConfig.LauncherEnvironment paramLauncherEnvironment) {
    switch (paramLauncherEnvironment) {
      case DEV:
        LogHelper.setDevEnabled(true);
        LogHelper.setStacktraceEnabled(true);
        LogHelper.setDebugEnabled(true);
        break;
      case DEBUG:
        LogHelper.setDebugEnabled(true);
        LogHelper.setStacktraceEnabled(true);
        break;
      case PROD:
        LogHelper.setStacktraceEnabled(false);
        LogHelper.setDebugEnabled(false);
        LogHelper.setDevEnabled(false);
        break;
    } 
  }
  
  public static String makeSpecialGuardDirName(JVMHelper.ARCH paramARCH, JVMHelper.OS paramOS) {
    return String.format("%s-%s", new Object[] { paramARCH.name, paramOS.name });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\Launcher.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
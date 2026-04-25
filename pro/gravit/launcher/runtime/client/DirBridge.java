package pro.gravit.launcher.runtime.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.JVMHelper;
import pro.gravit.utils.helper.LogHelper;

public class DirBridge {
  public static final String USE_CUSTOMDIR_PROPERTY = "launcher.usecustomdir";
  
  public static final String CUSTOMDIR_PROPERTY = "launcher.customdir";
  
  public static final String USE_OPTDIR_PROPERTY = "launcher.useoptdir";
  
  public static Path dir;
  
  public static Path dirUpdates;
  
  public static Path defaultUpdatesDir;
  
  public static boolean useLegacyDir;
  
  public static void move(Path paramPath) throws IOException {
    if (paramPath == null) {
      LogHelper.debug("Invalid dir (null)");
      if (LogHelper.isDevEnabled())
        LogHelper.dev(LogHelper.toString(new Throwable("Check stack of call DirBridge with null path..."))); 
      return;
    } 
    Path path = dirUpdates;
    dirUpdates = paramPath;
    LogHelper.dev(paramPath.toString());
    IOHelper.move(path, dirUpdates);
  }
  
  public static Path getAppDataDir() throws IOException {
    boolean bool = Boolean.getBoolean(System.getProperty("launcher.usecustomdir", "false"));
    if (bool)
      return Paths.get(System.getProperty("launcher.customdir"), new String[0]); 
    if (JVMHelper.OS_TYPE == JVMHelper.OS.LINUX) {
      boolean bool1 = Boolean.getBoolean(System.getProperty("launcher.useoptdir", "false"));
      if (bool1) {
        Path path1 = Paths.get("/", new String[0]).resolve("opt");
        if (!IOHelper.isDir(path1))
          Files.createDirectories(path1, (FileAttribute<?>[])new FileAttribute[0]); 
        return path1;
      } 
      Path path = IOHelper.HOME_DIR.resolve(".minecraftlauncher");
      if (!IOHelper.isDir(path))
        Files.createDirectories(path, (FileAttribute<?>[])new FileAttribute[0]); 
      return path;
    } 
    if (JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE) {
      if (System.getenv().containsKey("appdata"))
        return Paths.get(System.getenv().get("appdata"), new String[0]).toAbsolutePath(); 
      if (System.getenv().containsKey("APPDATA"))
        return Paths.get(System.getenv().get("APPDATA"), new String[0]).toAbsolutePath(); 
      Path path = IOHelper.HOME_DIR.resolve("AppData").resolve("Roaming");
      if (!IOHelper.isDir(path))
        Files.createDirectories(path, (FileAttribute<?>[])new FileAttribute[0]); 
      return path;
    } 
    if (JVMHelper.OS_TYPE == JVMHelper.OS.MACOSX) {
      Path path = IOHelper.HOME_DIR.resolve("minecraft");
      if (!IOHelper.isDir(path))
        Files.createDirectories(path, (FileAttribute<?>[])new FileAttribute[0]); 
      return path;
    } 
    return IOHelper.HOME_DIR;
  }
  
  public static Path getLauncherDir(String paramString) throws IOException {
    return getAppDataDir().resolve(paramString);
  }
  
  public static Path getGuardDir() {
    return dir.resolve("guard");
  }
  
  public static Path getGuardDir(JVMHelper.ARCH paramARCH, JVMHelper.OS paramOS) {
    Path path = getGuardDir().resolve(Launcher.makeSpecialGuardDirName(paramARCH, paramOS));
    try {
      IOHelper.createParentDirs(path);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    } 
    return path;
  }
  
  public static Path getLegacyLauncherDir(String paramString) {
    return IOHelper.HOME_DIR.resolve(paramString);
  }
  
  public static void setUseLegacyDir(boolean paramBoolean) {
    useLegacyDir = paramBoolean;
  }
  
  static {
    String str = (Launcher.getConfig()).projectName;
    try {
      dir = getLauncherDir(str);
      if (!IOHelper.exists(dir))
        Files.createDirectories(dir, (FileAttribute<?>[])new FileAttribute[0]); 
      defaultUpdatesDir = dir.resolve("updates");
      if (!IOHelper.exists(defaultUpdatesDir))
        Files.createDirectories(defaultUpdatesDir, (FileAttribute<?>[])new FileAttribute[0]); 
    } catch (IOException iOException) {
      LogHelper.error(iOException);
    } 
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\runtime\client\DirBridge.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
package pro.gravit.launcher.gui.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.LauncherConfig;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.utils.RuntimeCryptedFile;
import pro.gravit.utils.enfs.EnFS;
import pro.gravit.utils.enfs.dir.CachedFile;
import pro.gravit.utils.enfs.dir.FileEntry;
import pro.gravit.utils.enfs.dir.RealFile;
import pro.gravit.utils.enfs.dir.URLFile;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;
import pro.gravit.utils.helper.SecurityHelper;

public class EnFSHelper {
  private static final Set<String> themesCached = new HashSet<>(1);
  
  private static final String BASE_DIRECTORY = "tgui";
  
  public static void initEnFS() throws IOException {
    EnFS.main.newDirectory(Paths.get("tgui", new String[0]));
    if (LogHelper.isDevEnabled() || JavaFXApplication.getInstance().isDebugMode())
      EnFS.DEBUG_OUTPUT = new LauncherEnFsDebugOutput(); 
  }
  
  public static Path initEnFSDirectory(LauncherConfig paramLauncherConfig, String paramString, Path paramPath) throws IOException {
    String str = (paramString != null) ? paramString : "common";
    Path path1 = Paths.get("tgui", new String[] { str });
    if (themesCached.contains(str))
      return path1; 
    Path path2 = (paramPath == null) ? Path.of("", new String[0]) : paramPath;
    Path path3 = path2.resolve("themes").resolve(str);
    if (paramPath != null) {
      HashSet hashSet = new HashSet();
      Path path = paramPath.relativize(path3);
      Stream<Path> stream = Files.walk(paramPath, new java.nio.file.FileVisitOption[0]);
      try {
        stream.forEach(paramPath4 -> {
              if (Files.isDirectory(paramPath4, new java.nio.file.LinkOption[0]))
                return; 
              Path path = paramPath1.relativize(paramPath4);
              if (paramPath4.startsWith(paramPath2))
                paramSet.add(paramPath3.relativize(path)); 
            });
        if (stream != null)
          stream.close(); 
      } catch (Throwable throwable) {
        if (stream != null)
          try {
            stream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
      stream = Files.walk(paramPath, new java.nio.file.FileVisitOption[0]);
      try {
        stream.forEach(paramPath4 -> {
              if (Files.isDirectory(paramPath4, new java.nio.file.LinkOption[0]))
                return; 
              Path path = paramPath1.relativize(paramPath4);
              if (paramSet.contains(path)) {
                paramPath4 = paramPath2.resolve(path);
                paramSet.remove(path);
              } 
              try {
                Path path1 = paramPath3.resolve(path);
                EnFS.main.newDirectories(path1.getParent());
                RealFile realFile = new RealFile(paramPath4);
                EnFS.main.addFile(path1, (FileEntry)realFile);
              } catch (IOException iOException) {
                LogHelper.error(iOException);
              } 
            });
        if (stream != null)
          stream.close(); 
      } catch (Throwable throwable) {
        if (stream != null)
          try {
            stream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } else {
      HashSet hashSet = new HashSet();
      String str1 = String.format("themes/%s/", new Object[] { str });
      paramLauncherConfig.runtime.forEach((paramString2, paramArrayOfbyte) -> {
            if (paramString2.startsWith(paramString1))
              paramSet.add(paramString2.substring(paramString1.length())); 
          });
      paramLauncherConfig.runtime.forEach((paramString2, paramArrayOfbyte) -> {
            String str = paramString2;
            if (paramSet.contains(str)) {
              paramString2 = paramString1.concat(str);
              paramArrayOfbyte = (byte[])paramLauncherConfig.runtime.get(paramString2);
              paramSet.remove(str);
              LogHelper.dev("Replace %s to %s", new Object[] { str, paramString2 });
            } 
            try {
              Path path = paramPath.resolve(str);
              EnFS.main.newDirectories(path.getParent());
              FileEntry fileEntry = makeFile(paramLauncherConfig, paramString2, paramArrayOfbyte);
              LogHelper.dev("makeFile %s (%s) to %s", new Object[] { paramString2, SecurityHelper.toHex(paramArrayOfbyte), path.toString() });
              EnFS.main.addFile(path, fileEntry);
            } catch (IOException iOException) {
              LogHelper.error(iOException);
            } 
          });
    } 
    return path1;
  }
  
  private static FileEntry makeFile(LauncherConfig paramLauncherConfig, String paramString, byte[] paramArrayOfbyte) throws IOException {
    CachedFile cachedFile;
    if (paramLauncherConfig.runtimeEncryptKey == null) {
      URLFile uRLFile = new URLFile(Launcher.getResourceURL(paramString));
    } else {
      String str = "runtime/" + SecurityHelper.toHex(paramArrayOfbyte);
      cachedFile = new CachedFile((FileEntry)new RuntimeCryptedFile(() -> {
              try {
                return IOHelper.newInput(IOHelper.getResourceURL(paramString));
              } catch (IOException iOException) {
                throw new RuntimeException(iOException);
              } 
            }SecurityHelper.fromHex(paramLauncherConfig.runtimeEncryptKey)));
    } 
    return (FileEntry)cachedFile;
  }
  
  public static URL getURL(String paramString) throws IOException {
    try {
      InputStream inputStream = EnFS.main.getInputStream(Paths.get(paramString, new String[0]));
      try {
        URL uRL = (new URI("enfs", null, "/" + paramString, null)).toURL();
        if (inputStream != null)
          inputStream.close(); 
        return uRL;
      } catch (Throwable throwable) {
        if (inputStream != null)
          try {
            inputStream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (UnsupportedOperationException unsupportedOperationException) {
      throw new FileNotFoundException(paramString);
    } catch (URISyntaxException uRISyntaxException) {
      throw new RuntimeException(uRISyntaxException);
    } 
  }
  
  private static class LauncherEnFsDebugOutput implements EnFS.DebugOutput {
    public void debug(String param1String) {
      LogHelper.debug(param1String);
    }
    
    public void debug(String param1String, Object... param1VarArgs) {
      LogHelper.debug(param1String, param1VarArgs);
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\helper\EnFSHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
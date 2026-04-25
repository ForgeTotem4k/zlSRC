package pro.gravit.utils.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class UnpackHelper {
  public static void unpack(URL paramURL, Path paramPath) throws IOException {
    if (IOHelper.isFile(paramPath) && matches(paramPath, paramURL))
      return; 
    Files.deleteIfExists(paramPath);
    IOHelper.createParentDirs(paramPath);
    InputStream inputStream = IOHelper.newInput(paramURL);
    try {
      IOHelper.transfer(inputStream, paramPath);
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
  }
  
  private static boolean matches(Path paramPath, URL paramURL) {
    try {
      return Arrays.equals(SecurityHelper.digest(SecurityHelper.DigestAlgorithm.SHA256, paramURL), SecurityHelper.digest(SecurityHelper.DigestAlgorithm.SHA256, paramPath));
    } catch (IOException iOException) {
      return false;
    } 
  }
  
  public static boolean unpackZipNoCheck(URL paramURL, Path paramPath) throws IOException {
    if (Files.isDirectory(paramPath, new java.nio.file.LinkOption[0]))
      return false; 
    Files.deleteIfExists(paramPath);
    Files.createDirectory(paramPath, (FileAttribute<?>[])new FileAttribute[0]);
    ZipInputStream zipInputStream = IOHelper.newZipInput(paramURL);
    try {
      for (ZipEntry zipEntry = zipInputStream.getNextEntry(); zipEntry != null; zipEntry = zipInputStream.getNextEntry()) {
        if (!zipEntry.isDirectory())
          IOHelper.transfer(zipInputStream, paramPath.resolve(IOHelper.toPath(zipEntry.getName()))); 
      } 
      if (zipInputStream != null)
        zipInputStream.close(); 
    } catch (Throwable throwable) {
      if (zipInputStream != null)
        try {
          zipInputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
    return true;
  }
  
  public static void unpackZipNoCheck(String paramString, Path paramPath) throws IOException {
    try {
      if (Files.isDirectory(paramPath, new java.nio.file.LinkOption[0]))
        return; 
      Files.deleteIfExists(paramPath);
      Files.createDirectory(paramPath, (FileAttribute<?>[])new FileAttribute[0]);
      ZipInputStream zipInputStream = IOHelper.newZipInput(IOHelper.getResourceURL(paramString));
      try {
        for (ZipEntry zipEntry = zipInputStream.getNextEntry(); zipEntry != null; zipEntry = zipInputStream.getNextEntry()) {
          if (!zipEntry.isDirectory())
            IOHelper.transfer(zipInputStream, paramPath.resolve(IOHelper.toPath(zipEntry.getName()))); 
        } 
        if (zipInputStream != null)
          zipInputStream.close(); 
      } catch (Throwable throwable) {
        if (zipInputStream != null)
          try {
            zipInputStream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (NoSuchFileException noSuchFileException) {}
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\UnpackHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
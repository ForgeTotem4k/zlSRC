package pro.gravit.utils.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarHelper {
  public static void zipWalk(ZipInputStream paramZipInputStream, ZipWalkCallback paramZipWalkCallback) throws IOException {
    for (ZipEntry zipEntry = paramZipInputStream.getNextEntry(); zipEntry != null; zipEntry = paramZipInputStream.getNextEntry())
      paramZipWalkCallback.process(paramZipInputStream, zipEntry); 
  }
  
  public static void jarWalk(ZipInputStream paramZipInputStream, JarWalkCallback paramJarWalkCallback) throws IOException {
    for (ZipEntry zipEntry = paramZipInputStream.getNextEntry(); zipEntry != null; zipEntry = paramZipInputStream.getNextEntry()) {
      String str = zipEntry.getName();
      if (str.endsWith(".class")) {
        String str1 = str.replaceAll("/", ".").substring(0, str.length() - ".class".length());
        String str2 = str1.substring(str1.lastIndexOf('.') + 1);
        paramJarWalkCallback.process(paramZipInputStream, zipEntry, str1, str2);
      } 
    } 
  }
  
  public static Map<String, String> jarMap(ZipInputStream paramZipInputStream, boolean paramBoolean) throws IOException {
    HashMap<Object, Object> hashMap = new HashMap<>();
    jarMap(paramZipInputStream, (Map)hashMap, paramBoolean);
    return (Map)hashMap;
  }
  
  public static void jarMap(ZipInputStream paramZipInputStream, Map<String, String> paramMap, boolean paramBoolean) throws IOException {
    jarWalk(paramZipInputStream, (paramZipInputStream, paramZipEntry, paramString1, paramString2) -> {
          if (paramBoolean) {
            paramMap.put(paramString2, paramString1);
          } else {
            paramMap.putIfAbsent(paramString2, paramString1);
          } 
        });
  }
  
  public static Map<String, String> jarMap(Path paramPath, boolean paramBoolean) throws IOException {
    ZipInputStream zipInputStream = IOHelper.newZipInput(paramPath);
    try {
      Map<String, String> map = jarMap(zipInputStream, paramBoolean);
      if (zipInputStream != null)
        zipInputStream.close(); 
      return map;
    } catch (Throwable throwable) {
      if (zipInputStream != null)
        try {
          zipInputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  public static void jarMap(Path paramPath, Map<String, String> paramMap, boolean paramBoolean) throws IOException {
    ZipInputStream zipInputStream = IOHelper.newZipInput(paramPath);
    try {
      jarMap(zipInputStream, paramMap, paramBoolean);
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
  }
  
  public static Map<String, String> jarMap(Class<?> paramClass, boolean paramBoolean) throws IOException {
    Path path = IOHelper.getCodeSource(paramClass);
    return jarMap(path, paramBoolean);
  }
  
  public static void jarMap(Class<?> paramClass, Map<String, String> paramMap, boolean paramBoolean) throws IOException {
    Path path = IOHelper.getCodeSource(paramClass);
    jarMap(path, paramMap, paramBoolean);
  }
  
  public static String getClassFile(Class<?> paramClass) {
    return getClassFile(paramClass.getName());
  }
  
  public static String getClassFile(String paramString) {
    return paramString.replace('.', '/').concat(".class");
  }
  
  public static byte[] getClassBytes(Class<?> paramClass) throws IOException {
    return getClassBytes(paramClass, paramClass.getClassLoader());
  }
  
  public static byte[] getClassBytes(Class<?> paramClass, ClassLoader paramClassLoader) throws IOException {
    return IOHelper.read(paramClassLoader.getResourceAsStream(getClassFile(paramClass)));
  }
  
  public static InputStream getClassBytesStream(Class<?> paramClass) {
    return getClassBytesStream(paramClass, paramClass.getClassLoader());
  }
  
  public static InputStream getClassBytesStream(Class<?> paramClass, ClassLoader paramClassLoader) {
    return paramClassLoader.getResourceAsStream(getClassFile(paramClass));
  }
  
  public static byte[] getClassFromJar(String paramString, Path paramPath) throws IOException {
    String str = getClassFile(paramString);
    ZipInputStream zipInputStream = IOHelper.newZipInput(paramPath);
    try {
      for (ZipEntry zipEntry = zipInputStream.getNextEntry(); zipEntry != null; zipEntry = zipInputStream.getNextEntry()) {
        if (zipEntry.getName().equals(str)) {
          byte[] arrayOfByte = IOHelper.read(zipInputStream);
          if (zipInputStream != null)
            zipInputStream.close(); 
          return arrayOfByte;
        } 
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
    throw new FileNotFoundException(str);
  }
  
  static {
  
  }
  
  @FunctionalInterface
  public static interface ZipWalkCallback {
    void process(ZipInputStream param1ZipInputStream, ZipEntry param1ZipEntry);
    
    static {
    
    }
  }
  
  @FunctionalInterface
  public static interface JarWalkCallback {
    void process(ZipInputStream param1ZipInputStream, ZipEntry param1ZipEntry, String param1String1, String param1String2);
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravi\\utils\helper\JarHelper.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
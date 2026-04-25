package io.sentry.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
public final class FileUtils {
  public static boolean deleteRecursively(@Nullable File paramFile) {
    if (paramFile == null || !paramFile.exists())
      return true; 
    if (paramFile.isFile())
      return paramFile.delete(); 
    File[] arrayOfFile = paramFile.listFiles();
    if (arrayOfFile == null)
      return true; 
    for (File file : arrayOfFile) {
      if (!deleteRecursively(file))
        return false; 
    } 
    return paramFile.delete();
  }
  
  @Nullable
  public static String readText(@Nullable File paramFile) throws IOException {
    if (paramFile == null || !paramFile.exists() || !paramFile.isFile() || !paramFile.canRead())
      return null; 
    StringBuilder stringBuilder = new StringBuilder();
    BufferedReader bufferedReader = new BufferedReader(new FileReader(paramFile));
    try {
      String str;
      if ((str = bufferedReader.readLine()) != null)
        stringBuilder.append(str); 
      while ((str = bufferedReader.readLine()) != null)
        stringBuilder.append("\n").append(str); 
      bufferedReader.close();
    } catch (Throwable throwable) {
      try {
        bufferedReader.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
    return stringBuilder.toString();
  }
  
  public static byte[] readBytesFromFile(String paramString, long paramLong) throws IOException, SecurityException {
    File file = new File(paramString);
    if (!file.exists())
      throw new IOException(String.format("File '%s' doesn't exists", new Object[] { file.getName() })); 
    if (!file.isFile())
      throw new IOException(String.format("Reading path %s failed, because it's not a file.", new Object[] { paramString })); 
    if (!file.canRead())
      throw new IOException(String.format("Reading the item %s failed, because can't read the file.", new Object[] { paramString })); 
    if (file.length() > paramLong)
      throw new IOException(String.format("Reading file failed, because size located at '%s' with %d bytes is bigger than the maximum allowed size of %d bytes.", new Object[] { paramString, Long.valueOf(file.length()), Long.valueOf(paramLong) })); 
    FileInputStream fileInputStream = new FileInputStream(paramString);
    try {
      BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
      try {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
          byte[] arrayOfByte1 = new byte[1024];
          boolean bool = false;
          int i;
          while ((i = bufferedInputStream.read(arrayOfByte1)) != -1)
            byteArrayOutputStream.write(arrayOfByte1, bool, i); 
          byte[] arrayOfByte2 = byteArrayOutputStream.toByteArray();
          byteArrayOutputStream.close();
          bufferedInputStream.close();
          fileInputStream.close();
          return arrayOfByte2;
        } catch (Throwable throwable) {
          try {
            byteArrayOutputStream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } catch (Throwable throwable) {
        try {
          bufferedInputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      try {
        fileInputStream.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentr\\util\FileUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
package io.sentry.cache;

import io.sentry.JsonDeserializer;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class CacheUtils {
  private static final Charset UTF_8 = Charset.forName("UTF-8");
  
  static <T> void store(@NotNull SentryOptions paramSentryOptions, @NotNull T paramT, @NotNull String paramString1, @NotNull String paramString2) {
    File file1 = ensureCacheDir(paramSentryOptions, paramString1);
    if (file1 == null) {
      paramSentryOptions.getLogger().log(SentryLevel.INFO, "Cache dir is not set, cannot store in scope cache", new Object[0]);
      return;
    } 
    File file2 = new File(file1, paramString2);
    if (file2.exists()) {
      paramSentryOptions.getLogger().log(SentryLevel.DEBUG, "Overwriting %s in scope cache", new Object[] { paramString2 });
      if (!file2.delete())
        paramSentryOptions.getLogger().log(SentryLevel.ERROR, "Failed to delete: %s", new Object[] { file2.getAbsolutePath() }); 
    } 
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(file2);
      try {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream, UTF_8));
        try {
          paramSentryOptions.getSerializer().serialize(paramT, bufferedWriter);
          bufferedWriter.close();
        } catch (Throwable throwable) {
          try {
            bufferedWriter.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
        fileOutputStream.close();
      } catch (Throwable throwable) {
        try {
          fileOutputStream.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (Throwable throwable) {
      paramSentryOptions.getLogger().log(SentryLevel.ERROR, throwable, "Error persisting entity: %s", new Object[] { paramString2 });
    } 
  }
  
  static void delete(@NotNull SentryOptions paramSentryOptions, @NotNull String paramString1, @NotNull String paramString2) {
    File file1 = ensureCacheDir(paramSentryOptions, paramString1);
    if (file1 == null) {
      paramSentryOptions.getLogger().log(SentryLevel.INFO, "Cache dir is not set, cannot delete from scope cache", new Object[0]);
      return;
    } 
    File file2 = new File(file1, paramString2);
    if (file2.exists()) {
      paramSentryOptions.getLogger().log(SentryLevel.DEBUG, "Deleting %s from scope cache", new Object[] { paramString2 });
      if (!file2.delete())
        paramSentryOptions.getLogger().log(SentryLevel.ERROR, "Failed to delete: %s", new Object[] { file2.getAbsolutePath() }); 
    } 
  }
  
  @Nullable
  static <T, R> T read(@NotNull SentryOptions paramSentryOptions, @NotNull String paramString1, @NotNull String paramString2, @NotNull Class<T> paramClass, @Nullable JsonDeserializer<R> paramJsonDeserializer) {
    File file1 = ensureCacheDir(paramSentryOptions, paramString1);
    if (file1 == null) {
      paramSentryOptions.getLogger().log(SentryLevel.INFO, "Cache dir is not set, cannot read from scope cache", new Object[0]);
      return null;
    } 
    File file2 = new File(file1, paramString2);
    if (file2.exists()) {
      try {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file2), UTF_8));
        try {
          if (paramJsonDeserializer == null) {
            Object object1 = paramSentryOptions.getSerializer().deserialize(bufferedReader, paramClass);
            bufferedReader.close();
            return (T)object1;
          } 
          Object object = paramSentryOptions.getSerializer().deserializeCollection(bufferedReader, paramClass, paramJsonDeserializer);
          bufferedReader.close();
          return (T)object;
        } catch (Throwable throwable) {
          try {
            bufferedReader.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          } 
          throw throwable;
        } 
      } catch (Throwable throwable) {
        paramSentryOptions.getLogger().log(SentryLevel.ERROR, throwable, "Error reading entity from scope cache: %s", new Object[] { paramString2 });
      } 
    } else {
      paramSentryOptions.getLogger().log(SentryLevel.DEBUG, "No entry stored for %s", new Object[] { paramString2 });
    } 
    return null;
  }
  
  @Nullable
  private static File ensureCacheDir(@NotNull SentryOptions paramSentryOptions, @NotNull String paramString) {
    String str = paramSentryOptions.getCacheDirPath();
    if (str == null)
      return null; 
    File file = new File(str, paramString);
    file.mkdirs();
    return file;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\io\sentry\cache\CacheUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */
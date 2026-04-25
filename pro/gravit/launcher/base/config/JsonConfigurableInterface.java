package pro.gravit.launcher.base.config;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;

public interface JsonConfigurableInterface<T> {
  default void saveConfig() throws IOException {
    saveConfig(getPath());
  }
  
  default void loadConfig() throws IOException {
    loadConfig(getPath());
  }
  
  default void saveConfig(Gson paramGson, Path paramPath) throws IOException {
    BufferedWriter bufferedWriter = IOHelper.newWriter(paramPath);
    try {
      paramGson.toJson(getConfig(), getType(), bufferedWriter);
      if (bufferedWriter != null)
        bufferedWriter.close(); 
    } catch (Throwable throwable) {
      if (bufferedWriter != null)
        try {
          bufferedWriter.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        }  
      throw throwable;
    } 
  }
  
  default String toJsonString(Gson paramGson) {
    return paramGson.toJson(getConfig(), getType());
  }
  
  default String toJsonString() {
    return toJsonString(Launcher.gsonManager.configGson);
  }
  
  default void loadConfig(Gson paramGson, Path paramPath) throws IOException {
    if (generateConfigIfNotExists(paramPath))
      return; 
    try {
      BufferedReader bufferedReader = IOHelper.newReader(paramPath);
      try {
        Object object = paramGson.fromJson(bufferedReader, getType());
        if (object == null) {
          LogHelper.warning("Config %s is null", new Object[] { paramPath });
          resetConfig(paramPath);
          if (bufferedReader != null)
            bufferedReader.close(); 
          return;
        } 
        setConfig((T)object);
        if (bufferedReader != null)
          bufferedReader.close(); 
      } catch (Throwable throwable) {
        if (bufferedReader != null)
          try {
            bufferedReader.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (Exception exception) {
      LogHelper.error(exception);
      resetConfig(paramPath);
    } 
  }
  
  default void saveConfig(Path paramPath) throws IOException {
    saveConfig(Launcher.gsonManager.configGson, paramPath);
  }
  
  default void loadConfig(Path paramPath) throws IOException {
    loadConfig(Launcher.gsonManager.configGson, paramPath);
  }
  
  default void resetConfig() throws IOException {
    setConfig(getDefaultConfig());
    saveConfig();
  }
  
  default void resetConfig(Path paramPath) throws IOException {
    setConfig(getDefaultConfig());
    saveConfig(paramPath);
  }
  
  default boolean generateConfigIfNotExists(Path paramPath) throws IOException {
    if (IOHelper.isFile(paramPath))
      return false; 
    resetConfig(paramPath);
    return true;
  }
  
  default boolean generateConfigIfNotExists() throws IOException {
    if (IOHelper.isFile(getPath()))
      return false; 
    resetConfig();
    return true;
  }
  
  T getConfig();
  
  void setConfig(T paramT);
  
  T getDefaultConfig();
  
  Path getPath();
  
  Type getType();
  
  static {
  
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\base\config\JsonConfigurableInterface.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
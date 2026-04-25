package pro.gravit.launcher.gui.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;

public class FXMLFactory {
  private final ResourceBundle resources;
  
  private final ExecutorService executorService;
  
  public FXMLFactory(ResourceBundle paramResourceBundle, ExecutorService paramExecutorService) {
    this.resources = paramResourceBundle;
    this.executorService = paramExecutorService;
  }
  
  public <T extends Node> CompletableFuture<T> getAsync(String paramString) {
    return CompletableFuture.supplyAsync(() -> {
          try {
            return get(paramString);
          } catch (IOException iOException) {
            throw new FXMLLoadException(iOException);
          } 
        }this.executorService);
  }
  
  public <T extends Node> T get(String paramString) throws IOException {
    long l1 = System.currentTimeMillis();
    FXMLLoader fXMLLoader = newLoaderInstance(JavaFXApplication.getResourceURL(paramString));
    long l2 = System.currentTimeMillis();
    InputStream inputStream = IOHelper.newInput(JavaFXApplication.getResourceURL(paramString));
    try {
      Node node1 = (Node)fXMLLoader.load(inputStream);
      long l = System.currentTimeMillis();
      LogHelper.debug("Fxml load %s time: c: %d | l: %d | total: %d", new Object[] { paramString, Long.valueOf(l2 - l1), Long.valueOf(l - l2), Long.valueOf(l - l1) });
      Node node2 = node1;
      if (inputStream != null)
        inputStream.close(); 
      return (T)node2;
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
  
  public FXMLLoader newLoaderInstance(URL paramURL) {
    FXMLLoader fXMLLoader;
    try {
      fXMLLoader = new FXMLLoader(paramURL);
      if (this.resources != null)
        fXMLLoader.setResources(this.resources); 
    } catch (Exception exception) {
      LogHelper.error(exception);
      return null;
    } 
    fXMLLoader.setCharset(IOHelper.UNICODE_CHARSET);
    fXMLLoader.setClassLoader(FXMLFactory.class.getClassLoader());
    return fXMLLoader;
  }
  
  public static class FXMLLoadException extends RuntimeException {
    public FXMLLoadException() {}
    
    public FXMLLoadException(String param1String) {
      super(param1String);
    }
    
    public FXMLLoadException(String param1String, Throwable param1Throwable) {
      super(param1String, param1Throwable);
    }
    
    public FXMLLoadException(Throwable param1Throwable) {
      super(param1Throwable);
    }
    
    static {
    
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\impl\FXMLFactory.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
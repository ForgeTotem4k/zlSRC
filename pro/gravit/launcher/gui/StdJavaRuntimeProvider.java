package pro.gravit.launcher.gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Application;
import pro.gravit.launcher.runtime.LauncherEngine;
import pro.gravit.launcher.runtime.gui.RuntimeProvider;
import pro.gravit.launcher.runtime.utils.LauncherUpdater;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.LogHelper;

public class StdJavaRuntimeProvider implements RuntimeProvider {
  public static volatile Path updatePath;
  
  private static final AtomicReference<StdJavaRuntimeProvider> INSTANCE = new AtomicReference<>();
  
  public StdJavaRuntimeProvider() {
    INSTANCE.set(this);
  }
  
  public static StdJavaRuntimeProvider getInstance() {
    return INSTANCE.get();
  }
  
  public JavaFXApplication getApplication() {
    return JavaFXApplication.getInstance();
  }
  
  public void run(String[] paramArrayOfString) {
    LogHelper.debug("Start JavaFX Application");
    Application.launch(JavaFXApplication.class, paramArrayOfString);
    LogHelper.debug("Post Application.launch method invoked");
    if (updatePath != null) {
      LauncherUpdater.nothing();
      LauncherEngine.beforeExit(0);
      Path path = IOHelper.getCodeSource(LauncherUpdater.class);
      try {
        InputStream inputStream = IOHelper.newInput(updatePath);
        try {
          OutputStream outputStream = IOHelper.newOutput(path);
          try {
            IOHelper.transfer(inputStream, outputStream);
            if (outputStream != null)
              outputStream.close(); 
          } catch (Throwable throwable) {
            if (outputStream != null)
              try {
                outputStream.close();
              } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
              }  
            throw throwable;
          } 
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
        Files.deleteIfExists(updatePath);
      } catch (IOException iOException) {
        LogHelper.error(iOException);
        LauncherEngine.forceExit(-109);
      } 
      LauncherUpdater.restart();
    } 
  }
  
  public void preLoad() {}
  
  protected void registerPrivateCommands() {
    JavaFXApplication javaFXApplication = JavaFXApplication.getInstance();
    if (javaFXApplication != null)
      javaFXApplication.registerPrivateCommands(); 
  }
  
  public void init(boolean paramBoolean) {}
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\StdJavaRuntimeProvider.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
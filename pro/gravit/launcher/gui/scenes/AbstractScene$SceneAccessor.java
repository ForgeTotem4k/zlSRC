package pro.gravit.launcher.gui.scenes;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.impl.ContextHelper;
import pro.gravit.launcher.gui.overlays.AbstractOverlay;

public class SceneAccessor {
  public void showOverlay(AbstractOverlay paramAbstractOverlay, EventHandler<ActionEvent> paramEventHandler) throws Exception {
    AbstractScene.this.showOverlay(paramAbstractOverlay, paramEventHandler);
  }
  
  public JavaFXApplication getApplication() {
    return AbstractScene.access$000(AbstractScene.this);
  }
  
  public void errorHandle(Throwable paramThrowable) {
    AbstractScene.this.errorHandle(paramThrowable);
  }
  
  public void runInFxThread(ContextHelper.GuiExceptionRunnable paramGuiExceptionRunnable) {
    AbstractScene.access$100(AbstractScene.this).runInFxThread(paramGuiExceptionRunnable);
  }
  
  public <T extends pro.gravit.launcher.base.request.WebSocketEvent> void processRequest(String paramString, Request<T> paramRequest, Consumer<T> paramConsumer, EventHandler<ActionEvent> paramEventHandler) {
    AbstractScene.this.processRequest(paramString, paramRequest, paramConsumer, paramEventHandler);
  }
  
  public final <T extends pro.gravit.launcher.base.request.WebSocketEvent> void processRequest(String paramString, Request<T> paramRequest, Consumer<T> paramConsumer, Consumer<Throwable> paramConsumer1, EventHandler<ActionEvent> paramEventHandler) {
    AbstractScene.this.processRequest(paramString, paramRequest, paramConsumer, paramConsumer1, paramEventHandler);
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\AbstractScene$SceneAccessor.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
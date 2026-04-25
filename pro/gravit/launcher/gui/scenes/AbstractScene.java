package pro.gravit.launcher.gui.scenes;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import javafx.scene.layout.Pane;
import pro.gravit.launcher.base.Launcher;
import pro.gravit.launcher.base.LauncherConfig;
import pro.gravit.launcher.base.events.request.ExitRequestEvent;
import pro.gravit.launcher.base.request.Request;
import pro.gravit.launcher.base.request.WebSocketEvent;
import pro.gravit.launcher.base.request.auth.ExitRequest;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.launcher.gui.impl.AbstractStage;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;
import pro.gravit.launcher.gui.impl.ContextHelper;
import pro.gravit.launcher.gui.overlays.AbstractOverlay;

public abstract class AbstractScene extends AbstractVisualComponent {
  protected final LauncherConfig launcherConfig = Launcher.getConfig();
  
  protected Pane header;
  
  protected AbstractScene(String paramString, JavaFXApplication paramJavaFXApplication) {
    super(paramString, paramJavaFXApplication);
  }
  
  protected AbstractStage getCurrentStage() {
    return this.currentStage;
  }
  
  public void init() throws Exception {
    this.layout = (Pane)getFxmlRoot();
    this.header = LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#header" }).orElse(null);
    sceneBaseInit();
    super.init();
  }
  
  protected abstract void doInit();
  
  protected void doPostInit() {}
  
  protected void onShow() {}
  
  protected void onHide() {}
  
  public void showOverlay(AbstractOverlay paramAbstractOverlay, EventHandler<ActionEvent> paramEventHandler) throws Exception {
    paramAbstractOverlay.show(this.currentStage, paramEventHandler);
  }
  
  protected final <T extends WebSocketEvent> void processRequest(String paramString, Request<T> paramRequest, Consumer<T> paramConsumer, EventHandler<ActionEvent> paramEventHandler) {
    this.application.gui.processingOverlay.processRequest(this.currentStage, paramString, paramRequest, paramConsumer, paramEventHandler);
  }
  
  protected final <T extends WebSocketEvent> void processRequest(String paramString, Request<T> paramRequest, Consumer<T> paramConsumer, Consumer<Throwable> paramConsumer1, EventHandler<ActionEvent> paramEventHandler) {
    this.application.gui.processingOverlay.processRequest(this.currentStage, paramString, paramRequest, paramConsumer, paramConsumer1, paramEventHandler);
  }
  
  protected void sceneBaseInit() {
    initBasicControls((Parent)this.header);
    LookupHelper.lookupIfPossible((Node)this.header, new String[] { "#controls", "#deauth" }).ifPresent(paramButtonBase -> paramButtonBase.setOnAction(()));
  }
  
  protected void userExit() {
    processRequest(this.application.getTranslation("runtime.scenes.settings.exitDialog.processing"), (Request<WebSocketEvent>)new ExitRequest(), paramExitRequestEvent -> ContextHelper.runInFxThreadStatic(()), paramActionEvent -> {
        
        });
  }
  
  protected void switchToBackScene() throws Exception {
    this.currentStage.back();
  }
  
  public void disable() {
    this.currentStage.disable();
  }
  
  public void enable() {
    this.currentStage.enable();
  }
  
  public abstract void reset();
  
  protected void switchScene(AbstractScene paramAbstractScene) throws Exception {
    this.currentStage.setScene(paramAbstractScene, true);
    onHide();
    paramAbstractScene.onShow();
  }
  
  public Node getHeader() {
    return (Node)this.header;
  }
  
  public static void runLater(double paramDouble, EventHandler<ActionEvent> paramEventHandler) {
    fade(null, paramDouble, 0.0D, 1.0D, paramEventHandler);
  }
  
  public class SceneAccessor {
    public void showOverlay(AbstractOverlay param1AbstractOverlay, EventHandler<ActionEvent> param1EventHandler) throws Exception {
      AbstractScene.this.showOverlay(param1AbstractOverlay, param1EventHandler);
    }
    
    public JavaFXApplication getApplication() {
      return AbstractScene.this.application;
    }
    
    public void errorHandle(Throwable param1Throwable) {
      AbstractScene.this.errorHandle(param1Throwable);
    }
    
    public void runInFxThread(ContextHelper.GuiExceptionRunnable param1GuiExceptionRunnable) {
      AbstractScene.this.contextHelper.runInFxThread(param1GuiExceptionRunnable);
    }
    
    public <T extends WebSocketEvent> void processRequest(String param1String, Request<T> param1Request, Consumer<T> param1Consumer, EventHandler<ActionEvent> param1EventHandler) {
      AbstractScene.this.processRequest(param1String, param1Request, param1Consumer, param1EventHandler);
    }
    
    public final <T extends WebSocketEvent> void processRequest(String param1String, Request<T> param1Request, Consumer<T> param1Consumer, Consumer<Throwable> param1Consumer1, EventHandler<ActionEvent> param1EventHandler) {
      AbstractScene.this.processRequest(param1String, param1Request, param1Consumer, param1Consumer1, param1EventHandler);
    }
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\scenes\AbstractScene.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
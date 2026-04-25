package pro.gravit.launcher.gui.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBase;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.helper.LookupHelper;
import pro.gravit.utils.helper.LogHelper;

public abstract class AbstractVisualComponent {
  protected final JavaFXApplication application;
  
  protected final ContextHelper contextHelper;
  
  protected final FXExecutorService fxExecutor;
  
  protected AbstractStage currentStage;
  
  protected Pane layout;
  
  private final String sysFxmlPath;
  
  private Parent sysFxmlRoot;
  
  private CompletableFuture<Node> sysFxmlFuture;
  
  boolean isInit;
  
  boolean isPostInit;
  
  protected boolean isResetOnShow = false;
  
  protected AbstractVisualComponent(String paramString, JavaFXApplication paramJavaFXApplication) {
    this.application = paramJavaFXApplication;
    this.sysFxmlPath = paramString;
    this.contextHelper = new ContextHelper(this);
    this.fxExecutor = new FXExecutorService(this.contextHelper);
    if (paramJavaFXApplication.guiModuleConfig.lazy)
      this.sysFxmlFuture = paramJavaFXApplication.fxmlFactory.getAsync(this.sysFxmlPath); 
  }
  
  public static FadeTransition fade(Node paramNode, double paramDouble1, double paramDouble2, double paramDouble3, EventHandler<ActionEvent> paramEventHandler) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(100.0D), paramNode);
    if (paramEventHandler != null)
      fadeTransition.setOnFinished(paramEventHandler); 
    fadeTransition.setDelay(Duration.millis(paramDouble1));
    fadeTransition.setFromValue(paramDouble2);
    fadeTransition.setToValue(paramDouble3);
    fadeTransition.play();
    return fadeTransition;
  }
  
  protected void initBasicControls(Parent paramParent) {
    if (paramParent == null) {
      LogHelper.warning("Scene %s header button(#close, #hide) deprecated", new Object[] { getName() });
      LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#close" }).ifPresent(paramButtonBase -> paramButtonBase.setOnAction(()));
      LookupHelper.lookupIfPossible((Node)this.layout, new String[] { "#hide" }).ifPresent(paramButtonBase -> paramButtonBase.setOnAction(()));
    } else {
      LookupHelper.lookupIfPossible((Node)paramParent, new String[] { "#controls", "#exit" }).ifPresent(paramButtonBase -> paramButtonBase.setOnAction(()));
      LookupHelper.lookupIfPossible((Node)paramParent, new String[] { "#controls", "#minimize" }).ifPresent(paramButtonBase -> paramButtonBase.setOnAction(()));
    } 
    this.currentStage.enableMouseDrag((Node)this.layout);
  }
  
  public Pane getLayout() {
    return this.layout;
  }
  
  public boolean isInit() {
    return this.isInit;
  }
  
  public abstract String getName();
  
  protected synchronized Parent getFxmlRoot() {
    try {
      if (this.sysFxmlRoot == null) {
        if (this.sysFxmlFuture == null)
          this.sysFxmlFuture = this.application.fxmlFactory.getAsync(this.sysFxmlPath); 
        this.sysFxmlRoot = (Parent)this.sysFxmlFuture.get();
      } 
      return this.sysFxmlRoot;
    } catch (InterruptedException interruptedException) {
      throw new RuntimeException(interruptedException);
    } catch (ExecutionException executionException) {
      Throwable throwable = executionException.getCause();
      if (throwable instanceof java.util.concurrent.CompletionException)
        throwable = throwable.getCause(); 
      if (throwable instanceof RuntimeException) {
        RuntimeException runtimeException = (RuntimeException)throwable;
        throw runtimeException;
      } 
      throw new FXMLFactory.FXMLLoadException(throwable);
    } 
  }
  
  public void init() throws Exception {
    if (this.layout == null)
      this.layout = (Pane)getFxmlRoot(); 
    doInit();
    this.isInit = true;
  }
  
  public void postInit() throws Exception {
    if (!this.isPostInit) {
      doPostInit();
      this.isPostInit = true;
    } 
  }
  
  protected abstract void doInit();
  
  protected abstract void doPostInit();
  
  public abstract void reset();
  
  public abstract void disable();
  
  public abstract void enable();
  
  public void errorHandle(Throwable paramThrowable) {
    String str = null;
    if (paramThrowable instanceof java.util.concurrent.CompletionException)
      paramThrowable = paramThrowable.getCause(); 
    if (paramThrowable instanceof ExecutionException)
      paramThrowable = paramThrowable.getCause(); 
    if (paramThrowable instanceof pro.gravit.launcher.base.request.RequestException)
      str = paramThrowable.getMessage(); 
    if (str == null) {
      str = "%s: %s".formatted(new Object[] { paramThrowable.getClass().getName(), paramThrowable.getMessage() });
    } else {
      str = this.application.getTranslation("runtime.request.".concat(str), str);
    } 
    LogHelper.error(paramThrowable);
    this.application.messageManager.createNotification("Ошибка", str);
  }
  
  protected Parent getFxmlRootPrivate() {
    return getFxmlRoot();
  }
  
  public boolean isDisableReturnBack() {
    return false;
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\impl\AbstractVisualComponent.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */
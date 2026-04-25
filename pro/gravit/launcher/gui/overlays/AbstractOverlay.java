package pro.gravit.launcher.gui.overlays;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Duration;
import pro.gravit.launcher.gui.JavaFXApplication;
import pro.gravit.launcher.gui.impl.AbstractStage;
import pro.gravit.launcher.gui.impl.AbstractVisualComponent;

public abstract class AbstractOverlay extends AbstractVisualComponent {
  private final AtomicInteger useCounter = new AtomicInteger(0);
  
  private final AtomicReference<FadeTransition> fadeTransition = new AtomicReference<>();
  
  protected AbstractOverlay(String paramString, JavaFXApplication paramJavaFXApplication) {
    super(paramString, paramJavaFXApplication);
  }
  
  public final void init() throws Exception {
    super.init();
  }
  
  public final void hide(double paramDouble, EventHandler<ActionEvent> paramEventHandler) {
    if (!Platform.isFxApplicationThread())
      throw new RuntimeException("hide() called from non FX application thread"); 
    if (this.useCounter.decrementAndGet() != 0) {
      this.contextHelper.runInFxThread(() -> {
            if (paramEventHandler != null)
              paramEventHandler.handle(null); 
          });
      return;
    } 
    if (!isInit())
      throw new IllegalStateException("Using method hide before init"); 
    this.fadeTransition.set(fade((Node)getFxmlRoot(), paramDouble, 1.0D, 0.0D, paramActionEvent -> {
            if (paramEventHandler != null)
              paramEventHandler.handle((Event)paramActionEvent); 
            this.currentStage.pull((Node)getFxmlRoot());
            this.currentStage.enable();
            this.fadeTransition.set(null);
          }));
  }
  
  protected abstract void doInit();
  
  protected void doPostInit() {}
  
  public abstract void reset();
  
  public void disable() {}
  
  public void enable() {}
  
  public void show(AbstractStage paramAbstractStage, EventHandler<ActionEvent> paramEventHandler) throws Exception {
    if (!Platform.isFxApplicationThread())
      throw new RuntimeException("show() called from non FX application thread"); 
    if (!isInit())
      init(); 
    if (this.useCounter.incrementAndGet() != 1) {
      this.contextHelper.runInFxThread(() -> {
            if (paramEventHandler != null)
              paramEventHandler.handle(null); 
          });
      return;
    } 
    if (this.fadeTransition.get() != null) {
      this.currentStage.disable();
      ((FadeTransition)this.fadeTransition.get()).jumpTo(Duration.ZERO);
      ((FadeTransition)this.fadeTransition.get()).stop();
      this.contextHelper.runInFxThread(() -> {
            if (paramEventHandler != null)
              paramEventHandler.handle(null); 
          });
      this.fadeTransition.set(null);
      return;
    } 
    Parent parent = getFxmlRoot();
    this.currentStage = paramAbstractStage;
    this.currentStage.enableMouseDrag((Node)this.layout);
    this.currentStage.push((Node)parent);
    this.currentStage.disable();
    fade((Node)parent, 100.0D, 0.0D, 1.0D, paramActionEvent -> {
          if (paramEventHandler != null)
            paramEventHandler.handle((Event)paramActionEvent); 
        });
  }
}


/* Location:              C:\Users\forge\zLauncher\Launcher.jar!\pro\gravit\launcher\gui\overlays\AbstractOverlay.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */